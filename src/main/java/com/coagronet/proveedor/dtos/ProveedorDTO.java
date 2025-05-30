package com.coagronet.proveedor.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ProveedorDTO {
    private Long id;

    private Long empresaId;

    private LocalDateTime fechaCreacion;

    @NotNull(message = "El estado no puede ser nulo")
    private Long estadoId;

    @NotNull(message = "El tipo de identificación no puede ser nulo")
    private Long tipoIdentificacionId;

    @NotNull(message = "El nombre no puede ser nulo")
    @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres")
    private String nombre;

    @Size(max = 13, message = "El número de celular no puede exceder los 13 caracteres")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "El número de celular debe tener entre 10 y 13 dígitos y puede comenzar con +")
    private String celular;

    @Size(max = 255, message = "El nombre del contacto no puede exceder los 255 caracteres")
    private String contacto;

    @NotNull
    @Email(message = "El correo no es válido")
    @Size(max = 100, message = "El correo no puede exceder los 100 caracteres")
    private String correo;

    @NotNull(message = "La identificación no puede ser nula")
    @Size(max = 20, message = "La identificación no puede exceder los 20 caracteres")
    private String identificacion;
}
