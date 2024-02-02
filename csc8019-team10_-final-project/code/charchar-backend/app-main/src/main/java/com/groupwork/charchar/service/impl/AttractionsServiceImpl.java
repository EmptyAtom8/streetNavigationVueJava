package com.groupwork.charchar.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.*;
import com.groupwork.charchar.common.Constants;
import com.groupwork.charchar.dao.AttractionsDao;
import com.groupwork.charchar.entity.AttractionsEntity;
import com.groupwork.charchar.entity.ReviewsEntity;
import com.groupwork.charchar.exception.AttractionNotFoundException;
import com.groupwork.charchar.service.AttractionsService;
import com.groupwork.charchar.service.ReviewsService;
import com.groupwork.charchar.vo.AttractionDetailVO;
import com.groupwork.charchar.vo.UpdateAttractionRatingVO;
import lombok.SneakyThrows;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service("attractionsService")
public class AttractionsServiceImpl extends ServiceImpl<AttractionsDao, AttractionsEntity> implements AttractionsService {
    private final Logger logger = LoggerFactory.getLogger(AttractionsServiceImpl.class);
    @Value("${google.maps.api.key}")
    private String key;
    @Resource
    private AttractionsDao attractionsDao;
    @Resource
    private ReviewsService reviewsService;
    private static String cacheKey;
    @Autowired
    private RedisTemplate redisTemplate;
    private static final OkHttpClient CLIENT = new OkHttpClient().newBuilder().build();

    /**
     * Retrieve a list of nearby attractions based on the provided latitude, longitude, and radius.
     *
     * @param latitude  The latitude coordinate.
     * @param longitude The longitude coordinate.
     * @param radius    The search radius in meters.
     * @return A list of AttractionDetailVO objects representing the nearby attractions.
     * @throws IOException   If there is an error in the IO operation.
     * @throws JSONException If there is an error in JSON parsing.
     */
    @Override
    public List<AttractionDetailVO> getNearByLocation(double latitude, double longitude, double radius) throws IOException, JSONException {
        cacheKey = String.format("%.15f_%.15f_%d", latitude, longitude, (int) radius);
        List<AttractionDetailVO> showList = (List<AttractionDetailVO>) redisTemplate.opsForValue().get(cacheKey);
        if (showList == null) {
            final Set<String> placeIdSet = new HashSet<>();
            showList = new ArrayList<>();
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            String url = String.format("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=%f,%f&radius=%f&type=tourist_attraction&key=%s", latitude, longitude, radius, key);
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = client.newCall(request).execute();
            JsonObject json = null;
            if (response.body() != null) {
                json = JsonParser.parseString(response.body().string()).getAsJsonObject();
            }

            JsonArray datas = json.getAsJsonArray("results");
            for (JsonElement data : datas) {
                JsonObject curPlace = data.getAsJsonObject();
                String placeId = curPlace.get("place_id").getAsString();
                if (placeIdSet.contains(placeId)) continue;
                else placeIdSet.add(placeId);
                List<String> openingHourMK2 = this.getOpeningHourMK2(placeId);
                LocalDate today = LocalDate.now();
                DayOfWeek dayOfWeek = today.getDayOfWeek();
                String openingHours;
                if (null != openingHourMK2 && openingHourMK2.size() == 1) {
                    openingHours = openingHourMK2.get(0);
                } else {
                    openingHours = openingHourMK2.get(dayOfWeek.getValue() - 1);
                }
                // 在这里使用 openingHours
                AttractionsEntity attractionsEntity = attractionsDao.getAttractionByPlaceId(placeId);
                AttractionDetailVO attractionDetailVO = new AttractionDetailVO();
                if (null != attractionsEntity) {
                    attractionDetailVO.setAttractionId(attractionsEntity.getAttractionId());
                    attractionDetailVO.setAttractionName(attractionsEntity.getAttractionName());
                    attractionDetailVO.setDescription(attractionsEntity.getDescription());
                    attractionDetailVO.setCategory(attractionsEntity.getCategory());
                    attractionDetailVO.setLatitude(attractionsEntity.getLatitude());
                    attractionDetailVO.setLongitude(attractionsEntity.getLongitude());
                    attractionDetailVO.setTicketPrice(attractionsEntity.getTicketPrice());
                    attractionDetailVO.setImageUrl(attractionsEntity.getImageUrl());
                    attractionDetailVO.setAttrRating(attractionsEntity.getAttrRating());
                    attractionDetailVO.setWheelchairAllow(attractionsEntity.getWheelchairAllow());
                    attractionDetailVO.setPramAllow(attractionsEntity.getPramAllow());
                    attractionDetailVO.setHearingAllow(attractionsEntity.getHearingAllow());
                    attractionDetailVO.setAddress(attractionsEntity.getAddress());
                    attractionDetailVO.setPlaceId(attractionsEntity.getPlaceId());
                    attractionDetailVO.setOpeningHours(openingHours);
                    String walkingTime = this.getWalkTime(latitude, longitude, attractionsEntity.getLatitude().doubleValue(), attractionsEntity.getLongitude().doubleValue());
                    attractionDetailVO.setWalkingTime(walkingTime);
                    int currentOpeningStatus = this.getCurrentOpeningStatus(attractionsEntity.getPlaceId());
                    attractionDetailVO.setOpeningStatus(currentOpeningStatus);
                    showList.add(attractionDetailVO);
                }
            }
            redisTemplate.opsForValue().set(cacheKey, showList, 1, TimeUnit.MINUTES);
        }
        return showList;
    }

