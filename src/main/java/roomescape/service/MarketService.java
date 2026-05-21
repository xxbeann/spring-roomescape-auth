package roomescape.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.dao.MarketDao;
import roomescape.domain.Market;

@Service
@Transactional(readOnly = true)
public class MarketService {

    private final MarketDao marketDao;

    public MarketService(MarketDao marketDao) {
        this.marketDao = marketDao;
    }

    public List<Market> getMarkets() {
        return marketDao.findAllMarkets();
    }
}
