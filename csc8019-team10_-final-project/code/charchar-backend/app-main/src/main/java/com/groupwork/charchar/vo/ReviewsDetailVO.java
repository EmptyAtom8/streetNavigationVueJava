package com.groupwork.charchar.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

/**
 * @className: reviewsDetailVO
 * @Author: wyl
 * @Description: detail of review
 * @Date: 07/05/2023 16:35
 */
@Data
public class ReviewsDetailVO {
    /**
     * Review entity class.
     */
    @TableId
    private Integer reviewId;
    /**
     * ID of the attraction the review belongs to.
     */
    private Integer attractionId;
    /**
     * ID of the user who made the review.
     */
    private Integer userId;
    /**
     * Username of the user who made the review.
     */
    private String username;
    /**
     * Rating given in the review.
     */
    private Integer rating;
    /**
     * Text content of the review.
     */
    private String reviewText;
    /**
     * Count of the number of likes the review has received.
     */
    private Long likeCount;
    /**
     * Like status of the review. 0 indicates not liked, 1 indicates liked.
     */
    private Integer likeStatus;
    /**
     * Creation time of the review.
     */
    private Date createTime;
    /**
     * Last update time of the review.
     */
    private Date updateTime;


}
