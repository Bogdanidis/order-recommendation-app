package com.example.order_app.rest.cart;


import com.example.order_app.model.Cart;
import com.example.order_app.model.User;
import com.example.order_app.rest.base.BaseRestControllerTest;
import com.example.order_app.service.cart.ICartService;
import com.example.order_app.service.user.IUserService;
import com.example.order_app.util.TestDataUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CartRestControllerTest extends BaseRestControllerTest {

    @MockBean
    private ICartService cartService;

    @MockBean
    private IUserService userService;

    @Test
    @WithMockUser
    void shouldReturnCartForAuthenticatedUser() throws Exception {
        User testUser = TestDataUtil.createTestUser();
        Cart testCart = new Cart();
        testCart.setUser(testUser);

        when(userService.getAuthenticatedUser()).thenReturn(testUser);
        when(cartService.getCartByUserId(testUser.getId())).thenReturn(testCart);

        mockMvc.perform(get("/order-api/v1/carts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Cart retrieved successfully"));
    }
}