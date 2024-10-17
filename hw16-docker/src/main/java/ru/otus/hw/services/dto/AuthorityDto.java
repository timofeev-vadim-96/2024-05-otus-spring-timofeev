package ru.otus.hw.services.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.otus.hw.models.Authority;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorityDto {
    private Long id;

    private String authority;

    AuthorityDto(Authority authority) {
        this.id = authority.getId();
        this.authority = authority.getAuthority();
    }
}
