package com.ryan_mtg.servobot.data.factories;

import com.ryan_mtg.servobot.commands.hierarchy.Command;
import com.ryan_mtg.servobot.commands.CommandTable;
import com.ryan_mtg.servobot.commands.giveaway.RequestPrizeCommand;
import com.ryan_mtg.servobot.commands.giveaway.StartRaffleCommand;
import com.ryan_mtg.servobot.data.models.GiveawayRow;
import com.ryan_mtg.servobot.data.models.PrizeRow;
import com.ryan_mtg.servobot.data.repositories.GiveawayRepository;
import com.ryan_mtg.servobot.data.repositories.PrizeRepository;
import com.ryan_mtg.servobot.events.BotErrorException;
import com.ryan_mtg.servobot.model.giveaway.GiveawayCommandSettings;
import com.ryan_mtg.servobot.model.giveaway.Giveaway;
import com.ryan_mtg.servobot.model.giveaway.GiveawayEdit;
import com.ryan_mtg.servobot.model.giveaway.Prize;
import com.ryan_mtg.servobot.model.giveaway.RaffleSettings;
import com.ryan_mtg.servobot.user.HomedUser;
import com.ryan_mtg.servobot.user.HomedUserTable;
import com.ryan_mtg.servobot.user.User;
import com.ryan_mtg.servobot.utility.Flags;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class GiveawaySerializer {
    private static final int SELF_SERVICE_FLAG = 1 << 0;
    private static final int RAFFLE_FLAG = 1 << 1;

    @Autowired
    private GiveawayRepository giveawayRepository;

    @Autowired
    private PrizeRepository prizeRepository;

    @Autowired
    private CommandTableSerializer commandTableSerializer;

    @Transactional(rollbackOn = BotErrorException.class)
    public void commit(final int botHomeId, final GiveawayEdit giveawayEdit) {
        commandTableSerializer.commit(botHomeId, giveawayEdit.getCommandTableEdit());

        giveawayEdit.getSavedPrizes().forEach((key, value) -> savePrize(value, key));
        giveawayEdit.getSavedGiveaways().forEach(giveaway -> saveGiveaway(botHomeId, giveaway));
        giveawayEdit.getDeletedPrizes().forEach(prize -> prizeRepository.deleteById(prize.getId()));
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

        giveawayRow.setRaffleDuration((int)raffleSettings.getDuration().getSeconds());
        giveawayRow.setRaffleWinnerCount(raffleSettings.getWinnerCount());
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

        giveawayRow.setRaffleWinnerResponse(raffleSettings.getWinnerResponse());
        giveawayRow.setDiscordChannel(raffleSettings.getDiscordChannel());

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
        prizeRow.setDescription(prize.getDescription());
        prizeRow.setWinnerId(prize.getWinner() == null ? 0 : prize.getWinner().getId());

        prizeRepository.save(prizeRow);

        prize.setId(prizeRow.getId());
    }

    public List<Giveaway> createGiveaways(final int botHomeId, final HomedUserTable homedUserTable,
            final CommandTable commandTable) throws BotErrorException {
        Iterable<GiveawayRow> giveawayRows = giveawayRepository.findAllByBotHomeId(botHomeId);
        Iterable<Integer> giveawayIds = SerializationSupport.getIds(giveawayRows, GiveawayRow::getId);

        Map<Integer, List<PrizeRow>> prizeRowMap = SerializationSupport.getIdMapping(
            prizeRepository.findAllByGiveawayIdIn(giveawayIds), giveawayIds, PrizeRow::getGiveawayId);

        Set<Integer> userIds = new HashSet<>();
        prizeRowMap.forEach((giveawayId, prizeRows) -> prizeRows.forEach(prizeRow -> {
            if (prizeRow.getWinnerId() != User.UNREGISTERED_ID) {
                userIds.add(prizeRow.getWinnerId());
            }
        }));

        Map<Integer, HomedUser> homedUserMap = new HashMap<>();
        homedUserTable.getHomedUsers(userIds).forEach(homedUser -> homedUserMap.put(homedUser.getId(), homedUser));

        Map<Integer, List<Prize>> prizeMap = createPrizes(botHomeId, prizeRowMap, homedUserMap);

        List<Giveaway> giveaways = new ArrayList<>();
        for (GiveawayRow giveawayRow : giveawayRows) {
            giveaways.add(createGiveaway(giveawayRow, commandTable, prizeMap));
        }
        return giveaways;
    }

    private Map<Integer, List<Prize>> createPrizes(final int botHomeId, final Map<Integer, List<PrizeRow>> prizeRowMap,
            final Map<Integer, HomedUser> homedUserMap) throws BotErrorException {
        Map<Integer, List<Prize>> prizeMap = new HashMap<>();
        for(Map.Entry<Integer, List<PrizeRow>> entry : prizeRowMap.entrySet()) {
            List<Prize> prizes = new ArrayList<>();
            for (PrizeRow prizeRow : entry.getValue()) {
                prizes.add(createPrize(botHomeId, prizeRow, homedUserMap));
            }
            prizeMap.put(entry.getKey(), prizes);
        }
        prizeRowMap.forEach((giveawayId, prizeRows) -> {
        });
        return prizeMap;
    }

    private Prize createPrize(final int botHomeId, final PrizeRow prizeRow, final Map<Integer, HomedUser> homedUserMap)
            throws BotErrorException {
        Prize prize = new Prize(prizeRow.getId(), prizeRow.getReward(), prizeRow.getDescription());
        prize.setStatus(prizeRow.getStatus());
        if (prizeRow.getWinnerId() != User.UNREGISTERED_ID) {
            prize.setWinner(homedUserMap.get(prizeRow.getWinnerId()));
        }
        return prize;
    }

    private Giveaway createGiveaway(final GiveawayRow giveawayRow, final CommandTable commandTable,
            final Map<Integer, List<Prize>> prizeMap) throws BotErrorException {
        int flags = giveawayRow.getFlags();
        Giveaway giveaway = new Giveaway(giveawayRow.getId(), giveawayRow.getName(),
                Flags.hasFlag(flags, SELF_SERVICE_FLAG), Flags.hasFlag(flags, RAFFLE_FLAG));
        giveaway.setState(giveawayRow.getState());

        for (Prize prize : prizeMap.get(giveawayRow.getId())) {
            giveaway.addPrize(prize);
        }

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

            GiveawayCommandSettings startRaffle = new GiveawayCommandSettings(giveawayRow.getStartRaffleCommandName(),
                    giveawayRow.getStartRaffleFlags(), giveawayRow.getStartRafflePermission(),
                    giveawayRow.getStartRaffleMessage());

            GiveawayCommandSettings enterRaffle = new GiveawayCommandSettings(giveawayRow.getEnterRaffleCommandName(),
                    giveawayRow.getEnterRaffleFlags(), giveawayRow.getEnterRafflePermission(),
                    giveawayRow.getEnterRaffleMessage());

            GiveawayCommandSettings raffleStatus = new GiveawayCommandSettings(giveawayRow.getRaffleStatusCommandName(),
                    giveawayRow.getRaffleStatusFlags(), giveawayRow.getRaffleStatusPermission(),
                    giveawayRow.getRaffleStatusMessage());

            RaffleSettings raffleSettings = new RaffleSettings(startRaffle, enterRaffle, raffleStatus,
                    Duration.ofSeconds(giveawayRow.getRaffleDuration()), giveawayRow.getRaffleWinnerCount(),
                    giveawayRow.getRaffleWinnerResponse(), giveawayRow.getDiscordChannel());

            giveaway.setRaffleSettings(raffleSettings);
        }

        return giveaway;
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
