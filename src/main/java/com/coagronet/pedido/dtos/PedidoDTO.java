package com.coagronet.pedido.dtos;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PedidoDTO {
    private Long id;
    private LocalDateTime fechaHora;

    @NotNull
    private Long almacenId;

    @NotNull
    private Long produccionId;

    @NotNull
    @Size(max = 2048, message = "La descripcion no puede superar los 2048 caracteres")
    private String descripcion;

    @NotNull
    private Long estadoId;
    private Long empresaId;
}
