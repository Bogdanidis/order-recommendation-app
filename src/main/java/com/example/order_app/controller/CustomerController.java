package com.example.order_app.controller;

import com.example.order_app.model.Customer;
import com.example.order_app.repository.CustomerRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("customers")
public class CustomerController {

    private final CustomerRepository customerRepository;

    public CustomerController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @GetMapping("/list")
    public String listCustomer(Model model) {
        Iterable<Customer> customers = customerRepository.findAll();
        model.addAttribute("customers", customers);
        return "customer/customer_list";
    }

    @GetMapping("/add")
    public String showAddCustomerForm(Model model) {
        model.addAttribute("customer", new Customer());
        return "customer/customer_add";
    }


    @PostMapping("/add")
    public String addCustomer(Model model, Customer customer) {
        model.addAttribute("customer", customerRepository.save(customer));
        return "redirect:/customers/list";
    }

    @GetMapping("/edit/{id}")
    public String showEditCustomerForm(@PathVariable Long id, Model model) {
        model.addAttribute("customer", customerRepository.findById(id));
        return "customer/customer_add";
    }

    @GetMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable Long id) {
        customerRepository.deleteById(id);
        return "redirect:/customers/list";
    }

}
