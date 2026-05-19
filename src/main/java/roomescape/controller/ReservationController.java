package roomescape.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.domain.Reservation;
import roomescape.dto.request.ReservationCreateRequest;
import roomescape.dto.request.ReservationUpdateRequest;
import roomescape.dto.response.ReservationResponse;
import roomescape.service.ReservationService;

@RequestMapping("/api/v1/reservations")
@RestController
public class ReservationController {

    private static final String SESSION_KEY = "USER";

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getReservations(HttpSession session) {
        Long memberId = (Long) session.getAttribute(SESSION_KEY);

        List<Reservation> reservations = reservationService.getReservations(memberId);
        List<ReservationResponse> reservationResponses = ReservationResponse.fromAll(reservations);
        return ResponseEntity.ok().body(reservationResponses);
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(
            @Valid @RequestBody ReservationCreateRequest reservationCreateRequest,
            HttpSession session) {
        Long memberId = (Long) session.getAttribute(SESSION_KEY);

        Reservation savedReservation = reservationService.createReservation(
                memberId,
                reservationCreateRequest.date(),
                reservationCreateRequest.timeId(),
                reservationCreateRequest.themeId()
        );
        ReservationResponse reservationResponse = ReservationResponse.from(savedReservation);
        return ResponseEntity.created(URI.create("/api/v1/reservations/" + reservationResponse.id()))
                .body(reservationResponse);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ReservationResponse> updateReservation(
            @PathVariable Long id,
            @Valid @RequestBody ReservationUpdateRequest reservationUpdateRequest,
            HttpSession session) {
        Long memberId = (Long) session.getAttribute(SESSION_KEY);

        Reservation updatedReservation = reservationService.updateReservation(
                id,
                reservationUpdateRequest.date(),
                memberId,
                reservationUpdateRequest.timeId()
        );
        ReservationResponse reservationResponse = ReservationResponse.from(updatedReservation);
        return ResponseEntity.ok().body(reservationResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id, HttpSession session) {
        Long memberId = (Long) session.getAttribute(SESSION_KEY);

        reservationService.deleteReservation(id, memberId);
        return ResponseEntity.noContent().build();
    }
}
