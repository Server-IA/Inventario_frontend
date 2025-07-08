package com.coagronet.articuloKardex.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.BadRequestException;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.kardex.repositories.KardexRepository;
import com.coagronet.productoPresentacion.repositories.ProductoPresentacionRepository;
import com.coagronet.articuloKardex.dtos.ArticuloKardexDTO;
import com.coagronet.articuloKardex.mappers.ArticuloKardexMapper;
import com.coagronet.articuloKardex.repositories.ArticuloKardexRepository;
import com.coagronet.utils.UserEmpresaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ArticuloKardexService {

	private final UserEmpresaService userEmpresaService;
	private final ArticuloKardexMapper articuloKardexMapper;
	private final ArticuloKardexRepository articuloKardexRepository;
	private final KardexRepository kardexRepository;
	private final ProductoPresentacionRepository productoPresentacionRepository;
	private final EstadoRepository estadoRepository;

	public List<ArticuloKardexDTO> findAll() {
		return articuloKardexRepository
				.findByEmpresaIdOrderByIdAsc(
						userEmpresaService.getEmpresaIdFromCurrentRequest())
				.stream().map(articuloKardexMapper::toListDTO).collect(Collectors.toList());
	}

	public List<ArticuloKardexDTO> findAllByKardexId(Long kardexId) {
		return articuloKardexRepository
				.findByEmpresaIdAndKardexIdOrderByIdAsc(
						userEmpresaService.getEmpresaIdFromCurrentRequest(), kardexId)
				.stream().map(articuloKardexMapper::toListDTO).collect(Collectors.toList());
	}

	public Optional<ArticuloKardexDTO> findById(Long requestedId) {
		return articuloKardexRepository
				.findByIdAndEmpresaId(requestedId,
						userEmpresaService.getEmpresaIdFromCurrentRequest())
				.map(articuloKardexMapper::toListDTO);
	}

	public ArticuloKardexDTO create(ArticuloKardexDTO articuloKardexDTO) {
		kardexRepository
				.findByIdAndEmpresaId(articuloKardexDTO.getKardexId(),
						userEmpresaService.getEmpresaIdFromCurrentRequest())
				.orElseThrow(() -> new BadRequestException("El kardex no es válido."));

		productoPresentacionRepository
				.findByIdAndEmpresaId(articuloKardexDTO.getProductoPresentacionId(),
						userEmpresaService.getEmpresaIdFromCurrentRequest())
				.orElseThrow(() -> new BadRequestException("La presentación de producto no es válida."));

		estadoRepository.findById(articuloKardexDTO.getEstadoId())
				.orElseThrow(() -> new BadRequestException("El estado no es válido."));

		articuloKardexDTO.setId(null);
		articuloKardexDTO.setEmpresaId(
				userEmpresaService.getEmpresaIdFromCurrentRequest());

		return articuloKardexMapper
				.toDTO(articuloKardexRepository.save(articuloKardexMapper.toEntity(articuloKardexDTO)));
	}

	public void update(Long requestedId, ArticuloKardexDTO articuloKardexDTO) {
		articuloKardexRepository
				.findByIdAndEmpresaId(requestedId,
						userEmpresaService.getEmpresaIdFromCurrentRequest())
				.orElseThrow(() -> new NotFoundException("El artículo de kardex no fue encontrado."));

		kardexRepository
				.findByIdAndEmpresaId(articuloKardexDTO.getKardexId(),
						userEmpresaService.getEmpresaIdFromCurrentRequest())
				.orElseThrow(() -> new BadRequestException("El kardex no es válido."));

		productoPresentacionRepository
				.findByIdAndEmpresaId(articuloKardexDTO.getProductoPresentacionId(),
						userEmpresaService.getEmpresaIdFromCurrentRequest())
				.orElseThrow(() -> new BadRequestException("La presentación de producto no es válida."));

		estadoRepository.findById(articuloKardexDTO.getEstadoId())
				.orElseThrow(() -> new BadRequestException("El estado no es válido."));

		articuloKardexDTO.setId(requestedId);
		articuloKardexDTO.setEmpresaId(
				userEmpresaService.getEmpresaIdFromCurrentRequest());

		articuloKardexRepository.save(articuloKardexMapper.toEntity(articuloKardexDTO));
	}

	public void delete(Long id) {
		articuloKardexRepository
				.findByIdAndEmpresaId(id,
						userEmpresaService.getEmpresaIdFromCurrentRequest())
				.orElseThrow(() -> new NotFoundException("El artículo de kardex no fue encontrado."));

		articuloKardexRepository.deleteById(id);
	}

}
