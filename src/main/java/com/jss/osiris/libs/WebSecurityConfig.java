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
		http.authorizeHttpRequests((auth) -> auth.requestMatchers(HttpMethod.OPTIONS).permitAll()
				.requestMatchers(HttpMethod.GET, "/*sitemap*.xml").permitAll()
				.requestMatchers("/myjss/wordpress/**").permitAll()
				.requestMatchers("/profile/login").permitAll()
				.requestMatchers(HttpMethod.GET, "/myjss/profile/login").permitAll()
				.requestMatchers(HttpMethod.GET, "/myjss/quotation/constants").permitAll()
				.requestMatchers(HttpMethod.GET, "/myjss/profile/login/token/send").permitAll()
				.requestMatchers(HttpMethod.GET, "/myjss/quotation/countries").permitAll()
				.requestMatchers(HttpMethod.GET, "/myjss/quotation/departments").permitAll()
				.requestMatchers(HttpMethod.GET, "myjss/quotation/attachment/download").permitAll()
				.requestMatchers(HttpMethod.GET, "/myjss/quotation/civilities").permitAll()
				.requestMatchers(HttpMethod.GET, "/myjss/quotation/notice-types").permitAll()
				.requestMatchers(HttpMethod.GET, "/myjss/quotation/notice-type-families").permitAll()
				.requestMatchers(HttpMethod.POST, "/myjss/quotation/attachment/upload").permitAll()
				.requestMatchers(HttpMethod.POST, "/myjss/quotation/order/user/pricing").permitAll()
				.requestMatchers(HttpMethod.GET, "/myjss/crm/subscribe/demo").permitAll()
				.requestMatchers(HttpMethod.GET, "/myjss/crm/subscribe/prices").permitAll()
				.requestMatchers(HttpMethod.GET, "/myjss/crm/subscribe/contact").permitAll()
				.requestMatchers(HttpMethod.GET, "/myjss/quotation/service-types/provisions").permitAll()
				.requestMatchers(HttpMethod.GET, "/myjss/quotation/affaire/siret").permitAll()
				.requestMatchers(HttpMethod.GET, "/myjss/quotation/service-family-groups").permitAll()
				.requestMatchers(HttpMethod.GET, "/myjss/quotation/service-families/mandatory-documents").permitAll()
				.requestMatchers(HttpMethod.GET, "/myjss/quotation/service-type").permitAll()
				.requestMatchers(HttpMethod.GET, "/myjss/quotation/service-families/service-group").permitAll()
				.requestMatchers(HttpMethod.GET, "/myjss/quotation/service-type/service-family").permitAll()
				.requestMatchers(HttpMethod.GET, "/myjss/quotation/service-type").permitAll()
				.requestMatchers(HttpMethod.GET, "/myjss/quotation/domiciliation-contract-types").permitAll()
				.requestMatchers(HttpMethod.GET, "/myjss/quotation/languages").permitAll()
				.requestMatchers(HttpMethod.GET, "/myjss/quotation/mail-redirection-types").permitAll()
				.requestMatchers(HttpMethod.GET, "/myjss/quotation/building-domiciliations").permitAll()
				.requestMatchers(HttpMethod.GET, "/myjss/quotation/cities/search/name/country/postal-code").permitAll()
				.requestMatchers(HttpMethod.GET, "/myjss/quotation/cities/search/country").permitAll()
				.requestMatchers(HttpMethod.GET, "/myjss/quotation/cities/search/postal-code").permitAll()
				.requestMatchers(HttpMethod.GET, "/myjss/quotation/order/search/affaire").permitAll()
				.requestMatchers(HttpMethod.GET, "/myjss/quotation/affaire").permitAll()
				.requestMatchers(HttpMethod.GET, "/myjss/quotation/billing-label-types").permitAll()
				.requestMatchers(HttpMethod.GET, "/myjss/quotation/legal-forms/label").permitAll()
				.requestMatchers(HttpMethod.POST, "/myjss/quotation/order/pricing").permitAll()
				.requestMatchers(HttpMethod.POST, "/myjss/quotation/quotation/pricing").permitAll()
				.requestMatchers(HttpMethod.GET, "/myjss/quotation/quotation/emergency").permitAll()
				.requestMatchers(HttpMethod.GET, "/myjss/quotation/order/emergency").permitAll()
				.requestMatchers(HttpMethod.POST, "/myjss/quotation/order/document").permitAll()
				.requestMatchers(HttpMethod.POST, "/myjss/quotation/quotation/document").permitAll()
				.requestMatchers(HttpMethod.POST, "/myjss/quotation/order/save-order").permitAll()
				.requestMatchers(HttpMethod.POST, "/myjss/quotation/quotation/save-order").permitAll()
				.requestMatchers(HttpMethod.GET, "/myjss/quotation/order/subscription").permitAll()
				.requestMatchers(HttpMethod.GET, "/myjss/quotation/service-field-types").permitAll()
				.requestMatchers(HttpMethod.GET, "/myjss/quotation/asso-notice-template-fragment").permitAll()
				.requestMatchers(HttpMethod.POST, "/myjss/quotation/voucher/order/apply").permitAll()
				.requestMatchers(HttpMethod.POST, "/myjss/quotation/voucher/quotation/apply").permitAll()
				.requestMatchers(HttpMethod.GET, "/myjss/quotation/quotation/cancel").permitAll()
				.requestMatchers(HttpMethod.GET, "/myjss/quotation/order/cancel").permitAll()
				.requestMatchers(HttpMethod.GET, "/myjss/crm/communication-preferences/**").permitAll()
				.requestMatchers(HttpMethod.POST, "/myjss/crm/webinar/subscribe").permitAll()
				.requestMatchers(HttpMethod.GET, "/myjss/crm/webinar/subscribe/replay").permitAll()
				.requestMatchers(HttpMethod.GET, "/myjss/crm/webinar/next").permitAll()
				.requestMatchers(HttpMethod.GET, "/myjss/crm/webinar/last").permitAll()
				.requestMatchers(HttpMethod.POST, "/myjss/crm/subscribe/candidacy").permitAll()
				.requestMatchers(HttpMethod.POST, "/myjss/miscellaneous/google-analytics/view-list-item").permitAll()
				.requestMatchers(HttpMethod.POST, "/myjss/miscellaneous/google-analytics/add-to-cart").permitAll()
				.requestMatchers(HttpMethod.POST, "/myjss/miscellaneous/google-analytics/remove-from-cart").permitAll()
				.requestMatchers(HttpMethod.POST, "/myjss/miscellaneous/google-analytics/begin-checkout/quotation")
				.permitAll()
				.requestMatchers(HttpMethod.POST, "/myjss/miscellaneous/google-analytics/begin-checkout/customer-order")
				.permitAll()
				.requestMatchers(HttpMethod.POST, "/myjss/miscellaneous/google-analytics/add-payment-info/quotation")
				.permitAll()
				.requestMatchers(HttpMethod.POST,
						"/myjss/miscellaneous/google-analytics/add-payment-info/customer-order")
				.permitAll()
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
