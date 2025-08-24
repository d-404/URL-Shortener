package com.shortener.dto;

import java.time.OffsetDateTime;

public record CreateShortUrlResponse(String code, String shortUrl, OffsetDateTime expiresAt) {
}