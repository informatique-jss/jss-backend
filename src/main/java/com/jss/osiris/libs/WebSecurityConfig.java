package com.jss.osiris.libs;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.ldap.LdapBindAuthenticationManagerFactory;
import org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class WebSecurityConfig {

	@Value("${ldap.dc.level.0}")
	private String dc0;

	@Value("${ldap.dc.level.1}")
	private String dc1;

	@Value("${ldap.ou.osiris}")
	private String ouOsiris;

	@Value("${ldap.group.osiris.users}")
	private String osirisUserGroup;

	@Value("${ldap.server.host}")
	private String ldapHost;

	@Value("${ldap.server.port}")
	private String ldapPort;

	@Value("${ldap.manager.login}")
	private String ldapLogin;

	@Value("${ldap.manager.password}")
	private String ldapPassword;

	@Value("${dev.mode}")
	private Boolean devMode;

	@Bean
	SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
		if (devMode == null || devMode == false) {
			http.authorizeHttpRequests((auth) -> auth.requestMatchers(HttpMethod.OPTIONS).permitAll()
					.requestMatchers("/profile/login").permitAll()
					.requestMatchers(HttpMethod.GET, "/quotation/payment/cb/order/deposit").permitAll()
					.requestMatchers(HttpMethod.GET, "/quotation/payment/cb/quotation/deposit").permitAll()
					.requestMatchers(HttpMethod.POST, "/quotation/payment/cb/order/deposit/validate").permitAll()
					.requestMatchers(HttpMethod.POST, "/quotation/payment/cb/quotation/deposit/validate").permitAll()
					.requestMatchers(HttpMethod.GET, "/quotation/payment/cb/order/invoice").permitAll()
					.requestMatchers(HttpMethod.POST, "/quotation/payment/cb/order/invoice/validate").permitAll()
					.requestMatchers(HttpMethod.GET, "/quotation/payment/cb/quotation/validate").permitAll()
					.anyRequest().authenticated()).cors((cors) -> cors
							.configurationSource(customConfigurationSource()))
					.csrf((csrf -> csrf.disable()));
		} else {
			http.authorizeHttpRequests((auth) -> auth.anyRequest().permitAll()).cors((cors) -> cors
					.configurationSource(customConfigurationSource())).csrf((csrf -> csrf.disable()));
		}
		return http.build();
	}

	CorsConfigurationSource customConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		ArrayList<String> all = new ArrayList<String>();
		all.add("*");
		configuration.setAllowedOriginPatterns(all);
		configuration.setAllowedMethods(all);
		configuration.setAllowCredentials(true);
		configuration.setAllowedHeaders(all);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	@Bean
	public LdapContextSource contextSource() {
		LdapContextSource contextSource = new LdapContextSource();
		contextSource.setUrl("ldap://" + ldapHost + ":" + ldapPort);
		contextSource.setUserDn(ldapLogin);
		contextSource.setPassword(ldapPassword);
		contextSource.afterPropertiesSet();
		return contextSource;
	}

	@Bean
	AuthenticationManager authenticationManager(BaseLdapPathContextSource contextSource) {
		LdapBindAuthenticationManagerFactory factory = new LdapBindAuthenticationManagerFactory(contextSource);

		factory.setUserSearchBase("DC=" + dc1 + ",DC=" + dc0);
		factory.setUserSearchFilter(
				"(&(sAMAccountName={0})(memberOf=CN=" + osirisUserGroup + ",OU=OSIRIS,DC=" + dc1 + ",DC=" + dc0 + "))");

		DefaultLdapAuthoritiesPopulator populator = new DefaultLdapAuthoritiesPopulator(
				contextSource(), "OU=" + ouOsiris + ",DC=" + dc1 + ",DC=" + dc0);
		populator.setGroupSearchFilter("member={0}");
		factory.setLdapAuthoritiesPopulator(populator);

		return factory.createAuthenticationManager();
	}
}
