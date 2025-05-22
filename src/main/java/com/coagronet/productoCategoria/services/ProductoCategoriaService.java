package com.coagronet.productoCategoria.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.coagronet.empresa.Empresa;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.BadRequestException;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.productoCategoria.dtos.ProductoCategoriaDTO;
import com.coagronet.productoCategoria.mappers.ProductoCategoriaMapper;
import com.coagronet.productoCategoria.repositories.ProductoCategoriaRepository;
import com.coagronet.user.User;
import com.coagronet.utils.AuthenticationService;
import com.coagronet.utils.UserEmpresaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductoCategoriaService {

	private final ProductoCategoriaMapper productoCategoriaMapper;
	private final ProductoCategoriaRepository productoCategoriaRepository;
	private final AuthenticationService authenticationService;
	private final UserEmpresaService userEmpresaService;
	private final EstadoRepository estadoRepository;

	public List<ProductoCategoriaDTO> findAll() {
		User user = authenticationService.getAuthenticatedUser();
		Empresa empresa = userEmpresaService.getEmpresaFromUser(user);

		return productoCategoriaRepository.findByEmpresaIdOrderByIdAsc(empresa.getId()).stream()
				.map(productoCategoriaMapper::toListDto).collect(Collectors.toList());
	}

	public ProductoCategoriaDTO create(ProductoCategoriaDTO productoCategoriaDTO) {
		User user = authenticationService.getAuthenticatedUser();
		Empresa empresa = userEmpresaService.getEmpresaFromUser(user);

		estadoRepository.findById(productoCategoriaDTO.getEstadoId())
				.orElseThrow(() -> new BadRequestException("El estado no es válido"));

		productoCategoriaDTO.setId(null);
		productoCategoriaDTO.setEmpresaId(empresa.getId());

		return productoCategoriaMapper
				.toDTO(productoCategoriaRepository.save(productoCategoriaMapper.toEntity(productoCategoriaDTO)));
	}

	public void update(Long requestedId, ProductoCategoriaDTO productoCategoriaDTO) {
		User user = authenticationService.getAuthenticatedUser();
		Empresa empresa = userEmpresaService.getEmpresaFromUser(user);

		productoCategoriaRepository.findByIdAndEmpresaId(requestedId, empresa.getId())
				.orElseThrow(() -> new NotFoundException("Categoria de producto no encontrada"));

		estadoRepository.findById(productoCategoriaDTO.getEstadoId())
				.orElseThrow(() -> new BadRequestException("El estado no es válido"));

		productoCategoriaDTO.setId(requestedId);
		productoCategoriaDTO.setEmpresaId(empresa.getId());

		productoCategoriaRepository.save(productoCategoriaMapper.toEntity(productoCategoriaDTO));
	}

	public void delete(Long id) {
		User user = authenticationService.getAuthenticatedUser();
		Empresa empresa = userEmpresaService.getEmpresaFromUser(user);

		productoCategoriaRepository.findByIdAndEmpresaId(id, empresa.getId())
				.orElseThrow(() -> new NotFoundException("Categoria de producto no encontrada"));

		productoCategoriaRepository.deleteById(id);
	}

}
