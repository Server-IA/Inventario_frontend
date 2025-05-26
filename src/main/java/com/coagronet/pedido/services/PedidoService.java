package com.coagronet.pedido.services;

import com.coagronet.empresa.Empresa;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.pedido.Pedido;
import com.coagronet.pedido.dtos.PedidoDTO;
import com.coagronet.pedido.mappers.PedidoMapper;
import com.coagronet.pedido.repositories.PedidoRepository;
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
public class PedidoService {


    private final PedidoRepository pedidoRepository;
    private final PedidoMapper pedidoMapper;
    private final UserEmpresaService userEmpresaService;
    private final AuthenticationService authenticationService;
    private final EstadoRepository estadoRepository;



    public List<PedidoDTO> findAll(){
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);
        Long empresaId = empresa.getId();

        return pedidoRepository.findByEmpresaIdOrderByIdAsc(empresaId)
                .stream()
                .map(pedidoMapper::toDto)
                .collect(Collectors.toList());
    }


    public Optional<PedidoDTO> findById(Long requestId){
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);
        Long empresaId = empresa.getId();

        return pedidoRepository.findByIdAndEmpresaId(requestId, empresaId)
                .map(pedidoMapper::toDto);
    }


    @Transactional
    public PedidoDTO create(PedidoDTO pedidoDTO){
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);
        Long empresaId = empresa.getId();

        estadoRepository.findById(pedidoDTO.getEstadoId())
                .orElseThrow(()-> new NotFoundException("Estado no encontrado o no válido"));

        pedidoDTO.setEmpresaId(empresaId);
        Pedido pedido = pedidoMapper.toEntity(pedidoDTO);
        pedido = pedidoRepository.save(pedido);
        return pedidoMapper.toDto(pedido);
    }

    @Transactional
    public void update(Long requestId, PedidoDTO pedidoDTO){
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);
        Long empresaId = empresa.getId();

        pedidoRepository.findByIdAndEmpresaId(requestId, empresaId)
                .orElseThrow(()-> new NotFoundException("Pedido no encontrado o no válido"));

        estadoRepository.findById(pedidoDTO.getEstadoId())
                .orElseThrow(()-> new NotFoundException("Estado no encontrado o no válido"));


        pedidoDTO.setId(requestId);
        pedidoDTO.setEmpresaId(empresaId);
        pedidoRepository.save(pedidoMapper.toEntity(pedidoDTO));
    }

    @Transactional
    public void delete(Long requestId){
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(authenticatedUser);
        Long empresaId = empresa.getId();

        pedidoRepository.findByIdAndEmpresaId(requestId, empresaId)
                .orElseThrow(()-> new NotFoundException("Pedido no encontrado o no válido"));

        pedidoRepository.deleteById(requestId);

    }


}
