package com.coagronet.sede.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.coagronet.sede.dtos.SedeDTO;
import com.coagronet.sede.mappers.SedeMapper;
import com.coagronet.sede.repositories.SedeRepository;
import com.coagronet.utils.AuthenticationService;
import com.coagronet.utils.UserEmpresaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SedeService {

    private final SedeRepository sedeRepository;
    private final SedeMapper sedeMapper;
    private final AuthenticationService authenticationService;
    private final UserEmpresaService userEmpresaService;

    public List<SedeDTO> findAll() {
        return sedeRepository.findAll().stream()
                .map(sedeMapper::toDTO)
                .collect(Collectors.toList());
    }

}
