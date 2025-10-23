package com.coagronet.modulo.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.coagronet.menu.dtos.MenuModuloResponseDTO;
import com.coagronet.menu.repositories.projections.SubModuloRow;

/**
 * Mapper MapStruct para convertir la proyección {@link SubModuloRow} en el DTO
 * {@link MenuModuloResponseDTO}.
 * <p>
 * Centraliza el mapeo y mantiene el servicio libre de lógica de conversión.
 * </p>
 *
 * @author Juan J. Castro
 * @since 0.3.1
 */
@Mapper(componentModel = "spring")
public interface ModuloMapper {

	/**
	 * Convierte una fila plana de submódulo en un DTO de módulo para menú.
	 *
	 * @param row proyección con campos del subsistema y del módulo
	 * @return DTO con id, nombre, url e icono del módulo
	 */
	@Mapping(target = "id", source = "modNombreId")
	@Mapping(target = "nombre", source = "modNombre")
	@Mapping(target = "url", source = "modUrl")
	@Mapping(target = "icono", source = "modIcon")
	MenuModuloResponseDTO toDTO(SubModuloRow row);

}
