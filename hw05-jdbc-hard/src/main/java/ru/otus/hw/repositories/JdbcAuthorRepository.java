package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Author;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcAuthorRepository implements AuthorRepository {

    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public List<Author> findAll() {
        String sql = "select id, full_name from authors";

        return jdbc.query(sql, Map.of(), new AuthorRowMapper());
    }

    @Override
    public Optional<Author> findById(long id) {
        String sql = "select id, full_name from authors where id = :id";

        List<Author> authors = jdbc.query(sql, Map.of("id", id), new AuthorRowMapper());

        return authors.isEmpty() ? Optional.empty() : Optional.of(authors.get(0));
    }

    private static class AuthorRowMapper implements RowMapper<Author> {

        @Override
        public Author mapRow(ResultSet rs, int i) throws SQLException {
            return new Author(
                    rs.getLong("id"),
                    rs.getString("full_name")
            );
        }
    }
}
