/*=============================================================================
 Nombre del archivo : ArticuloKardexFactory.java
 Descripcion        : Fábrica para la creación de entidades ArticuloKardex.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |    Fecha   | Versión |       Autor          | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-06-21 | 0.4.0   | JUAN JOSE CASTRO     | Eliminación de la inyección |
 |            |         |                      | de AuthenticationService y  |
 |            |         |                      | del seteo manual del campo  |
 |            |         |                      | username en la entidad.     |
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/

package com.coagronet.articuloKardex.services.factory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.coagronet.articuloKardex.ArticuloKardex;
import com.coagronet.articuloKardex.dtos.ArticuloKardexDTO;
import com.coagronet.articuloKardex.mappers.ArticuloKardexMapper;
import com.coagronet.articuloKardex.repositories.ArticuloKardexRepository;
import com.coagronet.auditoria.RequestUtils;
import com.coagronet.exceptionHandler.custom.BadRequestException;
import com.coagronet.presentacionProducto.PresentacionProducto;
import com.coagronet.presentacionProducto.repositories.PresentacionProductoRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ArticuloKardexFactory {

	private final ArticuloKardexMapper articuloKardexMapper;

	private final ArticuloKardexRepository articuloKardexRepository;

	private final PresentacionProductoRepository presentacionProductoRepository;

	private final RequestUtils requestUtils;

	public List<ArticuloKardex> crearArticulos(ArticuloKardexDTO dto, Long empresaId, HttpServletRequest request) {
		if (esDesgregado(dto, empresaId)) {
			return crearArticulosDesgregados(dto, empresaId, request);
		} else {
			dto.setEmpresaId(empresaId);
			ArticuloKardex entidad = articuloKardexMapper.toEntity(dto);

			asignarDatosAuditoria(entidad, request);

			return List.of(articuloKardexRepository.save(entidad));
		}
	}

	private boolean esDesgregado(ArticuloKardexDTO dto, Long empresaId) {
		return presentacionProductoRepository.findByIdAndEmpresaId(dto.getPresentacionProductoId(), empresaId)
				.map(PresentacionProducto::getDesgregar)
				.orElse(false);
	}

	private List<ArticuloKardex> crearArticulosDesgregados(ArticuloKardexDTO dto, Long empresaId,
			HttpServletRequest request) {

		BigDecimal cantidad = dto.getCantidad();

		// Validar si tiene parte decimal usando BigDecimal (Ej: 1.5 % 1 != 0)
		if (cantidad.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) != 0) {
			throw new BadRequestException("Para presentaciones desgregadas, la cantidad debe ser un número entero.");
		}

		int unidades = cantidad.intValue();
		List<ArticuloKardex> creados = new ArrayList<>();

		for (int i = 0; i < unidades; i++) {
			ArticuloKardexDTO item = construirArticuloKardexUnitario(dto, empresaId);
			ArticuloKardex entidad = articuloKardexMapper.toEntity(item);
			asignarDatosAuditoria(entidad, request);
			creados.add(entidad);
		}

		// OPTIMIZACIÓN: Inserción en lote (Batch Insert). 1 solo viaje a la BD.
		return articuloKardexRepository.saveAll(creados);
	}

	private void asignarDatosAuditoria(ArticuloKardex entidad, HttpServletRequest request) {

		entidad.setIp(requestUtils.getClientIp(request));
		entidad.setHost(requestUtils.getClientHost(request));
		entidad.setRol(requestUtils.getAuthenticatedRole());
	}

	private static ArticuloKardexDTO construirArticuloKardexUnitario(ArticuloKardexDTO articuloKardexDTO,
			Long empresaId) {
		ArticuloKardexDTO item = new ArticuloKardexDTO();
		item.setEmpresaId(empresaId);
		item.setKardexId(articuloKardexDTO.getKardexId());
		item.setPresentacionProductoId(articuloKardexDTO.getPresentacionProductoId());
		item.setEstadoId(articuloKardexDTO.getEstadoId());

		item.setCantidad(BigDecimal.ONE);

		item.setPrecio(articuloKardexDTO.getPrecio());
		item.setFechaVencimiento(articuloKardexDTO.getFechaVencimiento());
		item.setIdentificadorProducto(articuloKardexDTO.getIdentificadorProducto());
		item.setLote(articuloKardexDTO.getLote());
		return item;
	}

}
