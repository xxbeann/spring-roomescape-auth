package roomescape.dao;

import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import roomescape.domain.Market;

@Repository
public class MarketDao {

    private final JdbcTemplate jdbcTemplate;

    public MarketDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Market> findAllMarkets() {
        String sql = "SELECT id, name FROM market";
        return jdbcTemplate.query(sql, marketRowMapper);
    }

    public Market findById(Long id) {
        String sql = "SELECT id, name FROM market WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, marketRowMapper, id);
    }

    private final RowMapper<Market> marketRowMapper = (rs, rowNum) ->
            new Market(rs.getLong("id"), rs.getString("name"));
}
