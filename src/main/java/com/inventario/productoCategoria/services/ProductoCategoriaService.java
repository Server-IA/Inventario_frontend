package com.inventario.productoCategoria.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.inventario.estado.repositories.EstadoRepository;
import com.inventario.exceptionHandler.NotFoundException;
import com.inventario.exceptionHandler.custom.BadRequestException;
import com.inventario.productoCategoria.dtos.ProductoCategoriaDTO;
import com.inventario.productoCategoria.mappers.ProductoCategoriaMapper;
import com.inventario.productoCategoria.repositories.ProductoCategoriaRepository;
import com.inventario.utils.UserEmpresaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductoCategoriaService {

	private final ProductoCategoriaMapper productoCategoriaMapper;

	private final ProductoCategoriaRepository productoCategoriaRepository;

	private final UserEmpresaService userEmpresaService;

	private final EstadoRepository estadoRepository;

	public List<ProductoCategoriaDTO> findAll() {
		return productoCategoriaRepository
				.findByEmpresaIdOrderByIdAsc(userEmpresaService.getEmpresaIdFromCurrentRequest())
				.stream()
				.map(productoCategoriaMapper::toListDto)
				.collect(Collectors.toList());
	}

	public ProductoCategoriaDTO create(ProductoCategoriaDTO productoCategoriaDTO) {
		estadoRepository.findById(productoCategoriaDTO.getEstadoId())
				.orElseThrow(() -> new BadRequestException("El estado no es válido"));

		productoCategoriaDTO.setId(null);
		productoCategoriaDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

		return productoCategoriaMapper
				.toDTO(productoCategoriaRepository.save(productoCategoriaMapper.toEntity(productoCategoriaDTO)));
	}

	public void update(Long requestedId, ProductoCategoriaDTO productoCategoriaDTO) {
		productoCategoriaRepository
				.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
				.orElseThrow(() -> new NotFoundException("Categoria de producto no encontrada"));

		estadoRepository.findById(productoCategoriaDTO.getEstadoId())
				.orElseThrow(() -> new BadRequestException("El estado no es válido"));

		productoCategoriaDTO.setId(requestedId);
		productoCategoriaDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

		productoCategoriaRepository.save(productoCategoriaMapper.toEntity(productoCategoriaDTO));
	}

	public void delete(Long id) {
		productoCategoriaRepository.findByIdAndEmpresaId(id, userEmpresaService.getEmpresaIdFromCurrentRequest())
				.orElseThrow(() -> new NotFoundException("Categoria de producto no encontrada"));

		productoCategoriaRepository.deleteById(id);
	}

}
