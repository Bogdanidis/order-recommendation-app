package com.example.order_app.rest.base;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public abstract class BaseRestControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // Common setup for all REST controller tests
    }
}
