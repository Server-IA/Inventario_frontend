package com.coagronet.productoPresentacion.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductoPresentacionDTO {
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

    @NotNull
    private Long estadoId;

    private Double cantidad;

    @NotNull
    private Long marcaId;

    @NotNull
    private Long presentacionId;


    private Long empresaId;

    @NotNull
    private Long ingredienteId;
}
