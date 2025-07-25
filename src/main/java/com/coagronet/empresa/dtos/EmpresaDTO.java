package com.coagronet.empresa.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmpresaDTO {

	private Long id;

	private String nombre;

	private String descripcion;

	private Long estadoId;

	private String celular;

	private String correo;

	private String contacto;

	private Long tipoIdentificacionId;

	private Long personaId;

	private String identificacion;

	private String logo;

	private String logoHash;

}
