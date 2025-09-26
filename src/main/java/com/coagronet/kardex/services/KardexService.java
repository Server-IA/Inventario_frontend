package com.coagronet.kardex.services;

import com.coagronet.almacen.Almacen;
import com.coagronet.empresa.Empresa;
import com.coagronet.empresa.repositories.EmpresaRepository;
import com.coagronet.entidadvalidator.EntidadValidator;
import com.coagronet.estado.Estado;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.kardex.Kardex;
import com.coagronet.kardex.mappers.KardexMapper;
import com.coagronet.kardex.repositories.KardexRepository;
import com.coagronet.kardex.dtos.KardexDTO;
import com.coagronet.produccion.Produccion;
import com.coagronet.tipoMovimiento.TipoMovimiento;
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
	private final UserEmpresaService userEmpresaService;
	private final EmpresaRepository empresaRepository;
	private final EntidadValidator entidadValidator;

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

		Empresa empresa = entidadValidator.validarEmpresa(empresaId);
		Kardex kardex = kardexMapper.toEntity(kardexDTO);
		kardex.setEmpresa(empresa);

		aplicarValidacionesYRelaciones(kardexDTO, kardex, empresaId);
		Kardex guardado = kardexRepository.save(kardex);
		return kardexMapper.toDto(guardado);
	}

	@Transactional
	public KardexDTO update(Long requestedId, KardexDTO kardexDTO) {
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

		Kardex kardexExistente = entidadValidator.validarKardex(requestedId, empresaId);
		kardexMapper.updateEntityFromDto(kardexDTO, kardexExistente);

		aplicarValidacionesYRelaciones(kardexDTO, kardexExistente, empresaId);

		Kardex guardado = kardexRepository.save(kardexExistente);
		return kardexMapper.toDto(guardado);
	}


	@Transactional
	public void delete(Long requestId) {
		kardexRepository.findByIdAndEmpresaId(requestId, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new NotFoundException("Kardex no encontrado o no válido"));

		kardexRepository.deleteById(requestId);
	}

	private void aplicarValidacionesYRelaciones(KardexDTO kardexDTO, Kardex kardex, Long empresaId) {
		Estado estado = entidadValidator.validarEstado(kardexDTO.getEstadoId());
		Almacen almacen = entidadValidator.validarAlmacen(kardexDTO.getAlmacenId(), empresaId);
		Produccion produccion = entidadValidator.validarProduccion(kardexDTO.getProduccionId(), empresaId);
		TipoMovimiento tipoMovimiento = entidadValidator.validarTipoMovimiento(kardexDTO.getTipoMovimientoId(), empresaId);

		kardex.setEstado(estado);
		kardex.setAlmacen(almacen);
		kardex.setProduccion(produccion);
		kardex.setTipoMovimiento(tipoMovimiento);

		if (kardexDTO.getClienteProveedorId() != null) {
			Empresa clienteProveedor = entidadValidator.validarClienteProveedor(kardexDTO.getClienteProveedorId());
			kardex.setClienteProveedor(clienteProveedor);
		}
	}
}
