package com.coagronet.productoLocalizacion.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductoLocalizacionDTO {

    private Long id;

    @NotNull(message = "El articulo kardex no puede ser nula")
    private Long articuloKardexId;

    @NotNull(message = "La subseccion no puede ser nula")
    private Long subseccionId;

    @NotNull(message = "El estado no puede ser nulo")
    private Long estadoId;


    private Long empresaId;





}
