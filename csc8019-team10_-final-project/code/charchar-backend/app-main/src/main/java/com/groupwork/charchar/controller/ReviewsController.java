package com.groupwork.charchar.controller;

import com.groupwork.charchar.dao.UsersDao;
import com.groupwork.charchar.entity.ReviewsEntity;
import com.groupwork.charchar.entity.UsersEntity;
import com.groupwork.charchar.service.ILikeService;
import com.groupwork.charchar.service.ReviewsService;
import com.groupwork.charchar.vo.ReviewsDetailVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;


/**
 * @author Yilong Wang
 * @email 571379772@qq.com
 * @date 2023-03-24 15:33:03
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("charchar/reviews")
public class ReviewsController {
    private Logger logger = LoggerFactory.getLogger(ReviewsController.class);
    @Autowired
    private ReviewsService reviewsService;
    @Autowired
    private ILikeService likeService;
    @Autowired
    private UsersDao usersDao;
    public final static int IS_VALID = 1;


    /**
     * Retrieve all reviews for a specific attraction.
     *
     * @param attractionId The ID of the attraction.
     * @param userId       The ID of the user.
     * @return A list of ReviewsDetailVO objects representing the reviews.
     */
    @GetMapping("/list/attr/{attractionId}/{userId}")
    public List<ReviewsDetailVO> listReviewsByAttraction(@PathVariable("attractionId") Integer attractionId,
                                                         @PathVariable("userId") Integer userId) {
        List<ReviewsEntity> reviews = reviewsService.listReviewsByAttractionId(attractionId);
        List<ReviewsDetailVO> res = new ArrayList<>();
        for (ReviewsEntity review : reviews) {
            UsersEntity usersEntity = usersDao.selectUserEntityById(review.getUserId());
            if (null == usersEntity) {
                throw new RuntimeException("userId:" + review.getUserId() + " not exists.");
            }
            if (usersEntity.getIsValid() != IS_VALID) {
                throw new RuntimeException("userId:" + review.getUserId() + " not valid");
            }
            ReviewsDetailVO reviewsDetailVO = new ReviewsDetailVO();
            reviewsDetailVO.setReviewId(review.getReviewId());
            reviewsDetailVO.setAttractionId(review.getAttractionId());
            reviewsDetailVO.setUserId(review.getUserId());
            reviewsDetailVO.setUsername(usersEntity.getUsername());
            reviewsDetailVO.setRating(review.getRating());
            reviewsDetailVO.setReviewText(review.getReviewText());
            reviewsDetailVO.setCreateTime(review.getCreateTime());
            reviewsDetailVO.setUpdateTime(review.getUpdateTime());
            long likeCount = likeService.findLikeCount(reviewsDetailVO.getReviewId());
            int likeStatus = likeService.findCurUserLikeStatus(userId, reviewsDetailVO.getReviewId());
            reviewsDetailVO.setLikeStatus(likeStatus);
            reviewsDetailVO.setLikeCount(likeCount);
            res.add(reviewsDetailVO);
        }
        return res;
    }

    /**
     * Retrieve all reviews by a specific user.
     *
     * @param userId The ID of the user.
     * @return A list of ReviewsEntity objects representing the reviews.
     */
    @GetMapping("/list/user/{userId}")
    public List<ReviewsEntity> listReviewsByUser(@PathVariable("userId") Integer userId) {
        List<ReviewsEntity> reviews = reviewsService.listReviewsByUserId(userId);
        return reviews;
    }


    /**
     * Save a review.
     *
     * @param reviews The ReviewsEntity object representing the review to be saved.
     * @return A map indicating the success of the operation.
     */
    @PostMapping("/save")
    public Map<String, Boolean> saveReview(@RequestBody ReviewsEntity reviews) {
        boolean success = reviewsService.save(reviews);
        Map<String, Boolean> response = new HashMap<>();
        response.put("success", success);
        return response;
    }


    /**
     * Delete a review.
     *
     * @param reviewIds An array of review IDs to be deleted.
     * @return A map indicating the success of the operation.
     */
    @DeleteMapping("/delete")
    public Map<String, Boolean> deleteReview(@RequestBody Integer[] reviewIds) {
        boolean success = reviewsService.removeByIds(Arrays.asList(reviewIds));
        Map<String, Boolean> response = new HashMap<>();
        response.put("success", success);
        return response;
    }

}
