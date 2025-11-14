package com.coagronet.ingredientePresentacionProducto.services;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coagronet.empresa.Empresa;
import com.coagronet.estado.Estado;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.BadRequestException;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.ingrediente.repositories.IngredienteRepository;
import com.coagronet.ingredientePresentacionProducto.IngredientePresentacionProducto;
import com.coagronet.ingredientePresentacionProducto.dtos.IngredientePresentacionProductoRequestDTO;
import com.coagronet.ingredientePresentacionProducto.dtos.IngredientePresentacionProductoResponseDTO;
import com.coagronet.ingredientePresentacionProducto.mappers.IngredientePresentacionProductoMapper;
import com.coagronet.ingredientePresentacionProducto.repositories.IngredientePresentacionProductoRepository;
import com.coagronet.presentacionProducto.repositories.PresentacionProductoRepository;
import com.coagronet.unidad.repositories.UnidadRepository;
import com.coagronet.utils.Constantes;
import com.coagronet.utils.UserEmpresaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IngredientePresentacionProductoService {

	private final UserEmpresaService userEmpresaService;

	private final IngredientePresentacionProductoMapper ingredientePresentacionProductoMapper;

	private final IngredientePresentacionProductoRepository ingredientePresentacionProductoRepository;

	private final IngredienteRepository ingredienteRepository;

	private final PresentacionProductoRepository presentacionProductoRepository;

	private final EstadoRepository estadoRepository;

	private final UnidadRepository unidadRepository;

	public Page<IngredientePresentacionProductoResponseDTO> listarPorEmpresa(Pageable pageable) {
		return ingredientePresentacionProductoMapper.toDto(ingredientePresentacionProductoRepository
				.findByEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest(), pageable));
	}

	public Optional<IngredientePresentacionProductoResponseDTO> findById(Long requestedId) {
		return ingredientePresentacionProductoRepository
				.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
				.map(ingredientePresentacionProductoMapper::toDto);
	}

	@Transactional
	public IngredientePresentacionProducto create(
			IngredientePresentacionProductoRequestDTO ingredientePresentacionProductoRequestDTO) {
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

		ingredienteRepository
				.findByIdAndEmpresaId(ingredientePresentacionProductoRequestDTO.ingredienteId(),
						empresaId)
				.orElseThrow(() -> new BadRequestException("ingrediente.not-valid"));

		presentacionProductoRepository
				.findByIdAndEmpresaId(ingredientePresentacionProductoRequestDTO.presentacionProductoId(),
						empresaId)
				.orElseThrow(() -> new BadRequestException("presentacion-producto.not-valid"));

		estadoRepository.findById(ingredientePresentacionProductoRequestDTO.estadoId())
				.orElseThrow(() -> new BadRequestException("estado.not-valid"));

		unidadRepository.findById(ingredientePresentacionProductoRequestDTO.unidadId())
				.orElseThrow(() -> new BadRequestException("unidad.not-valid"));

		IngredientePresentacionProducto savedIngredientePresentacionProducto = ingredientePresentacionProductoMapper
				.toEntity(ingredientePresentacionProductoRequestDTO);

		savedIngredientePresentacionProducto.setEmpresa(Empresa.builder().id(empresaId).build());

		return ingredientePresentacionProductoRepository.save(savedIngredientePresentacionProducto);
	}

	@Transactional
	public void update(Long requestedId,
			IngredientePresentacionProductoRequestDTO ingredientePresentacionProductoRequestDTO) {
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

		ingredienteRepository
				.findByIdAndEmpresaId(ingredientePresentacionProductoRequestDTO.ingredienteId(),
						empresaId)
				.orElseThrow(() -> new BadRequestException("ingrediente.not-valid"));

		presentacionProductoRepository
				.findByIdAndEmpresaId(ingredientePresentacionProductoRequestDTO.presentacionProductoId(),
						empresaId)
				.orElseThrow(() -> new BadRequestException("presentacion-producto.not-valid"));

		estadoRepository.findById(ingredientePresentacionProductoRequestDTO.estadoId())
				.orElseThrow(() -> new BadRequestException("estado.not-valid"));

		unidadRepository.findById(ingredientePresentacionProductoRequestDTO.unidadId())
				.orElseThrow(() -> new BadRequestException("unidad.not-valid"));

		IngredientePresentacionProducto savedIngredientePresentacionProducto = ingredientePresentacionProductoMapper
				.toEntity(ingredientePresentacionProductoRequestDTO);

		savedIngredientePresentacionProducto.setEmpresa(Empresa.builder().id(empresaId).build());

		ingredientePresentacionProductoRepository
				.save(savedIngredientePresentacionProducto);
	}

	@Transactional
	public void delete(Long id) {
		IngredientePresentacionProducto entity = ingredientePresentacionProductoRepository
				.findByIdAndEmpresaId(id, userEmpresaService.getEmpresaIdFromCurrentRequest())
				.orElseThrow(() -> new NotFoundException("ingrediente-presentacion-producto.not-found", id));

		entity.setEstado(Estado.builder().id(Constantes.ESTADO_INACTIVO).build());

		ingredientePresentacionProductoRepository.save(entity);
	}

}
