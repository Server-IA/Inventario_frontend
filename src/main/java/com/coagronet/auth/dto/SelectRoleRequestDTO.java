package com.coagronet.auth.dto;

import lombok.Data;

@Data
public class SelectRoleRequestDTO {
    private String username;
    private Long empresaId;
    private Long rolId;
}
