package com.ryan_mtg.servobot.data.factories;

import com.ryan_mtg.servobot.commands.Command;
import com.ryan_mtg.servobot.commands.CommandTable;
import com.ryan_mtg.servobot.commands.RequestPrizeCommand;
import com.ryan_mtg.servobot.data.models.GiveawayRow;
import com.ryan_mtg.servobot.data.models.PrizeRow;
import com.ryan_mtg.servobot.data.repositories.GiveawayRepository;
import com.ryan_mtg.servobot.data.repositories.PrizeRepository;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.model.giveaway.Giveaway;
import com.ryan_mtg.servobot.model.giveaway.GiveawayEdit;
import com.ryan_mtg.servobot.model.giveaway.Prize;
import com.ryan_mtg.servobot.user.HomedUser;
import com.ryan_mtg.servobot.utility.Flags;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Component
public class GiveawaySerializer {
    private static final int SELF_SERVICE_FLAG = 1 << 0;
    private static final int RAFFLE_FLAG = 1 << 1;

    @Autowired
    private GiveawayRepository giveawayRepository;

    @Autowired
    private PrizeRepository prizeRepository;

    @Autowired
    private UserSerializer userSerializer;

    @Autowired
    private CommandTableSerializer commandTableSerializer;

    @Transactional(rollbackOn = BotErrorException.class)
    public void commit(final int botHomeId, final GiveawayEdit giveawayEdit) {
        commandTableSerializer.commit(botHomeId, giveawayEdit.getCommandTableEdit());

        giveawayEdit.getSavedPrizes().forEach((key, value) -> savePrize(value, key));

        giveawayEdit.getSavedGiveaways().stream().forEach(giveaway -> saveGiveaway(botHomeId, giveaway));
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public void saveGiveaway(final int botHomeId, final Giveaway giveaway) {
        GiveawayRow giveawayRow = new GiveawayRow();
        giveawayRow.setId(giveaway.getId());
        giveawayRow.setBotHomeId(botHomeId);
        giveawayRow.setName(giveaway.getName());
        giveawayRow.setFlags(getFlags(giveaway));
        giveawayRow.setState(giveaway.getState());

        giveawayRow.setRequestPrizeCommandName(giveaway.getRequestPrizeCommandName());
        giveawayRow.setPrizeRequestLimit(giveaway.getPrizeRequestLimit());
        giveawayRow.setPrizeRequestUserLimit(giveaway.getPrizeRequestUserLimit());
        Command requestPrizeCommand = giveaway.getRequestPrizeCommand();
        giveawayRow.setRequestPrizeCommandId(requestPrizeCommand != null ? requestPrizeCommand.getId() : 0);

        giveawayRepository.save(giveawayRow);

        giveaway.setId(giveawayRow.getId());
    }

    @Transactional(rollbackOn = BotErrorException.class)
    public void savePrize(final int giveawayId, final Prize prize) {
        PrizeRow prizeRow = new PrizeRow();
        prizeRow.setGiveawayId(giveawayId);
        prizeRow.setId(prize.getId());
        prizeRow.setStatus(prize.getStatus());
        prizeRow.setReward(prize.getReward());
        prizeRow.setWinnerId(prize.getWinner() == null ? 0 : prize.getWinner().getId());

        prizeRepository.save(prizeRow);

        prize.setId(prizeRow.getId());
    }

    public List<Giveaway> createGiveaways(final int botHomeId, final CommandTable commandTable)
            throws BotErrorException {
        Iterable<GiveawayRow> giveawayRows = giveawayRepository.findAllByBotHomeId(botHomeId);
        List<Giveaway> giveaways = new ArrayList<>();
        for (GiveawayRow giveawayRow : giveawayRows) {
            giveaways.add(createGiveaway(botHomeId, giveawayRow, commandTable));
        }
        return giveaways;
    }

    private Giveaway createGiveaway(final int botHomeId, final GiveawayRow giveawayRow, final CommandTable commandTable)
            throws BotErrorException {
        int flags = giveawayRow.getFlags();
        Giveaway giveaway = new Giveaway(giveawayRow.getId(), giveawayRow.getName(),
                Flags.hasFlag(flags, SELF_SERVICE_FLAG), Flags.hasFlag(flags, RAFFLE_FLAG));
        giveaway.setState(giveawayRow.getState());

        if (giveaway.isSelfService()) {
            giveaway.setRequestPrizeCommandName(giveawayRow.getRequestPrizeCommandName());
            giveaway.setPrizeRequestLimit(giveawayRow.getPrizeRequestLimit());
            giveaway.setPrizeRequestUserLimit(giveawayRow.getPrizeRequestUserLimit());

            if (giveawayRow.getRequestPrizeCommandId() != Command.UNREGISTERED_ID) {
                RequestPrizeCommand requestPrizeCommand =
                        (RequestPrizeCommand) commandTable.getCommand(giveawayRow.getRequestPrizeCommandId());
                giveaway.setRequestPrizeCommand(requestPrizeCommand);
            }
        }

        for (PrizeRow prizeRow : prizeRepository.findAllByGiveawayId(giveaway.getId())) {
            giveaway.addPrize(createPrize(botHomeId, prizeRow));
        }

        return giveaway;
    }

    private Prize createPrize(final int botHomeId, final PrizeRow prizeRow) throws BotErrorException {
        Prize prize = new Prize(prizeRow.getId(), prizeRow.getReward());
        prize.setStatus(prizeRow.getStatus());
        if (prizeRow.getWinnerId() != HomedUser.UNREGISTERED_ID) {
            prize.setWinner(userSerializer.getHomedUser(botHomeId, prizeRow.getWinnerId()));
        }
        return prize;
    }

    private int getFlags(final Giveaway giveaway) {
        int flags = 0;
        if (giveaway.isSelfService()) {
            flags |= SELF_SERVICE_FLAG;
        }
        if (giveaway.isRaffle()) {
            flags |= RAFFLE_FLAG;
        }
        return flags;
    }
}
