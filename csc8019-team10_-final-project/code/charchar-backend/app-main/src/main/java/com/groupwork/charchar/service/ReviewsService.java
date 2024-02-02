package com.groupwork.charchar.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.groupwork.charchar.entity.ReviewsEntity;

import java.util.List;

/**
 * @author Yilong Wang
 * @email 571379772@qq.com
 * @date 2023-03-24 15:33:03
 */
public interface ReviewsService extends IService<ReviewsEntity> {
    /**
     * Retrieves a list of reviews for a given attraction ID.
     *
     * @param attractionId The ID of the attraction.
     * @return A list of ReviewsEntity objects.
     */
    List<ReviewsEntity> listReviewsByAttractionId(Integer attractionId);

    /**
     * Retrieves a list of reviews for a given user ID.
     *
     * @param userId The ID of the user.
     * @return A list of ReviewsEntity objects.
     */
    List<ReviewsEntity> listReviewsByUserId(Integer userId);

    /**
     * Retrieves a paginated list of reviews for a given attraction ID.
     *
     * @param attractionId The ID of the attraction.
     * @param page         The page number.
     * @param size         The number of reviews per page.
     * @return An IPage containing the paginated reviews.
     */
    IPage<ReviewsEntity> listReviewsByAttractionIdWithPage(Integer attractionId, Integer page, Integer size);

    /**
     * Retrieves a paginated list of reviews for a given user ID.
     *
     * @param userId The ID of the user.
     * @param page   The page number.
     * @param size   The number of reviews per page.
     * @return An IPage containing the paginated reviews.
     */
    IPage<ReviewsEntity> listReviewsByUserIdWithPage(Integer userId, Integer page, Integer size);

    /**
     * Retrieves a review entity by its review ID.
     *
     * @param reviewId The ID of the review.
     * @return The ReviewsEntity object.
     */
    ReviewsEntity getReviewEntityById(int reviewId);

    /**
     * Retrieves a list of reviews for a given attraction ID.
     *
     * @param attractionId The ID of the attraction.
     * @return A list of ReviewsEntity objects.
     */
    List<ReviewsEntity> getReviewsByAttractionId(int attractionId);
}

