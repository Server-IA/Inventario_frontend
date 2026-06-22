package com.coagronet.tipoFase.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


import org.springframework.stereotype.Service;

import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.BadRequestException;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.tipoFase.dtos.TipoFaseDTO;
import com.coagronet.tipoFase.mappers.TipoFaseMapper;
import com.coagronet.tipoFase.repositories.TipoFaseRepository;
import com.coagronet.utils.UserEmpresaService;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TipoFaseService {

    private final TipoFaseRepository tipoFaseRepository;
    private final TipoFaseMapper tipoFaseMapper;
    private final EstadoRepository estadoRepository;
    private final UserEmpresaService userEmpresaService;

    public List<TipoFaseDTO> findAll(){
        return tipoFaseRepository.findByEmpresaIdOrderByIdAsc(userEmpresaService.getEmpresaIdFromCurrentRequest())
                .stream()
                .map(tipoFaseMapper::toDto)
                .collect(Collectors.toList());
    }

    public Optional<TipoFaseDTO> findById(Long requestedId) {
        return tipoFaseRepository
                .findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
                .map(tipoFaseMapper::toDto);
    }

    @Transactional
    public TipoFaseDTO create(TipoFaseDTO tipoFaseDTO) {
        estadoRepository.findById(tipoFaseDTO.getEstadoId())
                .orElseThrow(() -> new BadRequestException("El estado no es válido."));

        tipoFaseDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

        return tipoFaseMapper.toDto(tipoFaseRepository.save(tipoFaseMapper.toEntity(tipoFaseDTO)));
    }

    @Transactional
    public void update(Long requestedId, TipoFaseDTO tipoFaseDTO) {
        tipoFaseRepository.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
            .orElseThrow(() -> new NotFoundException("Tipo de fase no encontrada o no válida."));
        
        estadoRepository.findById(tipoFaseDTO.getEstadoId())
            .orElseThrow(() -> new BadRequestException("El estado no es válido"));
        
        tipoFaseDTO.setId(requestedId);
        tipoFaseDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

        tipoFaseRepository.save(tipoFaseMapper.toEntity(tipoFaseDTO));
    }

    @Transactional
    public void delete(Long requestId){
        tipoFaseRepository.findByIdAndEmpresaId(requestId, userEmpresaService.getEmpresaIdFromCurrentRequest())
                .orElseThrow(() -> new NotFoundException("Tipo fase no encontrada o no válida."));
                tipoFaseRepository.deleteById((requestId));
    }

}