    /**
     * Save nearby attractions based on the provided latitude, longitude, and radius.
     *
     * @param latitude  The latitude coordinate.
     * @param longitude The longitude coordinate.
     * @param radius    The search radius in meters.
     * @return A list of AttractionsEntity objects representing the saved nearby attractions.
     * @throws IOException   If there is an error in the IO operation.
     * @throws JSONException If there is an error in JSON parsing.
     */
    @Override
    public List<AttractionsEntity> saveNearByAttraction(double latitude, double longitude, double radius) throws IOException, JSONException {
        List<AttractionsEntity> showList = new ArrayList<>();
        String url = String.format("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=%f,%f&radius=%f&type=tourist_attraction&key=%s", latitude, longitude, radius, key);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = CLIENT.newCall(request).execute();
        JsonObject json = JsonParser.parseString(response.body().string()).getAsJsonObject();

        JsonArray datas = json.getAsJsonArray("results");
        for (JsonElement data : datas) {
            JsonObject curPlace = data.getAsJsonObject();
            String name = curPlace.get("name").getAsString();
            String address = curPlace.get("vicinity").getAsString();
            double rating = curPlace.has("rating") ? curPlace.get("rating").getAsDouble() : 0.0;
            String placeId = curPlace.get("place_id").getAsString();
            String overview = getOverViewByGoogleID(placeId);
            double lat = curPlace.getAsJsonObject("geometry").getAsJsonObject("location").get("lat").getAsDouble();
            double lng = curPlace.getAsJsonObject("geometry").getAsJsonObject("location").get("lng").getAsDouble();
            List<String> photosList = new ArrayList<>();
            String photo;
            if (curPlace != null) {
                JsonArray photos = curPlace.getAsJsonArray("photos");
                if (photos != null && !photos.isJsonNull() && photos.size() > 0) {
                    int count = 0;
                    int max = 3;
                    for (JsonElement element : photos) {
                        if (count > max) break;
                        JsonObject photoObj = element.getAsJsonObject();
                        if (photoObj != null && !photoObj.isJsonNull() && photoObj.has("photo_reference")) {
                            JsonElement photoReference = photoObj.get("photo_reference");
                            if (photoReference != null && !photoReference.isJsonNull()) {
                                String photoRef = photoReference.getAsString();
                                int maxWidth = 400;
                                String apiKey = key;
                                photo = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=" + maxWidth + "&photoreference=" + photoRef + "&key=" + apiKey;
                                photosList.add(photo);
                                count++;
                            }
                        }
                    }
                }
            }
            Gson gson = new Gson();
            String photosJson = gson.toJson(photosList);
            int WC_Accessibilty = getWheelChair_AccessblityByGoogleID(placeId);
            AttractionsEntity attractions = new AttractionsEntity();
            attractions.setAttractionName(name);
            attractions.setDescription(overview);
            attractions.setLatitude(BigDecimal.valueOf(lat));
            attractions.setLongitude(BigDecimal.valueOf(lng));
            attractions.setAttrRating(rating);
            attractions.setWheelchairAllow(WC_Accessibilty);
            attractions.setPlaceId(placeId);
            attractions.setAddress(address);
            attractions.setImageUrl(photosJson);
            showList.add(attractions);
        }
        return showList;
    }

