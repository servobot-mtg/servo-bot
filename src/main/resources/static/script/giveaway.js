function showAddGiveawayForm() {
    const label = 'add-giveaway';
    hideElementById(label + '-button');

    let giveawayNameInput = document.getElementById(label + '-name');
    giveawayNameInput.value = '';
    showElementInlineById(label + '-form');
}

function addGiveaway(botHomeId) {
    const name = document.getElementById('add-giveaway-name').value;
    const selfService = document.getElementById('add-giveaway-self-service').checked;
    const raffle = document.getElementById('add-giveaway-raffle').checked;
    postAddGiveaway(botHomeId, name, selfService, raffle);
}

async function postAddGiveaway(botHomeId, name, selfService, raffle) {
    const label = 'add-giveaway';
    const parameters = {botHomeId: botHomeId, name: name, selfService: selfService, raffle: raffle};
    let response = await makePost('/api/add_giveaway', parameters, [], false);

    if (response.ok) {
        hideElementById(label + '-form');
        showElementInlineById(label + '-button');

        location.reload();
    }
}

function startGiveaway(botHomeId, giveawayId) {
    postStartGiveaway(botHomeId, botHomeId, giveawayId);
}

async function postStartGiveaway(botHomeId, giveawayId) {
    const parameters = {botHomeId: botHomeId, giveawayId: giveawayId};
    let response = await makePost('/api/start_giveaway', parameters, [], false);

    if (response.ok) {
        const label = 'giveaway-' + giveawayId;
        hideElementById(label + '-play-button');
        showElementInlineById(label + '-pause-button');
        document.getElementById(label + '-state');
        statusElement.innerHTML = 'Active';
    }
}

function saveSelfService(botHomeId, giveawayId) {
    const label = 'giveaway-' + giveawayId;
    const requestPrizeCommandName = document.getElementById(label + '-request-prize-command').value;
    const prizeRequestLimit = document.getElementById(label + '-prize-request-limit').value;
    const prizeRequestUserLimit = document.getElementById(label + '-prize-request-user-limit').value;

    postSaveGiveawaySelfService(botHomeId, giveawayId, requestPrizeCommandName, prizeRequestLimit,
        prizeRequestUserLimit);
}

async function postSaveGiveawaySelfService(botHomeId, giveawayId, requestPrizeCommandName, prizeRequestLimit,
                                           prizeRequestUserLimit) {
    const label = 'giveaway-' + giveawayId;
    const parameters = {botHomeId: botHomeId, giveawayId: giveawayId, requestPrizeCommandName: requestPrizeCommandName,
        prizeRequestLimit: prizeRequestLimit, prizeRequestUserLimit: prizeRequestUserLimit};
    const responseElement = document.getElementById(label + '-self-service-save-response');
    await makePost('/api/save_giveaway_self_service', parameters, [responseElement], true);
}

function getCommandSettings(label) {
    const name = document.getElementById(label + '-command').value;

    const secure = document.getElementById(label + '-secured').innerText == decodedLockedIcon;
    const twitch = getServiceValue(label, 'twitch');
    const discord = getServiceValue(label, 'discord');
    const permission = document.getElementById(label + '-permission').value;
    const message = document.getElementById(label + '-message').value;

    return {name: name, secure: secure, twitch: twitch, discord: discord, permission: permission, message: message};
}

function saveRaffleSettings(botHomeId, giveawayId) {
    const label = 'giveaway-' + giveawayId;
    const duration = document.getElementById(label + '-raffle-duration').value;
    const winnerCount = document.getElementById(label + '-raffle-winner-count').value;
    const timed = document.getElementById(label + '-raffle-end-input').checked;

    const startRaffle = getCommandSettings(label + '-start-raffle');
    const enterRaffle = getCommandSettings(label + '-enter-raffle');
    const raffleStatus = getCommandSettings(label + '-raffle-status');
    const selectWinner = getCommandSettings(label + '-select-winner');

    const discordChannel = document.getElementById(label + '-discord-channel').value;

    const parameters = {botHomeId: botHomeId, giveawayId: giveawayId, timed: timed, duration: duration,
        winnerCount: winnerCount, discordChannel: discordChannel, startRaffle: startRaffle, enterRaffle: enterRaffle,
        raffleStatus: raffleStatus, selectWinner: selectWinner};

    postSaveGiveawayRaffleSettings(botHomeId, parameters);
}

