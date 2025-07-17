package com.coagronet.empresa;

import com.coagronet.estado.Estado;
import com.coagronet.persona.Persona;
import com.coagronet.tipoIdentificacion.TipoIdentificacion;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Empresa {

    @Id
    @GeneratedValue
    @Column(name = "emp_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "emp_tipo_identificacion_id", referencedColumnName = "tii_id", columnDefinition = "integer default 6")
    private TipoIdentificacion tipoIdentificacion;

    @Column(name = "emp_identificacion")
    private String identificacion;

    @Column(name = "emp_nombre")
    private String nombre;

    @OneToOne
    @JoinColumn(name = "emp_persona_id", referencedColumnName = "per_id")
    private Persona persona;

    @Column(name = "emp_descripcion")
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emp_estado_id", referencedColumnName = "est_id")
    private Estado estado;

    @Column(name = "emp_celular")
    private String celular;

    @Column(name = "emp_correo")
    private String correo;

    @Column(name = "emp_contacto")
    private String contacto;

    @Column(name = "emp_logo")
}
