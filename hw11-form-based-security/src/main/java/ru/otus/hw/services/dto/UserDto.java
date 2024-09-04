package ru.otus.hw.services.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.otus.hw.models.CustomUser;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;

    private String principal;

    private String credentials;

    private String authority;

    public UserDto (CustomUser user) {
        this.id = user.getId();
        this.principal = user.getPrincipal();
        this.credentials = user.getCredentials();
        this.authority = user.getAuthority();
    }
}
