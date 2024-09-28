package ru.otus.hw.services.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.otus.hw.models.CustomUser;

import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;

    private String username;

    private Set<AuthorityDto> authorities;

    public UserDto(CustomUser user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.authorities = user.getAuthorities().stream()
                .map(AuthorityDto::new)
                .collect(Collectors.toSet());
    }
}
