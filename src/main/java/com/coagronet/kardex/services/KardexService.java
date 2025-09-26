package com.coagronet.kardex.services;

import com.coagronet.almacen.Almacen;
import com.coagronet.almacen.repositories.AlmacenRepository;
import com.coagronet.empresa.Empresa;
import com.coagronet.empresa.repositories.EmpresaRepository;
import com.coagronet.estado.Estado;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.BadRequestException;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.kardex.Kardex;
import com.coagronet.kardex.mappers.KardexMapper;
import com.coagronet.kardex.repositories.KardexRepository;
import com.coagronet.kardex.dtos.KardexDTO;
import com.coagronet.produccion.Produccion;
import com.coagronet.produccion.repositories.ProduccionRepository;
import com.coagronet.tipoMovimiento.TipoMovimiento;
import com.coagronet.tipoMovimiento.repositories.TipoMovimientoRepository;
import com.coagronet.utils.UserEmpresaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KardexService {

	private final KardexRepository kardexRepository;
	private final KardexMapper kardexMapper;
	private final EstadoRepository estadoRepository;
	private final UserEmpresaService userEmpresaService;
	private final EmpresaRepository empresaRepository;
	private final AlmacenRepository almacenRepository;
	private final ProduccionRepository produccionRepository;
	private final TipoMovimientoRepository tipoMovimientoRepository;

	public Page<KardexDTO> findAll(Pageable pageable) {
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
		return kardexRepository.findByEmpresaIdOrderByIdAsc(empresaId, pageable)
			.map(kardexMapper::toDto);
	}

	public Optional<KardexDTO> findById(Long requestedId) {
		return kardexRepository.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.map(kardexMapper::toDto);
	}

	@Transactional
	public KardexDTO create(KardexDTO kardexDTO) {
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

		kardexDTO.setEmpresaId(empresaId);

		Estado estado = estadoRepository.findById(kardexDTO.getEstadoId())
				.orElseThrow(()-> new BadRequestException("El estado no es válido"));

		Empresa empresa = empresaRepository.findById(empresaId)
				.orElseThrow(() -> new BadRequestException("Empresa no encontrada"));

		Almacen almacen = almacenRepository.findByIdAndEmpresaId(kardexDTO.getAlmacenId(), empresaId)
				.orElseThrow(()-> new BadRequestException("El almacen no es válido para esta empresa"));

		Produccion produccion = produccionRepository.findByIdAndEmpresaId(kardexDTO.getProduccionId(), empresaId)
				.orElseThrow(()-> new BadRequestException("La produccion no es válida para esta empresa"));

		TipoMovimiento tipoMovimiento = tipoMovimientoRepository.findByIdAndEmpresaId(kardexDTO.getTipoMovimientoId(), empresaId)
				.orElseThrow(()-> new BadRequestException("El tipo de movimiento no es válido para esta empresa"));


		Kardex kardex = kardexMapper.toEntity(kardexDTO);
		kardex.setEmpresa(empresa);
		kardex.setEstado(estado);
		kardex.setAlmacen(almacen);
		kardex.setProduccion(produccion);
		kardex.setTipoMovimiento(tipoMovimiento);

		if (kardexDTO.getClienteProveedorId() != null) {
			Empresa clienteProveedor = empresaRepository.findById(kardexDTO.getClienteProveedorId())
					.orElseThrow(() -> new BadRequestException("Cliente/Proveedor no encontrado"));
			kardex.setClienteProveedor(clienteProveedor);
		}
		Kardex guardado = kardexRepository.save(kardex);
		return kardexMapper.toDto(guardado);
	}

	@Transactional
	public KardexDTO update(Long requestedId, KardexDTO kardexDTO) {
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

		Kardex existente = kardexRepository.findByIdAndEmpresaId(requestedId, empresaId)
				.orElseThrow(() -> new NotFoundException("Kardex no encontrada o no válida"));

		Estado estado = estadoRepository.findById(kardexDTO.getEstadoId())
				.orElseThrow(() -> new BadRequestException("El estado no es válido"));

		Empresa empresa = empresaRepository.findById(empresaId)
				.orElseThrow(() -> new BadRequestException("Empresa no encontrada"));

		Almacen almacen = almacenRepository.findByIdAndEmpresaId(kardexDTO.getAlmacenId(), empresaId)
				.orElseThrow(() -> new BadRequestException("El almacén no es válido para esta empresa"));

		Produccion produccion = produccionRepository.findByIdAndEmpresaId(kardexDTO.getProduccionId(), empresaId)
				.orElseThrow(() -> new BadRequestException("La producción no es válida para esta empresa"));

		TipoMovimiento tipoMovimiento = tipoMovimientoRepository.findByIdAndEmpresaId(kardexDTO.getTipoMovimientoId(), empresaId)
				.orElseThrow(() -> new BadRequestException("El tipo de movimiento no es válido para esta empresa"));

		kardexDTO.setId(requestedId);
		kardexDTO.setEmpresaId(empresaId);

		Kardex kardex = kardexMapper.toEntity(kardexDTO);
		kardex.setEmpresa(empresa);
		kardex.setEstado(estado);
		kardex.setAlmacen(almacen);
		kardex.setProduccion(produccion);
		kardex.setTipoMovimiento(tipoMovimiento);

		if (kardexDTO.getClienteProveedorId() != null) {
			Empresa clienteProveedor = empresaRepository.findById(kardexDTO.getClienteProveedorId())
					.orElseThrow(() -> new BadRequestException("Cliente/Proveedor no encontrado"));
			kardex.setClienteProveedor(clienteProveedor);
		}

		Kardex guardado = kardexRepository.save(kardex);

		return kardexMapper.toDto(guardado);
	}


	@Transactional
	public void delete(Long requestId) {
		kardexRepository.findByIdAndEmpresaId(requestId, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new NotFoundException("Kardex no encontrado o no válido"));

		kardexRepository.deleteById(requestId);
	}

}
