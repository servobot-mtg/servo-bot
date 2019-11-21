package com.ryan_mtg.servobot.data.factories;

import com.ryan_mtg.servobot.data.models.ServiceHomeRow;
import com.ryan_mtg.servobot.data.models.ServiceRow;
import com.ryan_mtg.servobot.data.repositories.ServiceRepository;
import com.ryan_mtg.servobot.discord.model.DiscordService;
import com.ryan_mtg.servobot.discord.model.DiscordServiceHome;
import com.ryan_mtg.servobot.model.Service;
import com.ryan_mtg.servobot.model.ServiceHome;
import com.ryan_mtg.servobot.twitch.model.TwitchService;
import com.ryan_mtg.servobot.twitch.model.TwitchServiceHome;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ServiceSerializer {
    @Autowired
    private ServiceRepository serviceRepository;

    private Map<Integer, Service> serviceMap = new HashMap<>();

    @Bean
    public TwitchService twitchService() {
        ServiceRow serviceRow = serviceRepository.findByType(TwitchService.TYPE);
        return (TwitchService)createService(serviceRow);
    }

    public Map<Integer, Service> getServiceMap() {
        Map<Integer, Service> services = new HashMap<>();
        Iterable<ServiceRow> serviceRows = serviceRepository.findAll();
        for (ServiceRow serviceRow : serviceRows) {
            Service service = createService(serviceRow);
            services.put(service.getType(), service);
        }
        return services;
    }

    public Service createService(final ServiceRow serviceRow) {
        if (serviceMap.containsKey(serviceRow.getType())) {
            return serviceMap.get(serviceRow.getType());
        }

        switch (serviceRow.getType()) {
            case DiscordService.TYPE:
                return new DiscordService(serviceRow.getToken());
            case TwitchService.TYPE:
                return new TwitchService(serviceRow.getClientId(), serviceRow.getClientSecret(), serviceRow.getToken());
        }
        throw new IllegalArgumentException("Unknown Service type: " + serviceRow.getType());
    }

    public ServiceHome createServiceHome(final ServiceHomeRow serviceHomeRow, final Service service) {
        switch (serviceHomeRow.getServiceType()) {
            case DiscordService.TYPE:
                return new DiscordServiceHome((DiscordService) service, serviceHomeRow.getLong());
            case TwitchService.TYPE:
                return new TwitchServiceHome((TwitchService) service, serviceHomeRow.getLong());
        }
        throw new IllegalArgumentException("Unknown ServiceHome type: " + serviceHomeRow.getServiceType());
    }
}