    /**
     * Check if the given Place ID already exists in the database.
     * If it exists, return true; otherwise, return false.
     *
     * @param placeId The Place ID to check.
     * @return True if the Place ID exists; false otherwise.
     */

    public boolean checkPlaceIdExists(String placeId) {
        AttractionsEntity attraction = attractionsDao.findByPlaceId(placeId);
        return attraction != null;
    }

    /**
     * Retrieve the walking time in minutes between two sets of coordinates.
     *
     * @param departLat The latitude of the departure location.
     * @param departLng The longitude of the departure location.
     * @param desLat    The latitude of the destination location.
     * @param desLng    The longitude of the destination location.
     * @return The walking time in minutes as a string.
     */
    @SneakyThrows
    @Override
    public String getWalkTime(double departLat, double departLng, double desLat, double desLng) {

        String url = String.format("https://maps.googleapis.com/maps/api/directions/json?origin=%.15f,%.15f&destination=%.15f,%.15f&mode=walking&key=%s", departLat, departLng, desLat, desLng, key);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = CLIENT.newCall(request).execute();
        JsonObject json = JsonParser.parseString(response.body().string()).getAsJsonObject();
        JsonArray routes = json.getAsJsonArray("routes");
        if (routes != null && routes.size() > 0) {
            JsonObject firstRoute = routes.get(0).getAsJsonObject();
            JsonArray legs = firstRoute.getAsJsonArray("legs");
            if (legs != null && legs.size() > 0) {
                JsonObject walk = legs.get(0).getAsJsonObject();
                String time = walk.getAsJsonObject("duration").get("text").getAsString();

                Pattern pattern = Pattern.compile("(\\d+)\\s+(\\w+)");
                Matcher matcher = pattern.matcher(time);

                int totalMinutes = 0;
                while (matcher.find()) {
                    int value = Integer.parseInt(matcher.group(1));
                    String unit = matcher.group(2);

                    if ("days".equalsIgnoreCase(unit)) {
                        totalMinutes += value * 24 * 60;
                    } else if ("hours".equalsIgnoreCase(unit)) {
                        totalMinutes += value * 60;
                    } else if ("mins".equalsIgnoreCase(unit) || "min".equalsIgnoreCase(unit)) {
                        totalMinutes += value;
                    }
                }
                return String.valueOf(totalMinutes);
            }
        }
        return "can't access";
    }

    /**
     * Filter attractions by category based on a list of attraction IDs.
     *
     * @param attractionIDs The list of attraction IDs to filter.
     * @param category      The category to filter by.
     * @return A list of AttractionsEntity objects representing the filtered attractions.
     */
    @Override
    public List<AttractionsEntity> filterAttractionByCategory(List<Integer> attractionIDs, String category) {

        List<AttractionsEntity> tempAttractionEntities = new ArrayList<>();
        List<AttractionsEntity> filteredAttractions = new ArrayList<>();
        for (int a : attractionIDs) {
            tempAttractionEntities.add(attractionsDao.getAttractionById(a));
        }
        for (AttractionsEntity a : tempAttractionEntities) {
            if (a!=null){
                if (a.getCategory().equals(category)) {
                    filteredAttractions.add(a);
                }
            }
            }


        return filteredAttractions;
    }

