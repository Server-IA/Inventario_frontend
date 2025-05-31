package com.coagronet.ordenCompra.dtos;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OrdenCompraDTO {
    private Long id;
    @NotNull(message = "La fecha y hora no puede ser nula")
    private LocalDateTime fechaHora;

    @NotNull(message = "El ID del pedido no puede ser nulo")
    private Long pedidoId;

    @NotNull(message = "El ID del proveedor no puede ser nulo")
    private Long proveedorId;

    @Size(max = 255, message = "La descripción debe tener maximo 255 caracteres")
    private String descripcion;

    @NotNull(message = "El id del estado no puede ser nulo")
    private Long estadoId;
    private Long empresaId;
}
