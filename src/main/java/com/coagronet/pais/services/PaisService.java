package com.coagronet.pais.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.coagronet.pais.dtos.PaisDTO;
import com.coagronet.pais.mappers.PaisMapper;
import com.coagronet.pais.repositories.PaisRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaisService {
    private final PaisRepository paisRepository;
    private final PaisMapper paisMapper;

    public List<PaisDTO> findAll() {
        return paisRepository.findAll().stream()
                .map(paisMapper::toDTO)
                .collect(Collectors.toList());
    }
}
