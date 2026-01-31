package com.gst.masterdata.controller;

import com.gst.masterdata.dto.CustomerMasterDTO;
import com.gst.masterdata.service.CustomerMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer-master")
public class CustomerMasterController {

    @Autowired
    private CustomerMasterService customerMasterService;

    @PostMapping
    public CustomerMasterDTO createCustomer(@RequestBody CustomerMasterDTO customerMasterDTO) {
        return customerMasterService.createCustomer(customerMasterDTO);
    }

    @PutMapping("/{customerId}")
    public CustomerMasterDTO updateCustomer(@PathVariable Long customerId, @RequestBody CustomerMasterDTO customerMasterDTO) {
        return customerMasterService.updateCustomer(customerId, customerMasterDTO);
    }

    @GetMapping("/{customerId}")
    public CustomerMasterDTO getCustomerById(@PathVariable Long customerId) {
        return customerMasterService.getCustomerById(customerId);
    }

    @GetMapping
    public List<CustomerMasterDTO> getAllCustomers() {
        return customerMasterService.getAllCustomers();
    }

    @DeleteMapping("/{customerId}")
    public void deleteCustomer(@PathVariable Long customerId) {
        customerMasterService.deleteCustomer(customerId);
    }
}
