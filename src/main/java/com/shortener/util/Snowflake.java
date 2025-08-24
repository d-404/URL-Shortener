package com.shortener.util;

public class Snowflake {
	private final long nodeId;
	private long lastTimestamp = -1L;
	private long sequence = 0L;
	private static final long EPOCH = 1577836800000L;
	private static final long NODE_ID_BITS = 10L;
	private static final long SEQUENCE_BITS = 12L;
	private static final long MAX_NODE_ID = (1L << NODE_ID_BITS) - 1;
	private static final long MAX_SEQUENCE = (1L << SEQUENCE_BITS) - 1;

	public Snowflake(long nodeId) {
		if (nodeId < 0 || nodeId > MAX_NODE_ID) {
			throw new IllegalArgumentException("NodeId must be between 0 and " + MAX_NODE_ID);
		}
		this.nodeId = nodeId;
	}

	public synchronized long nextId() {
		long current = System.currentTimeMillis();
		if (current < lastTimestamp)
			throw new RuntimeException("Clock moved backwards!");
		if (current == lastTimestamp) {
			sequence = (sequence + 1) & MAX_SEQUENCE;
			if (sequence == 0)
				while ((current = System.currentTimeMillis()) == lastTimestamp) {
				}
		} else
			sequence = 0;
		lastTimestamp = current;
		return ((current - EPOCH) << (NODE_ID_BITS + SEQUENCE_BITS)) | (nodeId << SEQUENCE_BITS) | sequence;
	}
}
