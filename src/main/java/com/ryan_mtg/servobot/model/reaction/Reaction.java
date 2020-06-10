package com.ryan_mtg.servobot.model.reaction;

import com.ryan_mtg.servobot.commands.hierarchy.Command;
import com.ryan_mtg.servobot.error.UserError;
import com.ryan_mtg.servobot.model.Message;
import com.ryan_mtg.servobot.utility.Validation;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

public class Reaction {
    public static final int UNREGISTERED_ID = 0;
    private static final ReactionFilter ALWAYS_REACT = new AlwaysReact();

    @Getter @Setter
    private int id;

    @Getter
    private String emoteName;

    @Getter @Setter
    private boolean secure;

    @Getter
    private ReactionFilter filter;

    @Getter
    private List<Pattern> patterns;
    private List<ReactionCommand> commands;

    public Reaction(final int id, final String emoteName, final boolean secure, final ReactionFilter filter,
                    final List<Pattern> patterns, final List<ReactionCommand> commands) throws UserError {
        this.id = id;
        this.emoteName = emoteName;
        this.secure = secure;
        this.filter = filter;
        this.patterns = patterns;
        this.commands = commands;

        Validation.validateStringLength(emoteName, Validation.MAX_EMOTE_LENGTH, "Emote");
    }

    public void addPattern(final Pattern pattern) {
        patterns.add(pattern);
    }

    public void remove(final Pattern pattern) {
        patterns.remove(pattern);
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
        return commands.stream().map(ReactionCommand::getCommand).collect(Collectors.toList());
    }
}
