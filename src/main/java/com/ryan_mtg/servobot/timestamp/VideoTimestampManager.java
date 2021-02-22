package com.ryan_mtg.servobot.timestamp;

import com.ryan_mtg.servobot.data.factories.VideoTimestampSerializer;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.twitch.model.TwitchService;
import com.ryan_mtg.servobot.twitch.model.VodDescriptor;
import com.ryan_mtg.servobot.utility.Strings;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class VideoTimestampManager {
    private final TwitchService twitchService;
    private final VideoTimestampSerializer videoTimestampSerializer;

    @Getter
    private List<VideoTimestamp> videoTimestamps;

    public VideoTimestampManager(final TwitchService twitchService,
            final VideoTimestampSerializer videoTimestampSerializer) {
        this.twitchService = twitchService;
        this.videoTimestampSerializer = videoTimestampSerializer;

        videoTimestamps = videoTimestampSerializer.createVideoTimestampList();
    }

    public void addTimeStamp(final String channel, final String user, final String note) throws UserError {
        Instant now = Instant.now();
        VideoTimestamp videoTimeStamp = new VideoTimestamp();
        videoTimeStamp.setTime(now);
        videoTimeStamp.setChannel(channel);
        videoTimeStamp.setNote(Strings.isBlank(note) ? null : note);
        videoTimeStamp.setUser(Strings.isBlank(user) ? null : user);

        VodDescriptor vodDescriptor = twitchService.fetchVod(channel, now);
        String link = String.format("%s?t=%s", vodDescriptor.getLink(), vodDescriptor.getDuration());
        videoTimeStamp.setLink(link);
        videoTimeStamp.setOffset(vodDescriptor.getDuration());

        videoTimestamps.add(videoTimeStamp);

        videoTimestampSerializer.saveVideoTimestamp(videoTimeStamp);
    }
}