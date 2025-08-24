package com.shortener.dto;

public record CreateShortUrlRequest(String url, String customAlias, Long ttlSeconds) {
}