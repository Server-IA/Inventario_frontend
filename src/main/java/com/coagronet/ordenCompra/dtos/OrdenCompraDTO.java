package com.coagronet.ordenCompra.dtos;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrdenCompraDTO {

    private Long id;

    private LocalDateTime fechaHora;

    @NotNull(message = "El ID del pedido no puede ser nulo.")
    private Long pedidoId;

    @NotNull(message = "El ID del proveedor no puede ser nulo.")
    private Long proveedorId;

    @Size(max = 2048, message = "La descripción debe tener máximo 2048 caracteres.")
    private String descripcion;

    @NotNull(message = "El ID del estado no puede ser nulo.")
    private Long estadoId;

    private Long empresaId;
}
