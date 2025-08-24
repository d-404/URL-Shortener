package com.shortener.service;

import java.net.URI;
import java.time.Duration;
import java.time.OffsetDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.shortener.dto.CreateShortUrlRequest;
import com.shortener.dto.CreateShortUrlResponse;
import com.shortener.entity.ShortUrl;
import com.shortener.repository.ShortUrlRepository;
import com.shortener.util.Base62;
import com.shortener.util.Snowflake;

@Service
public class UrlService {
	private final ShortUrlRepository shortUrlRepository;
	private final RedisTemplate<String, String> redisTemplate;
	private final Snowflake snowflake = new Snowflake(1);

	@Value("${app.short-domain:http://localhost:8080}")
	private String shortDomain;

	public UrlService(ShortUrlRepository shortUrlRepository, RedisTemplate<String, String> redisTemplate) {
		this.shortUrlRepository = shortUrlRepository;
		this.redisTemplate = redisTemplate;
	}

	public CreateShortUrlResponse create(CreateShortUrlRequest createShortUrlRequest) {
		String normalized = validateAndNormalize(createShortUrlRequest.url());
		OffsetDateTime expires = createShortUrlRequest.ttlSeconds() != null
				? OffsetDateTime.now().plusSeconds(createShortUrlRequest.ttlSeconds())
				: null;
		String code;
		if (createShortUrlRequest.customAlias() != null && !createShortUrlRequest.customAlias().isBlank()) {
			code = validateAlias(createShortUrlRequest.customAlias());
			if (shortUrlRepository.existsByShortCode(code))
				throw new ResponseStatusException(HttpStatus.CONFLICT, "Alias already exists!");
			persist(code, normalized, expires);
		} else {
			long id = snowflake.nextId();
			code = Base62.encode(id);
			persist(code, normalized, expires);
		}
		cachePut(code, normalized, expires);
		return new CreateShortUrlResponse(code, shortDomain + "/" + code, expires);
	}

	public String resolve(String code) {
		String key = key(code);
		String cached = redisTemplate.opsForValue().get(key);
		if (cached != null)
			return cached;
		ShortUrl url = shortUrlRepository.findByShortCode(code).filter(
				s -> s.isActive() && (s.getExpiresAt() == null || s.getExpiresAt().isAfter(OffsetDateTime.now())))
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		cachePut(code, url.getTargetUrl(), url.getExpiresAt());
		return url.getTargetUrl();
	}

	private void cachePut(String code, String target, OffsetDateTime expires) {
		String key = key(code);
		if (expires != null) {
			long ttl = Duration.between(OffsetDateTime.now(), expires).toSeconds();
			if (ttl > 0)
				redisTemplate.opsForValue().set(key, target, Duration.ofSeconds(ttl));
		} else {
			redisTemplate.opsForValue().set(key, target, Duration.ofDays(30));
		}
	}

	private void persist(String code, String target, OffsetDateTime expires) {
		ShortUrl url = new ShortUrl();
		url.setId(snowflake.nextId());
		url.setShortCode(code);
		url.setTargetUrl(target);
		url.setExpiresAt(expires);
		shortUrlRepository.save(url);
	}

	private String validateAlias(String customAlias) {
		if (!customAlias.matches("^[a-zA-Z0-9_-]{3,30}$"))
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid alias");
		return customAlias;
	}

	private String validateAndNormalize(String url) {
		try {
			URI uri = URI.create(url.trim());
			if (uri.getScheme() == null
					|| !(uri.getScheme().equalsIgnoreCase("http") || uri.getScheme().equalsIgnoreCase("https")))
				throw new IllegalArgumentException("Only http/https URLs allowed!");
			return url.toString();
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid URL!");
		}
	}

	private String key(String code) {
		return "url:" + code;
	}
}
