// package com.coagronet.almacen.services;

// import java.util.List;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.Pageable;
// import org.springframework.stereotype.Service;

// import com.coagronet.almacen.Almacen;
// import com.coagronet.almacen.dtos.DTOAlmacen;
// import com.coagronet.almacen.mappers.AlmacenMapper;
// import com.coagronet.almacen.repositories.AlmacenRepository;
// import com.coagronet.estado.Estado;
// import com.coagronet.estado.repositories.EstadoRepository;

// import jakarta.persistence.EntityNotFoundException;

// @Service
// public class AlmacenService {

//     @Autowired
//     private AlmacenRepository almacenRepository;

//     @Autowired
//     private EstadoRepository estadoRepository;

//     public List<Almacen> ObtenerAlmacenesPorSede(Long sedeId, Long empresaId) {
//         return almacenRepository.buscarAlmacenesPorSede(sedeId, empresaId);
//     }

//     public Page<Almacen> ObtenerAlmacenesPorSedePage(Long sedeId, Long empresaId, Pageable paginacion) {
//         return almacenRepository.buscarAlmacenesPorSedePage(sedeId, empresaId, paginacion);
//     }

//     public Almacen guardarAlmacen(DTOAlmacen dtoAlmacen) {
//         Almacen almacen = AlmacenMapper.INSTANCE.toEntity(dtoAlmacen);
//         return almacenRepository.save(almacen);
//     }

//     public Almacen actualizarAlmacen(DTOAlmacen dtoAlmacen) {
//         Almacen almacen = AlmacenMapper.INSTANCE.toEntity(dtoAlmacen);
//         if (!almacenRepository.existsById(almacen.getId())) {
//             throw new EntityNotFoundException("Producción no encontrada");
//         }
//         return almacenRepository.save(almacen);
//     }

//     public void eliminarAlmacen(Integer id) {
//         Almacen almacen = almacenRepository.findById(id)
//                 .orElseThrow(() -> new RuntimeException("Almacen not found with id: " + id));

//         Estado nuevoEstado = estadoRepository.findById(2)
//                 .orElseThrow(() -> new RuntimeException("Estado not found with id: 2"));

//         almacen.setEstado(nuevoEstado);
//         almacenRepository.save(almacen);
//     }

// }
