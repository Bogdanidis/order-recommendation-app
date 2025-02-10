package com.example.order_app.controller.product;

import com.example.order_app.controller.base.BaseControllerTest;
import com.example.order_app.model.Product;
import com.example.order_app.service.product.IProductService;
import com.example.order_app.util.TestDataUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ProductControllerTest extends BaseControllerTest {

    @MockBean
    private IProductService productService;

    @Test
    @WithMockUser
    void shouldDisplayProductDetails() throws Exception {
        Product testProduct = TestDataUtil.createTestProduct();
        when(productService.getProductById(1L)).thenReturn(testProduct);

        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("product/details"))
                .andExpect(model().attributeExists("product"));
    }

    @Test
    @WithMockUser
    void shouldDisplayProductList() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(view().name("product/list"))
                .andExpect(model().attributeExists("products"));
    }
}
