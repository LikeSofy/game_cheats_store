package by.sofy.game_cheats_store.controllers;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application_test.properties")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void register__incorrectRequest__returnErrorMessage() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post("/registration")
                .param("login", "login")
                .param("password", "password")
                .param("replyPassword", "otherPassword"))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("Password mismatch")));
    }

    @Test
    void register__correctRequest__returnSuccessMessage() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post("/registration")
                .param("login", "login")
                .param("password", "Passw0rds")
                .param("replyPassword", "Passw0rds"))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("User registered")));
    }
}