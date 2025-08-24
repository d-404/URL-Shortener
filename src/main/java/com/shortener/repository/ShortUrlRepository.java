package com.shortener.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shortener.entity.ShortUrl;

public interface ShortUrlRepository extends JpaRepository<ShortUrl, Long> {
	Optional<ShortUrl> findByShortCode(String code);

	boolean existsByShortCode(String code);
}
