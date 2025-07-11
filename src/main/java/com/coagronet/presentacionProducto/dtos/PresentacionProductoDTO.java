package com.coagronet.presentacionProducto.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PresentacionProductoDTO {
    private Long id;

    @NotNull
    private Long productoId;

    @NotBlank
    @Size(max = 255, message = "El nombre no puede tener más de 255 caracteres")
    private String nombre;

    @NotNull
    private Long unidadId;

    @Size(max = 255, message = "La descripcion no puede tener más de 255 caracteres")
    private String descripcion;

    @NotNull(message = "El estado no puede ser nulo")
    private Long estadoId;

    private Double cantidad;

    @NotNull(message = "La marca no puede ser nula")
    private Long marcaId;

    @NotNull(message = "La presentacion no puede ser nula")
    private Long presentacionId;


    private Long empresaId;

    private Boolean desgregar;
}
