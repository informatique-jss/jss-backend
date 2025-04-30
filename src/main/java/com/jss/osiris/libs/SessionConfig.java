package com.jss.osiris.libs;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.web.http.CookieHttpSessionIdResolver;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.session.web.http.HttpSessionIdResolver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class SessionConfig {

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
                String cookieName = getCookieNameForHost(request.getHeader("Host"));

                DefaultCookieSerializer serializer = new DefaultCookieSerializer();
                serializer.setCookieName(cookieName);

                // Optionnel : scoper au sous-domaine courant
                // serializer.setDomainName(getDomainForHost(request.getHeader("Host")));

                CookieHttpSessionIdResolver resolver = new CookieHttpSessionIdResolver();
                resolver.setCookieSerializer(serializer);
                return resolver;
            }

            private String getCookieNameForHost(String host) {
                if (host != null) {
                    if (host.contains("osiris") || host.contains("4200")) {
                        return "OSIRIS";
                    } else if (host.contains("my") || host.contains("4202")) {
                        return "MYJSS";
                    }
                }
                return "DEFAULTSESSION";
            }

        };
    }
}
