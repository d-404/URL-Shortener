package com.shortener.entity;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class ShortUrl {
	@Id
	private Long id;
	@Column(name = "short_code", nullable = false, unique = true)
	private String shortCode;
	@Column(name = "target_url", nullable = false, columnDefinition = "text")
	private String targetUrl;
	@Column(name = "created_at", nullable = false)
	private OffsetDateTime createdAt = OffsetDateTime.now();
	@Column(name = "expires_at")
	private OffsetDateTime expiresAt;
	@Column(name = "is_active", nullable = false)
	private boolean active = true;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getShortCode() {
		return shortCode;
	}

	public void setShortCode(String shortCode) {
		this.shortCode = shortCode;
	}

	public String getTargetUrl() {
		return targetUrl;
	}

	public void setTargetUrl(String targetUrl) {
		this.targetUrl = targetUrl;
	}

	public OffsetDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(OffsetDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public OffsetDateTime getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(OffsetDateTime expiresAt) {
		this.expiresAt = expiresAt;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

}
