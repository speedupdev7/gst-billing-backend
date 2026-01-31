package com.gst.masterdata.controller;

import com.gst.masterdata.dto.SupplierMasterDTO;
import com.gst.masterdata.service.SupplierMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/supplier-master")
public class SupplierMasterController {

    @Autowired
    private SupplierMasterService supplierMasterService;

    @PostMapping
    public SupplierMasterDTO createSupplier(@RequestBody SupplierMasterDTO supplierMasterDTO) {
        return supplierMasterService.createSupplier(supplierMasterDTO);
    }

    @PutMapping("/{supplierId}")
    public SupplierMasterDTO updateSupplier(@PathVariable Long supplierId, @RequestBody SupplierMasterDTO supplierMasterDTO) {
        return supplierMasterService.updateSupplier(supplierId, supplierMasterDTO);
    }

    @GetMapping("/{supplierId}")
    public SupplierMasterDTO getSupplierById(@PathVariable Long supplierId) {
        return supplierMasterService.getSupplierById(supplierId);
    }

    @GetMapping
    public List<SupplierMasterDTO> getAllSuppliers() {
        return supplierMasterService.getAllSuppliers();
    }

    @DeleteMapping("/{supplierId}")
    public void deleteSupplier(@PathVariable Long supplierId) {
        supplierMasterService.deleteSupplier(supplierId);
    }
}
