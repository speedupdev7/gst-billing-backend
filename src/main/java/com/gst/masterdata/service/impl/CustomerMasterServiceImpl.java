package com.gst.masterdata.service.impl;

import com.gst.masterdata.dto.CustomerMasterDTO;
import com.gst.masterdata.entity.CustomerMasterEntity;
import com.gst.masterdata.exceptions.ResourceNotFoundException;
import com.gst.masterdata.repository.CustomerMasterRepository;
import com.gst.masterdata.service.CustomerMasterService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerMasterServiceImpl implements CustomerMasterService {

    @Autowired
    private CustomerMasterRepository customerMasterRepository;

    @Override
    public CustomerMasterDTO createCustomer(CustomerMasterDTO customerMasterDTO) {
        CustomerMasterEntity entity = new CustomerMasterEntity();
        BeanUtils.copyProperties(customerMasterDTO, entity);
        CustomerMasterEntity savedEntity = customerMasterRepository.save(entity);
        CustomerMasterDTO responseDTO = new CustomerMasterDTO();
        BeanUtils.copyProperties(savedEntity, responseDTO);
        return responseDTO;
    }

    @Override
    public CustomerMasterDTO updateCustomer(Long customerId, CustomerMasterDTO customerMasterDTO) {
        CustomerMasterEntity entity = customerMasterRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        BeanUtils.copyProperties(customerMasterDTO, entity, "customerId");
        CustomerMasterEntity updatedEntity = customerMasterRepository.save(entity);
        CustomerMasterDTO responseDTO = new CustomerMasterDTO();
        BeanUtils.copyProperties(updatedEntity, responseDTO);
        return responseDTO;
    }

    @Override
    public CustomerMasterDTO getCustomerById(Long customerId) {
        CustomerMasterEntity entity = customerMasterRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        CustomerMasterDTO responseDTO = new CustomerMasterDTO();
        BeanUtils.copyProperties(entity, responseDTO);
        return responseDTO;
    }

    @Override
    public List<CustomerMasterDTO> getAllCustomers() {
        return customerMasterRepository.findAll(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "customerId")).stream().map(entity -> {
            CustomerMasterDTO dto = new CustomerMasterDTO();
            BeanUtils.copyProperties(entity, dto);
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<CustomerMasterDTO> searchCustomersByNamePrefix(String customerNamePrefix) {
        return customerMasterRepository.findTop20ByCustomerNameStartingWithIgnoreCaseAndIsDeletedFalse(customerNamePrefix).stream().map(entity -> {
            CustomerMasterDTO dto = new CustomerMasterDTO();
            BeanUtils.copyProperties(entity, dto);
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public void deleteCustomer(Long customerId) {
        CustomerMasterEntity entity = customerMasterRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));
        entity.setIsDeleted(true);
        entity.setDeletedAt(LocalDateTime.now());
    }

}
