package com.coagronet.permiso;

import com.coagronet.entidad.Entidad;
import com.coagronet.estado.Estado;
import com.coagronet.metodo.Metodo;
import com.coagronet.modulo.Modulo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity @Table(name = "permiso", schema = "public") @Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Permiso {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "modulo_id", referencedColumnName = "mod_id")
    private Modulo modulo;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "metodo_id", referencedColumnName = "id")
    private Metodo metodo;

    @Column(name = "uri")
    private String uri;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "estado_id", referencedColumnName = "est_id")
    private Estado estado;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "entidad_id", referencedColumnName = "id")
    private Entidad entidad;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "autoridad")
    private String autoridad;

    @Column(name = "admin_empresa", nullable = false)
    private Boolean adminEmpresa;

}