    /**
     * Filter attractions by wheelchair accessibility based on a list of attraction Google IDs and the wheelchair accessibility requirement.
     *
     * @param attractionGoogleId The list of attraction Google IDs to filter.
     * @param wc_allowed         The wheelchair accessibility requirement (0 for not allowed, 1 for allowed).
     * @return A list of attraction Google IDs representing the filtered attractions.
     */
    @Override
    public List<String> filterAttractionByWheelChairAccessibility(List<String> attractionGoogleId, Integer wc_allowed) throws JSONException, IOException {
        List<String> filteredAttractions = new ArrayList<>();
        for (String s : attractionGoogleId) {
            if (getWheelChair_AccessblityByGoogleID(s) == wc_allowed) {
                filteredAttractions.add(s);
            }
        }
        return filteredAttractions;
    }

    /**
     * Retrieve the opening hours of a place based on its Place ID.
     *
     * @param placeID The Place ID of the attraction.
     * @return A list of strings representing the opening hours for each day of the week.
     * @throws JSONException If there is an error in JSON parsing.
     * @throws IOException   If there is an error in the IO operation.
     */
    @Override
    public List<String> getOpeningHourMK2(String placeID) throws JSONException, IOException {
        Request request = new Request.Builder()
                .url("https://maps.googleapis.com/maps/api/place/details/json?placeid=" + placeID + "&fields=opening_hours&key=" + key)
                .build();
        List<String> hoursList = new ArrayList<>();
        try (Response response = CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            JSONObject json = new JSONObject(response.body().string());
            JSONObject result = json.getJSONObject("result");
            if (result.has("opening_hours")) {
                JSONObject openingHours = result.getJSONObject("opening_hours");
                JSONArray periods = openingHours.getJSONArray("periods");

                String[] daysOfWeek = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
                for (int i = 0; i < daysOfWeek.length; i++) {
                    String dayOfWeek = daysOfWeek[i];
                    boolean isOpen = false;
                    for (int j = 0; j < periods.length(); j++) {
                        JSONObject period = periods.getJSONObject(j);
                        JSONObject open = period.getJSONObject("open");
                        int openDay = open.getInt("day");
                        if (openDay == i) {
                            isOpen = true;
                            String openTime = open.getString("time");
                            String openingHour = openTime.substring(0, 2);
                            String openingMinut = openTime.substring(2);
                            String resultOpeningTime = openingHour + ":" + openingMinut;
                            if (period.has("close")) {
                                JSONObject close = period.getJSONObject("close");
                                String closeTime = close.getString("time");
                                String closingHour = closeTime.substring(0, 2);
                                String closingMinute = closeTime.substring(2);
                                String resultClosingTime = closingHour + ":" + closingMinute;
                                hoursList.add(resultOpeningTime + " - " + resultClosingTime);
                            } else {
                                hoursList.add("Open 24 hours");
                            }
                        }
                    }
                    if (!isOpen) {
                        hoursList.add("Closed");
                    }
                }
            } else {
                hoursList.add("Open 24 Hours");
                hoursList.add("Open 24 Hours");
                hoursList.add("Open 24 Hours");
                hoursList.add("Open 24 Hours");
                hoursList.add("Open 24 Hours");
                hoursList.add("Open 24 Hours");
                hoursList.add("Open 24 Hours");
            }
        }
        return hoursList;
    }

