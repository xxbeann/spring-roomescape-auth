package roomescape.domain;

import java.time.LocalDate;
import roomescape.exception.WrongMarketAccessException;

public class Reservation {

    private final Long id;
    private final Long memberId;
    private final LocalDate date;
    private final ReservationTime time;
    private final Long themeId;
    private final Long marketId;

    public Reservation(Long id, Long memberId, LocalDate date, ReservationTime time, Long themeId, Long marketId) {
        validateMemberId(memberId);
        validateDate(date);
        validateTime(time);
        validateThemeId(themeId);
        validateMarketId(marketId);

        this.id = id;
        this.memberId = memberId;
        this.date = date;
        this.time = time;
        this.themeId = themeId;
        this.marketId = marketId;
    }

    public Long getId() {
        return id;
    }

    public Long getMemberId() {
        return memberId;
    }

    public ReservationTime getTime() {
        return time;
    }

    public LocalDate getDate() {
        return date;
    }

    public Long getThemeId() {
        return themeId;
    }

    public Long getMarketId() {
        return marketId;
    }

    public void validateMarketOwnership(Member member) {
        if (!this.marketId.equals(member.getMarketId())) {
            throw new WrongMarketAccessException();
        }
    }

    private void validateMemberId(Long memberId) {
        if (memberId == null) {
            throw new IllegalArgumentException("회원 ID는 비어 있을 수 없습니다.");
        }

        if (memberId <= 0) {
            throw new IllegalArgumentException("회원 ID는 양수여야 합니다.");
        }
    }

    private void validateDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("날짜는 비어 있을 수 없습니다.");
        }
    }

    private void validateTime(ReservationTime time) {
        if (time == null) {
            throw new IllegalArgumentException("예약시간은 비어 있을 수 없습니다.");
        }
    }

    private void validateThemeId(Long themeId) {
        if (themeId == null) {
            throw new IllegalArgumentException("테마 ID는 비어 있을 수 없습니다.");
        }

        if (themeId <= 0) {
            throw new IllegalArgumentException("테마 ID는 양수여야 합니다.");
        }
    }

    private void validateMarketId(Long marketId) {
        if (marketId == null) {
            throw new IllegalArgumentException("매장 ID는 비어 있을 수 없습니다.");
        }
        if (marketId <= 0) {
            throw new IllegalArgumentException("매장 ID는 양수여야 합니다.");
        }
    }
}
