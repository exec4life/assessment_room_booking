package com.user.arb.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.user.arb.controller.BookingController;
import com.user.arb.core.AbstractTest;
import com.user.arb.service.BookingService;
import com.user.arb.service.dto.BookingDTO;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BookingControllerTest extends AbstractTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private BookingService bookingService;

    private static boolean IS_INSERT = Boolean.FALSE;

    private List<BookingDTO> bookings = new ArrayList<>();

    @Before
    public void init() {
        if (!IS_INSERT) {
            TypeReference<List<BookingDTO>> bookingListType = new TypeReference<List<BookingDTO>>() {
            };
            bookings = this.fromJsonToList("json/booking.json", bookingListType, bookingService);
            IS_INSERT = !IS_INSERT;
        } else {
            bookings = bookingService.find();
        }
    }

    @Test
    public void find_whenBookingExisted_thenStatus200()
            throws Exception {

        mvc.perform(get("/api/booking/" + bookings.get(0).getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.Subject").value(bookings.get(0).getSubject()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void find_whenBookingDoesNotExisted_thenStatus400()
            throws Exception {

        mvc.perform(get("/api/booking/200")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void list_whenBookingsExisted_thenStatus200()
            throws Exception {

        mvc.perform(get("/api/booking/list/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void list_whenBookingsNotExisted_thenResultEmpty()
            throws Exception {

        mvc.perform(get("/api/booking/list/100")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void create_hasNoConflict_thenStatus200()
            throws Exception {

        String requestJson = "{\n" +
                "    \"Id\": 4,\n" +
                "    \"Subject\": \"Weekly Planet\",\n" +
                "    \"StartTime\": \"2020-05-05 08:00:00\",\n" +
                "    \"EndTime\": \"2020-05-05 08:30:00\",\n" +
                "    \"IsAllDay\": false,\n" +
                "    \"IsBlock\": false,\n" +
                "    \"IsReadonly\": false,\n" +
                "    \"RecurrenceRule\": \"FREQ=WEEKLY;BYDAY=MO,WE,FR;INTERVAL=1;COUNT=15\",\n" +
                "    \"RoomId\": 1,\n" +
                "    \"UserId\": 1\n" +
                "  }";

        mvc.perform(MockMvcRequestBuilders.post("/api/booking")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.Subject").value("Weekly Planet"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void create_hasConflict_thenStatus400()
            throws Exception {

        String requestJson = "{\n" +
                "    \"Id\": 4,\n" +
                "    \"Subject\": \"Weekly Planet\",\n" +
                "    \"StartTime\": \"2020-05-05 06:30:00\",\n" +
                "    \"EndTime\": \"2020-05-05 09:30:00\",\n" +
                "    \"IsAllDay\": false,\n" +
                "    \"IsBlock\": false,\n" +
                "    \"IsReadonly\": false,\n" +
                "    \"RecurrenceRule\": \"FREQ=WEEKLY;BYDAY=MO,WE,FR;INTERVAL=1;COUNT=15\",\n" +
                "    \"RoomId\": 1,\n" +
                "    \"UserId\": 1\n" +
                "  }";

        mvc.perform(MockMvcRequestBuilders.post("/api/booking")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void update_hasNoConflict_thenStatus200()
            throws Exception {

        String requestJson = "{\n" +
                "    \"Id\": 4,\n" +
                "    \"Subject\": \"Weekly Planet\",\n" +
                "    \"StartTime\": \"2020-05-05 08:30:00\",\n" +
                "    \"EndTime\": \"2020-05-05 09:30:00\",\n" +
                "    \"IsAllDay\": false,\n" +
                "    \"IsBlock\": false,\n" +
                "    \"IsReadonly\": false,\n" +
                "    \"RecurrenceRule\": \"FREQ=WEEKLY;BYDAY=MO,WE,FR;INTERVAL=1;COUNT=15\",\n" +
                "    \"RoomId\": 1,\n" +
                "    \"UserId\": 1\n" +
                "  }";

        mvc.perform(MockMvcRequestBuilders.put("/api/booking/4")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.StartTime").value("2020-05-05 08:30:00"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void update_hasConflict_thenStatus400()
            throws Exception {

        String requestJson = "{\n" +
                "    \"Id\": 4,\n" +
                "    \"Subject\": \"Weekly Planet update\",\n" +
                "    \"StartTime\": \"2020-05-02 01:30:00\",\n" +
                "    \"EndTime\": \"2020-05-02 02:30:00\",\n" +
                "    \"IsAllDay\": false,\n" +
                "    \"IsBlock\": false,\n" +
                "    \"IsReadonly\": false,\n" +
                "    \"RecurrenceRule\": \"FREQ=DAILY;INTERVAL=2;COUNT=10\",\n" +
                "    \"RoomId\": 1,\n" +
                "    \"UserId\": 1\n" +
                "  }";

        mvc.perform(MockMvcRequestBuilders.put("/api/booking/4")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void delete_whenBookingsExisted_thenStatus200()
            throws Exception {

        mvc.perform(delete("/api/booking/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void delete_whenBookingsNotExisted_thenStatus200()
            throws Exception {

        mvc.perform(delete("/api/booking/200")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void search_hasData_thenStatus200()
            throws Exception {

        String requestJson = "{\n" +
                "    \"StartTime\": \"2020-01-05 01:30:00\",\n" +
                "    \"EndTime\": \"2020-09-05 09:30:00\",\n" +
                "    \"Username\": \"admin\",\n" +
                "    \"RoomId\": 1\n" +
                "  }";

        mvc.perform(MockMvcRequestBuilders.post("/api/booking/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].Subject").isNotEmpty())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void search_hasNoData_thenStatus200()
            throws Exception {

        String requestJson = "{\n" +
                "    \"StartTime\": \"2020-05-05 01:30:00\",\n" +
                "    \"EndTime\": \"2020-05-05 09:30:00\",\n" +
                "    \"Username\": \"admin\",\n" +
                "    \"RoomId\": 1\n" +
                "  }";

        mvc.perform(MockMvcRequestBuilders.post("/api/booking/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)))
                .andDo(MockMvcResultHandlers.print());
    }
}
