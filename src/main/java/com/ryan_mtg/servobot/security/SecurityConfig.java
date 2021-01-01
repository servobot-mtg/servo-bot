package com.ryan_mtg.servobot.security;

import com.ryan_mtg.servobot.discord.model.DiscordService;
import com.ryan_mtg.servobot.twitch.model.TwitchService;
import com.ryan_mtg.servobot.user.UserTable;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.AuthenticatedVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.web.access.expression.WebExpressionVoter;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.filter.ForwardedHeaderFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
@ControllerAdvice
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserTable userTable;
    private final WebsiteUserFactory websiteUserFactory;
    private final TwitchService twitchService;
    private final DiscordService discordService;

    public SecurityConfig(final UserTable userTable, final WebsiteUserFactory websiteUserFactory,
            final TwitchService twitchService, final DiscordService discordService) {
        this.userTable = userTable;
        this.websiteUserFactory = websiteUserFactory;
        this.twitchService = twitchService;
        this.discordService = discordService;
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository(final List<ClientRegistration> registrations) {
        return new InMemoryClientRegistrationRepository(registrations);
    }

    @Bean
    public FilterRegistrationBean<ForwardedHeaderFilter> forwardedHeaderFilter() {
        // For the redirectUriTemplate to use the proper URL, because the server is behind a proxy
        final FilterRegistrationBean<ForwardedHeaderFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new ForwardedHeaderFilter());
        filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return filterRegistrationBean;
    }

    @Bean
    public ClientRegistration discordClientRegistration(final DiscordService discordService) {
        return ClientRegistration.withRegistrationId(discordService.getName().toLowerCase())
                .clientId(discordService.getClientId())
                .clientSecret(discordService.getSecret())
                .clientAuthenticationMethod(ClientAuthenticationMethod.POST)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .scope("identify")
                .redirectUriTemplate("{baseUrl}/login/oauth2/code/{registrationId}")
                .authorizationUri("https://discord.com/api/oauth2/authorize")
                .tokenUri("https://discord.com/api/oauth2/token")
                .userNameAttributeName("data")
                .clientName(discordService.getName())
                .build();
    }

    @Bean
    public ClientRegistration twitchClientRegistration(final TwitchService twitchService) {
        return ClientRegistration.withRegistrationId(twitchService.getName().toLowerCase())
                .clientId(twitchService.getClientId())
                .clientSecret(twitchService.getSecret())
                .clientAuthenticationMethod(ClientAuthenticationMethod.POST)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUriTemplate("{baseUrl}/login/oauth2/code/{registrationId}")
                .authorizationUri("https://id.twitch.tv/oauth2/authorize")
                .tokenUri("https://id.twitch.tv/oauth2/token")
                .userNameAttributeName("data")
                .clientName(twitchService.getName())
                .build();
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .antMatchers("/admin**", "/admin/**", "/script/admin.js").access("hasRole('ADMIN')")
            .antMatchers("/script/privileged.js").access("isPrivileged()")
            .antMatchers("/script/invite.js").access("isInvited()")
            .antMatchers("/home/{bot}/{home}").permitAll()
            .antMatchers("/home/{bot}/{home}/**").access("isPrivileged(#bot, #home)")
            .antMatchers("/", "/login**", "/images/**", "/script/**", "/style/**", "/home", "favicon.ico",
                    "/api/public/**", "/help", "/help/**", "/tournament**", "/tournament/**", "/mpl", "/rivals")
                .permitAll()
            .anyRequest().authenticated()
            .accessDecisionManager(accessDecisionManager(null))
            .and().oauth2Login().loginPage("/oauth2/authorization/twitch")
                .successHandler(new RefererSuccessHandler())
            .and().logout().logoutSuccessUrl("/").permitAll()
            .and().oauth2Login().userInfoEndpoint()
                .userService(new TwitchUserService(twitchService, discordService, userTable));
    }

    @ModelAttribute
    private void addUser(final Model model, final OAuth2AuthenticationToken oAuth2AuthenticationToken) {
        model.addAttribute("user", websiteUserFactory.createWebsiteUser(oAuth2AuthenticationToken));
    }

    @Bean
    public AccessDecisionManager accessDecisionManager(
            final BotWebSecurityExpressionHandler securityExpressionHandler) {
        WebExpressionVoter webExpressionVoter = new WebExpressionVoter();
        webExpressionVoter.setExpressionHandler(securityExpressionHandler);
        List<AccessDecisionVoter<?>> decisionVoters =
                Arrays.asList(webExpressionVoter, new RoleVoter(), new AuthenticatedVoter());
        return new AffirmativeBased(decisionVoters);
    }

    private static class RefererSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
        public RefererSuccessHandler() {
            super();
            setUseReferer(true);
        }
    }
}
