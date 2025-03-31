package com.coagronet.municipio.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.coagronet.municipio.dtos.MunicipioDTO;
import com.coagronet.municipio.mappers.MunicipioMapper;
import com.coagronet.municipio.repositories.MunicipioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MunicipioService {
    private final MunicipioRepository municipioRepository;
    private final MunicipioMapper municipioMapper;

    public List<MunicipioDTO> findAll() {
        return municipioRepository.findAll().stream()
                .map(municipioMapper::toDTO)
                .collect(Collectors.toList());
    }
}