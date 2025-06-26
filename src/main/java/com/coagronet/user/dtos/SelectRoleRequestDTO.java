package com.coagronet.user.dtos;

import lombok.Data;

@Data
public class SelectRoleRequestDTO {
    private String username;
    private Long empresaId;
    private Long rolId;
}
