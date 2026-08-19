package com.inventario.producto.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inventario.empresa.Empresa;
import com.inventario.empresa.repositories.EmpresaRepository;
import com.inventario.estado.Estado;
import com.inventario.exceptionHandler.NotFoundException;
import com.inventario.exceptionHandler.custom.BadRequestException;
import com.inventario.producto.Producto;
import com.inventario.producto.dtos.ProductoRequestDTO;
import com.inventario.producto.dtos.ProductoResponseDTO;
import com.inventario.producto.mappers.ProductoMapper;
import com.inventario.producto.repositories.ProductoRepository;
import com.inventario.productoCategoria.ProductoCategoria;
import com.inventario.productoCategoria.repositories.ProductoCategoriaRepository;
import com.inventario.unidad.Unidad;
import com.inventario.unidad.repositories.UnidadRepository;
import com.inventario.utils.Constantes;
import com.inventario.utils.UserEmpresaService;
import com.inventario.validator.EntidadValidatorFacade;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductoService {

	private final ProductoRepository productoRepository;

	private final ProductoMapper productoMapper;

	private final UserEmpresaService userEmpresaService;

	private final EntidadValidatorFacade entidadValidatorFacade;

	private final EmpresaRepository empresaRepository;

	private final UnidadRepository unidadRepository;

	private final ProductoCategoriaRepository productoCategoriaRepository;

	public Page<ProductoResponseDTO> findAllByFilters(Pageable pageable, Long estadoId, Long categoriaId) {
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
		return productoRepository.findByFilters(empresaId, estadoId, categoriaId, pageable)
				.map(productoMapper::toDto);
	}

	public ProductoResponseDTO findById(Long requestedId) {
		return productoRepository.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
				.map(productoMapper::toDto).orElseThrow(() -> new NotFoundException("producto.not-found", requestedId));
	}

	@Transactional
	public ProductoResponseDTO create(ProductoRequestDTO productoRequestDTO) {
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

		ProductoCategoria productoCategoria = productoCategoriaRepository
				.findByIdAndEstadoIdAndEmpresaId(productoRequestDTO.productoCategoriaId(), Constantes.ESTADO_ACTIVO,
						empresaId)
				.orElseThrow(() -> new BadRequestException("producto-categoria.not-valid"));
		Estado estado = entidadValidatorFacade.validarEstadoGeneral(productoRequestDTO.estadoId());
		Empresa empresa = empresaRepository.findById(empresaId).orElseThrow();
		Unidad unidad = unidadRepository
				.findByIdAndEstadoId(productoRequestDTO.unidadMinimaId(), Constantes.ESTADO_ACTIVO)
				.orElseThrow(() -> new BadRequestException("unidad.not-valid"));

		Producto productoWithTenant = productoMapper.toEntity(productoRequestDTO);
		productoWithTenant.setProductoCategoria(productoCategoria);
		productoWithTenant.setEstado(estado);
		productoWithTenant.setEmpresa(empresa);
		productoWithTenant.setUnidadMinima(unidad);

		Producto saveProducto = productoRepository.save(productoWithTenant);

		return productoMapper.toDto(saveProducto);

	}

	@Transactional
	public void update(Long requestedId, ProductoRequestDTO productoRequestDTO) {
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

		productoRepository.findByIdAndEmpresaId(requestedId, empresaId)
				.orElseThrow(() -> new NotFoundException("producto.not-found", requestedId));

		ProductoCategoria productoCategoria = productoCategoriaRepository
				.findByIdAndEstadoIdAndEmpresaId(productoRequestDTO.productoCategoriaId(), Constantes.ESTADO_ACTIVO,
						empresaId)
				.orElseThrow(() -> new BadRequestException("producto-categoria.not-valid"));
		Estado estado = entidadValidatorFacade.validarEstadoGeneral(productoRequestDTO.estadoId());
		Empresa empresa = empresaRepository.findById(empresaId).orElseThrow();
		Unidad unidad = unidadRepository
				.findByIdAndEstadoId(productoRequestDTO.unidadMinimaId(), Constantes.ESTADO_ACTIVO)
				.orElseThrow(() -> new BadRequestException("unidad.not-valid"));

		Producto productoWithTenant = productoMapper.toEntity(productoRequestDTO);
		productoWithTenant.setId(requestedId);
		productoWithTenant.setProductoCategoria(productoCategoria);
		productoWithTenant.setEstado(estado);
		productoWithTenant.setEmpresa(empresa);
		productoWithTenant.setUnidadMinima(unidad);

		productoRepository.save(productoWithTenant);
	}

	@Transactional
	public void delete(Long id) {
		Producto producto = productoRepository
				.findByIdAndEmpresaId(id, userEmpresaService.getEmpresaIdFromCurrentRequest())
				.orElseThrow(() -> new NotFoundException("producto.not-found", id));
		producto.setEstado(Estado.builder().id(Constantes.ESTADO_INACTIVO).build());
		productoRepository.save(producto);
	}

}
