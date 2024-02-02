package com.groupwork.charchar.controller;

import com.groupwork.charchar.service.ILikeService;
import com.groupwork.charchar.vo.TopReviewsVO;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Yilong Wang
 * @email 571379772@qq.com
 * @date 2023-04-24 16:45:03
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("charchar/like")
public class LikeController {
    @Resource
    ILikeService likeService;

    /**
     * Like a review by a user.
     *
     * @param userId   The ID of the user.
     * @param reviewId The ID of the review.
     * @return A map containing the updated like count and like status.
     */
    @PostMapping("/giveLike/{userId}/{reviewId}")
    public Map<String, Object> like(@PathVariable Integer userId, @PathVariable Integer reviewId) {
        likeService.like(userId, reviewId);
        long likeCount = likeService.findLikeCount(reviewId);
        int likeStatus = likeService.findCurUserLikeStatus(userId, reviewId);
        Map<String, Object> resMap = new HashMap<>();
        resMap.put("likeCount", likeCount);
        resMap.put("likeStatus", likeStatus);
        return resMap;
    }

    /**
     * Update the like count for the latest reviews of an attraction.
     *
     * @param attractionId The ID of the attraction.
     */
    @PostMapping("/updateReviewLike/{attractionId}")
    public void updateReviewLikeCount(@PathVariable Integer attractionId) {
        likeService.updateReviewLikeCount(attractionId);
    }

    /**
     * Retrieve the top N reviews with the highest number of likes for an attraction.
     *
     * @param attractionId The ID of the attraction.
     * @param topN         The number of top reviews to retrieve.
     * @return A list of TopReviewsVO objects representing the top reviews.
     */
    @GetMapping("/getTopReviews/{attractionId}/{topN}")
    public List<TopReviewsVO> getTopReviews(@PathVariable("attractionId") Integer attractionId,
                                            @PathVariable("topN") Integer topN) {
        return likeService.getTopReviews(attractionId, topN);
    }
}
