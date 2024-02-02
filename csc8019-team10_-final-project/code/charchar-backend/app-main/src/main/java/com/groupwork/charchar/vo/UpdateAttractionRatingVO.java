package com.groupwork.charchar.vo;

import lombok.Data;

/**
 * @Author: wyl
 * @Description: update attraction rating VO
 * @Date: Created in 11:39 19/04/2023
 */
@Data
public class UpdateAttractionRatingVO {
    /**
     * ID of the attraction.
     */
    private Integer attractionId;
    /**
     * Updated attraction rating.
     */
    private Double attrRating;
}
