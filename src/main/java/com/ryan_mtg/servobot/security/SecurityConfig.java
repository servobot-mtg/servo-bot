package com.ryan_mtg.servobot.security;

import com.ryan_mtg.servobot.data.factories.UserSerializer;
import com.ryan_mtg.servobot.twitch.model.TwitchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@Configuration
@EnableOAuth2Client
@ControllerAdvice
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserSerializer userSerializer;

    @Autowired
    private WebsiteUserFactory websiteUserFactory;

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
        http.authorizeRequests()
            .antMatchers("/admin**", "/admin/**", "/script/admin.js").hasRole("ADMIN")
            .antMatchers("/script/privledged.js").fullyAuthenticated()
            .antMatchers("/login**", "/images/**", "/script/**", "/style/**").permitAll()
            .anyRequest().permitAll()
            .and().oauth2Login().loginPage("/login")
            .and().logout().logoutSuccessUrl("/").permitAll()
            .and().oauth2Login().userInfoEndpoint().userService(new TwitchUserService(userSerializer));
    }

    @ModelAttribute
    private void addUser(final Model model, final OAuth2AuthenticationToken oAuth2AuthenticationToken) {
        model.addAttribute("user", websiteUserFactory.createWebsiteUser(oAuth2AuthenticationToken));
    }
}
