package com.coagronet.almacen.controllers;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.coagronet.almacen.Almacen;
import com.coagronet.almacen.dtos.AlmacenDTO;
import com.coagronet.almacen.dtos.AlmacenMinimalDTO;
import com.coagronet.almacen.mappers.AlmacenMapper;
import com.coagronet.almacen.repositories.AlmacenRepository;
import com.coagronet.empresa.Empresa;
import com.coagronet.estado.Estado;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.ResourceNotFoundException;
import com.coagronet.sede.repositories.SedeRepository;
import com.coagronet.user.User;
import com.coagronet.user.repositories.UserRepository;
import com.coagronet.userRole.UserRole;
import com.coagronet.userRole.repositories.UserRoleRepository;

@RestController
@RequestMapping("/api/v1/almacen")
@CrossOrigin(origins = "*")
public class AlmacenController {

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private UserRepository userRepository;

    private final AlmacenRepository almacenRepository;
    private final AlmacenMapper almacenMapper;
    private final EstadoRepository estadoRepository;
    private final SedeRepository sedeRepository;

    public AlmacenController(
            AlmacenRepository almacenRepository,
            AlmacenMapper almacenMapper,
            EstadoRepository estadoRepository,
            SedeRepository sedeRepository) {
        this.almacenRepository = almacenRepository;
        this.almacenMapper = almacenMapper;
        this.estadoRepository = estadoRepository;
        this.sedeRepository = sedeRepository;
    }

    private Empresa getEmpresaFromUser(User user) {
        return userRoleRepository.findByUser(user).stream()
                .map(UserRole::getEmpresa)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada para el usuario"));
    }

    private User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }

    @GetMapping("/{requestedId}")
    public ResponseEntity<AlmacenDTO> findById(@PathVariable Integer requestedId) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        return almacenRepository
                .findByIdAndSedeEmpresaIdAndEstadoIdNot(requestedId, empresa.getId(), 2)
                .map(almacenMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/sede/{sedeId}")
    public ResponseEntity<Page<AlmacenDTO>> findAllBySedeId(
            @PathVariable Long sedeId,
            @PageableDefault Pageable pageable) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);

        Page<AlmacenDTO> page = almacenRepository
                .findBySedeIdAndEstadoIdNotAndSedeEmpresaId(sedeId, 2, empresa.getId(), pageable)
                .map(AlmacenMapper.INSTANCE::toDTO);

        return page.hasContent()
                ? ResponseEntity.ok(page)
                : ResponseEntity.noContent().build();
    }

    @GetMapping("/minimal/sede/{sedeId}")
    public ResponseEntity<List<AlmacenMinimalDTO>> findAllMinimalBySedeId(@PathVariable Long sedeId) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);

        List<AlmacenMinimalDTO> almacenMinimalDTOs = almacenRepository
                .findBySedeIdAndEstadoIdNotAndSedeEmpresaIdOrderByIdAsc(sedeId, 2, empresa.getId())
                .stream()
                .map(almacenMapper::toMinimalDTO)
                .collect(Collectors.toList());

        return !almacenMinimalDTOs.isEmpty()
                ? ResponseEntity.ok(almacenMinimalDTOs)
                : ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<Void> createAlmacen(@RequestBody AlmacenDTO newAlmacenRequest, UriComponentsBuilder ucb) {
        AlmacenDTO newAlmacen = new AlmacenDTO(null, newAlmacenRequest.getNombre(), newAlmacenRequest.getSede(),
                newAlmacenRequest.getGeolocalizacion(), newAlmacenRequest.getCoordenadas(),
                newAlmacenRequest.getDescripcion(), newAlmacenRequest.getEstado());
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        if (sedeRepository.existsByIdAndEmpresaIdAndEstadoIdNot(newAlmacen.getSede(), empresa.getId(), 2)) {
            Almacen savedAlmacen = almacenMapper.toEntity(newAlmacen);
            almacenRepository.save(savedAlmacen);
            URI locationOfNewSede = ucb
                    .path("/api/v1/almacen/{id}")
                    .buildAndExpand(savedAlmacen.getId())
                    .toUri();
            return ResponseEntity.created(locationOfNewSede).build();
        }
        return ResponseEntity.badRequest().build();
    }

    @PutMapping("/{requestedId}")
    public ResponseEntity<Void> putAlmacen(@PathVariable Integer requestedId,
            @RequestBody AlmacenDTO almacenDTOUpdate) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        Almacen almacen = almacenRepository.findByIdAndSedeEmpresaIdAndEstadoIdNot(requestedId, empresa.getId(), 2)
                .orElseThrow(() -> new ResourceNotFoundException("Almacén no encontrado"));
        if (null != almacen) {
            AlmacenDTO updatedAlmacenDTO = new AlmacenDTO(requestedId, almacenDTOUpdate.getNombre(),
                    almacenDTOUpdate.getSede(), almacenDTOUpdate.getGeolocalizacion(),
                    almacenDTOUpdate.getCoordenadas(), almacenDTOUpdate.getDescripcion(), almacenDTOUpdate.getEstado());
            Almacen updatedAlmacen = almacenMapper.toEntity(updatedAlmacenDTO);
            almacenRepository.save(updatedAlmacen);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlmacen(@PathVariable Integer id) {
        User authenticatedUser = getAuthenticatedUser();
        Empresa empresa = getEmpresaFromUser(authenticatedUser);
        if (almacenRepository.existsByIdAndSedeEmpresaIdAndEstadoIdNot(id, empresa.getId(), 2)) {
            Almacen almacen = almacenRepository.findById(id).orElse(null);
            Estado estadoInactivo = estadoRepository.findById(2).orElse(null);
            almacen.setEstado(estadoInactivo);
            almacenRepository.save(almacen);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

}