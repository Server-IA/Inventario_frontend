package com.coagronet.utils;

import java.net.URI;

import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class UriBuilderUtil {

    public URI buildTipoEvaluacionUri(Integer id, UriComponentsBuilder ucb) {
        return ucb.path("/api/v1/tipo_evaluacion/{id}")
                .buildAndExpand(id)
                .toUri();
    }

    public URI buildSedeUri(Long id, UriComponentsBuilder ucb) {
        return ucb.path("/api/v1/sede/{id}")
                .buildAndExpand(id)
                .toUri();
    }

}
