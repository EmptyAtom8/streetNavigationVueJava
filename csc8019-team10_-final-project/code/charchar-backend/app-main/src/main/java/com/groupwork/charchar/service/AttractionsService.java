package com.groupwork.charchar.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.groupwork.charchar.entity.AttractionsEntity;
import com.groupwork.charchar.vo.AttractionDetailVO;
import com.groupwork.charchar.vo.UpdateAttractionRatingVO;
import org.json.JSONException;

import java.io.IOException;
import java.util.List;

/**
 * @author Yilong Wang
 * @email 571379772@qq.com
 * @date 2023-03-24 15:33:03
 */
public interface AttractionsService extends IService<AttractionsEntity> {
    /**
     * Retrieves a list of nearby attractions based on the given latitude, longitude, and radius.
     *
     * @param latitude  The latitude of the location.
     * @param longitude The longitude of the location.
     * @param radius    The radius within which to search for attractions.
     * @return A list of AttractionDetailVO objects representing the nearby attractions.
     * @throws IOException   if an I/O error occurs.
     * @throws JSONException if an error occurs while parsing JSON data.
     */
    List<AttractionDetailVO> getNearByLocation(double latitude, double longitude, double radius) throws IOException, JSONException;

    /**
     * Retrieves the walking time between two sets of coordinates.
     *
     * @param departLat The latitude of the departure location.
     * @param departLng The longitude of the departure location.
     * @param desLat    The latitude of the destination location.
     * @param desLng    The longitude of the destination location.
     * @return The walking time between the two locations.
     */
    String getWalkTime(double departLat, double departLng, double desLat, double desLng);

    /**
     * Filters attractions based on the given category.
     *
     * @param attractions A list of attraction IDs.
     * @param category    The category to filter by.
     * @return A list of AttractionsEntity objects that match the category.
     */
    List<AttractionsEntity> filterAttractionByCategory(List<Integer> attractions, String category);

    /**
     * Filters attractions based on wheelchair accessibility.
     *
     * @param attractionGoogleId A list of attraction Google IDs.
     * @param wheelchairAllow    The wheelchair accessibility status to filter by.
     * @return A list of attraction Google IDs that match the wheelchair accessibility status.
     * @throws JSONException if an error occurs while parsing JSON data.
     * @throws IOException   if an I/O error occurs.
     */
    List<String> filterAttractionByWheelChairAccessibility(List<String> attractionGoogleId, Integer wheelchairAllow) throws JSONException, IOException;

    /**
     * Retrieves the opening hours for a place specified by its ID.
     *
     * @param placeID The ID of the place.
     * @return A list of opening hours for each day of the week.
     * @throws JSONException if an error occurs while parsing JSON data.
     * @throws IOException   if an I/O error occurs.
     */

    List<String> getOpeningHourMK2(String placeID) throws JSONException, IOException;

    /**
     * Retrieves the current opening status of a place specified by its ID.
     *
     * @param placeID The ID of the place.
     * @return The current opening status of the place (0: closed, 1: open, -1: not specified).
     * @throws JSONException if an error occurs while parsing JSON data.
     * @throws IOException   if an I/O error occurs.
     */
    int getCurrentOpeningStatus(String placeID) throws JSONException, IOException;

    /**
     * Retrieves the Google Place ID for a place based on its name.
     *
     * @param attractionName The name of the attraction.
     * @return The Google Place ID of the attraction.
     * @throws IOException   if an I/O error occurs.
     * @throws JSONException if an error occurs while parsing JSON data.
     */
    String getGooglePlaceIDByName(String attractionName) throws IOException, JSONException;

    /**
     * Retrieves the Google Place ID for a place based on its name and address.
     *
     * @param attractionName    The name of the attraction.
     * @param attractionAddress The address of the attraction.
     * @return The Google Place ID of the attraction.
     * @throws IOException   if an I/O error occurs.
     * @throws JSONException if an error occurs while parsing JSON data.
     */
    String getGooglePlaceIDByCoordinateAndName(String attractionName, String attractionAddress) throws IOException, JSONException;

    /**
     * Retrieves the wheelchair accessibility status of a place specified by its Google ID.
     *
     * @param attractionGoogleID The Google ID of the attraction.
     * @return The wheelchair accessibility status of the attraction (0: not accessible, 1: accessible, -1: not specified).
     * @throws IOException   if an I/O error occurs.
     * @throws JSONException if an error occurs while parsing JSON data.
     */
    int getWheelChair_AccessblityByGoogleID(String attractionGoogleID) throws IOException, JSONException;

    /**
     * /**
     * <p>
     * Retrieves the total number of ratings of a place specified by its Google ID.
     *
     * @param attractionGoogleID The Google ID of the attraction.
     * @return The total number of ratings of the attraction.
     * @throws IOException   if an I/O error occurs.
     * @throws JSONException if an error occurs while parsing JSON data.
     */
    int getTotalNumberOfRatingsByGoogleID(String attractionGoogleID) throws IOException, JSONException;


