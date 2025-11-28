package com.coagronet.usuariorol.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.coagronet.usuariorol.dtos.UsuarioRolRequestDTO;
import com.coagronet.usuariorol.dtos.UsuarioRolResponseDTO;

import jakarta.servlet.http.HttpServletRequest;

public interface UsuarioRolService {

    Page<UsuarioRolResponseDTO> findAll(Pageable pageable);

    UsuarioRolResponseDTO findById(Long id);

    UsuarioRolResponseDTO create(UsuarioRolRequestDTO request, HttpServletRequest httpRequest);

    UsuarioRolResponseDTO update(Long id, UsuarioRolRequestDTO request, HttpServletRequest httpRequest);

    void delete(Long id);
}
