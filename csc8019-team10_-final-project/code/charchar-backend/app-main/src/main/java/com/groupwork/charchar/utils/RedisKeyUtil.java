package com.groupwork.charchar.utils;

/**
 * @className: RedisKeyUtil
 * @Author: wyl
 * @Description: a util for creating a redis key.
 * @Date: 07/05/2023 16:42
 */
public class RedisKeyUtil {
    private static final String SPLIT = ":";
    private static final String LIKE_PREFIX = "like:review";
    private static StringBuffer stringBuffer;

    public static String getRedisLikeKey(int reviewId) {
        stringBuffer = new StringBuffer();
        stringBuffer.append(SPLIT).append(LIKE_PREFIX).append(reviewId);
        return stringBuffer.toString();
    }

    public static String getRedisAttractionReviewKey(int attractionId) {
        return "attraction:" + attractionId + ":reviews";
    }
}
