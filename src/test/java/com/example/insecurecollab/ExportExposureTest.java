package com.example.insecurecollab;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ExportExposureTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void userCanExportAnotherWorkspace() throws Exception {
        MockHttpSession session = (MockHttpSession) mockMvc.perform(post("/login")
                        .param("email", "alex@acme.local")
                        .param("password", "password123"))
                .andReturn()
                .getRequest()
                .getSession(false);

        mockMvc.perform(post("/api/exports/workspaces/2")
                        .session(session)
                        .param("fileName", "demo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.archive").exists());
    }
}
