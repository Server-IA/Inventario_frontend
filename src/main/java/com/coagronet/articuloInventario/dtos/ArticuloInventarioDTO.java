package com.coagronet.articuloInventario.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticuloInventarioDTO {

    private Long id;    

    @Size(max = 2048, message = "La descripción no puede superar 2048 caracteres.")
    private String descripcion;

    @NotBlank(message = "El campo UUID es obligatorio.")
    private String uuid;

    private String identificadorProducto;

    @NotNull(message = "El campo inventarioId no puede ser nulo.")
    private Long inventarioId;

    @NotNull(message = "El campo estadoId no puede ser nulo.")
    private Long estadoId;

    private Long empresaId;

}
