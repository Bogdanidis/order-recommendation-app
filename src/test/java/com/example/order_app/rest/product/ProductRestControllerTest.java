package com.example.order_app.rest.product;

import com.example.order_app.dto.ProductDto;
import com.example.order_app.model.Product;
import com.example.order_app.rest.base.BaseRestControllerTest;
import com.example.order_app.service.product.IProductService;
import com.example.order_app.util.TestDataUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import java.util.Collections;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ProductRestControllerTest extends BaseRestControllerTest {

//    @MockBean
//    private IProductService productService;
//
//    @Test
//    @WithMockUser
//    void shouldReturnProductsList() throws Exception {
//        Product testProduct = TestDataUtil.createTestProduct();
//        Page<ProductDto> productPage = new PageImpl<>(Collections.singletonList(
//                new ProductDto() // populate with test data
//        ));
//
//        when(productService.searchProducts(any(), any(), any(), any())).thenReturn(productPage);
//
//        mockMvc.perform(get("/order-api/v1/products/search"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.message").value("Products found"))
//                .andExpect(jsonPath("$.data").exists());
//    }
}
