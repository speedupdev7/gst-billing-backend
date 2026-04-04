package com.gst.masterdata.service.impl;

import com.gst.masterdata.dto.SupplierMasterDTO;
import com.gst.masterdata.entity.SupplierMasterEntity;
import com.gst.masterdata.repository.SupplierMasterRepository;
import com.gst.masterdata.service.SupplierMasterService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SupplierMasterServiceImpl implements SupplierMasterService {

    @Autowired
    private SupplierMasterRepository supplierMasterRepository;

    @Override
    public SupplierMasterDTO createSupplier(SupplierMasterDTO supplierMasterDTO) {
        SupplierMasterEntity entity = new SupplierMasterEntity();
        BeanUtils.copyProperties(supplierMasterDTO, entity);
        SupplierMasterEntity savedEntity = supplierMasterRepository.save(entity);
        SupplierMasterDTO responseDTO = new SupplierMasterDTO();
        BeanUtils.copyProperties(savedEntity, responseDTO);
        return responseDTO;
    }

    @Override
    public SupplierMasterDTO updateSupplier(Long supplierId, SupplierMasterDTO supplierMasterDTO) {
        SupplierMasterEntity entity = supplierMasterRepository.findById(supplierId)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
        BeanUtils.copyProperties(supplierMasterDTO, entity, "supplierId");
        SupplierMasterEntity updatedEntity = supplierMasterRepository.save(entity);
        SupplierMasterDTO responseDTO = new SupplierMasterDTO();
        BeanUtils.copyProperties(updatedEntity, responseDTO);
        return responseDTO;
    }

    @Override
    public SupplierMasterDTO getSupplierById(Long supplierId) {
        SupplierMasterEntity entity = supplierMasterRepository.findById(supplierId)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
        SupplierMasterDTO responseDTO = new SupplierMasterDTO();
        BeanUtils.copyProperties(entity, responseDTO);
        return responseDTO;
    }

    @Override
    public List<SupplierMasterDTO> getAllSuppliers() {
        return supplierMasterRepository.findAll(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "supplierId")).stream().map(entity -> {
            SupplierMasterDTO dto = new SupplierMasterDTO();
            BeanUtils.copyProperties(entity, dto);
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public void deleteSupplier(Long supplierId) {
        supplierMasterRepository.deleteById(supplierId);
    }
}
