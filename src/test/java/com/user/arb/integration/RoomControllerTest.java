package com.user.arb.integration;

import com.user.arb.core.AbstractTest;
import com.user.arb.service.RoomService;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RoomControllerTest extends AbstractTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private RoomService roomService;

    @Test
    public void find_whenRoomExisted_thenStatus200()
            throws Exception {

        mvc.perform(get("/api/room/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.Name").value("Asia 1"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void find_whenRoomDoesNotExisted_thenStatus400()
            throws Exception {

        mvc.perform(get("/api/room/100")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void list_whenBookingsExisted_thenStatus200()
            throws Exception {

        mvc.perform(get("/api/room/list")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].Name").isNotEmpty())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void create_hasNoConflict_thenStatus200()
            throws Exception {

        String requestJson = "{\n" +
                "    \"Name\": \"New room\"\n" +
                "  }";

        mvc.perform(MockMvcRequestBuilders.post("/api/room")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.Name").value("New room"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void create_hasConflict_thenStatus400()
            throws Exception {

        String requestJson = "{\n" +
                "    \"Name\": \"Asia 1\"\n" +
                "  }";

        mvc.perform(MockMvcRequestBuilders.post("/api/room")
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
                "    \"Name\": \"New room name\"\n" +
                "  }";

        mvc.perform(MockMvcRequestBuilders.put("/api/room/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.Name").value("New room name"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void update_hasConflict_thenStatus400()
            throws Exception {

        String requestJson = "{\n" +
                "    \"Name\": \"Asia 2\"\n" +
                "  }";

        mvc.perform(MockMvcRequestBuilders.put("/api/room/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void archive_whenRoomExisted_thenStatus200()
            throws Exception {

        mvc.perform(delete("/api/room/3")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.active").value(false))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void archive_whenRoomNotExisted_thenStatus400()
            throws Exception {

        mvc.perform(delete("/api/room/300")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void active_whenRoomExisted_thenStatus200()
            throws Exception {

        mvc.perform(patch("/api/room/3")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.active").value(true))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void active_whenRoomNotExisted_thenStatus400()
            throws Exception {

        mvc.perform(patch("/api/room/300")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andDo(MockMvcResultHandlers.print());
    }
}
