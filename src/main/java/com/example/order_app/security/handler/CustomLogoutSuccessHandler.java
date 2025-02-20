//package com.example.order_app.security.config;
//
//
//import com.example.order_app.security.user.ShopUserDetails;
//import com.example.order_app.service.cart.ICartService;
//import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
//import org.springframework.stereotype.Component;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
//@Component
//@RequiredArgsConstructor
//public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {
//
//    private static final Logger logger = LoggerFactory.getLogger(CustomLogoutSuccessHandler.class);
//
//    private final ICartService cartService;
//
//
//    @Override
//    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
//                                Authentication authentication) throws IOException {
//        if (authentication != null && authentication.getPrincipal() instanceof ShopUserDetails) {
//            ShopUserDetails userDetails = (ShopUserDetails) authentication.getPrincipal();
//            logger.info("Logout success for user: {}", userDetails.getId());
//            try {
//                cartService.deleteCart(cartService.getCartByUserId(userDetails.getId()).getId());
//                logger.info("Cart deleted on logout for user: {}", userDetails.getId());
//            } catch (Exception e) {
//                logger.error("Error deleting cart on logout for user: {}", userDetails.getId(), e);
//            }
//        } else {
//            logger.warn("No authentication details available on logout");
//        }
//        response.sendRedirect(request.getContextPath() + "/home?logout=true");
//    }
//}