package com.gst.masterdata.service.impl;

import com.gst.masterdata.dto.EmployeeRequest;
import com.gst.masterdata.entity.CityMasterEntity;
import com.gst.masterdata.entity.DesignationMasterEntity;
import com.gst.masterdata.entity.EmployeeMasterEntity;
import com.gst.masterdata.entity.QualificationMasterEntity;
import com.gst.masterdata.entity.RoleMasterEntity;
import com.gst.masterdata.exceptions.ResourceNotFoundException;
import com.gst.masterdata.repository.CityMasterRepository;
import com.gst.masterdata.repository.DesignationMasterRepository;
import com.gst.masterdata.repository.EmployeeMasterRepository;
import com.gst.masterdata.repository.QualificationMasterRepository;
import com.gst.masterdata.repository.RoleMasterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeMasterRepository repository;

    @Mock
    private EmployeeMasterRepository employeeRepository;

    @Mock
    private DesignationMasterRepository designationRepository;

    @Mock
    private QualificationMasterRepository qualificationRepository;

    @Mock
    private CityMasterRepository cityRepository;

    @Mock
    private RoleMasterRepository roleRepository;

    private EmployeeServiceImpl service;

    private EmployeeMasterEntity entity;
    private DesignationMasterEntity designation;
    private QualificationMasterEntity qualification;
    private CityMasterEntity city;
    private RoleMasterEntity role;

    @BeforeEach
    void setUp() {
        service = new EmployeeServiceImpl(repository, employeeRepository, designationRepository,
                qualificationRepository, cityRepository, roleRepository);

        designation = new DesignationMasterEntity();
        designation.setDesignationId(1L);
        designation.setDesignationName("Manager");

        qualification = new QualificationMasterEntity();
        qualification.setQualificationId(2L);
        qualification.setQualificationName("Graduate");

        city = new CityMasterEntity();
        city.setCityId(3L);
        city.setCityName("Mumbai");

        role = new RoleMasterEntity();
        role.setRoleId(4L);
        role.setRoleName("Admin");

        entity = new EmployeeMasterEntity();
        entity.setEmployeeId(1L);
        entity.setEmployeeCode("EMP001");
        entity.setFirstName("John");
        entity.setLastName("Doe");
        entity.setDesignation(designation);
        entity.setQualification(qualification);
        entity.setCity(city);
        entity.setRole(role);
        entity.setIsActive(true);
    }

    @Test
    void create_shouldReturnEmployeeResponse_whenAllReferencesFound() {
        EmployeeRequest request = new EmployeeRequest();
        request.setEmployeeCode("EMP002");
        request.setFirstName("Jane");
        request.setLastName("Smith");
        request.setGender("F");
        request.setDateOfBirth(LocalDate.of(1990, 1, 1));
        request.setJoiningDate(LocalDate.of(2024, 1, 1));
        request.setMobileNo("9999999999");
        request.setEmailId("jane.smith@example.com");
        request.setPanNumber("ABCDE1234F");
        request.setAadhaarNumber("123412341234");
        request.setAddress("123 Main St");
        request.setIsActive(true);
        request.setDesignationId(1L);
        request.setQualifictionId(2L);
        request.setCityId(3L);
        request.setRoleId(4L);

        when(designationRepository.findById(1L)).thenReturn(Optional.of(designation));
        when(qualificationRepository.findById(2L)).thenReturn(Optional.of(qualification));
        when(cityRepository.findById(3L)).thenReturn(Optional.of(city));
        when(roleRepository.findById(4L)).thenReturn(Optional.of(role));
        when(repository.save(any(EmployeeMasterEntity.class))).thenAnswer(invocation -> {
            EmployeeMasterEntity saved = invocation.getArgument(0);
            saved.setEmployeeId(2L);
            return saved;
        });

        var response = service.create(request);

        assertEquals(2L, response.getEmployeeId());
        assertEquals("EMP002", response.getEmployeeCode());
        assertEquals("Manager", response.getDesignationName());
    }

    @Test
    void create_shouldThrow_whenDesignationNotFound() {
        EmployeeRequest request = new EmployeeRequest();
        request.setDesignationId(1L);
        when(designationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.create(request));
    }

    @Test
    void getById_shouldThrow_whenEmployeeMissing() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getById(1L));
    }

    @Test
    void delete_shouldMarkEmployeeDeleted() {
        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        service.delete(1L);

        assertTrue(entity.getIsDeleted());
        assertNotNull(entity.getDeletedAt());
    }
}
