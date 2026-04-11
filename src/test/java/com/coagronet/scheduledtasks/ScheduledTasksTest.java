package com.coagronet.scheduledtasks;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.coagronet.usuariorol.utils.UsuarioRolContratoService;
import com.coagronet.verificationToken.services.TokenCleanupService;

@ExtendWith(MockitoExtension.class)
class ScheduledTasksTest {

    @Mock
    private TokenCleanupService tokenCleanupService;

    @Mock
    private UsuarioRolContratoService usuarioRolContratoService;

    @InjectMocks
    private ScheduledTasks scheduledTasks;

    @Test
    void cleanUpExpiredTokens_delegatesToTokenCleanupService() {
        scheduledTasks.cleanUpExpiredTokens();

        verify(tokenCleanupService).deleteExpiredTokens();
    }

    @Test
    void procesarContratosUsuarioRol_delegatesToContratoService() {
        scheduledTasks.procesarContratosUsuarioRol();

        verify(usuarioRolContratoService).procesarContratos();
    }

}