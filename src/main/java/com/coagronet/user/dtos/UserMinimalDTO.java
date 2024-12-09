package com.coagronet.user.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserMinimalDTO {
    private Long Id;
    private String username;
}
