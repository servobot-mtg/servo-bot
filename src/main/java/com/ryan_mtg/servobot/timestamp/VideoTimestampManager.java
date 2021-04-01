package com.ryan_mtg.servobot.timestamp;

import com.ryan_mtg.servobot.data.factories.VideoTimestampSerializer;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.twitch.model.TwitchService;
import com.ryan_mtg.servobot.twitch.model.VodDescriptor;
import com.ryan_mtg.servobot.utility.Strings;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Component
public class VideoTimestampManager {
    private final TwitchService twitchService;
    private final VideoTimestampSerializer videoTimestampSerializer;

    @Getter
    private final List<VideoTimestamp> videoTimestamps;

    public VideoTimestampManager(final TwitchService twitchService,
            final VideoTimestampSerializer videoTimestampSerializer) {
        this.twitchService = twitchService;
        this.videoTimestampSerializer = videoTimestampSerializer;

        videoTimestamps = videoTimestampSerializer.createVideoTimestampList();
        Collections.sort(videoTimestamps, new TimestampOrder());
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
        Collections.sort(videoTimestamps, new TimestampOrder());

        videoTimestampSerializer.saveVideoTimestamp(videoTimeStamp);
    }

    public void delete(final int videoTimeStampId) {
        for (VideoTimestamp videoTimeStamp : videoTimestamps) {
            if (videoTimeStamp.getId() == videoTimeStampId) {
                videoTimestamps.remove(videoTimeStamp);
                videoTimestampSerializer.deleteVideoTimestamp(videoTimeStamp);
                break;
            }
        }
    }

    private static class TimestampOrder implements Comparator<VideoTimestamp> {
        @Override
        public int compare(final VideoTimestamp timestamp, final VideoTimestamp otherTimestamp) {
            return -timestamp.getTime().compareTo(otherTimestamp.getTime());
        }
    }
}