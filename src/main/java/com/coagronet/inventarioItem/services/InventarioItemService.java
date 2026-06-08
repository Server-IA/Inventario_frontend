package com.coagronet.inventarioItem.services;

import com.coagronet.articuloKardex.ArticuloKardex;
import com.coagronet.articuloKardex.repositories.ArticuloKardexRepository;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.BadRequestException;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.inventarioItem.mappers.InventarioItemMapper;
import com.coagronet.inventarioItem.repositories.InventarioItemRepository;
import com.coagronet.inventarioItem.InventarioItem;
import com.coagronet.inventarioItem.dtos.InventarioItemDTO;
import com.coagronet.utils.UserEmpresaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InventarioItemService {

	private final InventarioItemRepository inventarioItemRepository;

	private final EstadoRepository estadoRepository;

	private final InventarioItemMapper inventarioItemMapper;

	private final UserEmpresaService userEmpresaService;

	private final ArticuloKardexRepository articuloKardexRepository;

	public Page<InventarioItemDTO> findAll(Pageable pageable) {
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
		return inventarioItemRepository.findByEmpresaIdOrderByIdAsc(empresaId, pageable)
			.map(inventarioItemMapper::toDTO);
	}

	public Optional<InventarioItemDTO> findById(Long requestedId) {

		return inventarioItemRepository
			.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.map(inventarioItemMapper::toDTO);
	}

	@Transactional
	public InventarioItemDTO create(InventarioItemDTO inventarioItemDTO) {
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
		estadoRepository.findById(inventarioItemDTO.getEstadoId())
			.orElseThrow(() -> new BadRequestException("Estado no encontrado o no válido"));

		ArticuloKardex articuloKardex = articuloKardexRepository
			.findByidentificadorProductoAndEmpresaId(inventarioItemDTO.getProductoIdentificadorId(), empresaId)
			.orElseThrow(() -> new BadRequestException("ArticuloKardex no encontrado para ese identificador"));

		inventarioItemDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

		InventarioItem inventarioItem = inventarioItemMapper.toEntity(inventarioItemDTO);
		inventarioItem.setArticuloKardex(articuloKardex);
		inventarioItem = inventarioItemRepository.save(inventarioItem);
		return inventarioItemMapper.toDTO(inventarioItem);
	}

	@Transactional
	public void update(Long requestedId, InventarioItemDTO inventarioItemDTO) {
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

		inventarioItemRepository.findByIdAndEmpresaId(requestedId, empresaId)
			.orElseThrow(() -> new NotFoundException("InventarioItem no encontrada en su empresa"));

		estadoRepository.findById(inventarioItemDTO.getEstadoId())
			.orElseThrow(() -> new BadRequestException("El estado no es válido"));

		inventarioItemDTO.setId(requestedId);
		inventarioItemDTO.setEmpresaId(empresaId);
		inventarioItemRepository.save(inventarioItemMapper.toEntity(inventarioItemDTO));
	}

	@Transactional
	public void delete(Long requestedId) {
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

		inventarioItemRepository.findByIdAndEmpresaId(requestedId, empresaId)
			.orElseThrow(() -> new NotFoundException("InventarioItem no encontrada en su empresa"));

		inventarioItemRepository.deleteById(requestedId);
	}

}
