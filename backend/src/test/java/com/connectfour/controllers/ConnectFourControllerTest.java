package com.connectfour.controllers;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ConnectFourController.class)
class ConnectFourControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String BASE = "/api/connectfour";

    @BeforeEach
    void resetGame() throws Exception {
        // The controller holds a single game instance, so isolate each test with a reset.
        mockMvc.perform(post(BASE + "/reset")).andExpect(status().isOk());
    }

    @Test
    void positionReturnsEmptySixBySevenGrid() throws Exception {
        mockMvc.perform(get(BASE + "/position"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(6))
                .andExpect(jsonPath("$[0].length()").value(7))
                .andExpect(jsonPath("$[5][0]").value(nullValue()));
    }

    @Test
    void moveDropsDiscInColumn() throws Exception {
        mockMvc.perform(post(BASE + "/move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"player\":\"red\",\"column\":3}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.column").value(3))
                .andExpect(jsonPath("$.board[5][3]").value("red"));
    }

    @Test
    void computeReturnsLegalColumnForGivenDifficulty() throws Exception {
        mockMvc.perform(post(BASE + "/compute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"player\":\"yellow\",\"difficulty\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.column").value(greaterThanOrEqualTo(0)))
                .andExpect(jsonPath("$.column").value(lessThan(7)))
                .andExpect(jsonPath("$.board.length()").value(6));
    }

    @Test
    void undoRemovesLastMove() throws Exception {
        mockMvc.perform(post(BASE + "/move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"player\":\"red\",\"column\":2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.board[5][2]").value("red"));

        mockMvc.perform(post(BASE + "/undo").param("count", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[5][2]").value(nullValue()));
    }

    @Test
    void winnerReflectsBoardState() throws Exception {
        for (int i = 0; i < 4; i++) {
            mockMvc.perform(post(BASE + "/move")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"player\":\"red\",\"column\":0}"))
                    .andExpect(status().isOk());
        }

        mockMvc.perform(get(BASE + "/winner"))
                .andExpect(status().isOk())
                .andExpect(content().string("red"));
    }
}
