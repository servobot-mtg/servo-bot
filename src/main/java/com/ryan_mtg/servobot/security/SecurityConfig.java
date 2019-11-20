package com.ryan_mtg.servobot.security;

import com.ryan_mtg.servobot.twitch.model.TwitchService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

@Configuration
@EnableOAuth2Client
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Bean
    public ClientRegistrationRepository clientRegistrationRepository(final ClientRegistration clientRegistration) {
        return new InMemoryClientRegistrationRepository(clientRegistration);
    }

    @Bean
    public ClientRegistration twitchClientRegistration(final TwitchService twitchService) {
        return ClientRegistration.withRegistrationId(twitchService.getName().toLowerCase())
                .clientId(twitchService.getClientId())
                .clientSecret(twitchService.getSecret())
                .clientAuthenticationMethod(ClientAuthenticationMethod.POST)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUriTemplate("{baseUrl}/login/oauth2/code/{registrationId}")
                // .scope("user_read")
                .authorizationUri("https://id.twitch.tv/oauth2/authorize")
                .tokenUri("https://id.twitch.tv/oauth2/token")
                .userInfoUri("https://api.twitch.tv/helix/users")
                .userNameAttributeName("data")
                .clientName(twitchService.getName())
                .build();
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http
            .antMatcher("/**")
            .authorizeRequests()
            .antMatchers("/", "/login/**", "/error**")
            .permitAll()
            .anyRequest()
            .authenticated()
            .and().logout().logoutSuccessUrl("/").permitAll()
            .and().oauth2Login().userInfoEndpoint().userService(new TwitchUserService());

        //.and().addFilterBefore(twitchFilter, BasicAuthenticationFilter.class);
    }
}
