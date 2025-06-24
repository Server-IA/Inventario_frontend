package com.coagronet.articuloPedido.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.coagronet.articuloPedido.dtos.ArticuloPedidoDTO;
import com.coagronet.articuloPedido.mappers.ArticuloPedidoMapper;
import com.coagronet.articuloPedido.repositories.ArticuloPedidoRepository;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.BadRequestException;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.pedido.repositories.PedidoRepository;
import com.coagronet.productoPresentacion.repositories.ProductoPresentacionRepository;
import com.coagronet.utils.AuthenticationService;
import com.coagronet.utils.UserEmpresaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ArticuloPedidoService {

        private final AuthenticationService authenticationService;
        private final UserEmpresaService userEmpresaService;
        private final ArticuloPedidoMapper articuloPedidoMapper;
        private final ArticuloPedidoRepository articuloPedidoRepository;
        private final PedidoRepository pedidoRepository;
        private final ProductoPresentacionRepository productoPresentacionRepository;
        private final EstadoRepository estadoRepository;

        public List<ArticuloPedidoDTO> findAll() {
                return articuloPedidoRepository
                                .findByEmpresaIdOrderByIdAsc(
                                                (userEmpresaService.getEmpresaFromUser(
                                                                authenticationService.getAuthenticatedUser())).getId())
                                .stream().map(articuloPedidoMapper::toListDTO).collect(Collectors.toList());
        }

        public List<ArticuloPedidoDTO> findAllByPedidoId(Long pedidoId) {
                return articuloPedidoRepository
                                .findByEmpresaIdAndPedidoIdOrderByIdAsc(
                                                (userEmpresaService.getEmpresaFromUser(
                                                                authenticationService.getAuthenticatedUser())).getId(),
                                                pedidoId)
                                .stream().map(articuloPedidoMapper::toListDTO).collect(Collectors.toList());
        }

        public Optional<ArticuloPedidoDTO> findById(Long requestedId) {
                return articuloPedidoRepository
                                .findByIdAndEmpresaId(requestedId,
                                                (userEmpresaService.getEmpresaFromUser(
                                                                authenticationService.getAuthenticatedUser())).getId())
                                .map(articuloPedidoMapper::toListDTO);
        }

        public ArticuloPedidoDTO create(ArticuloPedidoDTO articuloPedidoDTO) {
                pedidoRepository.findById(articuloPedidoDTO.getPedidoId())
                                .orElseThrow(() -> new BadRequestException("El pedido no es válido."));

                productoPresentacionRepository.findById(articuloPedidoDTO.getProductoPresentacionId())
                                .orElseThrow(() -> new BadRequestException(
                                                "La presentación de producto no es válida."));

                estadoRepository.findById(articuloPedidoDTO.getEstadoId())
                                .orElseThrow(() -> new BadRequestException("El estado no es válido."));

                articuloPedidoDTO.setId(null);
                articuloPedidoDTO.setEmpresaId(
                                (userEmpresaService.getEmpresaFromUser(authenticationService.getAuthenticatedUser()))
                                                .getId());

                return articuloPedidoMapper
                                .toDTO(articuloPedidoRepository.save(articuloPedidoMapper.toEntity(articuloPedidoDTO)));
        }

        public void update(Long requestedId, ArticuloPedidoDTO articuloPedidoDTO) {
                articuloPedidoRepository
                                .findByIdAndEmpresaId(requestedId,
                                                (userEmpresaService.getEmpresaFromUser(
                                                                authenticationService.getAuthenticatedUser())).getId())
                                .orElseThrow(() -> new NotFoundException("El artículo de pedido no fue encontrado."));

                pedidoRepository.findById(articuloPedidoDTO.getPedidoId())
                                .orElseThrow(() -> new BadRequestException("El pedido no es válido."));

                productoPresentacionRepository.findById(articuloPedidoDTO.getProductoPresentacionId())
                                .orElseThrow(() -> new BadRequestException(
                                                "La presentación de producto no es válida."));

                estadoRepository.findById(articuloPedidoDTO.getEstadoId())
                                .orElseThrow(() -> new BadRequestException("El estado no es válido."));

                articuloPedidoDTO.setId(requestedId);
                articuloPedidoDTO.setEmpresaId(
                                (userEmpresaService.getEmpresaFromUser(authenticationService.getAuthenticatedUser()))
                                                .getId());

                articuloPedidoRepository.save(articuloPedidoMapper.toEntity(articuloPedidoDTO));
        }

        public void delete(Long id) {
                articuloPedidoRepository
                                .findByIdAndEmpresaId(id,
                                                (userEmpresaService.getEmpresaFromUser(
                                                                authenticationService.getAuthenticatedUser())).getId())
                                .orElseThrow(() -> new NotFoundException("El artículo de pedido no fue encontrado."));

                articuloPedidoRepository.deleteById(id);
        }

}
