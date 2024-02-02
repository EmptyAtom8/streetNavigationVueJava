package com.groupwork.charchar.vo;

import lombok.Data;

/**
 * @className: TopReviewsVO
 * @Author: wyl
 * @Description: return top review to front-end entity
 * @Date: 03/05/2023 17:28
 */
@Data
public class TopReviewsVO {
    /**
     * ID of the user who made the review.
     */
    private Integer userId;
    /**
     * Content of the review.
     */
    private String reviewContent;
    /**
     * Rating given in the review.
     */
    private Integer rating;
    /**
     * Count of the number of likes the review has received.
     */
    private Integer likeCount;

}
