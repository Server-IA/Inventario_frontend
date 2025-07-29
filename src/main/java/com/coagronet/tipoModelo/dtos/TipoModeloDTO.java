package com.coagronet.tipoModelo.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TipoModeloDTO {
    
    private Long id;

    @NotBlank(message = "El nombre no puede estar vácio.")
    @Size(max = 100, message = "El nombre no puede exceder los 100 carácteres.")
    private String nombre;

    @Size(max = 2048, message = "La descripción no puede exceder los 2048 carácteres.")
    private String descripcion;

    @NotNull(message = "El estado no puede ser nulo.")
    private Long estadoId;

    private Long empresaId;
}
