package com.groupwork.charchar.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Yilong Wang
 * @email 571379772@qq.com
 * @date 2023-03-24 15:33:03
 */
@Data
@TableName("attractions")
public class AttractionsEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Attraction entity class.
     */
    @TableId(type = IdType.AUTO)
    private Integer attractionId;
    /**
     * Name of the attraction.
     */
    private String attractionName;
    /**
     * Description of the attraction.
     */
    private String description;
    /**
     * Category of the attraction.
     */
    private String category;
    /**
     * Latitude coordinate of the attraction's location.
     */
    private BigDecimal latitude;
    /**
     * Longitude coordinate of the attraction's location.
     */
    private BigDecimal longitude;
    /**
     * Opening hours of the attraction.
     */
    private String openingHours;
    /**
     * Ticket price of the attraction.
     */
    private BigDecimal ticketPrice;
    /**
     * Image URL of the attraction.
     */
    private String imageUrl;
    /**
     * Rating of the attraction.
     */
    private Double attrRating;
    /**
     * Flag indicating if the attraction is wheelchair accessible.
     */
    private Integer wheelchairAllow;
    /**
     * Flag indicating if the attraction allows prams/strollers.
     */
    private Integer pramAllow;
    /**
     * Flag indicating if the attraction has hearing support.
     */
    private Integer hearingAllow;
    /**
     * Address of the attraction.
     */
    private String address;
    /**
     * Place ID of the attraction.
     */
    private String placeId;
}
