package com.coagronet.moduloempresa;

import java.io.Serializable;

import com.coagronet.empresa.Empresa;
import com.coagronet.estado.Estado;
import com.coagronet.modulo.Modulo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "modulo_empresa", schema = "public")
@SequenceGenerator(name = "MOE_SEQ", sequenceName = "modulo_empresa_moe_id_seq", schema = "public", initialValue = 1, allocationSize = 1)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = { "modulo", "empresa", "estado" })
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ModuloEmpresa implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MOE_SEQ")
    @Column(name = "moe_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "moe_modulo_id", referencedColumnName = "mod_id", nullable = false, foreignKey = @ForeignKey(name = "modulo_empresa_moe_modulo_id_fkey"))
    private Modulo modulo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "moe_empresa_id", referencedColumnName = "emp_id", nullable = false, foreignKey = @ForeignKey(name = "modulo_empresa_moe_empresa_id_fkey"))
    private Empresa empresa;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "moe_estado_id", referencedColumnName = "est_id", nullable = false, foreignKey = @ForeignKey(name = "modulo_empresa_moe_estado_id_fkey"))
    private Estado estado;

}
