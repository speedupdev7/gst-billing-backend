package com.gst.masterdata.controller;

import com.gst.masterdata.dto.UnitMasterDTO;
import com.gst.masterdata.service.UnitMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/unit-master")
public class UnitMasterController {

    @Autowired
    private UnitMasterService unitMasterService;

    @PostMapping
    public UnitMasterDTO createUnit(@RequestBody UnitMasterDTO unitMasterDTO) {
        return unitMasterService.createUnit(unitMasterDTO);
    }

    @PutMapping("/{unitId}")
    public UnitMasterDTO updateUnit(@PathVariable Long unitId, @RequestBody UnitMasterDTO unitMasterDTO) {
        return unitMasterService.updateUnit(unitId, unitMasterDTO);
    }

    @GetMapping("/{unitId}")
    public UnitMasterDTO getUnitById(@PathVariable Long unitId) {
        return unitMasterService.getUnitById(unitId);
    }

    @GetMapping
    public List<UnitMasterDTO> getAllUnits() {
        return unitMasterService.getAllUnits();
    }

    @DeleteMapping("/{unitId}")
    public void deleteUnit(@PathVariable Long unitId) {
        unitMasterService.deleteUnit(unitId);
    }
}
