package com.jss.osiris.libs;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
// @EnableWebMvc
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

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
	public CorsConfigurationSource corsConfigurationSource() {
		final CorsConfiguration configuration = new CorsConfiguration();
		ArrayList<String> all = new ArrayList<String>();
		all.add("*");
		configuration.setAllowedOriginPatterns(all);
		configuration.setAllowedMethods(all);
		configuration.setAllowCredentials(true);
		configuration.setAllowedHeaders(all);

		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);

		return source;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		if (devMode == null || devMode == false) {
			http.authorizeRequests()
					.antMatchers(HttpMethod.OPTIONS).permitAll()
					.antMatchers("/profile/login").permitAll()
					.antMatchers(HttpMethod.GET, "/quotation/payment/cb/order/deposit").permitAll()
					.antMatchers(HttpMethod.GET, "/quotation/payment/cb/quotation/deposit").permitAll()
					.antMatchers(HttpMethod.POST, "/quotation/payment/cb/order/deposit/validate").permitAll()
					.antMatchers(HttpMethod.POST, "/quotation/payment/cb/quotation/deposit/validate").permitAll()
					.antMatchers(HttpMethod.GET, "/quotation/payment/cb/order/invoice").permitAll()
					.antMatchers(HttpMethod.POST, "/quotation/payment/cb/order/invoice/validate").permitAll()
					.antMatchers(HttpMethod.GET, "/quotation/payment/cb/quotation/validate").permitAll()
					.anyRequest().authenticated()
					.and().cors().and().csrf().disable();
		} else {
			http.authorizeRequests()
					.anyRequest().permitAll()
					.and().cors().and().csrf().disable();
		}
	}

	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth
				.ldapAuthentication()
				.userSearchFilter(
						"(&(sAMAccountName={0})(memberOf=CN=" + osirisUserGroup + ",OU=OSIRIS,DC=" + dc1 + ",DC=" + dc0
								+ "))")
				.userSearchBase("DC=" + dc1 + ",DC=" + dc0)
				.groupSearchBase("OU=" + ouOsiris + ",DC=" + dc1 + ",DC=" + dc0)
				.groupSearchFilter("member={0}")
				.contextSource()
				.url("ldap://" + ldapHost + ":" + ldapPort)
				.managerDn(ldapLogin)
				.managerPassword(ldapPassword);
	}

	@Bean(name = BeanIds.AUTHENTICATION_MANAGER)
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

}
