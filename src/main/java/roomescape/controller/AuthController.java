package roomescape.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.domain.Member;
import roomescape.service.AuthService;

@RequestMapping("/api/v1/auth")
@RestController
public class AuthController {

    private static final String SESSION_KEY = "USER";
    private static final String USERNAME_FIELD = "email";
    private static final String PASSWORD_FIELD = "password";

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<Void> sessionLogin(HttpServletRequest request, HttpSession session) {
        String email = request.getParameter(USERNAME_FIELD);
        String password = request.getParameter(PASSWORD_FIELD);
        Member member = authService.checkValidLogin(email,password);
        session.setAttribute(SESSION_KEY, member.getId());
        return ResponseEntity.ok().build();
    }
}
