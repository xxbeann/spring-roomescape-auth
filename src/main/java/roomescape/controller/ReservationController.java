package roomescape.controller;

import jakarta.servlet.http.HttpServletRequest;
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
import roomescape.auth.JwtTokenProvider;
import roomescape.auth.TokenExtractor;
import roomescape.domain.Reservation;
import roomescape.dto.request.ReservationCreateRequest;
import roomescape.dto.request.ReservationUpdateRequest;
import roomescape.dto.response.ReservationResponse;
import roomescape.exception.UnauthorizedException;
import roomescape.service.ReservationService;

@RequestMapping("/api/v1/reservations")
@RestController
public class ReservationController {

    private final JwtTokenProvider jwtTokenProvider;
    private final ReservationService reservationService;

    public ReservationController(JwtTokenProvider jwtTokenProvider, ReservationService reservationService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.reservationService = reservationService;
    }

    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getReservations(HttpServletRequest request) {
        Long memberId = extractMemberId(request);

        List<Reservation> reservations = reservationService.getReservations(memberId);
        List<ReservationResponse> reservationResponses = ReservationResponse.fromAll(reservations);
        return ResponseEntity.ok().body(reservationResponses);
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(
            @Valid @RequestBody ReservationCreateRequest reservationCreateRequest,
            HttpServletRequest request) {
        Long memberId = extractMemberId(request);

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
            HttpServletRequest request) {
        Long memberId = extractMemberId(request);

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
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id, HttpServletRequest request) {
        Long memberId = extractMemberId(request);

        reservationService.deleteReservation(id, memberId);
        return ResponseEntity.noContent().build();
    }

    private Long extractMemberId(HttpServletRequest request) {
        String token = TokenExtractor.extract(request);
        if (token == null) {
            throw new UnauthorizedException();
        }
        return jwtTokenProvider.getMemberId(token);
    }
}
