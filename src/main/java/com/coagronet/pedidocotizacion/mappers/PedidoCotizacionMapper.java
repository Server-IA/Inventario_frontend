package com.coagronet.pedidocotizacion.mappers;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.coagronet.pedidocotizacion.PedidoCotizacion;
import com.coagronet.pedidocotizacion.dtos.PedidoCotizacionRequestDTO;
import com.coagronet.pedidocotizacion.dtos.PedidoCotizacionResponseDTO;

/**
 * Mapper de MapStruct para convertir entre la entidad
 * {@link com.coagronet.pedidocotizacion.PedidoCotizacion}
 * y sus DTOs
 * {@link com.coagronet.pedidocotizacion.dtos.PedidoCotizacionRequestDTO} y
 * {@link com.coagronet.pedidocotizacion.dtos.PedidoCotizacionResponseDTO}.
 *
 * <p>
 * Este componente respeta el principio de responsabilidad única (SRP) al
 * encapsular toda la lógica de mapeo y mantener la capa de servicio libre de
 * código de conversión.
 * </p>
 *
 * <p>
 * Convenciones:
 * </p>
 * <ul>
 * <li>En {@code toDTO}, los IDs de asociaciones se proyectan desde
 * {@code pedido.id},
 * {@code proveedor.id} y {@code estado.id}.</li>
 * <li>En {@code toEntity}, se aplica la configuración inversa; los IDs se
 * establecen
 * en las referencias correspondientes a través de la lógica de
 * servicio/validación.</li>
 * </ul>
 *
 * @author Juan J. Castro
 * @since 0.3.1
 */
@Mapper(componentModel = "spring")
public interface PedidoCotizacionMapper {

	/**
	 * Convierte una entidad en su representación de salida.
	 *
	 * @param pedidoCotizacion entidad origen
	 * @return DTO de respuesta con IDs proyectados y campos simples
	 */
	@Mapping(target = "pedidoId", source = "pedido.id")
	@Mapping(target = "proveedorId", source = "proveedor.id")
	@Mapping(target = "estadoId", source = "estado.id")
	PedidoCotizacionResponseDTO toDTO(PedidoCotizacion pedidoCotizacion);

	/**
	 * Convierte un DTO de entrada en entidad.
	 *
	 * <p>
	 * Las referencias a {@code Pedido}, {@code Proveedor} y {@code Estado}
	 * deben completarse/validarse en la capa de servicio, ya que aquí solo
	 * se mapean los IDs.
	 * </p>
	 *
	 * @param pedidoCotizacionRequestDTO DTO de entrada
	 * @return entidad lista para asociar referencias y persistir
	 */
	@InheritInverseConfiguration(name = "toDTO")
	PedidoCotizacion toEntity(PedidoCotizacionRequestDTO pedidoCotizacionRequestDTO);

}
