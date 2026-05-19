package roomescape.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.Test;

public class ReservationTest {

    @Test
    void memberId가_null이면_예약을_생성할_수_없다() {
        assertThatThrownBy(() -> new Reservation(
                1L,
                null,
                LocalDate.now().plusDays(1),
                new ReservationTime(1L, LocalTime.of(10, 0)),
                1L
        )).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void memberId가_음수이면_예약을_생성할_수_없다() {
        assertThatThrownBy(() -> new Reservation(
                1L,
                -1L,
                LocalDate.now().plusDays(1),
                new ReservationTime(1L, LocalTime.of(10, 0)),
                1L
        )).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 날짜가_null이면_예약을_생성할_수_없다() {
        assertThatThrownBy(() -> new Reservation(
                1L,
                1L,
                null,
                new ReservationTime(1L, LocalTime.of(10, 0)),
                1L
        )).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 예약시간이_null이면_예약을_생성할_수_없다() {
        assertThatThrownBy(() -> new Reservation(
                1L,
                1L,
                LocalDate.now().plusDays(1),
                null,
                1L
        )).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void themeId가_null이면_예약을_생성할_수_없다() {
        assertThatThrownBy(() -> new Reservation(
                1L,
                1L,
                LocalDate.now().plusDays(1),
                new ReservationTime(1L, LocalTime.of(10, 0)),
                null
        )).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void themeId가_음수이면_예약을_생성할_수_없다() {
        assertThatThrownBy(() -> new Reservation(
                1L,
                1L,
                LocalDate.now().plusDays(1),
                new ReservationTime(1L, LocalTime.of(10, 0)),
                -1L
        )).isInstanceOf(IllegalArgumentException.class);
    }
}
