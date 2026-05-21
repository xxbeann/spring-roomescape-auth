package roomescape.domain;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

public class MarketTest {

    @Test
    void 정상_생성된다() {
        assertThatCode(() -> new Market(1L, "강남점"))
                .doesNotThrowAnyException();
    }

    @Test
    void 이름이_null이면_예외() {
        assertThatThrownBy(() -> new Market(1L, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("매장 이름");
    }

    @Test
    void 이름이_공백이면_예외() {
        assertThatThrownBy(() -> new Market(1L, "   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("매장 이름");
    }

    @Test
    void 이름이_50자_초과면_예외() {
        String tooLong = "a".repeat(51);
        assertThatThrownBy(() -> new Market(1L, tooLong))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
