package com.ryan_mtg.servobot.security;

import com.ryan_mtg.servobot.data.factories.UserSerializer;
import com.ryan_mtg.servobot.model.BotHome;
import com.ryan_mtg.servobot.model.BotRegistrar;
import com.ryan_mtg.servobot.twitch.model.TwitchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.expression.SecurityExpressionOperations;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.AuthenticatedVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.access.expression.WebExpressionVoter;
import org.springframework.security.web.access.expression.WebSecurityExpressionRoot;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Arrays;
import java.util.List;

@Configuration
@ControllerAdvice
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserSerializer userSerializer;

    @Autowired
    private WebsiteUserFactory websiteUserFactory;

    @Autowired
    private BotRegistrar botRegistrar;

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
            .antMatchers("/admin**", "/admin/**", "/script/admin.js").access("hasRole('ADMIN')")
            .antMatchers("/script/privledged.js").access("isPrivledged()")
            .antMatchers("/script/invite.js").access("isInvited()")
            .antMatchers("/home/{home}/**").access("isPrivledged(#home)")
            .antMatchers("/login**", "/images/**", "/script/**", "/style/**", "/home", "/home/{home}").permitAll()
            .anyRequest().authenticated()
            .accessDecisionManager(accessDecisionManager())
            .and().oauth2Login().loginPage("/oauth2/authorization/twitch").defaultSuccessUrl("/home")
            .and().logout().logoutSuccessUrl("/").permitAll()
            .and().oauth2Login().userInfoEndpoint().userService(new TwitchUserService(userSerializer));
    }

    @ModelAttribute
    private void addUser(final Model model, final OAuth2AuthenticationToken oAuth2AuthenticationToken) {
        model.addAttribute("user", websiteUserFactory.createWebsiteUser(oAuth2AuthenticationToken));
    }

    @Bean
    public AccessDecisionManager accessDecisionManager() {
        WebExpressionVoter webExpressionVoter = new WebExpressionVoter();
        webExpressionVoter.setExpressionHandler(new WebSecurityExpressionHandler());
        List<AccessDecisionVoter<? extends Object>> decisionVoters =
                Arrays.asList(webExpressionVoter,new RoleVoter(), new AuthenticatedVoter());
        return new AffirmativeBased(decisionVoters);
    }

    @Component
    public class WebSecurityExpressionHandler extends DefaultWebSecurityExpressionHandler {
        private AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();

        @Override
        protected SecurityExpressionOperations createSecurityExpressionRoot(final Authentication authentication,
                                                                            final FilterInvocation filterInvocation) {
            WebsiteUser websiteUser = websiteUserFactory.createWebsiteUser(authentication);
            WebSecurityExpressionRoot root =
                    new BotWebSecurityExpressionRoot(authentication, websiteUser, filterInvocation, botRegistrar);
            root.setPermissionEvaluator(getPermissionEvaluator());
            root.setTrustResolver(trustResolver);
            root.setRoleHierarchy(getRoleHierarchy());
            return root;
        }
    }

    private class BotWebSecurityExpressionRoot extends WebSecurityExpressionRoot {
        private BotRegistrar botRegistrar;
        private WebsiteUser websiteUser;

        public BotWebSecurityExpressionRoot(final Authentication authentication, final WebsiteUser websiteUser,
                final FilterInvocation filterInvocation, final BotRegistrar botRegistrar) {
            super(authentication, filterInvocation);
            this.botRegistrar = botRegistrar;
            this.websiteUser = websiteUser;
        }

        public boolean isPrivledged() {
            return websiteUser.isPrivledged();
        }

        public boolean isInvited() {
            return websiteUser.hasInvite();
        }

        public boolean isPrivledged(final String botHomeName) {
            BotHome botHome = botRegistrar.getBotHome(botHomeName);
            return websiteUser.isPrivledged(botHome);
        }
    }
}
