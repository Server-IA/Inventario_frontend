package com.coagronet.productopresentacionstock.mappers;

import com.coagronet.productopresentacionstock.ProductoPresentacionStock;
import com.coagronet.productopresentacionstock.dtos.ProductoPresentacionStockResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface ProductoPresentacionStockMapper {

    @Mapping(source = "productoPresentacion.id", target = "productoPresentacionId")
    @Mapping(source = "productoPresentacion.nombre", target = "productoPresentacionNombre")
    @Mapping(source = "empresa.id", target = "empresaId")
    ProductoPresentacionStockResponseDTO toResponseDTO(ProductoPresentacionStock entity);
}
