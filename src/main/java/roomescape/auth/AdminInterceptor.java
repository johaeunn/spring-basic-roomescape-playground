package roomescape.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import roomescape.member.Member;
import roomescape.member.MemberService;

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
        String token = tokenCookieExtractor.extractToken(request.getCookies());

        Long memberId = tokenProvider.extractMemberId(token);

        Member member = memberService.getMemberById(memberId);

        if (!member.getRole().equals("ADMIN")) {
            response.setStatus(401);
            return false;
        }

        return true;
    }
}
