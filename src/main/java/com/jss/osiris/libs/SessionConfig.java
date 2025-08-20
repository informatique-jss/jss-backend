package com.jss.osiris.libs;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.web.http.CookieHttpSessionIdResolver;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.session.web.http.HttpSessionIdResolver;
import org.springframework.web.filter.ForwardedHeaderFilter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class SessionConfig {

    @Value("${dev.mode}")
    private Boolean devMode;

    @Bean
    public ForwardedHeaderFilter forwardedHeaderFilter() {
        return new ForwardedHeaderFilter();
    }

    @Bean
    public HttpSessionIdResolver httpSessionIdResolver() {
        return new HttpSessionIdResolver() {

            @Override
            public List<String> resolveSessionIds(HttpServletRequest request) {
                CookieHttpSessionIdResolver resolver = buildResolver(request);
                return resolver.resolveSessionIds(request);
            }

            @Override
            public void setSessionId(HttpServletRequest request, HttpServletResponse response, String sessionId) {
                CookieHttpSessionIdResolver resolver = buildResolver(request);
                resolver.setSessionId(request, response, sessionId);
            }

            @Override
            public void expireSession(HttpServletRequest request, HttpServletResponse response) {
                CookieHttpSessionIdResolver resolver = buildResolver(request);
                resolver.expireSession(request, response);
            }

            private CookieHttpSessionIdResolver buildResolver(HttpServletRequest request) {
                String domainHeader = request.getHeader("domain");
                String cookieName = getCookieNameForHost(domainHeader);

                if (cookieName == null)
                    return new CookieHttpSessionIdResolver();

                DefaultCookieSerializer serializer = new DefaultCookieSerializer();
                serializer.setCookieName(cookieName);

                if (!devMode)
                    serializer.setDomainName("jss.fr");

                CookieHttpSessionIdResolver resolver = new CookieHttpSessionIdResolver();
                resolver.setCookieSerializer(serializer);
                return resolver;
            }

            private String getCookieNameForHost(String host) {
                String cookieName = "";
                if (host != null) {
                    if (host.contains("osiris") || host.contains("4200")) {
                        cookieName = "OSIRIS";
                    } else if (host.contains("jss") || host.contains("4202")) {
                        cookieName = "JSS";
                    }
                    if (host.toUpperCase().contains("_REC"))
                        cookieName += "_REC";
                    return cookieName;
                }
                return "OSIRIS";
            }

        };
    }
}
