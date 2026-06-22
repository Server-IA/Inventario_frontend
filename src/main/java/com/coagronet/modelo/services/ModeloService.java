package com.coagronet.modelo.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.coagronet.exceptionHandler.BadRequestException;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.modelo.dtos.ModeloDTO;
import com.coagronet.modelo.mappers.ModeloMapper;
import com.coagronet.modelo.repositories.ModeloRepository;
import com.coagronet.utils.UserEmpresaService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ModeloService {
    private final ModeloRepository modeloRepository;
    private final ModeloMapper modeloMapper;
    private final EstadoRepository estadoRepository;
    private final UserEmpresaService userEmpresaService;


    public List <ModeloDTO> findAll(){
        return modeloRepository.findByEmpresaIdOrderByIdAsc(userEmpresaService.getEmpresaIdFromCurrentRequest())
            .stream()
            .map(modeloMapper::toDto)
            .collect(Collectors.toList());
    }

    public Optional<ModeloDTO> findById(Long requestedId) {
        return modeloRepository
                .findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
                .map(modeloMapper::toDto);
    }

    @Transactional
    public ModeloDTO create (ModeloDTO modeloDTO) {
        estadoRepository.findById(modeloDTO.getEstadoId())
                .orElseThrow(() -> new BadRequestException("El estado no es válido."));
        
        modeloDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());
        return modeloMapper.toDto(modeloRepository.save(modeloMapper.toEntity(modeloDTO)));
    }

    @Transactional
    public void update (Long requestedId, ModeloDTO modeloDTO) {
        modeloRepository.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
                .orElseThrow(() -> new NotFoundException("El modelo no se ha encontrado o no es válido."));

        estadoRepository.findById(modeloDTO.getEstadoId())
                .orElseThrow(() -> new BadRequestException("El estado no es válido."));
        modeloDTO.setId(requestedId);
        modeloDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());
        modeloRepository.save(modeloMapper.toEntity(modeloDTO));
    }

    @Transactional
    public void delete (Long requestId) {
        modeloRepository.findByIdAndEmpresaId(requestId, userEmpresaService.getEmpresaIdFromCurrentRequest())
                .orElseThrow(() -> new NotFoundException("El modelo no se ha encontrado o no es válido."));
        modeloRepository.deleteById(requestId);
    }
}
