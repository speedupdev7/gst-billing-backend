package com.gst.masterdata.service;

import com.gst.masterdata.dto.UnitMasterDTO;

import java.util.List;

public interface UnitMasterService {
    UnitMasterDTO createUnit(UnitMasterDTO unitMasterDTO);
    UnitMasterDTO updateUnit(Long unitId, UnitMasterDTO unitMasterDTO);
    UnitMasterDTO getUnitById(Long unitId);
    List<UnitMasterDTO> getAllUnits();
    void deleteUnit(Long unitId);
}
