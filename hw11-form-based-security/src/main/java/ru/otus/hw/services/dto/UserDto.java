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

    private String username;

    private String authority;

    public UserDto (CustomUser user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.authority = user.getAuthority();
    }
}
