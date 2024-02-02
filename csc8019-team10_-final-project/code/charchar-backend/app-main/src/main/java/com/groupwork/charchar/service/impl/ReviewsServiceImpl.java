package com.groupwork.charchar.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.groupwork.charchar.dao.ReviewsDao;
import com.groupwork.charchar.entity.ReviewsEntity;
import com.groupwork.charchar.service.ReviewsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Service("reviewsService")
public class ReviewsServiceImpl extends ServiceImpl<ReviewsDao, ReviewsEntity> implements ReviewsService {

    @Resource
    private ReviewsDao reviewsDao;

    /**
     * Retrieve a list of reviews by attraction ID.
     *
     * @param attractionId The ID of the attraction.
     * @return A list of ReviewsEntity objects.
     */
    @Override
    public List<ReviewsEntity> listReviewsByAttractionId(Integer attractionId) {
        QueryWrapper<ReviewsEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("attraction_id", attractionId);
        return this.list(wrapper);
    }

    /**
     * Retrieve a list of reviews by user ID.
     *
     * @param userId The ID of the user.
     * @return A list of ReviewsEntity objects.
     */
    @Override
    public List<ReviewsEntity> listReviewsByUserId(Integer userId) {
        QueryWrapper<ReviewsEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        return this.list(wrapper);
    }

    /**
     * Retrieve a page of reviews by attraction ID.
     *
     * @param attractionId The ID of the attraction.
     * @param page         The page number.
     * @param size         The number of reviews per page.
     * @return An IPage containing the reviews.
     */
    @Override
    public IPage<ReviewsEntity> listReviewsByAttractionIdWithPage(Integer attractionId, Integer page, Integer size) {
        Page<ReviewsEntity> reviewPage = new Page<>(page, size);
        QueryWrapper<ReviewsEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("attraction_id", attractionId);
        return this.page(reviewPage, wrapper);
    }

    /**
     * Retrieve a page of reviews by user ID.
     *
     * @param userId The ID of the user.
     * @param page   The page number.
     * @param size   The number of reviews per page.
     * @return An IPage containing the reviews.
     */
    @Override
    public IPage<ReviewsEntity> listReviewsByUserIdWithPage(Integer userId, Integer page, Integer size) {
        Page<ReviewsEntity> reviewPage = new Page<>(page, size);
        QueryWrapper<ReviewsEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        return this.page(reviewPage, wrapper);
    }

    /**
     * Get a review entity by review ID.
     *
     * @param reviewId The ID of the review.
     * @return The ReviewsEntity object.
     */
    @Override
    public ReviewsEntity getReviewEntityById(int reviewId) {
        QueryWrapper<ReviewsEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("review_id", reviewId);
        return reviewsDao.selectOne(wrapper);
    }

    /**
     * Retrieve a list of reviews by attraction ID.
     *
     * @param attractionId The ID of the attraction.
     * @return A list of ReviewsEntity objects.
     */
    @Override
    public List<ReviewsEntity> getReviewsByAttractionId(int attractionId) {
        QueryWrapper<ReviewsEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("attraction_id", attractionId);
        return this.list(wrapper);
    }
}