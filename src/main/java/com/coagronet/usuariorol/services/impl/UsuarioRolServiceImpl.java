package com.coagronet.usuariorol.services.impl;

import java.time.OffsetDateTime;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coagronet.auditoria.RequestUtils;
import com.coagronet.empresa.Empresa;
import com.coagronet.empresa.repositories.EmpresaRepository;
import com.coagronet.empresarol.repositories.EmpresaRolRepository;
import com.coagronet.estado.Estado;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.rol.Rol;
import com.coagronet.rol.repositories.RolRepository;
import com.coagronet.user.User;
import com.coagronet.user.repositories.UserRepository;
import com.coagronet.usuariorol.UsuarioRol;
import com.coagronet.usuariorol.dtos.UsuarioRolRequestDTO;
import com.coagronet.usuariorol.dtos.UsuarioRolRequestForCurrentEmpresaDTO;
import com.coagronet.usuariorol.dtos.UsuarioRolResponseDTO;
import com.coagronet.usuariorol.dtos.UsuarioRolResponseForCurrentEmpresaDTO;
import com.coagronet.usuariorol.mappers.UsuarioRolMapper;
import com.coagronet.usuariorol.repositories.UsuarioRolRepository;
import com.coagronet.usuariorol.services.UsuarioRolService;
import com.coagronet.utils.AuthenticatedUser;
import com.coagronet.utils.UserEmpresaService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioRolServiceImpl implements UsuarioRolService {

        private static final Long ESTADO_ACTIVO_ID = 1L;
        private static final Long ESTADO_INACTVIO_ID = 2L;

        private final UsuarioRolRepository usuarioRolRepository;
        private final UsuarioRolMapper usuarioRolMapper;

        private final UserRepository userRepository;
        private final RolRepository rolRepository;
        private final EmpresaRepository empresaRepository;
        private final EstadoRepository estadoRepository;
        private final EmpresaRolRepository empresaRolRepository;

        private final AuthenticatedUser authenticatedUser;
        private final RequestUtils requestUtils;

        private final UserEmpresaService userEmpresaService;

        @Override
        @Transactional(readOnly = true)
        public Page<UsuarioRolResponseDTO> findAll(Pageable pageable) {
                return usuarioRolRepository
                                .findByDeletedAtIsNullAndEstadoIdNotOrderByIdDesc(pageable, ESTADO_INACTVIO_ID)
                                .map(usuarioRolMapper::toResponse);
        }

        @Override
        @Transactional(readOnly = true)
        public Page<UsuarioRolResponseForCurrentEmpresaDTO> findAllForCurrentEmpresa(Pageable pageable) {
                Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
                return usuarioRolRepository
                                .findAllByEmpresaIdAndDeletedAtIsNullAndEstadoIdNotOrderByIdDesc(pageable, empresaId,
                                                ESTADO_INACTVIO_ID)
                                .map(usuarioRolMapper::toResponseForCurrentEmpresa);
        }

        @Override
        @Transactional(readOnly = true)
        public UsuarioRolResponseDTO findById(Long id) {
                UsuarioRol entity = usuarioRolRepository
                                .findByIdAndDeletedAtIsNullAndEstadoIdNot(id, ESTADO_INACTVIO_ID)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "UsuarioRol no encontrado con id " + id));
                return usuarioRolMapper.toResponse(entity);
        }

        @Override
        @Transactional(readOnly = true)
        public UsuarioRolResponseForCurrentEmpresaDTO findByIdForCurrentEmpresa(Long id) {
                Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
                UsuarioRol entity = usuarioRolRepository
                                .findByIdAndEmpresaIdAndDeletedAtIsNullAndEstadoIdNot(id, empresaId, ESTADO_INACTVIO_ID)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "UsuarioRol no encontrado con id " + id));
                return usuarioRolMapper.toResponseForCurrentEmpresa(entity);
        }

        @Override
        public UsuarioRolResponseDTO create(UsuarioRolRequestDTO request, HttpServletRequest httpRequest) {
                User currentUser = authenticatedUser.getCurrentUser();

                User usuario = userRepository.findById(request.usuarioId())
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Usuario no encontrado con id " + request.usuarioId()));

                Empresa empresa = empresaRepository.findById(request.empresaId())
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Empresa no encontrada con id " + request.empresaId()));

                Rol rol = rolRepository.findById(request.rolId())
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Rol no encontrado con id " + request.rolId()));

                Long estadoId = request.estadoId() != null ? request.estadoId() : ESTADO_ACTIVO_ID;
                Estado estado = estadoRepository.findById(estadoId)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Estado no encontrado con id " + estadoId));

                boolean existsActivo = usuarioRolRepository
                                .existsByUser_IdAndEmpresa_IdAndRol_IdAndEstado_IdAndFinalizaContratoEnIsNull(
                                                usuario.getId(), empresa.getId(), rol.getId(), ESTADO_ACTIVO_ID);
                if (existsActivo && estadoId.equals(ESTADO_ACTIVO_ID)) {
                        throw new DataIntegrityViolationException(
                                        "El usuario ya tiene este rol activo para la empresa seleccionada");
                }

                UsuarioRol entity = usuarioRolMapper.toEntity(request);

                entity.setUser(usuario);
                entity.setEmpresa(empresa);
                entity.setRol(rol);
                entity.setEstado(estado);

                OffsetDateTime now = OffsetDateTime.now();
                entity.setIniciaContratoEn(
                                request.iniciaContratoEn() != null ? request.iniciaContratoEn() : now);

                entity.setCreatedAt(now);
                entity.setCreatedBy(currentUser);
                entity.setUpdatedAt(null);
                entity.setUpdatedBy(null);
                entity.setDeletedAt(null);
                entity.setDeletedBy(null);

                String requestIp = requestUtils.getClientIp(httpRequest);
                String requestHost = requestUtils.getClientHost(httpRequest);
                entity.setRequestIp(requestIp);
                entity.setRequestHost(requestHost);

                UsuarioRol saved = usuarioRolRepository.save(entity);
                return usuarioRolMapper.toResponse(saved);
        }

        @Override
        public UsuarioRolResponseDTO createForCurrentEmpresa(UsuarioRolRequestForCurrentEmpresaDTO request,
                        HttpServletRequest httpRequest) {
                User currentUser = authenticatedUser.getCurrentUser();
                Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

                User usuario = userRepository.findById(request.usuarioId())
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Usuario no encontrado con id " + request.usuarioId()));

                Empresa empresa = empresaRepository.findById(empresaId)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Empresa no encontrada con id " + empresaId));

                Rol rol = empresaRolRepository.findRolByEmpresaIdAndRolId(empresaId, request.rolId())
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "El rol " + request.rolId() + " no está asignado a la empresa "
                                                                + empresaId));

                Long estadoId = request.estadoId() != null ? request.estadoId() : ESTADO_ACTIVO_ID;
                Estado estado = estadoRepository.findById(estadoId)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Estado no encontrado con id " + estadoId));

                boolean existsActivo = usuarioRolRepository
                                .existsByUser_IdAndEmpresa_IdAndRol_IdAndEstado_IdAndFinalizaContratoEnIsNull(
                                                usuario.getId(), empresa.getId(), rol.getId(), ESTADO_ACTIVO_ID);
                if (existsActivo && estadoId.equals(ESTADO_ACTIVO_ID)) {
                        throw new DataIntegrityViolationException(
                                        "El usuario ya tiene este rol activo para la empresa seleccionada");
                }

                UsuarioRol entity = usuarioRolMapper.toEntity(request);

                entity.setUser(usuario);
                entity.setEmpresa(empresa);
                entity.setRol(rol);
                entity.setEstado(estado);

                OffsetDateTime now = OffsetDateTime.now();
                entity.setIniciaContratoEn(
                                request.iniciaContratoEn() != null ? request.iniciaContratoEn() : now);

                entity.setCreatedAt(now);
                entity.setCreatedBy(currentUser);
                entity.setUpdatedAt(null);
                entity.setUpdatedBy(null);
                entity.setDeletedAt(null);
                entity.setDeletedBy(null);

                String requestIp = requestUtils.getClientIp(httpRequest);
                String requestHost = requestUtils.getClientHost(httpRequest);
                entity.setRequestIp(requestIp);
                entity.setRequestHost(requestHost);

                UsuarioRol saved = usuarioRolRepository.save(entity);
                return usuarioRolMapper.toResponse(saved);
        }

        @Override
        public UsuarioRolResponseDTO update(
                        Long id,
                        UsuarioRolRequestDTO request,
                        HttpServletRequest httpRequest) {
                User currentUser = authenticatedUser.getCurrentUser();

                UsuarioRol entity = usuarioRolRepository
                                .findByIdAndDeletedAtIsNullAndEstadoIdNot(id, ESTADO_INACTVIO_ID)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "UsuarioRol no encontrado con id " + id));

                // =========================
                // 1. Determinar IDs objetivo (por si cambian)
                // =========================
                Long targetUsuarioId = (request.usuarioId() != null)
                                ? request.usuarioId()
                                : entity.getUser().getId();

                Long targetEmpresaId = (request.empresaId() != null)
                                ? request.empresaId()
                                : entity.getEmpresa().getId();

                Long targetRolId = (request.rolId() != null)
                                ? request.rolId()
                                : entity.getRol().getId();

                Long targetEstadoId = (request.estadoId() != null)
                                ? request.estadoId()
                                : entity.getEstado().getId();

                // =========================
                // 2. Validar unicidad si queda ACTIVO
                // =========================
                if (ESTADO_ACTIVO_ID.equals(targetEstadoId)) {
                        boolean existsActivo = usuarioRolRepository
                                        .existsByUser_IdAndEmpresa_IdAndRol_IdAndEstado_IdAndFinalizaContratoEnIsNull(
                                                        targetUsuarioId,
                                                        targetEmpresaId,
                                                        targetRolId,
                                                        ESTADO_ACTIVO_ID);

                        boolean seVuelveActivoConNuevaComb =
                                        // pasa de NO activo a ACTIVO
                                        !ESTADO_ACTIVO_ID.equals(entity.getEstado().getId())
                                                        // o cambia usuario/empresa/rol con estado ACTIVO
                                                        || !entity.getUser().getId().equals(targetUsuarioId)
                                                        || !entity.getEmpresa().getId().equals(targetEmpresaId)
                                                        || !entity.getRol().getId().equals(targetRolId);

                        if (existsActivo && seVuelveActivoConNuevaComb) {
                                throw new DataIntegrityViolationException(
                                                "El usuario ya tiene este rol activo para la empresa seleccionada");
                        }
                }

                // =========================
                // 3. Actualizar campos simples (incluye fechas) con el mapper
                // -> actualización REAL: SET_TO_NULL si el DTO trae null
                // =========================
                usuarioRolMapper.updateEntityFromRequest(request, entity);

                // =========================
                // 4. Actualizar relaciones si vienen nuevos IDs
                // =========================
                if (request.usuarioId() != null
                                && !request.usuarioId().equals(entity.getUser().getId())) {
                        User nuevoUsuario = userRepository.findById(request.usuarioId())
                                        .orElseThrow(() -> new EntityNotFoundException(
                                                        "Usuario no encontrado con id " + request.usuarioId()));
                        entity.setUser(nuevoUsuario);
                }

                if (request.empresaId() != null
                                && !request.empresaId().equals(entity.getEmpresa().getId())) {
                        Empresa nuevaEmpresa = empresaRepository.findById(request.empresaId())
                                        .orElseThrow(() -> new EntityNotFoundException(
                                                        "Empresa no encontrada con id " + request.empresaId()));
                        entity.setEmpresa(nuevaEmpresa);
                }

                if (request.rolId() != null
                                && !request.rolId().equals(entity.getRol().getId())) {
                        Rol nuevoRol = rolRepository.findById(request.rolId())
                                        .orElseThrow(() -> new EntityNotFoundException(
                                                        "Rol no encontrado con id " + request.rolId()));
                        entity.setRol(nuevoRol);
                }

                if (request.estadoId() != null
                                && !request.estadoId().equals(entity.getEstado().getId())) {
                        Estado nuevoEstado = estadoRepository.findById(request.estadoId())
                                        .orElseThrow(() -> new EntityNotFoundException(
                                                        "Estado no encontrado con id " + request.estadoId()));
                        entity.setEstado(nuevoEstado);
                }

                // =========================
                // 5. Auditoría
                // =========================
                entity.setUpdatedAt(OffsetDateTime.now());
                entity.setUpdatedBy(currentUser);

                // =========================
                // 6. IP y Host desde RequestUtils
                // =========================
                entity.setRequestIp(requestUtils.getClientIp(httpRequest));
                entity.setRequestHost(requestUtils.getClientHost(httpRequest));

                UsuarioRol saved = usuarioRolRepository.save(entity);
                return usuarioRolMapper.toResponse(saved);
        }

        @Override
        public UsuarioRolResponseDTO updateForCurrentEmpresa(
                        Long id,
                        UsuarioRolRequestForCurrentEmpresaDTO request,
                        HttpServletRequest httpRequest) {

                User currentUser = authenticatedUser.getCurrentUser();
                Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

                // Aseguramos que el registro pertenece a la empresa del token
                UsuarioRol entity = usuarioRolRepository
                                .findByIdAndEmpresaIdAndDeletedAtIsNullAndEstadoIdNot(id, empresaId, ESTADO_INACTVIO_ID)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "UsuarioRol no encontrado con id " + id + " para la empresa actual"));

                // =========================
                // 1. Determinar IDs objetivo (por si cambian)
                // =========================
                Long targetUsuarioId = (request.usuarioId() != null)
                                ? request.usuarioId()
                                : entity.getUser().getId();

                Long targetRolId = (request.rolId() != null)
                                ? request.rolId()
                                : entity.getRol().getId();

                Long targetEstadoId = (request.estadoId() != null)
                                ? request.estadoId()
                                : entity.getEstado().getId();

                // La empresa objetivo SIEMPRE es la del token
                Long targetEmpresaId = empresaId;

                // =========================
                // 2. Validar unicidad si queda ACTIVO
                // =========================
                if (ESTADO_ACTIVO_ID.equals(targetEstadoId)) {

                        boolean existsActivo = usuarioRolRepository
                                        .existsByUser_IdAndEmpresa_IdAndRol_IdAndEstado_IdAndFinalizaContratoEnIsNull(
                                                        targetUsuarioId,
                                                        targetEmpresaId,
                                                        targetRolId,
                                                        ESTADO_ACTIVO_ID);

                        boolean seVuelveActivoConNuevaComb =
                                        // pasa de NO activo a ACTIVO
                                        !ESTADO_ACTIVO_ID.equals(entity.getEstado().getId())
                                                        // o cambia usuario/rol con estado ACTIVO
                                                        || !entity.getUser().getId().equals(targetUsuarioId)
                                                        || !entity.getRol().getId().equals(targetRolId);
                        // empresa NO cambia, siempre es la misma del token

                        if (existsActivo && seVuelveActivoConNuevaComb) {
                                throw new DataIntegrityViolationException(
                                                "El usuario ya tiene este rol activo para la empresa seleccionada");
                        }
                }

                // =========================
                // 3. Actualizar campos simples con el mapper
                // (sin tocar empresa, ni auditoría)
                // =========================
                usuarioRolMapper.updateEntityFromDTO(request, entity);

                // =========================
                // 4. Actualizar relaciones si vienen nuevos IDs
                // =========================

                // Usuario
                if (request.usuarioId() != null
                                && !request.usuarioId().equals(entity.getUser().getId())) {

                        User nuevoUsuario = userRepository.findById(request.usuarioId())
                                        .orElseThrow(() -> new EntityNotFoundException(
                                                        "Usuario no encontrado con id " + request.usuarioId()));
                        entity.setUser(nuevoUsuario);
                }

                // Empresa NO se cambia nunca en este flujo (empresa = token)

                // Rol (validando que pertenezca a la empresa del token)
                if (request.rolId() != null
                                && !request.rolId().equals(entity.getRol().getId())) {

                        Rol nuevoRol = empresaRolRepository
                                        .findRolByEmpresaIdAndRolId(empresaId, request.rolId())
                                        .orElseThrow(() -> new EntityNotFoundException(
                                                        "El rol " + request.rolId() + " no está asignado a la empresa "
                                                                        + empresaId));

                        entity.setRol(nuevoRol);
                }

                // Estado
                if (request.estadoId() != null
                                && !request.estadoId().equals(entity.getEstado().getId())) {

                        Long estadoId = request.estadoId();
                        Estado nuevoEstado = estadoRepository.findById(estadoId)
                                        .orElseThrow(() -> new EntityNotFoundException(
                                                        "Estado no encontrado con id " + estadoId));
                        entity.setEstado(nuevoEstado);
                }

                // =========================
                // 5. Auditoría
                // =========================
                entity.setUpdatedAt(OffsetDateTime.now());
                entity.setUpdatedBy(currentUser);

                // =========================
                // 6. IP y Host desde RequestUtils
                // =========================
                entity.setRequestIp(requestUtils.getClientIp(httpRequest));
                entity.setRequestHost(requestUtils.getClientHost(httpRequest));

                UsuarioRol saved = usuarioRolRepository.save(entity);
                return usuarioRolMapper.toResponse(saved);
        }

        @Override
        public void delete(Long id) {
                User currentUser = authenticatedUser.getCurrentUser();

                UsuarioRol entity = usuarioRolRepository
                                .findByIdAndDeletedAtIsNullAndEstadoIdNot(id, ESTADO_INACTVIO_ID)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "UsuarioRol no encontrado con id " + id));

                Long estadoId = ESTADO_INACTVIO_ID;
                Estado estado = estadoRepository.findById(estadoId)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Estado no encontrado con id " + estadoId));

                entity.setDeletedAt(OffsetDateTime.now());
                entity.setDeletedBy(currentUser);
                entity.setEstado(estado);

                usuarioRolRepository.save(entity);
        }

        @Override
        public void deleteForCurrentEmpresa(Long id) {
                User currentUser = authenticatedUser.getCurrentUser();
                Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

                UsuarioRol entity = usuarioRolRepository
                                .findByIdAndEmpresaIdAndDeletedAtIsNullAndEstadoIdNot(id, empresaId, ESTADO_INACTVIO_ID)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "UsuarioRol no encontrado con id " + id));

                Long estadoId = ESTADO_INACTVIO_ID;
                Estado estado = estadoRepository.findById(estadoId)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Estado no encontrado con id " + estadoId));

                entity.setDeletedAt(OffsetDateTime.now());
                entity.setDeletedBy(currentUser);
                entity.setEstado(estado);

                usuarioRolRepository.save(entity);
        }

        @Override
        public void toggleEstado(Long id, Long empresaId) {
                User currentUser = authenticatedUser.getCurrentUser();

                UsuarioRol entity = usuarioRolRepository
                                .findByIdAndEmpresaIdAndDeletedAtIsNull(id, empresaId)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "UsuarioRol no encontrado con id " + id + " para la empresa " + empresaId));

                boolean isActivo = ESTADO_ACTIVO_ID.equals(entity.getEstado().getId());
                Long nuevoEstadoId = isActivo ? ESTADO_INACTVIO_ID : ESTADO_ACTIVO_ID;

                Estado nuevoEstado = estadoRepository.findById(nuevoEstadoId)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Estado no encontrado con id " + nuevoEstadoId));

                entity.setEstado(nuevoEstado);
                entity.setUpdatedAt(OffsetDateTime.now());
                entity.setUpdatedBy(currentUser);

                usuarioRolRepository.save(entity);
        }

        @Override
        public void toggleEstadoForCurrentEmpresa(Long id) {
                User currentUser = authenticatedUser.getCurrentUser();
                Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

                UsuarioRol entity = usuarioRolRepository
                                .findByIdAndEmpresaIdAndDeletedAtIsNull(id, empresaId)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "UsuarioRol no encontrado con id " + id + " para la empresa actual"));

                boolean isActivo = ESTADO_ACTIVO_ID.equals(entity.getEstado().getId());
                Long nuevoEstadoId = isActivo ? ESTADO_INACTVIO_ID : ESTADO_ACTIVO_ID;

                Estado nuevoEstado = estadoRepository.findById(nuevoEstadoId)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Estado no encontrado con id " + nuevoEstadoId));

                entity.setEstado(nuevoEstado);
                entity.setUpdatedAt(OffsetDateTime.now());
                entity.setUpdatedBy(currentUser);

                usuarioRolRepository.save(entity);
        }

}
