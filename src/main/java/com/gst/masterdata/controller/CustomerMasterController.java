package com.gst.masterdata.controller;

import com.gst.masterdata.dto.CustomerMasterDTO;
import com.gst.masterdata.service.CustomerMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
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

    @GetMapping("/search")
    public List<CustomerMasterDTO> searchCustomers(@RequestParam String q) {
        if (q == null || q.trim().length() < 3) {
            return Collections.emptyList();
        }
        return customerMasterService.searchCustomersByNamePrefix(q.trim());
    }

    @DeleteMapping("/{customerId}")
    public void deleteCustomer(@PathVariable Long customerId) {
        customerMasterService.deleteCustomer(customerId);
    }
}