    /**
     * Retrieves the category of a place specified by its Google ID.
     *
     * @param attractionGoogleID The Google ID of the attraction.
     * @return The category of the attraction.
     * @throws IOException   if an I/O error occurs.
     * @throws JSONException if an error occurs while parsing JSON data.
     */
    String getCategoryByGoogleID(String attractionGoogleID) throws IOException, JSONException;

    /**
     * Retrieves the rating of a place specified by its Google ID.
     *
     * @param attractionGoogleID The Google ID of the attraction.
     * @return The rating of the attraction.
     * @throws IOException   if an I/O error occurs.
     * @throws JSONException if an error occurs while parsing JSON data.
     */
    double getRatingByGoogleID(String attractionGoogleID) throws IOException, JSONException;

    /**
     * Retrieves the phone number of a place specified by its Google ID.
     *
     * @param attractionGoogleID The Google ID of the attraction.
     * @return The phone number of the attraction.
     * @throws IOException   if an I/O error occurs.
     * @throws JSONException if an error occurs while parsing JSON data.
     */
    String getPhoneNumberByGoogleID(String attractionGoogleID) throws IOException, JSONException;

    /**
     * Retrieves the official website of a place specified by its Google ID.
     *
     * @param attractionGoogleID The Google ID of the attraction.
     * @return The official website of the attraction.
     * @throws IOException   if an I/O error occurs.
     * @throws JSONException if an error occurs while parsing JSON data.
     */
    String getOfficalWebsiteByGoogleID(String attractionGoogleID) throws IOException, JSONException;

    /**
     * Retrieves the address of a place specified by its Google ID.
     *
     * @param attractionGoogleID The Google ID of the attraction.
     * @return The address of the attraction.
     * @throws IOException   if an I/O error occurs.
     * @throws JSONException if an error occurs while parsing JSON data.
     */
    String getAddressByGoogleID(String attractionGoogleID) throws IOException, JSONException;

    /**
     * Retrieves the overview of a place specified by its Google ID.
     *
     * @param attractionGoogleID The Google ID of the attraction.
     * @return The overview of the attraction.
     * @throws IOException   if an I/O error occurs.
     * @throws JSONException if an error occurs while parsing JSON data.
     */
    String getOverViewByGoogleID(String attractionGoogleID) throws IOException, JSONException;

    /**
     * Retrieves the name of a place specified by its Google ID.
     *
     * @param attractionGoogleID The Google ID of the attraction.
     * @return The name of the attraction.
     * @throws IOException   if an I/O error occurs.
     * @throws JSONException if an error occurs while parsing JSON data.
     */
    String getNameByGoogleID(String attractionGoogleID) throws IOException, JSONException;

    /**
     * Retrieves the latitude coordinate of a place specified by its Google ID.
     *
     * @param attractionGoogleID The Google ID of the attraction.
     * @return The latitude coordinate of the attraction.
     * @throws IOException   if an I/O error occurs.
     * @throws JSONException if an error occurs while parsing JSON data.
     */
    double getLatCoordByGoogleID(String attractionGoogleID) throws IOException, JSONException;

    /**
     * Retrieves the longitude coordinate of a place specified by its Google ID.
     *
     * @param attractionGoogleID The Google ID of the attraction.
     * @return The longitude coordinate of the attraction.
     * @throws IOException   if an I/O error occurs.
     * @throws JSONException if an error occurs while parsing JSON data.
     */
    double getLngCoordByGoogleID(String attractionGoogleID) throws IOException, JSONException;

    /**
     * Checks if a place with the specified ID exists.
     *
     * @param placeId The ID of the place to check.
     * @return {@code true} if the place ID exists, {@code false} otherwise.
     */
    boolean checkPlaceIdExists(String placeId);

    /**
     * Saves nearby attractions based on the given latitude, longitude, and radius.
     *
     * @param latitude  The latitude of the location.
     * @param longitude The longitude of the location.
     * @param radius    The radius within which to search for attractions.
     * @return A list of AttractionsEntity objects representing the saved nearby attractions.
     * @throws IOException   if an I/O error occurs.
     * @throws JSONException if an error occurs while parsing JSON data.
     */


    List<AttractionsEntity> saveNearByAttraction(double latitude, double longitude, double radius) throws IOException, JSONException;

    /**
     * Updates the rating of an attraction based on its ID.
     *
     * @param attractionId The ID of the attraction to update.
     * @return An UpdateAttractionRatingVO object containing the updated attraction's ID and rating.
     */
    UpdateAttractionRatingVO updateAttractionRating(Integer attractionId);
}

