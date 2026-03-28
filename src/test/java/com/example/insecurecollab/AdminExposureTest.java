package com.example.insecurecollab;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminExposureTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void nonAdminCanOpenAdminDashboard() throws Exception {
        MockHttpSession session = (MockHttpSession) mockMvc.perform(post("/login")
                        .param("email", "alex@acme.local")
                        .param("password", "password123"))
                .andReturn()
                .getRequest()
                .getSession(false);

        mockMvc.perform(get("/admin").session(session))
                .andExpect(status().isOk());
    }

    @Test
    void debugEndpointLeaksFakeSecretsWithoutAuth() throws Exception {
        mockMvc.perform(get("/admin/debug"))
                .andExpect(status().isOk());
    }
}
