package com.ryan_mtg.servobot.data.factories;

import com.ryan_mtg.servobot.data.models.ServiceHomeRow;
import com.ryan_mtg.servobot.data.models.ServiceRow;
import com.ryan_mtg.servobot.data.repositories.ServiceRepository;
import com.ryan_mtg.servobot.discord.model.DiscordService;
import com.ryan_mtg.servobot.discord.model.DiscordServiceHome;
import com.ryan_mtg.servobot.error.SystemError;
import com.ryan_mtg.servobot.model.Service;
import com.ryan_mtg.servobot.model.ServiceHome;
import com.ryan_mtg.servobot.twitch.model.TwitchService;
import com.ryan_mtg.servobot.twitch.model.TwitchServiceHome;
import com.ryan_mtg.servobot.user.UserTable;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

@Component
public class ServiceSerializer {
    private final ServiceRepository serviceRepository;
    private final UserTable userTable;
    private final ScheduledExecutorService executorService;
    private final LoggedMessageSerializer loggedMessageSerializer;
    private final Map<ServiceKey, Service> serviceMap = new HashMap<>();

    public ServiceSerializer(final ServiceRepository serviceRepository, final UserTable userTable,
            final ScheduledExecutorService executorService, final LoggedMessageSerializer loggedMessageSerializer) {
        this.serviceRepository = serviceRepository;
        this.userTable = userTable;
        this.executorService = executorService;
        this.loggedMessageSerializer = loggedMessageSerializer;
    }

    @Bean
    public TwitchService twitchService() {
        int defaultBotId = 1;
        ServiceRow serviceRow = serviceRepository.findByBotIdAndType(defaultBotId, TwitchService.TYPE);
        return (TwitchService)createService(defaultBotId, serviceRow);
    }

    public Map<Integer, Service> getServiceMap(final int botId) {
        Map<Integer, Service> services = new HashMap<>();
        Iterable<ServiceRow> serviceRows = serviceRepository.findAllByBotId(botId);
        for (ServiceRow serviceRow : serviceRows) {
            Service service = createService(botId, serviceRow);
            services.put(service.getType(), service);
        }
        return services;
    }

    public Service createService(final int botId, final ServiceRow serviceRow) {
        return SystemError.filter(() -> {
            int serviceType = serviceRow.getType();
            ServiceKey key = new ServiceKey(botId, serviceType);
            if (serviceMap.containsKey(key)) {
                return serviceMap.get(key);
            }

            Service service;
            switch (serviceType) {
                case DiscordService.TYPE:
                    service = new DiscordService(serviceRow.getToken(), userTable, loggedMessageSerializer);
                    break;
                case TwitchService.TYPE:
                    service = new TwitchService(serviceRow.getClientId(), serviceRow.getClientSecret(),
                            serviceRow.getToken(), userTable, executorService, loggedMessageSerializer);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown Service type: " + serviceRow.getType());
            }
            serviceMap.put(key, service);
            return service;
        });
    }

    public ServiceHome createServiceHome(final ServiceHomeRow serviceHomeRow, final Service service) {
        switch (serviceHomeRow.getServiceType()) {
            case DiscordService.TYPE:
                return new DiscordServiceHome((DiscordService) service, serviceHomeRow.getLongValue());
            case TwitchService.TYPE:
                return new TwitchServiceHome((TwitchService) service, serviceHomeRow.getLongValue());
        }
        throw new IllegalArgumentException("Unknown ServiceHome type: " + serviceHomeRow.getServiceType());
    }

    @Data @AllArgsConstructor
    private static class ServiceKey {
        private int botId;
        private int serviceType;
    }
}
