package roomescape.auth;

import jakarta.servlet.http.Cookie;
import org.springframework.stereotype.Component;

@Component
public class TokenCookieExtractor {
    public String extractToken(Cookie[] cookies) {
        if (cookies == null) {
            throw new RuntimeException("인증 정보가 존재하지 않습니다.");
        }

        for (Cookie cookie : cookies) {
            if ("token".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        throw new RuntimeException("토큰이 존재하지 않습니다.");
    }
}