    /**
     * Get the current opening status of a place.
     *
     * @param placeID The Google Place ID of the place.
     * @return The opening status code: 1 for open, 0 for closed, -1 for unknown, and 4 for other cases.
     * @throws JSONException If there is an error in JSON parsing.
     * @throws IOException   If there is an error in the IO operation.
     */
    @Override
    public int getCurrentOpeningStatus(String placeID) throws JSONException, IOException {
        int openStatus = 4;


        Request request = new Request.Builder()
                .url("https://maps.googleapis.com/maps/api/place/details/json?placeid=" + placeID + "&fields=opening_hours&key=" + key)
                .build();
        try (Response response = CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            JSONObject json = new JSONObject(response.body().string());
            JSONObject result = json.getJSONObject("result");
            if (result.has("opening_hours")) {
                JSONObject openingHours = result.getJSONObject("opening_hours");
                boolean openNow = openingHours.getBoolean("open_now");
                if (openNow == true) {
                    openStatus = 1;
                } else if (openNow == false) {
                    openStatus = 0;
                }
            } else {

                openStatus = -1;
            }
        }
        return openStatus;
    }

    /**
     * Get the Google Place ID for a place based on its name and address.
     *
     * @param attractionName    The name of the attraction.
     * @param attractionAddress The address of the attraction.
     * @return The Google Place ID of the place, or null if no place is found with the given name and address.
     * @throws IOException   If there is an error in the IO operation.
     * @throws JSONException If there is an error in JSON parsing.
     */
    @Override
    public String getGooglePlaceIDByCoordinateAndName(String attractionName, String attractionAddress) throws IOException, JSONException {
        String input = attractionName + " " + attractionAddress;
        Request request = new Request.Builder()
                .url("https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input=" + input + "&inputtype=textquery&fields=place_id&key=" + key)
                .build();
        String tempPlaceId = null;
        try (Response response = CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            JSONObject json = new JSONObject(response.body().string());
            JSONArray candidates = json.getJSONArray("candidates");

            if (candidates.length() > 0) {
                JSONObject candidate = candidates.getJSONObject(0);
                tempPlaceId = candidate.getString("place_id");
            } else {
                logger.error("No place found with that name and address.");
            }
        }
        return tempPlaceId;
    }

    /**
     * Get the wheelchair accessibility status of a place based on its Google ID.
     *
     * @param attractionGoogleID The Google ID of the attraction.
     * @return The wheelchair accessibility status of the place:
     * - 1 if wheelchair accessible
     * - 0 if not wheelchair accessible
     * - -1 if the accessibility status is not available
     * @throws IOException   If there is an error in the IO operation.
     * @throws JSONException If there is an error in JSON parsing.
     */

    @Override
    public int getWheelChair_AccessblityByGoogleID(String attractionGoogleID) throws IOException, JSONException {
        Request request = new Request.Builder()
                .url("https://maps.googleapis.com/maps/api/place/details/json?placeid=" + attractionGoogleID + "&fields=wheelchair_accessible_entrance&key=" + key)
                .build();
        boolean wheelchairAccessible = false;
        int accessibility = -1;
        try (Response response = CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            JSONObject json = new JSONObject(response.body().string());
            JSONObject result = json.getJSONObject("result");
            if (result.has("wheelchair_accessible_entrance")) {
                wheelchairAccessible = result.getBoolean("wheelchair_accessible_entrance");
                if (wheelchairAccessible == true) {
                    accessibility = 1;
                } else if (wheelchairAccessible == false) {
                    accessibility = 0;
                }
            } else {
                accessibility = -1;
            }
        }
        return accessibility;
    }

    /**
     * Get the category of a place based on its Google ID.
     *
     * @param attractionGoogleID The Google ID of the attraction.
     * @return The category of the place, or "Category not Specified" if the category is not available.
     * @throws IOException   If there is an error in the IO operation.
     * @throws JSONException If there is an error in JSON parsing.
     */
    @Override
    public String getCategoryByGoogleID(String attractionGoogleID) throws IOException, JSONException {
        Request request = new Request.Builder()
                .url("https://maps.googleapis.com/maps/api/place/details/json?placeid=" + attractionGoogleID + "&fields=types&key=" + key)
                .build();
        String category = null;
        try (Response response = CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            JSONObject json = new JSONObject(response.body().string());
            JSONObject result = json.getJSONObject("result");
            if (result.has("types")) {
                category = result.getString("types");

            } else {
                category = "Category not Specified";
            }
        }
        return category;
    }

