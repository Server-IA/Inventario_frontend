package com.coagronet.tipoInventario.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TipoInventarioDTO {

    private Long id;

    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    private String nombre;

    @Size(max = 2048, message = "La descripcion no puede superar los 2048 caracteres")
    private String descripcion;

    @NotNull
    private Long estadoId;

    private Long empresaId;




}
