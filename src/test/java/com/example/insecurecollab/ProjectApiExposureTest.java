package com.example.insecurecollab;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProjectApiExposureTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void anonymousCallerCanReadPrivateProjectJson() throws Exception {
        mockMvc.perform(get("/api/projects/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Quarterly Planning"));
    }

    @Test
    void bulkCloseValidatesOnlyFirstProject() throws Exception {
        MockHttpSession session = (MockHttpSession) mockMvc.perform(post("/login")
                        .param("email", "alex@acme.local")
                        .param("password", "password123"))
                .andReturn()
                .getRequest()
                .getSession(false);

        mockMvc.perform(post("/api/projects/bulk-close")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[1,2]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.closed").value(2));
    }
}
