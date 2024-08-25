/*
package controller;

import model.Customer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import repository.CustomerRepository;

import java.util.List;

@RestController
@RequestMapping("/order-api/customers")
public class CustomerController {
    private final CustomerRepository customerRepository;


    @GetMapping
    ResponseEntity<List<Customer>> getCustomers(){
        return new ResponseEntity<>((List<Customer>) customerRepository.findAll(), HttpStatus.OK);
    }

}
*/