package com.coagronet.productopresentacionstock.dtos;

import com.coagronet.empresa.Empresa;
import com.coagronet.presentacionProducto.PresentacionProducto;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoPresentacionStockResponseDTO {

    private Long id;

    private BigDecimal stock;

    private LocalDateTime fechaHora;
    private Long productoPresentacionId;
    private String productoPresentacionNombre;

    private Long empresaId;
}
