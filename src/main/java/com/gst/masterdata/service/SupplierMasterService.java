package com.gst.masterdata.service;

import com.gst.masterdata.dto.SupplierMasterDTO;

import java.util.List;

public interface SupplierMasterService {
    SupplierMasterDTO createSupplier(SupplierMasterDTO supplierMasterDTO);
    SupplierMasterDTO updateSupplier(Long supplierId, SupplierMasterDTO supplierMasterDTO);
    SupplierMasterDTO getSupplierById(Long supplierId);
    List<SupplierMasterDTO> getAllSuppliers();
    void deleteSupplier(Long supplierId);
}
