package com.shortener.filter;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

	private ConcurrentHashMap<String, Bucket> cache = new ConcurrentHashMap<>();

	@SuppressWarnings("deprecation")
	private Bucket createNewBucket() {
		Refill refill = Refill.intervally(10, Duration.ofMinutes(1));
		Bandwidth limit = Bandwidth.classic(10, refill);
		return Bucket.builder().addLimit(limit).build();
	}

	private Bucket resolveBucket(String ip) {
		return cache.computeIfAbsent(ip, k -> createNewBucket());
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String ip = request.getRemoteAddr();
		Bucket bucket = resolveBucket(ip);
		if (bucket.tryConsume(1))
			filterChain.doFilter(request, response);
		else {
			response.setStatus(429);
			response.getWriter().write("Too manay requests - try again later!");
		}
	}

}
