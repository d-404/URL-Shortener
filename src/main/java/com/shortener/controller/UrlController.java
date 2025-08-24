package com.shortener.controller;

import java.net.URI;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.shortener.dto.CreateShortUrlRequest;
import com.shortener.dto.CreateShortUrlResponse;
import com.shortener.service.UrlService;

@RestController
public class UrlController {
	private final UrlService urlService;

	public UrlController(UrlService urlService) {
		this.urlService = urlService;
	}

	@PostMapping("/api/v1/urls")
	public CreateShortUrlResponse create(@RequestBody CreateShortUrlRequest req) {
		return urlService.create(req);
	}

	@GetMapping("/{code}")
	public ResponseEntity<Void> redirect(@PathVariable String code) {
		String target = urlService.resolve(code);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(URI.create(target));
		return new ResponseEntity<>(headers, HttpStatus.FOUND);
	}
}
