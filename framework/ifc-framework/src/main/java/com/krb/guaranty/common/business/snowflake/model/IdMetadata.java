package com.krb.guaranty.common.business.snowflake.model;

/**
 * 一个long表示的每个区， 最高位不用
 */
public class IdMetadata {
    /**
     * 自增ID 12位
     */
    public static byte SEQUENCE_BIT = 0;

    /**
     * 序列号的掩码
     */
    public static long SEQUENCE_MASK = ~(-1L << 12);


    /**
     * worker 10 位
     */
    public static byte WORKER_BIT = 12;

    /**
     * 时间戳
     */
    public static byte TIMESTAMP_BIT = 22;

    public static long TIMESTAMP_MASK = (-1L << 41);

    public static long TIME_START = 1542796877695L;
}
