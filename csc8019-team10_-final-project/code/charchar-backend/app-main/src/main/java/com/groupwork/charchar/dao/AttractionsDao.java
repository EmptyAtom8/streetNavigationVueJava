package com.groupwork.charchar.dao;

import com.groupwork.charchar.entity.AttractionsEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author wangyilong
 * @email 571379772@qq.com
 * @date 2023-03-24 15:33:03
 */
@Mapper
public interface AttractionsDao extends BaseMapper<AttractionsEntity> {


    /**
     * Retrieves an attraction by its ID.
     *
     * @param attractionId The ID of the attraction.
     * @return The attraction entity.
     */
    AttractionsEntity getAttractionById(Integer attractionId);

    /**
     * Updates the rating of an attraction.
     *
     * @param attractionId The ID of the attraction.
     * @param attrRating   The updated attraction rating.
     * @return The number of affected rows in the database.
     */
    int updateAttractionRating(Integer attractionId, Double attrRating);

    /**
     * Retrieves an attraction by its Google Place ID.
     *
     * @param placeId The Google Place ID of the attraction.
     * @return The attraction entity.
     */
    AttractionsEntity getAttractionByPlaceId(String placeId);

    /**
     * Finds an attraction by its Google Place ID.
     *
     * @param placeId The Google Place ID of the attraction.
     * @return The attraction entity.
     */
    AttractionsEntity findByPlaceId(String placeId);
}
