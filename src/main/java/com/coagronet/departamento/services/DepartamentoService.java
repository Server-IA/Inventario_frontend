package com.coagronet.departamento.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.coagronet.departamento.dtos.DepartamentoDTO;
import com.coagronet.departamento.mappers.DepartamentoMapper;
import com.coagronet.departamento.repositories.DepartamentoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DepartamentoService {
    private final DepartamentoRepository departamentoRepository;
    private final DepartamentoMapper departamentoMapper;

    public List<DepartamentoDTO> findAll() {
        return departamentoRepository.findAll().stream()
                .map(departamentoMapper::toDTO)
                .collect(Collectors.toList());
    }
}