async function postSaveGiveawayRaffleSettings(giveawayId, parameters) {
    const label = 'giveaway-' + giveawayId;
    const responseElement = document.getElementById(label + '-raffle-save-response');
    await makePost('/api/save_giveaway_raffle_settings', parameters, [responseElement], true);
}

function showAddPrizeForm(giveawayId) {
    const label = 'giveaway-' + giveawayId + '-add-prize';
    hideElementById(label + '-button');

    document.getElementById(label + '-multiple').checked = false;
    let rewardInputElement = document.getElementById(label + '-reward');
    rewardInputElement.value = '';
    showElementInlineById(label + '-form');
    rewardInputElement.focus();
}

function toggleAddPrizeMultiples(giveawayId) {
    const label = 'giveaway-' + giveawayId + '-add-prize';
    const multiples = document.getElementById(label + '-multiple').checked;
    if (multiples) {
        hideElementById(label + '-reward-div');
        showElementById(label + '-reward-multiple-div');
    } else {
        showElementById(label + '-reward-div');
        hideElementById(label + '-reward-multiple-div');
    }
}

function addPrize(botHomeId, giveawayId) {
    const label = 'giveaway-' + giveawayId + '-add-prize';
    const multiples = document.getElementById(label + '-multiple').checked;
    const description = document.getElementById(label + '-description').value;
    if (multiples) {
        const rewards = document.getElementById(label + '-reward-multiple').value;
        postAddPrizes(botHomeId, giveawayId, rewards, description);
    } else {
        const reward = document.getElementById(label + '-reward').value;
        postAddPrize(botHomeId, giveawayId, reward, description);
    }
}

async function postAddPrize(botHomeId, giveawayId, reward, description) {
    const label = 'giveaway-' + giveawayId + '-add-prize';
    const parameters = {botHomeId: botHomeId, giveawayId: giveawayId, reward: reward, description: description};
    let response = await makePost('/api/add_prize', parameters, [], false);

    if (response.ok) {
        hideElementById(label + '-form');
        showElementInlineById(label + '-button');

        let prize = await response.json();
        addPrizeRow(botHomeId, giveawayId, prize);
    }
}

async function postAddPrizes(botHomeId, giveawayId, rewards, description) {
    const label = 'giveaway-' + giveawayId + '-add-prize';
    const parameters = {botHomeId: botHomeId, giveawayId: giveawayId, rewards: rewards, description: description};
    let response = await makePost('/api/add_prizes', parameters, [], false);

    if (response.ok) {
        hideElementById(label + '-form');
        showElementInlineById(label + '-button');

        let prizes = await response.json();
        for (let i = 0; i < prizes.length; i++) {
            addPrizeRow(botHomeId, giveawayId, prizes[i]);
        }
    }
}

function addPrizeRow(botHomeId, giveawayId, prize) {
    const label = 'giveaway-' + giveawayId;
    let prizeTable = document.getElementById(label + '-prize-table');
    let newRow = prizeTable.insertRow();

    const prizeLabel = 'prize-' + prize.id;
    newRow.id = label + '-row';
    let rewardCell = newRow.insertCell();
    rewardCell.innerHTML = prize.reward;

    let statusCell = newRow.insertCell();
    statusCell.id = label + '-status';
    statusCell.innerHTML = prize.status;

    let descriptionCell = newRow.insertCell();
    descriptionCell.innerHTML = prize.description;

    let actionsCell = newRow.insertCell();
    actionsCell.innerHTML = "";

    let winnerCell = newRow.insertCell();
    if (prize.winner) {
        winnerCell.innerHTML = prize.winner;
    } else {
        winnerCell.innerHTML = emptySetIcon;
    }

    let deleteCell = newRow.insertCell();
    deleteCell.classList.add('pseudo-link');
    deleteCell.innerHTML = trashcanIcon;
    deleteCell.onclick = function () {
        deletePrize(botHomeId, giveawayId, prize.id);
    };
}

