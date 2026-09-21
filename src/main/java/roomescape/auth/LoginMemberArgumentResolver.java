package roomescape.auth;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import roomescape.member.Member;
import roomescape.member.MemberService;

@Component
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {
    private final MemberService memberService;
    private final TokenProvider tokenProvider;
    private final TokenCookieExtractor tokenCookieExtractor;

    public LoginMemberArgumentResolver(MemberService memberService, TokenProvider tokenProvider, TokenCookieExtractor tokenCookieExtractor) {
        this.memberService = memberService;
        this.tokenProvider = tokenProvider;
        this.tokenCookieExtractor = tokenCookieExtractor;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(LoginMember.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);

        String token = tokenCookieExtractor.extractToken(request.getCookies());

        Long memberId = tokenProvider.extractMemberId(token);

        Member member = memberService.getMemberById(memberId);

        return new LoginMember(member.getId(), member.getName(), member.getEmail(), member.getRole().name());
    }
}
