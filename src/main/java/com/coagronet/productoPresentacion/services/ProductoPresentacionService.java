// package com.coagronet.productoPresentacion.services;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.Pageable;
// import org.springframework.stereotype.Service;

// import com.coagronet.estado.repositories.EstadoRepository;
// import com.coagronet.marca.Marca;
// import com.coagronet.presentacion.Presentacion;
// import com.coagronet.producto.Producto;
// import com.coagronet.productoPresentacion.ProductoPresentacion;
// import com.coagronet.productoPresentacion.dtos.ProductoPresentacionDTO;
// import com.coagronet.productoPresentacion.repositories.ProductoPresentacionRepository;
// import com.coagronet.unidad.Unidad;

// import jakarta.persistence.EntityNotFoundException;

// @Service
// public class ProductoPresentacionService {

//     @Autowired
//     private ProductoPresentacionRepository productoPresentacionRepository;

//     @Autowired
//     private EstadoRepository estadoRepository;

//     public Page<ProductoPresentacion> getAllProductoPresentaciones(Pageable pageable) {
//         return productoPresentacionRepository.findByEstadoNot(2, pageable);
//     }

//     public ProductoPresentacionDTO createProductoPresentacion(ProductoPresentacionDTO productoPresentacionDTO) {
//         if (productoPresentacionDTO.getId() != null) {
//             throw new IllegalArgumentException("El ID debe ser nulo al crear un nuevo ProductoPresentacion");
//         }

//         validarYGuardarEntidadesReferenciadas(productoPresentacionDTO);

//         ProductoPresentacion productoPresentacion = productoPresentacionMapper.toEntity(productoPresentacionDTO);
//         ProductoPresentacion savedProductoPresentacion = productoPresentacionRepository.save(productoPresentacion);
//         return productoPresentacionMapper.toDto(savedProductoPresentacion);
//     }

//     public ProductoPresentacionDTO updateProductoPresentacion(Integer id,
//             ProductoPresentacionDTO productoPresentacionDTO) {
//         ProductoPresentacion existingProductoPresentacion = productoPresentacionRepository.findByIdAndEstado(id, 1)
//                 .orElseThrow(() -> new EntityNotFoundException("ProductoPresentacion no encontrado"));

//         validarYGuardarEntidadesReferenciadas(productoPresentacionDTO);

//         ProductoPresentacion updatedProductoPresentacion = productoPresentacionMapper.toEntity(productoPresentacionDTO);
//         updatedProductoPresentacion.setId(existingProductoPresentacion.getId());
//         ProductoPresentacion savedProductoPresentacion = productoPresentacionRepository
//                 .save(updatedProductoPresentacion);
//         return productoPresentacionMapper.toDto(savedProductoPresentacion);
//     }

//     public void deleteProductoPresentacion(Integer id) {
//         ProductoPresentacion existingProductoPresentacion = productoPresentacionRepository.findByIdAndEstadoNot(id, 2)
//                 .orElseThrow(() -> new EntityNotFoundException("ProductoPresentacion no encontrado"));
//         existingProductoPresentacion.setEstado(2); // Cambiar estado a 2 en lugar de eliminar
//         productoPresentacionRepository.save(existingProductoPresentacion);
//     }

//     private void validarYGuardarEntidadesReferenciadas(ProductoPresentacionDTO productoPresentacionDTO) {
//         if (productoPresentacionDTO.getProductoId() != null) {
//             Producto producto = productoRepository.findById(productoPresentacionDTO.getProductoId().intValue())
//                     .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));

//             productoRepository.save(producto);
//         }

//         if (productoPresentacionDTO.getUnidadId() != null) {
//             Unidad unidad = unidadRepository.findById(productoPresentacionDTO.getUnidadId().intValue())
//                     .orElseThrow(() -> new EntityNotFoundException("Unidad no encontrada"));
//             unidadRepository.save(unidad);
//         }

//         if (productoPresentacionDTO.getMarcaId() != null) {
//             Marca marca = marcaRepository.findById(productoPresentacionDTO.getMarcaId().longValue())
//                     .orElseThrow(() -> new EntityNotFoundException("Marca no encontrada"));
//             marcaRepository.save(marca);
//         }

//         if (productoPresentacionDTO.getPresentacionId() != null) {
//             Presentacion presentacion = presentacionRepository
//                     .findById(productoPresentacionDTO.getPresentacionId().longValue())
//                     .orElseThrow(() -> new EntityNotFoundException("Presentacion no encontrada"));
//             presentacionRepository.save(presentacion);
//         }
//     }

// }
