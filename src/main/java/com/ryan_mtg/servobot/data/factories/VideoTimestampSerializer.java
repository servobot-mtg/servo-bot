package com.ryan_mtg.servobot.data.factories;

import com.ryan_mtg.servobot.data.models.VideoTimestampRow;
import com.ryan_mtg.servobot.data.repositories.VideoTimestampRepository;
import com.ryan_mtg.servobot.timestamp.VideoTimestamp;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class VideoTimestampSerializer {
    private final VideoTimestampRepository videoTimestampRepository;

    @Transactional(rollbackOn = Exception.class)
    public void saveVideoTimestamp(final VideoTimestamp videoTimestamp) {
        VideoTimestampRow videoTimestampRow = new VideoTimestampRow();
        videoTimestampRow.setId(videoTimestamp.getId());
        videoTimestampRow.setTime(videoTimestamp.getTime().getEpochSecond());
        videoTimestampRow.setChannel(videoTimestamp.getChannel());
        videoTimestampRow.setUser(videoTimestamp.getUser());
        videoTimestampRow.setLink(videoTimestamp.getLink());
        videoTimestampRow.setNote(videoTimestamp.getNote());
        videoTimestampRow.setOffset(videoTimestamp.getOffset());

        videoTimestampRepository.save(videoTimestampRow);

        videoTimestamp.setId(videoTimestampRow.getId());
    }

    @Transactional(rollbackOn = Exception.class)
    public List<VideoTimestamp> createVideoTimestampList() {
        List<VideoTimestamp> videoTimestamps = new ArrayList<>();
        for (VideoTimestampRow videoTimestampRow : videoTimestampRepository.findAll()) {
            VideoTimestamp videoTimestamp = new VideoTimestamp();

            videoTimestamp.setId(videoTimestampRow.getId());
            videoTimestamp.setTime(Instant.ofEpochSecond(videoTimestampRow.getTime()));
            videoTimestamp.setChannel(videoTimestampRow.getChannel());
            videoTimestamp.setUser(videoTimestampRow.getUser());
            videoTimestamp.setLink(videoTimestampRow.getLink());
            videoTimestamp.setNote(videoTimestampRow.getNote());
            videoTimestamp.setOffset(videoTimestampRow.getOffset());

            videoTimestamps.add(videoTimestamp);
        }
        return videoTimestamps;
    }

    public void deleteVideoTimestamp(final VideoTimestamp videoTimeStamp) {
        videoTimestampRepository.deleteById(videoTimeStamp.getId());
    }
}