package com.coagronet.seccion.services;

import com.coagronet.exceptionHandler.BadRequestException;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.espacio.repositories.EspacioRepository;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.seccion.dtos.SeccionDTO;
import com.coagronet.seccion.mapper.SeccionMapper;
import com.coagronet.seccion.repositories.SeccionRepository;
import com.coagronet.utils.AuthenticationService;
import com.coagronet.utils.UserEmpresaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeccionService {

        private final AuthenticationService authenticationService;
        private final UserEmpresaService userEmpresaService;
        private final SeccionMapper seccionMapper;
        private final SeccionRepository seccionRepository;
        private final EstadoRepository estadoRepository;
        private final EspacioRepository espacioRepository;

        public List<SeccionDTO> findAll() {
                return seccionRepository.findByEmpresaIdOrderByIdAsc(userEmpresaService
                                .getEmpresaFromUser(authenticationService.getAuthenticatedUser()).getId())
                                .stream()
                                .map(seccionMapper::toListDTO)
                                .collect(Collectors.toList());
        }

        public Optional<SeccionDTO> findById(Long requestedId) {
                return seccionRepository
                                .findByIdAndEmpresaId(requestedId,
                                                userEmpresaService.getEmpresaFromUser(
                                                                authenticationService.getAuthenticatedUser()).getId())
                                .map(seccionMapper::toListDTO);
        }

        public SeccionDTO create(SeccionDTO seccionDTO) {
                espacioRepository.findByIdAndEmpresaId(seccionDTO.getEspacioId(),
                                userEmpresaService.getEmpresaFromUser(
                                                authenticationService.getAuthenticatedUser()).getId())
                                .orElseThrow(() -> new BadRequestException("El espacio no es válido"));

                estadoRepository.findById(seccionDTO.getEstadoId())
                                .orElseThrow(() -> new BadRequestException("El estado no es válido"));

                seccionDTO.setId(null);
                seccionDTO.setEmpresaId(userEmpresaService
                                .getEmpresaFromUser(authenticationService.getAuthenticatedUser()).getId());

                return seccionMapper.toDTO(seccionRepository.save(seccionMapper.toEntity(seccionDTO)));
        }

        public void update(Long requestedId, SeccionDTO seccionDTO) {
                Long empresaId = userEmpresaService.getEmpresaFromUser(authenticationService.getAuthenticatedUser())
                                .getId();

                seccionRepository.findByIdAndEmpresaId(requestedId, userEmpresaService
                                .getEmpresaFromUser(authenticationService.getAuthenticatedUser()).getId())
                                .orElseThrow(() -> new NotFoundException("La sección no fue encontrada."));

                espacioRepository.findByIdAndEmpresaId(seccionDTO.getEspacioId(),
                                userEmpresaService
                                                .getEmpresaFromUser(authenticationService.getAuthenticatedUser())
                                                .getId())
                                .orElseThrow(() -> new BadRequestException("El espacio no es válido"));

                estadoRepository.findById(seccionDTO.getEstadoId())
                                .orElseThrow(() -> new BadRequestException("El estado no es válido"));

                seccionDTO.setId(requestedId);
                seccionDTO.setEmpresaId(empresaId);

                seccionRepository.save(seccionMapper.toEntity(seccionDTO));
        }

        public void delete(Long id) {
                seccionRepository.findByIdAndEmpresaId(id, userEmpresaService
                                .getEmpresaFromUser(authenticationService.getAuthenticatedUser()).getId())
                                .orElseThrow(() -> new NotFoundException("La sección no fue encontrada."));

                seccionRepository.deleteById(id);
        }
}