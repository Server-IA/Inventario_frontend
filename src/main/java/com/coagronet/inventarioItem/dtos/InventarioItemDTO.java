package com.coagronet.inventarioItem.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InventarioItemDTO {

    private Long id;

    @NotNull(message = "El id del inventario no puede ser nulo")
    private Long inventarioId;

    @Size(max = 2048, message = "La descripción no puede tener más de 2048 caracteres")
    private String descripcion;

    private String uuid;

    private Long empresaId;

    @NotNull(message = "El estado no puede ser nulo")
    private Long estadoId;

    @NotNull(message = "El producto identificador o KardexItemId es obligatorio")
    private Long articuloKardexId;

}
