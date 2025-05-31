package com.coagronet.kardex.services;

import com.coagronet.empresa.Empresa;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.BadRequestException;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.kardex.mappers.KardexMapper;
import com.coagronet.kardex.repositories.KardexRepository;
import com.coagronet.kardex.Kardex;
import com.coagronet.kardex.dtos.KardexDTO;
import com.coagronet.user.User;
import com.coagronet.utils.AuthenticationService;
import com.coagronet.utils.UserEmpresaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KardexService {

    private final KardexRepository kardexRepository;
    private final KardexMapper kardexMapper;
    private final EstadoRepository estadoRepository;
    private final UserEmpresaService userEmpresaService;
    private final AuthenticationService authenticationService;


    public List<KardexDTO> findAll() {
        User user = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(user);
        Long empresaId = empresa.getId();
        return kardexRepository.findByEmpresaIdOrderByIdAsc(empresaId)
                .stream()
                .map(kardexMapper::toDto)
                .collect(Collectors.toList());
    }

    public Optional<KardexDTO> findById(Long requestedId) {
        User user = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(user);
        Long empresaId = empresa.getId();

        return kardexRepository.findByIdAndEmpresaId(requestedId, empresaId)
                .map(kardexMapper::toDto);
    }


    @Transactional
    public KardexDTO create(KardexDTO kardexDTO) {
        User user = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(user);
        Long empresaId = empresa.getId();

        estadoRepository.findById(kardexDTO.getEstadoId())
                .orElseThrow(()-> new BadRequestException("El estado no es válido"));

        kardexDTO.setEmpresaId(empresaId);

        Kardex kardex = kardexMapper.toEntity(kardexDTO);
        kardex = kardexRepository.save(kardex);
        return kardexMapper.toDto(kardex);
    }

    @Transactional
    public void update(Long requestedId, KardexDTO kardexDTO) {
        User user = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(user);
        Long empresaId = empresa.getId();

        kardexRepository.findByIdAndEmpresaId(requestedId, empresaId)
                .orElseThrow(()-> new NotFoundException("Kardex no encontrada o no válida"));

        estadoRepository.findById(kardexDTO.getEstadoId())
                .orElseThrow(()-> new BadRequestException("El estado no es válido"));

        kardexDTO.setId(requestedId);
        kardexDTO.setEmpresaId(empresaId);
        kardexRepository.save(kardexMapper.toEntity(kardexDTO));
    }

    @Transactional
    public void delete(Long requestId) {
        User user = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(user);
        Long empresaId = empresa.getId();
        kardexRepository.findByIdAndEmpresaId(requestId, empresaId)
                .orElseThrow(()-> new NotFoundException("Kardex no encontrado o no válido"));

        kardexRepository.deleteById(requestId);
    }

}
