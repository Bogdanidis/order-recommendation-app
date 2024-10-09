package com.example.order_app.controller;


import com.example.order_app.dto.UserDto;
import com.example.order_app.model.Product;
import com.example.order_app.model.User;
import com.example.order_app.service.cart.ICartService;
import com.example.order_app.service.category.ICategoryService;
import com.example.order_app.service.order.IOrderService;
import com.example.order_app.service.product.IProductService;
import com.example.order_app.service.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequiredArgsConstructor
public class HomeController {

    private final IProductService productService;
    private final ICategoryService categoryService;
    private final IOrderService orderService;
    private final IUserService userService;
    //private final RecommendationService recommendationService;

    @GetMapping("/")
    public String redirectToHome() {
            return "redirect:/home";
    }

    @GetMapping("/home")
    public String home(Authentication authentication, Model model,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "6") int size) {
        if (authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/admin/dashboard";
        }

        // Load paginated products for the main section
        Page<Product> productPage = productService.getAllProducts(PageRequest.of(page, size));
        model.addAttribute("featuredProducts", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalItems", productPage.getTotalElements());

        // Load paginated products for the main section
        model.addAttribute("categories", categoryService.getAllCategories());

        // Add user to model and recommended products if the user is logged in
        if (authentication != null && authentication.isAuthenticated()) {

            User user= userService.getAuthenticatedUser();
            UserDto userDto = userService.convertUserToDto(user);
            model.addAttribute("user", userDto);
        }

        return "home";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {
         //Add any necessary data for the admin dashboard
        model.addAttribute("totalUsers", userService.countUsers());
        model.addAttribute("totalProducts", productService.countProducts());
        model.addAttribute("todayOrders", orderService.countTodaysOrders());
        model.addAttribute("todayRevenue", orderService.getTodaysRevenue());
        return "dashboard";
    }
}
