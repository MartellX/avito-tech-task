package org.martellx.avitotech;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;
import org.martellx.avitotech.entities.Booking;
import org.martellx.avitotech.entities.Room;
import org.martellx.avitotech.repositories.BookingRepository;
import org.martellx.avitotech.repositories.RoomRepository;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockReset;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;
//
//    @MockBean(reset = MockReset.NONE)
//    RoomRepository roomRepository;
//
//    @MockBean(reset = MockReset.NONE)
//    BookingRepository bookingRepository;

    @Test
    public void createValidBooking_returnIsCreatedAndValidId() throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        MvcResult result = mockMvc.perform(
                post("/rooms")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                                new BasicNameValuePair("description", "Test room 1"),
                                new BasicNameValuePair("cost", "900")))))).andReturn();

        Long room_id1 = mapper.readTree(result.getResponse().getContentAsString()).get("room_id").asLong();

        mockMvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("room_id", room_id1.toString()),
                        new BasicNameValuePair("date_start", "2021-01-15"),
                        new BasicNameValuePair("date_end", "2021-01-20"))))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", allOf(hasKey("booking_id"), aMapWithSize(1))));
    }

    @Test
    public void whenDelete_returnIsOk() throws Exception{

        ObjectMapper mapper = new ObjectMapper();

        MvcResult result = mockMvc.perform(
                post("/rooms")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                                new BasicNameValuePair("description", "Test room 1"),
                                new BasicNameValuePair("cost", "900")))))).andReturn();

        long roomid = mapper.readTree(result.getResponse().getContentAsString()).get("room_id").asLong();

        result = mockMvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("room_id", String.valueOf(roomid)),
                        new BasicNameValuePair("date_start", "2021-01-15"),
                        new BasicNameValuePair("date_end", "2021-01-20")))))).andReturn();

        Long bookingid = mapper.readTree(result.getResponse().getContentAsString()).get("booking_id").asLong();

        mockMvc.perform(delete("/bookings").queryParam("booking_id", bookingid.toString()))
                .andExpect(status().isOk());
    }

    @Test
    public void whenGetBookingsByRoom_returnValidBookingsInValidOrder() throws Exception{
        ObjectMapper mapper = new ObjectMapper();


        MvcResult result = mockMvc.perform(
                post("/rooms")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                                new BasicNameValuePair("description", "Test room 1"),
                                new BasicNameValuePair("cost", "900")))))).andReturn();

        Long room_id1 = mapper.readTree(result.getResponse().getContentAsString()).get("room_id").asLong();
        result = mockMvc.perform(
                post("/rooms")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                                new BasicNameValuePair("description", "Test room 2"),
                                new BasicNameValuePair("cost", "300")))))).andReturn();
        Long room_id2 = mapper.readTree(result.getResponse().getContentAsString()).get("room_id").asLong();

        mockMvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("room_id", room_id1.toString()),
                        new BasicNameValuePair("date_start", "2021-01-15"),
                        new BasicNameValuePair("date_end", "2021-01-20"))))));

//         long id1 = mapper.readTree(result.getResponse().getContentAsString()).get("booking_id").asLong();

        mockMvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("room_id", room_id1.toString()),
                        new BasicNameValuePair("date_start", "2021-01-12"),
                        new BasicNameValuePair("date_end", "2021-01-20"))))));

//        long id2 = mapper.readTree(result.getResponse().getContentAsString()).get("booking_id").asLong();

        result = mockMvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("room_id", room_id2.toString()),
                        new BasicNameValuePair("date_start", "2021-01-15"),
                        new BasicNameValuePair("date_end", "2021-01-20")))))).andReturn();

        long id3 = mapper.readTree(result.getResponse().getContentAsString()).get("booking_id").asLong();

        result = mockMvc.perform(get("/bookings?room_id=" + room_id1.toString()))
                .andExpect(status().isOk()).andReturn();

        JsonNode bookingArray = mapper.readTree(result.getResponse().getContentAsString());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        df.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));

        Date prevDate = df.parse(bookingArray.get(0).get("date_start").asText());
        for (var elem:bookingArray
             ) {
            assertThat(elem.get("booking_id").asLong()).isNotEqualTo(id3);
            assertThat(df.parse(elem.get("date_start").asText()).getTime()).isGreaterThanOrEqualTo(prevDate.getTime());
        }

    }

}
