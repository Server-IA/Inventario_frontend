package com.coagronet.ordenCompra.services;

import com.coagronet.empresa.Empresa;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.BadRequestException;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.ordenCompra.dtos.OrdenCompraDTO;
import com.coagronet.ordenCompra.mappers.OrdenCompraMapper;
import com.coagronet.ordenCompra.repositories.OrdenCompraRepository;
import com.coagronet.ordenCompra.OrdenCompra;
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
public class OrdenCompraService {

    private final OrdenCompraRepository ordenCompraRepository;
    private final OrdenCompraMapper ordenCompraMapper;
    private final EstadoRepository estadoRepository;
    private final AuthenticationService authenticationService;
    private final UserEmpresaService userEmpresaService;



    public List<OrdenCompraDTO> findAll() {
        User user = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(user);
        Long empresaId = empresa.getId();
        return ordenCompraRepository.findByEmpresaIdOrderByIdAsc(empresaId)
                .stream()
                .map(ordenCompraMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<OrdenCompraDTO> findById(Long requestedId) {
        User user = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(user);
        Long empresaId = empresa.getId();

        return ordenCompraRepository.findByIdAndEmpresaId(requestedId, empresaId)
                .map(ordenCompraMapper::toDTO);
    }


    @Transactional
    public OrdenCompraDTO create(OrdenCompraDTO ordenCompraDTO) {
        User user = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(user);
        Long empresaId = empresa.getId();

        estadoRepository.findById(ordenCompraDTO.getEstadoId())
                .orElseThrow(()-> new BadRequestException("El estado no es válido"));

        ordenCompraDTO.setEmpresaId(empresaId);

        OrdenCompra ordenCompra = ordenCompraMapper.toEntity(ordenCompraDTO);
        ordenCompra = ordenCompraRepository.save(ordenCompra);
        return ordenCompraMapper.toDTO(ordenCompra);
    }

    @Transactional
    public void update(Long requestedId, OrdenCompraDTO ordenCompraDTO) {
        User user = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(user);
        Long empresaId = empresa.getId();

        ordenCompraRepository.findByIdAndEmpresaId(requestedId, empresaId)
                .orElseThrow(()-> new NotFoundException("OrdenCompra no encontrada o no válida"));

        estadoRepository.findById(ordenCompraDTO.getEstadoId())
                .orElseThrow(()-> new BadRequestException("El estado no es válido"));

        ordenCompraDTO.setId(requestedId);
        ordenCompraDTO.setEmpresaId(empresaId);
        ordenCompraRepository.save(ordenCompraMapper.toEntity(ordenCompraDTO));
    }

    @Transactional
    public void delete(Long requestId) {
        User user = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(user);
        Long empresaId = empresa.getId();
        ordenCompraRepository.findByIdAndEmpresaId(requestId, empresaId)
                .orElseThrow(()-> new NotFoundException("OrdenCompra no encontrado o no válido"));

        ordenCompraRepository.deleteById(requestId);
    }
}