    /**
     * Get the current opening status of a place based on its Place ID.
     *
     * @param attractionGoogleID The Place ID of the attraction.
     * @return An integer representing the opening status:
     * 0：closed  1：open
     * @throws JSONException If there is an error in JSON parsing.
     * @throws IOException   If there is an error in the IO operation.
     */
    @Override
    public double getRatingByGoogleID(String attractionGoogleID) throws IOException, JSONException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://maps.googleapis.com/maps/api/place/details/json?placeid=" + attractionGoogleID + "&fields=rating&key=" + key)
                .build();
        double rating = 0;
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            JSONObject json = new JSONObject(response.body().string());
            JSONObject result = json.getJSONObject("result");
            if (result.has("rating")) {
                rating = result.getDouble("rating");

            } else {
                rating = -1;
            }
        }
        return rating;
    }

    /**
     * Get the phone number of a place based on its Google ID.
     *
     * @param attractionGoogleID The Google ID of the attraction.
     * @return The phone number of the place, or "Phone Number not Specified" if the phone number is not available.
     * @throws IOException   If there is an error in the IO operation.
     * @throws JSONException If there is an error in JSON parsing.
     */
    @Override
    public String getPhoneNumberByGoogleID(String attractionGoogleID) throws IOException, JSONException {
        Request request = new Request.Builder()
                .url("https://maps.googleapis.com/maps/api/place/details/json?placeid=" + attractionGoogleID + "&fields=formatted_phone_number&key=" + key)
                .build();
        String phone = null;
        try (Response response = CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            JSONObject json = new JSONObject(response.body().string());
            JSONObject result = json.getJSONObject("result");
            if (result.has("formatted_phone_number")) {
                phone = result.getString("formatted_phone_number");
            } else {
                phone = "Phone Number not Specified";
            }
        }
        return phone;


    }

    /**
     * Get the address of a place based on its Google ID.
     *
     * @param attractionGoogleID The Google ID of the attraction.
     * @return The address of the place, or "Address not Specified" if the address is not available.
     * @throws IOException   If there is an error in the IO operation.
     * @throws JSONException If there is an error in JSON parsing.
     */
    @Override
    public String getAddressByGoogleID(String attractionGoogleID) throws IOException, JSONException {

        Request request = new Request.Builder()
                .url("https://maps.googleapis.com/maps/api/place/details/json?placeid=" + attractionGoogleID + "&fields=formatted_address&key=" + key)
                .build();
        String address = null;
        try (Response response = CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            JSONObject json = new JSONObject(response.body().string());
            JSONObject result = json.getJSONObject("result");
            if (result.has("formatted_address")) {
                address = result.getString("formatted_address");
            } else {
                address = "Address not Specified";
            }
        }
        return address;

    }

    /**
     * Get the address of a place based on its Google ID.
     *
     * @param attractionGoogleID The Google ID of the attraction.
     * @return The address of the place, or "Address not Specified" if the address is not available.
     * @throws IOException   If there is an error in the IO operation.
     * @throws JSONException If there is an error in JSON parsing.
     */
    @Override
    public String getOverViewByGoogleID(String attractionGoogleID) throws IOException, JSONException {
        Request request = new Request.Builder()
                .url("https://maps.googleapis.com/maps/api/place/details/json?placeid=" + attractionGoogleID + "&fields=editorial_summary&key=" + key)
                .build();
        String overview = null;
        try (Response response = CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            JSONObject json = new JSONObject(response.body().string());
            JSONObject result = json.getJSONObject("result");
            if (result.has("editorial_summary")) {
                overview = result.getString("editorial_summary");
            } else {
                overview = "overview not Specified";
            }
        }
        return overview;

    }

    /**
     * Get the name of a place based on its Google ID.
     *
     * @param attractionGoogleID The Google ID of the attraction.
     * @return The name of the place, or "Name not Specified" if the name is not available.
     * @throws IOException   If there is an error in the IO operation.
     * @throws JSONException If there is an error in JSON parsing.
     */
    @Override
    public String getNameByGoogleID(String attractionGoogleID) throws IOException, JSONException {
        Request request = new Request.Builder()
                .url("https://maps.googleapis.com/maps/api/place/details/json?placeid=" + attractionGoogleID + "&fields=name&key=" + key)
                .build();
        String name = null;
        try (Response response = CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            JSONObject json = new JSONObject(response.body().string());
            JSONObject result = json.getJSONObject("result");
            if (result.has("name")) {
                name = result.getString("name");
            } else {
                name = "overview not Specified";
            }
        }
        return name;

    }

    /**
     * Get the latitude coordinate of a place based on its Google ID.
     *
     * @param attractionGoogleID The Google ID of the attraction.
     * @return The latitude coordinate of the place.
     * @throws IOException   If there is an error in the IO operation.
     * @throws JSONException If there is an error in JSON parsing.
     */
    @Override
    public double getLatCoordByGoogleID(String attractionGoogleID) throws IOException, JSONException {
        String urlString = "https://maps.googleapis.com/maps/api/place/details/json?place_id=" + attractionGoogleID + "&fields=geometry&key=" + key;

        // Create a request object
        Request request = new Request.Builder()
                .url(urlString)
                .build();
        double lat = 0;
        try {
            // Execute the request and get the response
            Response response = CLIENT.newCall(request).execute();

            // Parse the response as a JSON object
            JSONObject jsonResponse = new JSONObject(response.body().string());

            // Extract the latitude and longitude coordinates from the response
            lat = jsonResponse.getJSONObject("result").getJSONObject("geometry").getJSONObject("location").getDouble("lat");

        } catch (IOException e) {
            e.printStackTrace();
        }
        return lat;
    }

    /**
     * Get the longitude coordinate of a place based on its Google ID.
     *
     * @param attractionGoogleID The Google ID of the attraction.
     * @return The longitude coordinate of the place.
     * @throws IOException   If there is an error in the IO operation.
     * @throws JSONException If there is an error in JSON parsing.
     */

    @Override
    public double getLngCoordByGoogleID(String attractionGoogleID) throws IOException, JSONException {
        String urlString = "https://maps.googleapis.com/maps/api/place/details/json?place_id=" + attractionGoogleID + "&fields=geometry&key=" + key;

        // Create an OkHttpClient instance

        // Create a request object
        Request request = new Request.Builder()
                .url(urlString)
                .build();
        double lng = 0;
        try {
            // Execute the request and get the response
            Response response = CLIENT.newCall(request).execute();

            // Parse the response as a JSON object
            JSONObject jsonResponse = new JSONObject(response.body().string());

            // Extract the latitude and longitude coordinates from the response
            lng = jsonResponse.getJSONObject("result").getJSONObject("geometry").getJSONObject("location").getDouble("lng");
            logger.info("Latitude: " + lng);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return lng;


    }

    /**
     * Get the official website of a place based on its Google ID.
     *
     * @param attractionGoogleID The Google ID of the attraction.
     * @return The official website of the place, or "Website not Specified" if the website is not available.
     * @throws IOException   If there is an error in the IO operation.
     * @throws JSONException If there is an error in JSON parsing.
     */
    @Override
    public String getOfficalWebsiteByGoogleID(String attractionGoogleID) throws IOException, JSONException {

        Request request = new Request.Builder()
                .url("https://maps.googleapis.com/maps/api/place/details/json?placeid=" + attractionGoogleID + "&fields=website&key=" + key)
                .build();
        String website = null;
        try (Response response = CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            JSONObject json = new JSONObject(response.body().string());
            JSONObject result = json.getJSONObject("result");
            if (result.has("website")) {
                website = result.getString("website");

            } else {
                website = "Phone not Specified";
            }
        }
        return website;

    }

    /**
     * Get the total number of ratings of a place based on its Google ID.
     *
     * @param attractionGoogleI The Google ID of the attraction.
     * @return The total number of ratings of the place, or -1 if the information is not available.
     * @throws IOException   If there is an error in the IO operation.
     * @throws JSONException If there is an error in JSON parsing.
     */
    @Override
    public int getTotalNumberOfRatingsByGoogleID(String attractionGoogleI) throws IOException, JSONException {
        Request request = new Request.Builder()
                .url("https://maps.googleapis.com/maps/api/place/details/json?placeid=" + attractionGoogleI + "&fields=user_ratings_total&key=" + key)
                .build();
        int NOR = 0;
        try (Response response = CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            JSONObject json = new JSONObject(response.body().string());
            JSONObject result = json.getJSONObject("result");
            if (result.has("user_ratings_total")) {
                NOR = result.getInt("user_ratings_total");

            } else {
                NOR = -1;
            }
        }
        return NOR;

    }

    /**
     * Get the Google Place ID based on the attraction name.
     *
     * @param attractionName The name of the attraction.
     * @return The Google Place ID of the attraction, or null if no place is found with the given name.
     * @throws IOException   If there is an error in the IO operation.
     * @throws JSONException If there is an error in JSON parsing.
     */
    @Override
    public String getGooglePlaceIDByName(String attractionName) throws IOException, JSONException {

        Request request = new Request.Builder()
                .url("https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input=" + attractionName + "&inputtype=textquery&fields=place_id&key=" + key)
                .build();
        String tempPlaceId = null;
        try (Response response = CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            JSONObject json = new JSONObject(response.body().string());
            JSONArray candidates = json.getJSONArray("candidates");

            if (candidates.length() > 0) {
                JSONObject candidate = candidates.getJSONObject(0);
                tempPlaceId = candidate.getString("place_id");

                logger.info("Place ID: " + tempPlaceId);
            } else {
                logger.error("No place found with that name.");
            }
        }
        return tempPlaceId;
    }

    /**
     * Update the attraction rating based on the reviews.
     *
     * @param attractionId The ID of the attraction.
     * @return The updated attraction rating.
     * @throws AttractionNotFoundException If the attraction is not found.
     */
    @Override
    public UpdateAttractionRatingVO updateAttractionRating(Integer attractionId) {
        AttractionsEntity attraction = attractionsDao.getAttractionById(attractionId);
        if (attraction == null) {
            throw new AttractionNotFoundException(Constants.ResponseCode.UN_ERROR.getCode(), Constants.ResponseCode.UN_ERROR.getInfo() + ": " + attractionId);
        }
        Double attrSumRating = 0d;
        Integer reviewCount = 0;
        List<ReviewsEntity> reviews = reviewsService.listReviewsByAttractionId(attractionId);
        // if there is no one review the attraction, if will return a empty attraction entity.
        if (null == reviews || reviews.size() == 0) {
            return new UpdateAttractionRatingVO();
        }
        for (ReviewsEntity review : reviews) {
            attrSumRating += review.getRating();
            reviewCount++;
        }
        Double attrRating = attrSumRating / reviewCount;
        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        attrRating = Double.parseDouble(decimalFormat.format(attrRating));
        try {
            int updateStatus = attractionsDao.updateAttractionRating(attractionId, attrRating);
            if (updateStatus == 0) {
                throw new RuntimeException("update fail , attraction : " + attractionId);
            }
        } catch (Exception e) {
            logger.error("Failed to update attraction rating, attraction ID: " + attractionId, e);
            throw new RuntimeException("can't update, attraction : " + attractionId);
        }
        UpdateAttractionRatingVO updateAttraction = new UpdateAttractionRatingVO();
        updateAttraction.setAttractionId(attractionId);
        updateAttraction.setAttrRating(attrRating);
        return updateAttraction;
    }


}