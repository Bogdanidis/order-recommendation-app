package com.example.order_app.controller;


import com.example.order_app.model.Cart;
import com.example.order_app.model.Product;
import com.example.order_app.model.User;
import com.example.order_app.security.user.ShopUserDetails;
import com.example.order_app.service.cart.CartService;
import com.example.order_app.service.cart.ICartService;
import com.example.order_app.service.category.CategoryService;
import com.example.order_app.service.category.ICategoryService;
import com.example.order_app.service.product.IProductService;
import com.example.order_app.service.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequiredArgsConstructor
public class HomeController {

    private final IProductService productService;
    private final ICategoryService categoryService;
    private final ICartService cartService;
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

        // Add recommended products if the user is logged in
        if (authentication != null && authentication.isAuthenticated()) {

            User user= userService.getAuthenticatedUser();

            Cart cart = cartService.getCartByUserId(user.getId());
            if(cart==null){
                cart=cartService.initializeNewCart(user);
            }
            //model.addAttribute("cart", cart);
           // model.addAttribute("user", user);
        }

        return "home";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {
        // Add any necessary data for the admin dashboard
        //model.addAttribute("totalUsers", userService.getTotalUsers());
        //model.addAttribute("totalProducts", productService.getTotalProducts());
        //model.addAttribute("todayOrders", orderService.getTodayOrdersCount());
        //model.addAttribute("todayRevenue", orderService.getTodayRevenue());

        return "admin-dashboard";
    }
}
