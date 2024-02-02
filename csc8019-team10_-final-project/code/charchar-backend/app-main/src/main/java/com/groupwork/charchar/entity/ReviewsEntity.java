package com.groupwork.charchar.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Yilong Wang
 * @email 571379772@qq.com
 * @date 2023-03-24 15:33:03
 */
@Data
@TableName("reviews")
public class ReviewsEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Review entity class.
     */
    @TableId(type = IdType.AUTO)
    private Integer reviewId;
    /**
     * ID of the attraction the review belongs to.
     */
    private Integer attractionId;
    /**
     * ID of the user who wrote the review.
     */
    private Integer userId;
    /**
     * Rating given in the review.
     */
    private Integer rating;
    /**
     * Text content of the review.
     */
    private String reviewText;
    /**
     * Creation time of the review.
     */
    private Date createTime;
    /**
     * Update time of the review.
     */
    private Date updateTime;

}
