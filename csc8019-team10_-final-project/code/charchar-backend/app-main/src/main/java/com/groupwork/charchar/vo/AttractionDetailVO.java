package com.groupwork.charchar.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @className: AttractionDetailVO
 * @Author: wyl
 * @Description:
 * @Date: 07/05/2023 16:42
 */
@Data
public class AttractionDetailVO implements Serializable {
    private static final long serialVersionUID = -3826891916021780628L;
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
     * Latitude coordinate of the attraction.
     */
    private BigDecimal latitude;
    /**
     * Longitude coordinate of the attraction.
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
     * Flag indicating if wheelchair accessibility is allowed at the attraction.
     */
    private Integer wheelchairAllow;
    /**
     * Flag indicating if pram accessibility is allowed at the attraction.
     */
    private Integer pramAllow;
    /**
     * Flag indicating if hearing accessibility is allowed at the attraction.
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
    /**
     * Walking time from the user's location to the attraction.
     */
    private String walkingTime;
    /**
     * Current opening status of the attraction.
     */
    private Integer openingStatus;
}