function deletePrize(botHomeId, giveawayId, prizeId) {
    const label = 'prize-' + prizeId;
    let performDelete = true;
    const statusElement = document.getElementById(label + '-status');
    if (statusElement.innerText != 'BESTOWED') {
        performDelete = window.confirm('Are you sure you want to delete the command?');
    }
    if (performDelete) {
        const parameters = {botHomeId: botHomeId, giveawayId: giveawayId, objectId: prizeId};
        postDelete('/api/delete_prize', parameters, label + '-row');
    }
}

function awardReward(botHomeId, rewardId) {
    postAwardReward(botHomeId, rewardId);
}

async function postAwardReward(botHomeId, rewardId) {
    const parameters = {botHomeId: botHomeId, objectId: rewardId};
    let response = await makePost('/api/award_reward', parameters, [], false);

    if (response.ok) {
        let winner = await response.json();
        if (winner.discordName) {
            document.getElementById('reward-' + rewardId + '-winner').innerText = winner.discordName;
            document.getElementById('reward-' + rewardId + '-status').innerText = 'AWARDED';
        }
    }
}

function bestowPrize(botHomeId, giveawayId, prizeId) {
    postBestowPrize(botHomeId, giveawayId, prizeId);
}

async function postBestowPrize(botHomeId, giveawayId, prizeId) {
    const parameters = {botHomeId: botHomeId, giveawayId: giveawayId, prizeId: prizeId};
    let response = await makePost('/api/bestow_prize', parameters, [], false);

    if (response.ok) {
        document.getElementById('prize-' + prizeId + '-status').innerText = 'BESTOWED';
    }
}

function copyPrizeMessage(prizeId) {
    let element = document.getElementById('prize-' + prizeId + '-message');
    console.log(element.value);
    navigator.clipboard.writeText(element.value);
}

const giveawayCommandsData = [
    {},
    {label: 'start-raffle'},
    {label: 'enter-raffle'},
    {label: 'raffle-status'},
    {label: 'select-winner'},
];

function toggleTwitchCommandSetting(giveawayId, settingsId) {
    setCommandService(giveawayId, settingsId, 'twitch');
}

function toggleDiscordCommandSetting(giveawayId, settingsId) {
    setCommandService(giveawayId, settingsId, 'discord');
}

function getServiceValue(label, service) {
    let imgElement = document.getElementById(label + '-' + service + '-img');
    return imgElement.src.endsWith('/images/' + service + '.ico');
}

function setCommandService(giveawayId, settingsId, service, serviceType) {
    const label = 'giveaway-' + giveawayId + '-' + giveawayCommandsData[settingsId].label;
    let imgElement = document.getElementById(label + '-' + service + '-img');
    const serviceValue = getServiceValue(label, service);
    if (serviceValue) {
        imgElement.src = '/images/no-' + service + '.ico';
    } else {
        imgElement.src = '/images/' + service + '.ico';
    }
}

function toggleSecureCommandSetting(giveawayId, settingsId) {
    const label = 'giveaway-' + giveawayId + '-' + giveawayCommandsData[settingsId].label;
    let valueElement = document.getElementById(label + '-secured');
    const secure = valueElement.innerText != decodedLockedIcon;

    if (secure) {
        valueElement.innerHTML = lockedIcon;
    } else {
        valueElement.innerHTML = unlockedIcon;
    }
}
