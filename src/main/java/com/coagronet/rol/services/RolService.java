package com.coagronet.rol.services;

import java.util.List;

import com.coagronet.rol.dtos.RolRequestDTO;
import com.coagronet.rol.dtos.RolResponseDTO;

public interface RolService {

    RolResponseDTO create(RolRequestDTO request);

    RolResponseDTO update(Long id, RolRequestDTO request);

    RolResponseDTO getById(Long id);

    List<RolResponseDTO> getAll();

    void delete(Long id);

}
