package com.gst.masterdata.service.impl;

import com.gst.masterdata.dto.UnitMasterDTO;
import com.gst.masterdata.entity.UnitMasterEntity;
import com.gst.masterdata.repository.UnitMasterRepository;
import com.gst.masterdata.service.UnitMasterService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UnitMasterServiceImpl implements UnitMasterService {

    @Autowired
    private UnitMasterRepository unitMasterRepository;

    @Override
    public UnitMasterDTO createUnit(UnitMasterDTO unitMasterDTO) {
        UnitMasterEntity entity = new UnitMasterEntity();
        BeanUtils.copyProperties(unitMasterDTO, entity);
        UnitMasterEntity savedEntity = unitMasterRepository.save(entity);
        UnitMasterDTO responseDTO = new UnitMasterDTO();
        BeanUtils.copyProperties(savedEntity, responseDTO);
        return responseDTO;
    }

    @Override
    public UnitMasterDTO updateUnit(Long unitId, UnitMasterDTO unitMasterDTO) {
        UnitMasterEntity entity = unitMasterRepository.findById(unitId)
                .orElseThrow(() -> new RuntimeException("Unit not found"));
        BeanUtils.copyProperties(unitMasterDTO, entity, "unitId");
        UnitMasterEntity updatedEntity = unitMasterRepository.save(entity);
        UnitMasterDTO responseDTO = new UnitMasterDTO();
        BeanUtils.copyProperties(updatedEntity, responseDTO);
        return responseDTO;
    }

    @Override
    public UnitMasterDTO getUnitById(Long unitId) {
        UnitMasterEntity entity = unitMasterRepository.findById(unitId)
                .orElseThrow(() -> new RuntimeException("Unit not found"));
        UnitMasterDTO responseDTO = new UnitMasterDTO();
        BeanUtils.copyProperties(entity, responseDTO);
        return responseDTO;
    }

    @Override
    public List<UnitMasterDTO> getAllUnits() {
        return unitMasterRepository.findAll().stream().map(entity -> {
            UnitMasterDTO dto = new UnitMasterDTO();
            BeanUtils.copyProperties(entity, dto);
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public void deleteUnit(Long unitId) {
        unitMasterRepository.deleteById(unitId);
    }
}
