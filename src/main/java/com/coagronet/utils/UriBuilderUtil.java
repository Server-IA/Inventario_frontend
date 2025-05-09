package com.coagronet.utils;

import java.net.URI;

import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class UriBuilderUtil {

    public URI buildTipoIdentificacionUri(Long id, UriComponentsBuilder ucb) {
        return ucb.path("/api/v1/tipo_identificacion/{id}")
                .buildAndExpand(id)
                .toUri();
    }

    public URI buildDepartamentoUri(Long id, UriComponentsBuilder ucb) {
        return ucb.path("/api/v1/departamento/{id}")
                .buildAndExpand(id)
                .toUri();
    }

    public URI buildTipoEvaluacionUri(Long id, UriComponentsBuilder ucb) {
        return ucb.path("/api/v1/tipo_evaluacion/{id}")
                .buildAndExpand(id)
                .toUri();
    }

    public URI buildSedeUri(Long id, UriComponentsBuilder ucb) {
        return ucb.path("/api/v1/sede/{id}")
                .buildAndExpand(id)
                .toUri();
    }

    public URI buildBloqueUri(Long id, UriComponentsBuilder ucb) {
        return ucb.path("/api/v1/bloque/{id}")
                .buildAndExpand(id)
                .toUri();
    }

}
