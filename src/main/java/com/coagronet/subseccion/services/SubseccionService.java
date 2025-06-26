package com.coagronet.subseccion.services;

import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.BadRequestException;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.subseccion.mappers.SubseccionMapper;
import com.coagronet.subseccion.repositories.SubseccionRepository;
import com.coagronet.subseccion.dtos.SubseccionDTO;
import com.coagronet.utils.UserEmpresaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubseccionService {

    private final SubseccionRepository subseccionRepository;
    private final SubseccionMapper subseccionMapper;
    private final EstadoRepository estadoRepository;
    private final UserEmpresaService userEmpresaService;

    public List<SubseccionDTO> findAll() {
        return subseccionRepository.findByEmpresaIdOrderByIdAsc(userEmpresaService.getEmpresaIdFromCurrentRequest())
                .stream()
                .map(subseccionMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<SubseccionDTO> findById(Long requestedId) {
        return subseccionRepository
                .findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
                .map(subseccionMapper::toDTO);
    }

    @Transactional
    public SubseccionDTO create(SubseccionDTO subseccionDTO) {
        estadoRepository.findById(subseccionDTO.getEstadoId())
                .orElseThrow(() -> new BadRequestException("Estado no encontrado o no válido"));

        subseccionDTO.setId(null);
        subseccionDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

        return subseccionMapper.toDTO(subseccionRepository.save(subseccionMapper.toEntity(subseccionDTO)));
    }

    @Transactional
    public void update(Long requestedId, SubseccionDTO subseccionDTO) {
        subseccionRepository.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
                .orElseThrow(() -> new NotFoundException("Subseccion no encontrada en su empresa"));

        estadoRepository.findById(subseccionDTO.getEstadoId())
                .orElseThrow(() -> new BadRequestException("El estado no es válido"));

        subseccionDTO.setId(requestedId);
        subseccionDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

        subseccionRepository.save(subseccionMapper.toEntity(subseccionDTO));
    }

    @Transactional
    public void delete(Long requestedId) {
        subseccionRepository.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
                .orElseThrow(() -> new NotFoundException("Subseccion no encontrada en su empresa"));

        subseccionRepository.deleteById(requestedId);
    }

}
