package com.coagronet.pedido.controllers;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.coagronet.almacen.repositories.AlmacenRepository;
import com.coagronet.empresa.Empresa;
import com.coagronet.pedido.Pedido;
import com.coagronet.pedido.dtos.PedidoDTO;
import com.coagronet.pedido.mappers.PedidoMapper;
import com.coagronet.pedido.repositories.PedidoRepository;
import com.coagronet.produccion.repositories.ProduccionRepository;
import com.coagronet.user.User;
import com.coagronet.user.repositories.UserRepository;
import com.coagronet.userRole.UserRole;
import com.coagronet.userRole.repositories.UserRoleRepository;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/pedido")
public class PedidoController {/*
								 * 
								 * private final PedidoRepository pedidoRepository; private final PedidoMapper
								 * pedidoMapper; private final UserRoleRepository userRoleRepository; private
								 * final UserRepository userRepository; private final AlmacenRepository
								 * almacenRepository; private final ProduccionRepository produccionRepository;
								 * 
								 * private PedidoController( PedidoRepository pedidoRepository, PedidoMapper
								 * pedidoMapper, UserRoleRepository userRoleRepository, UserRepository
								 * userRepository, AlmacenRepository almacenRepository, ProduccionRepository
								 * produccionRepository) { this.pedidoRepository = pedidoRepository;
								 * this.pedidoMapper = pedidoMapper; this.userRoleRepository =
								 * userRoleRepository; this.userRepository = userRepository;
								 * this.almacenRepository = almacenRepository; this.produccionRepository =
								 * produccionRepository; }
								 * 
								 * private Empresa getEmpresaFromUser(User user) { return
								 * userRoleRepository.findByUser(user).stream() .map(UserRole::getEmpresa)
								 * .findFirst() .orElseThrow(() -> new
								 * RuntimeException("Empresa no encontrada para el usuario")); }
								 * 
								 * private User getAuthenticatedUser() { String username =
								 * SecurityContextHolder.getContext().getAuthentication().getName(); return
								 * userRepository.findByUsername(username) .orElseThrow(() -> new
								 * UsernameNotFoundException("Usuario no encontrado")); }
								 * 
								 * @GetMapping("/{requestedId}") private ResponseEntity<PedidoDTO>
								 * findById(@PathVariable Integer requestedId) { User authenticatedUser =
								 * getAuthenticatedUser(); Empresa empresa =
								 * getEmpresaFromUser(authenticatedUser); return pedidoRepository
								 * .findByIdAndAlmacenSedeEmpresaId(requestedId, empresa.getId())
								 * .map(pedidoMapper::toDto) .map(ResponseEntity::ok)
								 * .orElse(ResponseEntity.notFound().build()); }
								 * 
								 * @PostMapping private ResponseEntity<Void> createPedido(@RequestBody PedidoDTO
								 * newPedidoRequest, UriComponentsBuilder ucb) { User authenticatedUser =
								 * getAuthenticatedUser(); Empresa empresa =
								 * getEmpresaFromUser(authenticatedUser); if
								 * (almacenRepository.existsByIdAndSedeEmpresaIdAndEstadoIdNot(
								 * newPedidoRequest.getAlmacen(), empresa.getId(), 2) &&
								 * produccionRepository.existsByIdAndEspacioBloqueSedeEmpresaIdAndEstadoIdNot(
								 * newPedidoRequest.getProduccion(), empresa.getId(), 2)) { PedidoDTO newPedido
								 * = new PedidoDTO( null, newPedidoRequest.getFechaHora(),
								 * newPedidoRequest.getAlmacen(), newPedidoRequest.getProduccion(),
								 * newPedidoRequest.getDescripcion(), newPedidoRequest.getEstado()); Pedido
								 * savedPedido = pedidoMapper.toEntity(newPedido);
								 * pedidoRepository.save(savedPedido); URI locationOfNewPedido = ucb
								 * .path("/api/v1/pedido/{id}") .buildAndExpand(savedPedido.getId()) .toUri();
								 * return ResponseEntity.created(locationOfNewPedido).build(); } return
								 * ResponseEntity.badRequest().build(); }
								 * 
								 * @GetMapping("/almacen/{requestedId}") private ResponseEntity<Page<PedidoDTO>>
								 * findAllByAlmacenId(@PathVariable Integer requestedId,
								 * 
								 * @PageableDefault Pageable pageable) { User authenticatedUser =
								 * getAuthenticatedUser(); Empresa empresa =
								 * getEmpresaFromUser(authenticatedUser); Page<PedidoDTO> page =
								 * pedidoRepository .findByAlmacenIdAndAlmacenSedeEmpresaIdAndEstadoIdNot(
								 * requestedId, empresa.getId(), 2, pageable) .map(pedidoMapper::toDto); return
								 * page.hasContent() ? ResponseEntity.ok(page) :
								 * ResponseEntity.noContent().build(); }
								 * 
								 * @PutMapping("/{requestedId}") private ResponseEntity<Void>
								 * putPedido(@PathVariable Integer requestedId,
								 * 
								 * @RequestBody PedidoDTO pedidoDTOUpdate) { User authenticatedUser =
								 * getAuthenticatedUser(); Empresa empresa =
								 * getEmpresaFromUser(authenticatedUser); if
								 * (pedidoRepository.existsByIdAndAlmacenSedeEmpresaId(requestedId,
								 * empresa.getId()) &&
								 * almacenRepository.existsByIdAndSedeEmpresaIdAndEstadoIdNot(pedidoDTOUpdate.
								 * getAlmacen(), empresa.getId(), 2) &&
								 * produccionRepository.existsByIdAndEspacioBloqueSedeEmpresaIdAndEstadoIdNot(
								 * pedidoDTOUpdate.getProduccion(), empresa.getId(), 2)) { PedidoDTO
								 * updatePedidoDTO = new PedidoDTO( requestedId, pedidoDTOUpdate.getFechaHora(),
								 * pedidoDTOUpdate.getAlmacen(), pedidoDTOUpdate.getProduccion(),
								 * pedidoDTOUpdate.getDescripcion(), pedidoDTOUpdate.getEstado()); Pedido
								 * updatedPedido = pedidoMapper.toEntity(updatePedidoDTO);
								 * pedidoRepository.save(updatedPedido); return
								 * ResponseEntity.noContent().build(); } return
								 * ResponseEntity.notFound().build(); }
								 * 
								 * @DeleteMapping("/{id}") private ResponseEntity<Void>
								 * deletePedido(@PathVariable Integer id) { try { User authenticatedUser =
								 * getAuthenticatedUser(); Empresa empresa =
								 * getEmpresaFromUser(authenticatedUser); if
								 * (pedidoRepository.existsByIdAndAlmacenSedeEmpresaId(id, empresa.getId())) {
								 * pedidoRepository.deleteById(id); return ResponseEntity.noContent().build(); }
								 * return ResponseEntity.notFound().build(); } catch (Exception e) { return
								 * ResponseEntity.internalServerError().build(); } }
								 */
}
