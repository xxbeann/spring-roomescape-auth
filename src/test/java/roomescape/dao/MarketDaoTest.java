package roomescape.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import roomescape.domain.Market;

@JdbcTest
@ActiveProfiles("test")
@Import(MarketDao.class)
public class MarketDaoTest {

    private static final String INSERT_THREE_MARKETS_SQL = """
            INSERT INTO market (id, name)
            VALUES (1, '강남점'),
                   (2, '홍대점'),
                   (3, '판교점');
            """;

    @Autowired
    private MarketDao marketDao;

    @Test
    @Sql(statements = INSERT_THREE_MARKETS_SQL)
    void 모든_매장을_조회한다() {
        List<Market> markets = marketDao.findAllMarkets();

        assertThat(markets).hasSize(3);
        assertThat(markets)
                .extracting(Market::getId, Market::getName)
                .containsExactlyInAnyOrder(
                        tuple(1L, "강남점"),
                        tuple(2L, "홍대점"),
                        tuple(3L, "판교점")
                );
    }

    @Test
    @Sql(statements = INSERT_THREE_MARKETS_SQL)
    void ID로_매장을_조회한다() {
        Market market = marketDao.findById(2L);

        assertThat(market.getId()).isEqualTo(2L);
        assertThat(market.getName()).isEqualTo("홍대점");
    }

    @Test
    void 매장이_없으면_빈_리스트를_반환한다() {
        List<Market> markets = marketDao.findAllMarkets();

        assertThat(markets).isEmpty();
    }
}
