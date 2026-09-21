package roomescape.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import roomescape.member.Member;
import roomescape.member.MemberService;
import roomescape.member.Role;

@Component
public class AdminInterceptor implements HandlerInterceptor {
    private final TokenProvider tokenProvider;
    private final MemberService memberService;
    private final TokenCookieExtractor tokenCookieExtractor;

    public AdminInterceptor(TokenProvider tokenProvider, MemberService memberService, TokenCookieExtractor tokenCookieExtractor) {
        this.tokenProvider = tokenProvider;
        this.memberService = memberService;
        this.tokenCookieExtractor = tokenCookieExtractor;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!requiresAdmin(request, handler)) {
            return true;
        }

        String token = tokenCookieExtractor.extractToken(request.getCookies());
        Long memberId = tokenProvider.extractMemberId(token);
        Member member = memberService.getMemberById(memberId);

        if (member.getRole() != Role.ADMIN) {
            response.setStatus(401);
            return false;
        }

        return true;
    }

    private boolean requiresAdmin(HttpServletRequest request, Object handler) {
        String requestUri = request.getRequestURI();

        if (requestUri.equals("/admin") || requestUri.startsWith("/admin/")) {
            return true;
        }

        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return false;
        }

        return handlerMethod.hasMethodAnnotation(AdminOnly.class);
    }
}
