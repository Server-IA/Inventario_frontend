package com.coagronet.pasantia.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class InventarioProgresoId implements Serializable {

    @Column(name = "inventario_id", nullable = false)
    private Long inventarioId;

    @Column(name = "producto_identificador", length = 50, nullable = false)
    private String productoIdentificador;
}