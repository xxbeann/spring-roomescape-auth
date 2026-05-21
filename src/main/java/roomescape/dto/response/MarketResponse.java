package roomescape.dto.response;

import java.util.List;
import roomescape.domain.Market;

public record MarketResponse(
        Long id,
        String name
) {
    public static MarketResponse from(Market market) {
        return new MarketResponse(market.getId(), market.getName());
    }

    public static List<MarketResponse> fromAll(List<Market> markets) {
        return markets.stream()
                .map(MarketResponse::from)
                .toList();
    }
}
