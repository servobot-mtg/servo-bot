package com.ryan_mtg.servobot.discord.event;

import com.ryan_mtg.servobot.model.BotHome;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.user.UserActivityEndEvent;
import net.dv8tion.jda.api.events.user.UserActivityStartEvent;
import net.dv8tion.jda.api.events.user.update.GenericUserPresenceEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StreamStartRegulator {
    private final Map<Integer, Boolean> isStreamingMap = new HashMap<>();

    public boolean startActivity(final UserActivityStartEvent event, final int botHomeId) {
        if (!isStreamer(event) || !isStreaming(event.getNewActivity())) {
            return false;
        }

        if (isStreamingMap.containsKey(botHomeId) && isStreamingMap.get(botHomeId)) {
            return false;
        }

        isStreamingMap.put(botHomeId, true);
        return true;
    }

    public void endActivity(final UserActivityEndEvent event, final int botHomeId) {
        if (!isStreamer(event) || !isStreaming(event.getOldActivity())) {
            return;
        }

        if (containsStreaming(event.getMember().getActivities())) {
            isStreamingMap.put(botHomeId, false);
        }
    }

    public void addHome(final BotHome botHome, final boolean isStreaming) {
        isStreamingMap.put(botHome.getId(), isStreaming);
    }

    public void removeHome(final BotHome botHome) {
        isStreamingMap.remove(botHome.getId());
    }

    public void setIsStreaming(final int botHomeId, final boolean isStreaming) {
        isStreamingMap.put(botHomeId, isStreaming);
    }

    public boolean isStreaming(final int botHomeId) {
        return isStreamingMap.get(botHomeId);
    }

    private static boolean isStreamer(final GenericUserPresenceEvent event) {
        return event.getMember().getIdLong() == event.getGuild().getOwnerIdLong();
    }

    private static boolean containsStreaming(final List<Activity> activities) {
        return activities.stream().anyMatch(StreamStartRegulator::isStreaming);
    }

    private static boolean isStreaming(final Activity activity) {
        return activity.getType() == Activity.ActivityType.STREAMING;
    }
}
