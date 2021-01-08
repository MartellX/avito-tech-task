package org.martellx.avitotech;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.assertj.core.error.ShouldHaveSizeGreaterThanOrEqualTo;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.matchers.GreaterThan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.MultiValueMap;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@AutoConfigureMockMvc
public class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @Test
    public void whenCreatingShouldReturnValidIdForNewRoom() throws Exception {
        mockMvc.perform(
                post("/rooms")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                                new BasicNameValuePair("description", "Test room"),
                                new BasicNameValuePair("cost", "321.32"))))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", hasKey("room_id")))
                .andExpect(jsonPath("$", aMapWithSize(1)))
                .andExpect(jsonPath("$.room_id", allOf(greaterThanOrEqualTo(0), anyOf(isA(Long.class), isA(Integer.class)))));
    }

    @Test
    public void whenRoomExistShouldDeleteItSuccessfully() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        MvcResult result = mockMvc.perform(
                post("/rooms")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                                new BasicNameValuePair("description", "Test room"),
                                new BasicNameValuePair("cost", "321.32")))))).andReturn();

        Long room_id = mapper.readTree(result.getResponse().getContentAsString()).get("room_id").asLong();
        mockMvc.perform(delete("/rooms")
                .queryParam("room_id", room_id.toString()))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnTwoValidRooms() throws Exception{
        mockMvc.perform(
                post("/rooms")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                                new BasicNameValuePair("description", "Test room 1"),
                                new BasicNameValuePair("cost", "900"))))));

        mockMvc.perform(
                post("/rooms")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                                new BasicNameValuePair("description", "Test room 2"),
                                new BasicNameValuePair("cost", "300"))))));

        mockMvc.perform(get("/rooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$[0]", allOf(
//                        is(aMapWithSize(3)),
                        hasKey("description"),
                        hasKey("cost"),
                        hasKey("room_id"))))
                .andExpect(jsonPath("$[0].description", isA(String.class)))
                .andExpect(jsonPath("$[0].cost", anyOf(isA(Double.class), isA(Float.class), isA(Integer.class))))
                .andExpect(jsonPath("$[0].room_id", anyOf(isA(Long.class), isA(Integer.class))));
    }

    @Test
    public void whenSortingEqualsOneSortByCost() throws Exception {
        mockMvc.perform(
                post("/rooms")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                                new BasicNameValuePair("description", "Test room 1"),
                                new BasicNameValuePair("cost", "900"))))));
        mockMvc.perform(
                post("/rooms")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                                new BasicNameValuePair("description", "Test room 2"),
                                new BasicNameValuePair("cost", "300"))))));

        String jsonContent = mockMvc.perform(get("/rooms?sorting=1"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode roomArray = mapper.readTree(jsonContent);
        assertThat(roomArray.get(0).get("cost").asDouble()).isLessThanOrEqualTo(roomArray.get(1).get("cost").asDouble());

    }

    @Test
    public void whenSortingEqualsTwoSortByDate() throws Exception {
        mockMvc.perform(
                post("/rooms")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                                new BasicNameValuePair("description", "Test room 1"),
                                new BasicNameValuePair("cost", "900"))))));
//        TimeUnit.SECONDS.sleep(1);
        mockMvc.perform(
                post("/rooms")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                                new BasicNameValuePair("description", "Test room 2"),
                                new BasicNameValuePair("cost", "300"))))));

        String jsonContent = mockMvc.perform(get("/rooms?sorting=2"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode roomArray = mapper.readTree(jsonContent);
        assertThat(roomArray.get(0).get("room_id").asLong()).isLessThan(roomArray.get(1).get("room_id").asLong());

    }

    @Test
    public void whenSortingEqualsOneAndIsDescendingSortByCostDescending() throws Exception {
        mockMvc.perform(
                post("/rooms")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                                new BasicNameValuePair("description", "Test room 1"),
                                new BasicNameValuePair("cost", "900"))))));
        mockMvc.perform(
                post("/rooms")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                                new BasicNameValuePair("description", "Test room 2"),
                                new BasicNameValuePair("cost", "300"))))));

        String jsonContent = mockMvc.perform(get("/rooms?sorting=1&isDescending=true"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode roomArray = mapper.readTree(jsonContent);
        assertThat(roomArray.get(0).get("cost").asDouble()).isGreaterThanOrEqualTo(roomArray.get(1).get("cost").asDouble());

    }

    @Test
    public void whenSortingEqualsTwoAndIsDescendingSortByDateDescending() throws Exception {
        mockMvc.perform(
                post("/rooms")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                                new BasicNameValuePair("description", "Test room 1"),
                                new BasicNameValuePair("cost", "900"))))));
//        TimeUnit.SECONDS.sleep(1);
        mockMvc.perform(
                post("/rooms")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                                new BasicNameValuePair("description", "Test room 2"),
                                new BasicNameValuePair("cost", "300"))))));

        String jsonContent = mockMvc.perform(get("/rooms?sorting=2&isDescending=true"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode roomArray = mapper.readTree(jsonContent);
        assertThat(roomArray.get(0).get("room_id").asLong()).isGreaterThan(roomArray.get(1).get("room_id").asLong());

    }

    @Test
    public void whenIsNotEnoughParametersInForm_returnBadRequestWithMessage() throws Exception {
        mockMvc.perform(
                post("/rooms")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", allOf(hasKey("status"), hasKey("message"))));
    }

    @Test
    public void whenIsNoRoomWithThisId_deleteReturnNotFound() throws Exception{
        mockMvc.perform(
                delete("/rooms?room_id=-1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$", allOf(hasKey("status"), hasKey("message"))));
    }





}
