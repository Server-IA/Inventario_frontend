// package com.coagronet.pais.services;

// import com.coagronet.pais.dtos.PaisDTO;
// import com.coagronet.pais.mappers.PaisMapper;
// import com.coagronet.pais.repositories.PaisRepository;
// import com.coagronet.user.User;
// import com.coagronet.empresa.Empresa;
// import com.coagronet.utils.AuthenticationService;
// import com.coagronet.utils.UserEmpresaService;
// import com.coagronet.estado.repositories.EstadoRepository;
// import com.coagronet.estado.Estado;
// import com.coagronet.pais.Pais;
// import com.coagronet.exceptionHandler.BadRequestException;
// import com.coagronet.exceptionHandler.NotFoundException;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;

// import java.util.Optional;
// import java.util.Collections;

// import static org.mockito.Mockito.*;
// import static org.assertj.core.api.Assertions.*;

// @ExtendWith(MockitoExtension.class)
// public class PaisServiceTest {

//     @Mock
//     PaisRepository paisRepository;
//     @Mock
//     PaisMapper paisMapper;
//     @Mock
//     AuthenticationService authenticationService;
//     @Mock
//     UserEmpresaService userEmpresaService;
//     @Mock
//     EstadoRepository estadoRepository;

//     @InjectMocks
//     PaisService paisService;

//     @Test
//     void testFindAll_ReturnsPaisDTOList() {
//         User user = new User();
//         Empresa empresa = Empresa.builder().id(1L).build();

//         when(authenticationService.getAuthenticatedUser()).thenReturn(user);
//         when(userEmpresaService.getEmpresaFromUser(user)).thenReturn(empresa);
//         when(paisRepository.findByEmpresaIdOrderByIdAsc(1L)).thenReturn(Collections.emptyList());

//         assertThat(paisService.findAll()).isEmpty();
//         verify(paisRepository).findByEmpresaIdOrderByIdAsc(1L);
//     }

//     @Test
//     void testCreate_Successful() {
//         User user = new User();
//         Empresa empresa = Empresa.builder().id(1L).build();
//         PaisDTO paisDTO = new PaisDTO(null, "Colombia", 57L, "COL", null, 1L);
//         Estado estado = new Estado();
//         estado.setId(1L);

//         Pais paisEntity = new Pais(); // puedes construirlo con builder si lo tienes
//         PaisDTO savedDTO = new PaisDTO(1L, "Colombia", 57L, "COL", 1L, 1L);

//         when(authenticationService.getAuthenticatedUser()).thenReturn(user);
//         when(userEmpresaService.getEmpresaFromUser(user)).thenReturn(empresa);
//         when(estadoRepository.findById(1L)).thenReturn(Optional.of(estado));
//         when(paisMapper.toEntity(any(PaisDTO.class))).thenReturn(paisEntity);
//         when(paisRepository.save(paisEntity)).thenReturn(paisEntity);
//         when(paisMapper.toDTO(paisEntity)).thenReturn(savedDTO);

//         PaisDTO result = paisService.create(paisDTO);

//         assertThat(result).isNotNull();
//         assertThat(result.getNombre()).isEqualTo("Colombia");
//         verify(paisRepository).save(any(Pais.class));
//     }

//     @Test
//     void testCreate_FailsWhenEstadoNotFound() {
//         User user = new User();
//         Empresa empresa = Empresa.builder().id(1L).build();
//         PaisDTO paisDTO = new PaisDTO(null, "Colombia", 57L, "COL", null, 99L);

//         when(authenticationService.getAuthenticatedUser()).thenReturn(user);
//         when(userEmpresaService.getEmpresaFromUser(user)).thenReturn(empresa);
//         when(estadoRepository.findById(99L)).thenReturn(Optional.empty());

//         assertThatThrownBy(() -> paisService.create(paisDTO))
//                 .isInstanceOf(BadRequestException.class)
//                 .hasMessageContaining("El estado no es válido");
//     }

//     @Test
//     void testUpdate_FailsWhenPaisNotFound() {
//         User user = new User();
//         Empresa empresa = Empresa.builder().id(1L).build();
//         PaisDTO paisDTO = new PaisDTO(null, "Colombia", 57L, "COL", null, 1L);

//         when(authenticationService.getAuthenticatedUser()).thenReturn(user);
//         when(userEmpresaService.getEmpresaFromUser(user)).thenReturn(empresa);
//         when(paisRepository.findByIdAndEmpresaId(1L, 1L)).thenReturn(Optional.empty());

//         assertThatThrownBy(() -> paisService.update(1L, paisDTO))
//                 .isInstanceOf(NotFoundException.class)
//                 .hasMessageContaining("Pais no encontrado");
//     }

//     @Test
//     void testDelete_FailsWhenPaisNotFound() {
//         User user = new User();
//         Empresa empresa = Empresa.builder().id(1L).build();

//         when(authenticationService.getAuthenticatedUser()).thenReturn(user);
//         when(userEmpresaService.getEmpresaFromUser(user)).thenReturn(empresa);
//         when(paisRepository.findByIdAndEmpresaId(1L, 1L)).thenReturn(Optional.empty());

//         assertThatThrownBy(() -> paisService.delete(1L))
//                 .isInstanceOf(NotFoundException.class)
//                 .hasMessageContaining("Pais no encontrado");
//     }

//     @Test
//     void testDelete_Successful() {
//         User user = new User();
//         Empresa empresa = Empresa.builder().id(1L).build();
//         Pais pais = new Pais();

//         when(authenticationService.getAuthenticatedUser()).thenReturn(user);
//         when(userEmpresaService.getEmpresaFromUser(user)).thenReturn(empresa);
//         when(paisRepository.findByIdAndEmpresaId(1L, 1L)).thenReturn(Optional.of(pais));

//         paisService.delete(1L);

//         verify(paisRepository).deleteById(1L);
//     }
// }