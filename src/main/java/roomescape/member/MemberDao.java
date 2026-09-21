package roomescape.member;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class MemberDao {
    private JdbcTemplate jdbcTemplate;

    public MemberDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Member save(Member member) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            var ps = connection.prepareStatement("INSERT INTO member(name, email, password, role) VALUES (?, ?, ?, ?)", new String[]{"id"});
            ps.setString(1, member.getName());
            ps.setString(2, member.getEmail());
            ps.setString(3, member.getPassword());
            ps.setString(4, member.getRole());
            return ps;
        }, keyHolder);

        return new Member(keyHolder.getKey().longValue(), member.getName(), member.getEmail(), "USER");
    }

    public Optional<Member> findByEmailAndPassword(String email, String password) {
        List<Member> members = jdbcTemplate.query(
                "SELECT id, name, email, role FROM member WHERE email = ? AND password = ?",
                (rs, rowNum) -> new Member(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("role")
                ),
                email, password
        );

        return members.stream().findFirst();
    }

    public Member findByName(String name) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT id, name, email, role FROM member WHERE name = ?",
                    (rs, rowNum) -> new Member(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("role")
                    ),
                    name
            );
        } catch (EmptyResultDataAccessException e) {
            throw new MemberNotFoundException();
        }
    }

    public Member findById(Long id) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT id, name, email, role FROM member WHERE id = ?",
                    (rs, rowNum) -> new Member(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("role")
                    ),
                    id
            );
        } catch (EmptyResultDataAccessException e) {
            throw new MemberNotFoundException();
        }
    }
}
