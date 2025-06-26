package com.coagronet.articuloOrdenCompra.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.coagronet.articuloOrdenCompra.dtos.ArticuloOrdenCompraDTO;
import com.coagronet.articuloOrdenCompra.mappers.ArticuloOrdenCompraMapper;
import com.coagronet.articuloOrdenCompra.repositories.ArticuloOrdenCompraRepository;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.BadRequestException;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.ordenCompra.repositories.OrdenCompraRepository;
import com.coagronet.productoPresentacion.repositories.ProductoPresentacionRepository;
import com.coagronet.utils.UserEmpresaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ArticuloOrdenCompraService {

        private final UserEmpresaService userEmpresaService;
        private final ArticuloOrdenCompraMapper articuloOrdenCompraMapper;
        private final ArticuloOrdenCompraRepository articuloOrdenCompraRepository;
        private final OrdenCompraRepository ordenCompraRepository;
        private final ProductoPresentacionRepository productoPresentacionRepository;
        private final EstadoRepository estadoRepository;

        public List<ArticuloOrdenCompraDTO> findAll() {
                return articuloOrdenCompraRepository
                                .findByEmpresaIdOrderByIdAsc(
                                                userEmpresaService.getEmpresaIdFromCurrentRequest())
                                .stream().map(articuloOrdenCompraMapper::toListDTO).collect(Collectors.toList());
        }

        public Optional<ArticuloOrdenCompraDTO> findById(Long requestedId) {
                return articuloOrdenCompraRepository
                                .findByIdAndEmpresaId(requestedId,
                                                userEmpresaService.getEmpresaIdFromCurrentRequest())
                                .map(articuloOrdenCompraMapper::toListDTO);
        }

        public ArticuloOrdenCompraDTO create(ArticuloOrdenCompraDTO articuloOrdenCompraDTO) {
                ordenCompraRepository
                                .findByIdAndEmpresaId(articuloOrdenCompraDTO.getOrdenCompraId(),
                                                userEmpresaService.getEmpresaIdFromCurrentRequest())
                                .orElseThrow(() -> new BadRequestException("La orden de compra no es válida."));

                productoPresentacionRepository.findByIdAndEmpresaId(articuloOrdenCompraDTO.getProductoPresentacionId(),
                                userEmpresaService.getEmpresaIdFromCurrentRequest())
                                .orElseThrow(() -> new BadRequestException(
                                                "La presentación de producto no es válida."));

                estadoRepository.findById(articuloOrdenCompraDTO.getEstadoId())
                                .orElseThrow(() -> new BadRequestException("El estado no es válido."));

                articuloOrdenCompraDTO.setId(null);
                articuloOrdenCompraDTO.setEmpresaId(
                                userEmpresaService.getEmpresaIdFromCurrentRequest());

                return articuloOrdenCompraMapper
                                .toDTO(articuloOrdenCompraRepository
                                                .save(articuloOrdenCompraMapper.toEntity(articuloOrdenCompraDTO)));
        }

        public void update(Long requestedId, ArticuloOrdenCompraDTO articuloOrdenCompraDTO) {
                articuloOrdenCompraRepository
                                .findByIdAndEmpresaId(requestedId,
                                                userEmpresaService.getEmpresaIdFromCurrentRequest())
                                .orElseThrow(() -> new NotFoundException(
                                                "El artículo de la orden de compra no fue encontrado."));

                ordenCompraRepository
                                .findByIdAndEmpresaId(articuloOrdenCompraDTO.getOrdenCompraId(),
                                                userEmpresaService.getEmpresaIdFromCurrentRequest())
                                .orElseThrow(() -> new BadRequestException("La orden de compra no es válida."));

                productoPresentacionRepository.findByIdAndEmpresaId(articuloOrdenCompraDTO.getProductoPresentacionId(),
                                userEmpresaService.getEmpresaIdFromCurrentRequest())
                                .orElseThrow(() -> new BadRequestException(
                                                "La presentación de producto no es válida."));

                estadoRepository.findById(articuloOrdenCompraDTO.getEstadoId())
                                .orElseThrow(() -> new BadRequestException("El estado no es válido."));

                articuloOrdenCompraDTO.setId(requestedId);
                articuloOrdenCompraDTO.setEmpresaId(
                                userEmpresaService.getEmpresaIdFromCurrentRequest());

                articuloOrdenCompraRepository.save(articuloOrdenCompraMapper.toEntity(articuloOrdenCompraDTO));
        }

        public void delete(Long id) {
                articuloOrdenCompraRepository
                                .findByIdAndEmpresaId(id,
                                                userEmpresaService.getEmpresaIdFromCurrentRequest())
                                .orElseThrow(() -> new NotFoundException(
                                                "El artículo de la orden de compra no fue encontrado."));

                articuloOrdenCompraRepository.deleteById(id);
        }

}
