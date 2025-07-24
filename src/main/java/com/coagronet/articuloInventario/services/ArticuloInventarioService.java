package com.coagronet.articuloInventario.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.coagronet.articuloInventario.dtos.ArticuloInventarioDTO;
import com.coagronet.articuloInventario.mappers.ArticuloInventarioMapper;
import com.coagronet.articuloInventario.repositories.ArticuloInventarioRepository;
import com.coagronet.articuloKardex.repositories.ArticuloKardexRepository;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.BadRequestException;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.inventario.repositories.InventarioRepository;
import com.coagronet.utils.UserEmpresaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ArticuloInventarioService {

	private final UserEmpresaService userEmpresaService;

	private final ArticuloInventarioMapper articuloInventarioMapper;

	private final ArticuloInventarioRepository articuloInventarioRepository;

	private final InventarioRepository inventarioRepository;

	private final ArticuloKardexRepository articuloKardexRepository;

	private final EstadoRepository estadoRepository;

	public List<ArticuloInventarioDTO> findAll() {
		return articuloInventarioRepository
			.findByEmpresaIdOrderByIdAsc(userEmpresaService.getEmpresaIdFromCurrentRequest())
			.stream()
			.map(articuloInventarioMapper::toDTO)
			.collect(Collectors.toList());
	}

	public Optional<ArticuloInventarioDTO> findById(Long requestedId) {
		return articuloInventarioRepository
			.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.map(articuloInventarioMapper::toDTO);
	}

	public ArticuloInventarioDTO create(ArticuloInventarioDTO articuloInventarioDTO) {
		inventarioRepository
			.findByIdAndEmpresaId(articuloInventarioDTO.getInventarioId(),
					userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new BadRequestException("El campo inventarioId no es válido."));

		articuloKardexRepository
			.findByidentificadorProductoAndEmpresaId(articuloInventarioDTO.getIdentificadorProducto(),
					userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new BadRequestException("El campo identificadorProducto no es válido."));

		estadoRepository.findById(articuloInventarioDTO.getEstadoId())
			.orElseThrow(() -> new BadRequestException("El campo estadoId no es válido."));

		articuloInventarioDTO.setId(null);
		articuloInventarioDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

		return articuloInventarioMapper
			.toDTO(articuloInventarioRepository.save(articuloInventarioMapper.toEntity(articuloInventarioDTO)));
	}

	public void update(Long requestedId, ArticuloInventarioDTO articuloInventarioDTO) {
		articuloInventarioRepository
			.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

		inventarioRepository
			.findByIdAndEmpresaId(articuloInventarioDTO.getInventarioId(),
					userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new BadRequestException("El campo inventarioId no es válido."));

		articuloKardexRepository
			.findByidentificadorProductoAndEmpresaId(articuloInventarioDTO.getIdentificadorProducto(),
					userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new BadRequestException("El campo identificadorProducto no es válido."));

		estadoRepository.findById(articuloInventarioDTO.getEstadoId())
			.orElseThrow(() -> new BadRequestException("El campo estadoId no es válido."));

		articuloInventarioDTO.setId(requestedId);
		articuloInventarioDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

		articuloInventarioRepository.save(articuloInventarioMapper.toEntity(articuloInventarioDTO));
	}

	public void delete(Long id) {
		articuloInventarioRepository.findByIdAndEmpresaId(id, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new NotFoundException("El artículo de inventario no fue encontrado."));

		articuloInventarioRepository.deleteById(id);
	}

}
