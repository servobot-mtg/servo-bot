package com.ryan_mtg.servobot.security;

import com.ryan_mtg.servobot.model.BotHome;
import com.ryan_mtg.servobot.model.BotRegistrar;
import org.springframework.security.access.expression.SecurityExpressionOperations;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.access.expression.WebSecurityExpressionRoot;
import org.springframework.stereotype.Component;

@Component
public class BotWebSecurityExpressionHandler extends DefaultWebSecurityExpressionHandler {
    private final AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();

    private final BotRegistrar botRegistrar;
    private final WebsiteUserFactory websiteUserFactory;

    public BotWebSecurityExpressionHandler(final BotRegistrar botRegistrar,
            final WebsiteUserFactory websiteUserFactory) {
        this.botRegistrar = botRegistrar;
        this.websiteUserFactory = websiteUserFactory;
    }

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

    private static class BotWebSecurityExpressionRoot extends WebSecurityExpressionRoot {
        private final BotRegistrar botRegistrar;
        private final WebsiteUser websiteUser;

        public BotWebSecurityExpressionRoot(final Authentication authentication, final WebsiteUser websiteUser,
                final FilterInvocation filterInvocation, final BotRegistrar botRegistrar) {
            super(authentication, filterInvocation);
            this.botRegistrar = botRegistrar;
            this.websiteUser = websiteUser;
        }

        public boolean isPrivileged() {
            return websiteUser.isPrivileged();
        }

        public boolean isInvited() {
            return websiteUser.hasInvite();
        }

        public boolean isPrivileged(final String botName, final String botHomeName) {
            BotHome botHome = botRegistrar.getBotHome(botName, botHomeName);
            return websiteUser.isPrivileged(botHome);
        }
    }
}
