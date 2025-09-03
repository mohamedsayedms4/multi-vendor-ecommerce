    package org.example.ecommerce.infrastructure.filter;

    import jakarta.servlet.FilterChain;
    import jakarta.servlet.ServletException;
    import jakarta.servlet.http.Cookie;
    import jakarta.servlet.http.HttpServletRequest;
    import jakarta.servlet.http.HttpServletResponse;
    import org.springframework.security.web.csrf.CsrfToken;
    import org.springframework.stereotype.Component;
    import org.springframework.web.filter.OncePerRequestFilter;
    import org.springframework.web.util.WebUtils;

    import java.io.IOException;
    @Component
    public class CsrfCookieFilter extends OncePerRequestFilter {

        @Override
        protected void doFilterInternal(HttpServletRequest request,
                                        HttpServletResponse response,
                                        FilterChain filterChain) throws ServletException, IOException {
            CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
            if (csrf != null) {
                Cookie cookie = WebUtils.getCookie(request, "XSRF-TOKEN");
                String token = csrf.getToken();
                if (cookie == null || token != null && !token.equals(cookie.getValue())) {
                    Cookie csrfCookie = new Cookie("XSRF-TOKEN", token);
                    csrfCookie.setPath("/");
                    csrfCookie.setSecure(false); // خليه true لو بتستخدم https
                    csrfCookie.setHttpOnly(false); // علشان JavaScript يقدر يقرأه
                    response.addCookie(csrfCookie);
                }
            }
            filterChain.doFilter(request, response);
        }
    }
