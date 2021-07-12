package com.user.arb.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.user.arb.core.AbstractIntegrationTest;
import com.user.arb.service.ScheduleService;
import com.user.arb.service.dto.ScheduleDTO;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

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
public class ScheduleControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ScheduleService scheduleService;

    private static boolean IS_INSERT = Boolean.FALSE;

    private List<ScheduleDTO> schedules = new ArrayList<>();

    @Before
    public void init() {
        if (!IS_INSERT) {
            TypeReference<List<ScheduleDTO>> scheduleListType = new TypeReference<List<ScheduleDTO>>() {
            };
            schedules = this.fromJsonToList("json/schedule.json", scheduleListType, scheduleService);
            IS_INSERT = !IS_INSERT;
        } else {
            schedules = scheduleService.find();
        }
    }

    @Test
    public void find_whenBookingExisted_thenStatus200()
            throws Exception {

        mvc.perform(get("/api/schedule/" + schedules.get(0).getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.Subject").value(schedules.get(0).getSubject()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void find_whenBookingDoesNotExisted_thenStatus400()
            throws Exception {

        mvc.perform(get("/api/schedule/200")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void list_whenBookingsExisted_thenStatus200()
            throws Exception {

        String requestJson = "{\"RoomIds\" : [1,2]}";

        mvc.perform(MockMvcRequestBuilders.post("/api/schedule/list")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void list_whenBookingsNotExisted_thenResultEmpty()
            throws Exception {

        String requestJson = "{\"RoomIds\" : [100,200]}";

        mvc.perform(MockMvcRequestBuilders.post("/api/schedule/list")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void create_hasNoConflict_thenStatus200()
            throws Exception {
        String requestJson = readJsonFile("json/schedule_request_1.json");

        mvc.perform(MockMvcRequestBuilders.post("/api/schedule/syncfusion")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.Subject").value("New schedule 1"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void create_hasConflict_thenStatus400()
            throws Exception {
        String requestJson = readJsonFile("json/schedule_request_2.json");

        mvc.perform(MockMvcRequestBuilders.post("/api/schedule/syncfusion")
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
        String requestJson = readJsonFile("json/schedule_request_3.json");

        mvc.perform(MockMvcRequestBuilders.post("/api/schedule/syncfusion")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.Subject").value("New schedule 3"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void update_hasConflict_thenStatus400()
            throws Exception {
        String requestJson = readJsonFile("json/schedule_request_4.json");

        mvc.perform(MockMvcRequestBuilders.post("/api/schedule/syncfusion")
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

        mvc.perform(delete("/api/schedule/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void delete_whenBookingsNotExisted_thenStatus200()
            throws Exception {

        mvc.perform(delete("/api/schedule/200")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void search_hasData_thenStatus200()
            throws Exception {

        String requestJson = "{\n" +
                "    \"StartTime\": \"2021-07-05T01:00:00.000Z\",\n" +
                "    \"EndTime\": \"2021-07-25T01:00:00.000Z\",\n" +
                "    \"Username\": \"admin\",\n" +
                "    \"RoomId\": 1\n" +
                "  }";
        mvc.perform(MockMvcRequestBuilders.post("/api/schedule/search")
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
                "    \"StartTime\": \"2020-07-05T01:00:00.000Z\",\n" +
                "    \"EndTime\": \"2020-07-06T01:00:00.000Z\",\n" +
                "    \"Username\": \"admin\",\n" +
                "    \"RoomId\": 1\n" +
                "  }";

        mvc.perform(MockMvcRequestBuilders.post("/api/schedule/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)))
                .andDo(MockMvcResultHandlers.print());
    }
}
