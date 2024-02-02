package com.groupwork.charchar.service;

import com.groupwork.charchar.vo.TopReviewsVO;

import java.util.List;

/**
 * @author Yilong Wang
 * @email 571379772@qq.com
 * @date 2023-04-24 16:56:14
 */
public interface ILikeService {
    /**
     * Records a user's like for a review.
     *
     * @param userId   The ID of the user.
     * @param reviewId The ID of the review.
     */
    void like(Integer userId, Integer reviewId);

    /**
     * Retrieves the like count for a review.
     *
     * @param reviewId The ID of the review.
     * @return The number of likes for the review.
     */
    long findLikeCount(Integer reviewId);

    /**
     * Retrieves the like status of a user for a review.
     *
     * @param userId   The ID of the user.
     * @param reviewId The ID of the review.
     * @return 1 if the user has liked the review, 0 otherwise.
     */
    int findCurUserLikeStatus(Integer userId, Integer reviewId);

    /**
     * Updates the like count for reviews of an attraction.
     *
     * @param attractionId The ID of the attraction.
     */
    void updateReviewLikeCount(Integer attractionId);

    /**
     * Retrieves the top reviews for an attraction based on the number of likes.
     *
     * @param attractionId The ID of the attraction.
     * @param topN         The number of top reviews to retrieve.
     * @return A list of TopReviewsVO objects representing the top reviews.
     */
    List<TopReviewsVO> getTopReviews(Integer attractionId, Integer topN);
}
