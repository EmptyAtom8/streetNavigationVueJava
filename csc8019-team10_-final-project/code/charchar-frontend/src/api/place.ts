import request from "@/util/serive";
import Url from "./url";

// get nearby location
export function getNearByLocation(params) {
    const { latitude, longitude, radius } = params;
    return request({
        url: `/charchar/attractions/near/location/${latitude}/${longitude}/${radius}`,
        method: "get",
    });
}

// get attraction information
export function getAttractionInfo(params) {
    const { placeId } = params;
    return request({
        url: `/charchar/attractions/findAttractionByID/${placeId}`,
        method: "get",
    });
}

// Get a list of reviews of the current user's attractions
export function getAttractionReviewsList(params) {
    const { attractionId,userId } = params;
    // params = {attractionId, userId}
    return request({
        url: `/charchar/reviews/list/attr/${attractionId}/${userId}`,
        method: "get",
        //params
    });
}

// add a comment
export function saveReviews(data) {
    /**
     * attractionId;
       userId;
       rating;
       reviewText;
     */
    return request({
        url: "/charchar/reviews/save",
        method: "post",
        data,
    });
}

// User liked comments
export function giveLikeOfReviews(data) {
    const { userId, reviewId } = data
    return request({
        url: `/charchar/like/giveLike/${userId}/${reviewId}`,
        method: "post",
        data,
    });
}

// Get a list of user comments
export function getUserReviewsList(params) {
    const { userId } = params
    return request({
        url: `/charchar/reviews/list/user/${userId}`,
        method: "get",
    });
}

// Delete user comment list
export function deleteUserReviews(data) {
    
    return request({
        url: `/charchar/reviews/delete`,
        method: "delete",
        data
    });
}
