package com.example.order_app.controller;


import com.example.order_app.controller.base.BaseControllerTest;
import com.example.order_app.service.product.IProductService;
import com.example.order_app.service.category.ICategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class HomeControllerTest extends BaseControllerTest {

    @MockBean
    private IProductService productService;

    @MockBean
    private ICategoryService categoryService;

    @Test
    void shouldRedirectToHome() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldDisplayHomePageForUser() throws Exception {
        mockMvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeExists("products", "categories"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldRedirectAdminToDashboard() throws Exception {
        mockMvc.perform(get("/home"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dashboard"));
    }
}