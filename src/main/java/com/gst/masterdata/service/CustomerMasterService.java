package com.gst.masterdata.service;

import com.gst.masterdata.dto.CustomerMasterDTO;

import java.util.List;

public interface CustomerMasterService {
    CustomerMasterDTO createCustomer(CustomerMasterDTO customerMasterDTO);
    CustomerMasterDTO updateCustomer(Long customerId, CustomerMasterDTO customerMasterDTO);
    CustomerMasterDTO getCustomerById(Long customerId);
    List<CustomerMasterDTO> getAllCustomers();
    List<CustomerMasterDTO> searchCustomersByNamePrefix(String customerNamePrefix);
    void deleteCustomer(Long customerId);
}
