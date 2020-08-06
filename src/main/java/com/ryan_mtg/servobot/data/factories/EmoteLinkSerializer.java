package com.ryan_mtg.servobot.data.factories;

import com.ryan_mtg.servobot.data.models.EmoteLinkRow;
import com.ryan_mtg.servobot.data.repositories.EmoteLinkRepository;
import com.ryan_mtg.servobot.error.SystemError;
import com.ryan_mtg.servobot.model.EmoteLink;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EmoteLinkSerializer {
    private final EmoteLinkRepository emoteLinkRepository;

    public EmoteLinkSerializer(final EmoteLinkRepository emoteLinkRepository) {
        this.emoteLinkRepository = emoteLinkRepository;
    }

    public List<EmoteLink> createEmoteLinks(final int botHomeId) {
        List<EmoteLink> emoteLinks = new ArrayList<>();
        for (EmoteLinkRow emoteLinkRow : emoteLinkRepository.findAllByBotHomeId(botHomeId)) {
            emoteLinks.add(SystemError.filter(() ->
                new EmoteLink(emoteLinkRow.getId(), emoteLinkRow.getTwitchEmoteName(),
                        emoteLinkRow.getDiscordEmoteName())
            ));
        }
        return emoteLinks;
    }

    public void save(final int botHomeId, final EmoteLink emoteLink) {
        EmoteLinkRow emoteLinkRow = new EmoteLinkRow(emoteLink.getId(), botHomeId, emoteLink.getTwitchEmote(),
                emoteLink.getDiscordEmote());
        emoteLinkRepository.save(emoteLinkRow);
        emoteLink.setId(emoteLinkRow.getId());
    }

    public void delete(final EmoteLink emoteLink) {
        emoteLinkRepository.deleteById(emoteLink.getId());
    }
}