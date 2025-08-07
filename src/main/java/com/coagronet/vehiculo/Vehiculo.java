package com.coagronet.vehiculo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "vehiculo", schema = "parqueadero")
public class Vehiculo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vehiculo_generator")
    @SequenceGenerator(name = "vehiculo_generator", sequenceName = "parqueadero.vehiculo_veh_id_sequence", allocationSize = 1)
    @Column(name = "veh_id", nullable = false)
    private Long id;

    @Column(name = "veh_placa", length = 100,nullable = false)
    private String placa;

    @Column(name = "veh_marca", length = 100, nullable = false)
    private String marca;

    @Column(name = "veh_color", length = 100, nullable = false)
    private String color;

    @Column(name = "veh_tipo_vehiculo_id", nullable = false)
    private Long tipoVehiculo;
}
