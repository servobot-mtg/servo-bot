package com.ryan_mtg.servobot.model.chat_draft;

import com.ryan_mtg.servobot.commands.Permission;
import com.ryan_mtg.servobot.commands.hierarchy.Command;
import com.ryan_mtg.servobot.model.giveaway.GiveawayCommandSettings;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class ChatDraft {
    public static final int UNREGISTERED_ID = 0;
    private static final int DEFAULT_COMMAND_FLAGS = Command.TEMPORARY_FLAG | Command.TWITCH_FLAG;

    public enum State {
        CONFIGURING,
        RECRUITING,
        ACTIVE,
    }

    @Getter @Setter
    private int id;

    @Getter @Setter
    private State state;

    @Getter @Setter
    private GiveawayCommandSettings openCommandSettings;

    @Getter @Setter
    private GiveawayCommandSettings enterCommandSettings;

    @Getter @Setter
    private GiveawayCommandSettings statusCommandSettings;

    @Getter @Setter
    private GiveawayCommandSettings beginCommandSettings;

    @Getter @Setter
    private GiveawayCommandSettings nextCommandSettings;

    @Getter @Setter
    private GiveawayCommandSettings closeCommandSettings;

    @Getter @Setter
    private Command openCommand;

    @Getter @Setter
    private Command enterCommand;

    @Getter @Setter
    private Command statusCommand;

    @Getter @Setter
    private Command beginCommand;

    @Getter @Setter
    private Command nextCommand;

    @Getter @Setter
    private Command closeCommand;

    private List<DraftEntrant> entrants;

    public ChatDraft(final int id, final State state) {
        this.id = id;
        this.state = state;
    }

    public ChatDraft() {
        this(UNREGISTERED_ID, State.CONFIGURING);

        openCommandSettings = new GiveawayCommandSettings("chatDraft", DEFAULT_COMMAND_FLAGS,
                Permission.STREAMER, "A chat draft is about to start! To take part in it type !%chatDraft.enter%");
        enterCommandSettings = new GiveawayCommandSettings("enter", DEFAULT_COMMAND_FLAGS,
                Permission.ANYONE, "%sender% has joined the chat draft.");
        statusCommandSettings = new GiveawayCommandSettings("status", DEFAULT_COMMAND_FLAGS,
                Permission.ANYONE, "%chatDraft.entrantCount% people have joined the chat draft.");
        beginCommandSettings = new GiveawayCommandSettings("start", DEFAULT_COMMAND_FLAGS,
                Permission.ANYONE, "%chatDraft.entrantCount% people have joined the chat draft.");
        nextCommandSettings = new GiveawayCommandSettings("next", DEFAULT_COMMAND_FLAGS, Permission.MOD,
            "The next pick is pack %chatDraft.pack% pick %chatDraft.pick% and belongs to %chatDraft.currentDrafter%.");
        closeCommandSettings = new GiveawayCommandSettings("close", DEFAULT_COMMAND_FLAGS,
                Permission.STREAMER, "The chat draft has ended!");
    }

    public void addDraftEntrant(final DraftEntrant draftEntrant) {
        entrants.add(draftEntrant);
    }

    public String getEnter() {
        return enterCommandSettings.getCommandName();
    }

    public String getCurrentDrafter() {
        return null;
    }
}
