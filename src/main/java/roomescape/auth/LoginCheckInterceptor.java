package roomescape.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;
import roomescape.exception.UnauthorizedException;

public class LoginCheckInterceptor implements HandlerInterceptor {

    private static final String SESSION_KEY = "USER";

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) throws Exception {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(SESSION_KEY) == null) {
            throw new UnauthorizedException();
        }
        return true;
    }
}
