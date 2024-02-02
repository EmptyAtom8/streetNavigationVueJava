package com.groupwork.charchar;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.groupwork.charchar.entity.AttractionsEntity;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * @author zhuhaoyu
 * @email 784301058@qq.com
 * @date 2023-04-26 19:24:21
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class AttractionsControllerTest {

    @Autowired
    private MockMvc mockMvc;
    private static ObjectMapper objectMapper;
    private static AttractionsEntity attractionsEntity;
    private static AttractionsEntity attractionsEntity1;
    private static AttractionsEntity attractionsEntity2;

    @BeforeAll
    static void setUp(){
        objectMapper = new ObjectMapper();
        attractionsEntity = new AttractionsEntity();
        attractionsEntity.setAttractionId(1);
        attractionsEntity.setAttractionName("test");
        attractionsEntity.setDescription("test");
        attractionsEntity.setCategory("History");
        attractionsEntity.setAttrRating(4.5D);
        attractionsEntity.setLatitude(new BigDecimal("24.988778"));
        attractionsEntity.setLongitude(new BigDecimal("109.232323"));
        attractionsEntity.setOpeningHours("10");
        attractionsEntity.setTicketPrice(new BigDecimal("100"));
        attractionsEntity.setImageUrl("http://");

        attractionsEntity1 = new AttractionsEntity();
        attractionsEntity1.setAttractionId(3);
        attractionsEntity1.setAttractionName("test");
        attractionsEntity1.setDescription("test");
        attractionsEntity1.setCategory("Nature");
        attractionsEntity1.setAttrRating(3.6D);
        attractionsEntity1.setLatitude(new BigDecimal("24.988778"));
        attractionsEntity1.setLongitude(new BigDecimal("109.232323"));
        attractionsEntity1.setOpeningHours("10");
        attractionsEntity1.setTicketPrice(new BigDecimal("100"));
        attractionsEntity1.setImageUrl("http://");

        attractionsEntity2 = new AttractionsEntity();
        attractionsEntity2.setAttractionId(1);
        attractionsEntity2.setAttractionName("test");
        attractionsEntity2.setDescription("test");
        attractionsEntity2.setCategory("History");
        attractionsEntity2.setAttrRating(4.2D);
        attractionsEntity2.setLatitude(new BigDecimal("24.988778"));
        attractionsEntity2.setLongitude(new BigDecimal("109.232323"));
        attractionsEntity2.setOpeningHours("10");
        attractionsEntity2.setTicketPrice(new BigDecimal("100"));
        attractionsEntity2.setImageUrl("http://");

    }

    /*
    Confirm that the List can be returned
     */
    @Test
    @Order(1)
    public void shouldReturnOKWhenGetNearByLocationList() throws Exception {
        String result  = this.mockMvc.perform(get("http://localhost:9090/charchar/attractions/near/location/{latitude}/{longitude}/{radius}",attractionsEntity.getLatitude(),attractionsEntity.getLongitude(),"10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn().getResponse().getContentAsString();;
        List<AttractionsEntity> attractionsEntityList = new ObjectMapper().readValue(result,
                new TypeReference<List<AttractionsEntity>>(){});
        System.out.println(attractionsEntityList);
    }

    /*
     Confirm that the WalkingTime result can be returned
     */
    @Test
    @Order(2)
    public void shouldReturnWalkingTimeWhenGetWalkTime() throws Exception {
        String result  =  this.mockMvc.perform(get("http://localhost:9090/charchar/attractions/walktime/{departLat}/{departLng}/{desLat}/{desLng}", 24.09998,109.0987,24.087655,108.09873))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn().getResponse().getContentAsString();;
        System.out.println(result);
    }

    /*
         Verify that the update was successful
       */
    @Test
    @Order(3)
    public void shouldReturnOKWhenUpdateAttractions() throws Exception {
        String result  = this.mockMvc.perform(post("http://localhost:9090/charchar/attractions/update/rating/{attractionId}",1219)
                        .contentType("application/json")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn().getResponse().getContentAsString();;
        AttractionsEntity attractionsEntity = new ObjectMapper().readValue(result,AttractionsEntity.class);
        System.out.println(attractionsEntity);

    }

    /*
       Check if your query was successful
    */
    @Test
    @Order(4)
    public void shouldReturnWhenFindAttractionByID() throws Exception {
        String result  =this.mockMvc.perform(get("http://localhost:9090/charchar/attractions/findAttractionByID/{attractionId}", attractionsEntity1.getAttractionId()))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn().getResponse().getContentAsString();;
        System.out.println(result);
    }

    /*
      Check if the search for open door attractions returns results
   */
    @Test
    @Order(5)
    public void shouldReturnWhenGetAttractionByOpeningStatus() throws Exception {
        List<String> attractionsEntities  = new ArrayList<>();
        attractionsEntities.add("ChIJCyVAuT53fkgRnx6FEl-Zs4U");
        attractionsEntities.add("ChIJcxn3CX5xfkgRmVleYKd_xF8");
        attractionsEntities.add("ChIJ5fGymkxxfkgRA8JCemAiq2Y");
        String jsonStr= JSON.toJSONString(attractionsEntities);
        String result  = this.mockMvc.perform(get("http://localhost:9090/charchar/attractions/filterOpenAttractionsMK2")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonStr))
                .andDo(print())
                .andReturn().getResponse().getContentAsString();;
        System.out.println(result);
    }


    /*
      Check the availability of today's opening hours
     */
//    @Test
//    @Order(6)
//    public void shouldReturnTimeWhenGetOperationHoursToday() throws Exception {
//        String result =  this.mockMvc.perform(get("http://localhost:9090/charchar/attractions/getOperationHoursToday/{attractionId}", attractionsEntity.getAttractionId()))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andReturn().getResponse().getContentAsString();
//        List<AttractionsEntity> attractionsEntityList = new ObjectMapper().readValue(result,
//                new TypeReference<List<AttractionsEntity>>(){});
//        System.out.println(attractionsEntityList);
//    }

    /*
     Confirm availability of 7-day opening hours for the week
    */
    @Test
    @Order(7)
    public void shouldReturnTimeWhenGetOpeningHoursThisWeek() throws Exception {
        String result =  this.mockMvc.perform(get("http://localhost:9090/charchar/attractions/openingHoursForTheWeek/{attractionId}", "ChIJCyVAuT53fkgRnx6FEl-Zs4U"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn().getResponse().getContentAsString();
        System.out.println(result);
    }

    /*
    Sorting attractions by rating
    */
    @Test
    @Order(8)
    public void shouldReturnWhenSortattractionsByRating() throws Exception {
        List<String> attractionsEntities  = new ArrayList<>();
        attractionsEntities.add("ChIJCyVAuT53fkgRnx6FEl-Zs4U");
        attractionsEntities.add("ChIJcxn3CX5xfkgRmVleYKd_xF8");
        attractionsEntities.add("ChIJ5fGymkxxfkgRA8JCemAiq2Y");
        String jsonStr= JSON.toJSONString(attractionsEntities);
        String result = this.mockMvc.perform(get("http://localhost:9090/charchar/attractions/sortAttractionByRating/")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonStr))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn().getResponse().getContentAsString();
        System.out.println(result);
    }

    /*
     Filter attractions by category
   */
    @Test
    @Order(9)
    public void shouldReturnWhenGetAttractionByCategory() throws Exception {
        List<Integer> attractionsEntities  = new ArrayList<>();
        attractionsEntities.add(1);
        attractionsEntities.add(2);
        attractionsEntities.add(3);
        String jsonStr= JSON.toJSONString(attractionsEntities);
        String result = this.mockMvc.perform(get("http://localhost:9090/charchar/attractions/filterAttractionByCategory/{category}", attractionsEntity.getCategory()) .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonStr))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn().getResponse().getContentAsString();
        List<AttractionsEntity> attractionsEntityList = new ObjectMapper().readValue(result,
                new TypeReference<List<AttractionsEntity>>(){});
        System.out.println(attractionsEntityList);
    }

    /*
    Filtering attractions according to wheelchair use
    */
    @Test
    @Order(10)
    public void shouldReturnWheGetAttractionByWheelChairAccessibility() throws Exception {
        List<String> attractionsEntities  = new ArrayList<>();
        attractionsEntities.add("ChIJCyVAuT53fkgRnx6FEl-Zs4U");
        attractionsEntities.add("ChIJcxn3CX5xfkgRmVleYKd_xF8");
        attractionsEntities.add("ChIJ5fGymkxxfkgRA8JCemAiq2Y");
        String jsonStr= JSON.toJSONString(attractionsEntities);
        String result = this.mockMvc.perform(get("http://localhost:9090/charchar/attractions/filterAttractionByWheelChairAccessibility/{wc_allowed}",111)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonStr))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn().getResponse().getContentAsString();
        System.out.println(result);
    }
    /*
   Check if the storage is successful and if the return is OK
    */
    @Test
    @Order(11)
    public void shouldReturnOKWhenSaveAttractions() throws Exception {
        String result =  this.mockMvc.perform(post("http://localhost:9090/charchar/attractions/save")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(attractionsEntity1)))
                        .andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
                         .andDo(print())
                        .andReturn().getResponse().getContentAsString();
        System.out.println(result);
    }

    /*
        Verify that the update was successful
    */
//    @Test
//    @Order(12)
//    public void shouldReturnWhenUpdateAttractions() throws Exception {
//        String newAttractText = "newtest";
//        attractionsEntity1.setAttractionName(newAttractText);
//        // Check update function
//        String result =  this.mockMvc.perform(put("http://localhost:9090/charchar/attractions/update")
//                        .contentType(MediaType.valueOf("application/json"))
//                        .content(objectMapper.writeValueAsString(attractionsEntity1)))
//                .andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andReturn().getResponse().getContentAsString();
//        System.out.println(result);
//
//    }


    /*
        Verify that delete Attractions was successful
     */
    @Test
    @Order(13)
    public void shouldReturnOKWhenDeleteAttractions() throws Exception {
        // Check update function
        String jsonStr = "[" + attractionsEntity1.getAttractionId() + "]";
        String result = this.mockMvc.perform(delete("http://localhost:9090/charchar/attractions/delete/{attractionsID}",attractionsEntity1.getAttractionId())
                        .contentType("application/json")
                        .content(jsonStr))
                        .andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andReturn().getResponse().getContentAsString();
        System.out.println(result);

    }


}