package com.ryan_mtg.servobot.model.reaction;

import com.ryan_mtg.servobot.commands.Command;
import com.ryan_mtg.servobot.data.models.ReactionRow;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.model.Message;

import java.util.List;
import java.util.stream.Collectors;

public class Reaction {
    public static final int UNREGISTERED_ID = 0;
    private static final ReactionFilter ALWAYS_REACT = new AlwaysReact();
    private static final int MAX_EMOTE_SIZE = ReactionRow.MAX_EMOTE_SIZE;

    private int id;
    private String emoteName;
    private boolean secure;
    private ReactionFilter filter;
    private List<Pattern> patterns;
    private List<ReactionCommand> commands;

    public Reaction(final int id, final String emoteName, final boolean secure, final ReactionFilter filter,
                    final List<Pattern> patterns, final List<ReactionCommand> commands) throws BotErrorException {
        this.id = id;
        this.emoteName = emoteName;
        this.secure = secure;
        this.filter = filter;
        this.patterns = patterns;
        this.commands = commands;

        if (emoteName.length() > MAX_EMOTE_SIZE) {
            throw new BotErrorException(
                    String.format("Emote too long (max %d): %s", MAX_EMOTE_SIZE, emoteName));
        }
    }

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getEmoteName() {
        return emoteName;
    }

    public boolean isSecure() {
        return secure;
    }

    public void setSecure(final boolean secure) {
        this.secure = secure;
    }

    public ReactionFilter getFilter() {
        return filter;
    }

    public void addPattern(final Pattern pattern) {
        patterns.add(pattern);
    }

    public void remove(final Pattern pattern) {
        patterns.remove(pattern);
    }

    public List<Pattern> getPatterns() {
        return patterns;
    }

    public boolean matches(final Message message) {
        if (!filter.shouldReact(message.getSender())) {
            return false;
        }
        final String text = message.getContent();
        for (Pattern pattern : patterns) {
            if (pattern.matches(text)) {
                return true;
            }
        }
        return false;
    }

    public List<Command> getCommands() {
        return commands.stream().map(reactionCommand -> reactionCommand.getCommand()).collect(Collectors.toList());
    }
}
