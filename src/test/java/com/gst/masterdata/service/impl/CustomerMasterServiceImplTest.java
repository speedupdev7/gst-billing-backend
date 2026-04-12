package com.gst.masterdata.service.impl;

import com.gst.masterdata.dto.CustomerMasterDTO;
import com.gst.masterdata.entity.CustomerMasterEntity;
import com.gst.masterdata.repository.CustomerMasterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerMasterServiceImplTest {

    @Mock
    private CustomerMasterRepository customerMasterRepository;

    @InjectMocks
    private CustomerMasterServiceImpl service;

    private CustomerMasterDTO dto;
    private CustomerMasterEntity entity;

    @BeforeEach
    void setUp() {
        dto = new CustomerMasterDTO();
        dto.setCustomerName("Acme Corp");
        dto.setGstin("27ABCDE1234F2Z5");
        dto.setState("Maharashtra");
        dto.setStateCode("27");
        dto.setEmail("contact@acme.com");
        dto.setMobileNo("9810012345");
        dto.setCustomerType("B2B");
        dto.setPinCode("400001");
        dto.setDistrict("Mumbai");
        dto.setBillingAddress("123 Marine Drive");

        entity = new CustomerMasterEntity();
        entity.setCustomerId(1L);
        entity.setCustomerName(dto.getCustomerName());
        entity.setGstin(dto.getGstin());
        entity.setState(dto.getState());
        entity.setStateCode(dto.getStateCode());
        entity.setEmail(dto.getEmail());
        entity.setMobileNo(dto.getMobileNo());
        entity.setCustomerType(dto.getCustomerType());
        entity.setPinCode(dto.getPinCode());
        entity.setDistrict(dto.getDistrict());
        entity.setBillingAddress(dto.getBillingAddress());
    }

    @Test
    void createCustomer_shouldReturnSavedDto() {
        when(customerMasterRepository.save(any(CustomerMasterEntity.class))).thenReturn(entity);

        CustomerMasterDTO result = service.createCustomer(dto);

        assertNotNull(result);
        assertEquals(1L, result.getCustomerId());
        assertEquals("Acme Corp", result.getCustomerName());
        assertEquals("27ABCDE1234F2Z5", result.getGstin());
        verify(customerMasterRepository, times(1)).save(any(CustomerMasterEntity.class));
    }

    @Test
    void getCustomerById_shouldReturnDto_whenFound() {
        when(customerMasterRepository.findById(1L)).thenReturn(Optional.of(entity));

        CustomerMasterDTO result = service.getCustomerById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getCustomerId());
        assertEquals("Acme Corp", result.getCustomerName());
        verify(customerMasterRepository, times(1)).findById(1L);
    }

    @Test
    void getCustomerById_shouldThrow_whenNotFound() {
        when(customerMasterRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.getCustomerById(1L));
        verify(customerMasterRepository).findById(1L);
    }

    @Test
    void getAllCustomers_shouldReturnDtoList() {
        when(customerMasterRepository.findAll(any(Sort.class))).thenReturn(List.of(entity));

        List<CustomerMasterDTO> result = service.getAllCustomers();

        assertEquals(1, result.size());
        assertEquals("Acme Corp", result.get(0).getCustomerName());
        verify(customerMasterRepository).findAll(eq(Sort.by(Direction.DESC, "customerId")));
    }

    @Test
    void deleteCustomer_shouldCallRepositoryDelete() {
        service.deleteCustomer(1L);

        verify(customerMasterRepository, times(1)).deleteById(1L);
    }
}
