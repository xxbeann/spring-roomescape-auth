package roomescape.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.domain.Market;
import roomescape.dto.response.MarketResponse;
import roomescape.service.MarketService;

@RestController
@RequestMapping("/api/v1/markets")
public class MarketController {

    private final MarketService marketService;

    public MarketController(MarketService marketService) {
        this.marketService = marketService;
    }

    @GetMapping
    public ResponseEntity<List<MarketResponse>> getMarkets() {
        List<Market> markets = marketService.getMarkets();
        return ResponseEntity.ok().body(MarketResponse.fromAll(markets));
    }
}
