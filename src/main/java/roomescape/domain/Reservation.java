package roomescape.domain;

import java.time.LocalDate;

public class Reservation {

    private final Long id;
    private final Long memberId;
    private final LocalDate date;
    private final ReservationTime time;
    private final Long themeId;

    public Reservation(Long id, Long memberId, LocalDate date, ReservationTime time, Long themeId) {
        validateMemberId(memberId);
        validateDate(date);
        validateTime(time);
        validateThemeId(themeId);

        this.id = id;
        this.memberId = memberId;
        this.date = date;
        this.time = time;
        this.themeId = themeId;
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
}
