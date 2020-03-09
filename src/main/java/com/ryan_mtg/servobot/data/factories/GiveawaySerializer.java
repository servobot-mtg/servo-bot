package com.ryan_mtg.servobot.data.factories;

import com.ryan_mtg.servobot.commands.Command;
import com.ryan_mtg.servobot.commands.CommandTable;
import com.ryan_mtg.servobot.commands.RequestPrizeCommand;
import com.ryan_mtg.servobot.commands.StartRaffleCommand;
import com.ryan_mtg.servobot.data.models.GiveawayRow;
import com.ryan_mtg.servobot.data.models.PrizeRow;
import com.ryan_mtg.servobot.data.repositories.GiveawayRepository;
import com.ryan_mtg.servobot.data.repositories.PrizeRepository;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.model.giveaway.CommandSettings;
import com.ryan_mtg.servobot.model.giveaway.Giveaway;
import com.ryan_mtg.servobot.model.giveaway.GiveawayEdit;
import com.ryan_mtg.servobot.model.giveaway.Prize;
import com.ryan_mtg.servobot.model.giveaway.RaffleSettings;
import com.ryan_mtg.servobot.user.HomedUser;
import com.ryan_mtg.servobot.utility.Flags;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.Duration;
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
        Command requestPrizeCommand = giveaway.getRequestPrizeCommand();
        giveawayRow.setRequestPrizeCommandId(requestPrizeCommand != null ? requestPrizeCommand.getId() : 0);
        giveawayRow.setPrizeRequestLimit(giveaway.getPrizeRequestLimit());
        giveawayRow.setPrizeRequestUserLimit(giveaway.getPrizeRequestUserLimit());

        RaffleSettings raffleSettings = giveaway.getRaffleSettings();

        giveawayRow.setRaffleDuration((int)raffleSettings.getRaffleDuration().getSeconds());
        Command startRaffleCommand = giveaway.getStartRaffleCommand();
        giveawayRow.setStartRaffleCommandId(startRaffleCommand != null ? startRaffleCommand.getId() : 0);

        giveawayRow.setStartRaffleCommandName(raffleSettings.getStartRaffle().getCommandName());
        giveawayRow.setStartRaffleFlags(raffleSettings.getStartRaffle().getFlags());
        giveawayRow.setStartRafflePermission(raffleSettings.getStartRaffle().getPermission());
        giveawayRow.setStartRaffleMessage(raffleSettings.getStartRaffle().getMessage());

        giveawayRow.setEnterRaffleCommandName(raffleSettings.getEnterRaffle().getCommandName());
        giveawayRow.setEnterRaffleFlags(raffleSettings.getEnterRaffle().getFlags());
        giveawayRow.setEnterRafflePermission(raffleSettings.getEnterRaffle().getPermission());
        giveawayRow.setEnterRaffleMessage(raffleSettings.getEnterRaffle().getMessage());

        giveawayRow.setRaffleStatusCommandName(raffleSettings.getRaffleStatus().getCommandName());
        giveawayRow.setRaffleStatusFlags(raffleSettings.getRaffleStatus().getFlags());
        giveawayRow.setRaffleStatusPermission(raffleSettings.getRaffleStatus().getPermission());
        giveawayRow.setRaffleStatusMessage(raffleSettings.getRaffleStatus().getMessage());

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

        if (giveaway.isRafflesEnabled()) {
            if (giveawayRow.getStartRaffleCommandId() != Command.UNREGISTERED_ID) {
                StartRaffleCommand startRaffleCommand =
                        (StartRaffleCommand) commandTable.getCommand(giveawayRow.getStartRaffleCommandId());
                giveaway.setStartRaffleCommand(startRaffleCommand);
            }

            CommandSettings startRaffle = new CommandSettings(giveawayRow.getStartRaffleCommandName(),
                    giveawayRow.getStartRaffleFlags(), giveawayRow.getStartRafflePermission(),
                    giveawayRow.getStartRaffleMessage());

            CommandSettings enterRaffle = new CommandSettings(giveawayRow.getEnterRaffleCommandName(),
                    giveawayRow.getEnterRaffleFlags(), giveawayRow.getEnterRafflePermission(),
                    giveawayRow.getEnterRaffleMessage());

            CommandSettings raffleStatus = new CommandSettings(giveawayRow.getRaffleStatusCommandName(),
                    giveawayRow.getRaffleStatusFlags(), giveawayRow.getRaffleStatusPermission(),
                    giveawayRow.getRaffleStatusMessage());

            RaffleSettings raffleSettings = new RaffleSettings(startRaffle, enterRaffle, raffleStatus,
                    Duration.ofSeconds(giveawayRow.getRaffleDuration()));

            giveaway.setRaffleSettings(raffleSettings);

            for (PrizeRow prizeRow : prizeRepository.findAllByGiveawayId(giveaway.getId())) {
                giveaway.addPrize(createPrize(botHomeId, prizeRow));
            }
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
        if (giveaway.isRafflesEnabled()) {
            flags |= RAFFLE_FLAG;
        }
        return flags;
    }
}
