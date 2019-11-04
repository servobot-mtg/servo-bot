package com.ryan_mtg.servobot.discord.events;

import com.ryan_mtg.servobot.discord.reaction.Reaction;
import com.ryan_mtg.servobot.discord.reaction.ReactionTable;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.List;

public class ReactionListener extends ListenerAdapter {
    private static Logger LOGGER = LoggerFactory.getLogger(CommandListener.class);
    private final ReactionTable reactionTable;

    public ReactionListener(final ReactionTable reactionTable) {
        this.reactionTable = reactionTable;
    }

    @Override
    public void onGuildMessageReceived(final @Nonnull GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }

        Message message = event.getMessage();
        String content = message.getContentRaw();
        Guild guild = message.getGuild();
        for (Reaction reaction : reactionTable) {
            if (reaction.matches(content)) {
                List<Emote> emotes = guild.getEmotesByName(reaction.getEmoteName(), true);
                if (!emotes.isEmpty()) {
                    Emote emote = emotes.get(0);
                    LOGGER.info("Adding a " + emote.getName() + " reaction to " + message.getAuthor() + "'s message.");
                    message.addReaction(emote).queue();
                } else {
                    LOGGER.warn("Unable to find emote " + reaction.getEmoteName() + " in " + message.getGuild());
                }
            }
        }
    }
}
