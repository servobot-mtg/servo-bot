function decodeHtmlEntity(html) {
    let txt = document.createElement('textarea');
    txt.innerHTML = html;
    return txt.value;
}

const lockedIcon = '&#x1F512;';
const unlockedIcon = '&#x1F511;';
const decodedLockedIcon = decodeHtmlEntity('&#x1F512;');
const checkmarkIcon = '&#x2714;&#xFE0F;';
const crossIcon = '&#x274C;';
const yellowCircleIcon = '&#x1F7E1;';
const trashcanIcon = '&#x1F5D1;';
const penIcon = '&#x270F;&#xFE0F;';
const bookIcon = '&#x1F4BE;';
const defaultCommandFlags = 2+4;

function secureCommand(botHomeId, commandId) {
    postSecureCommand(botHomeId, commandId, 'command-' + commandId);
}

async function postSecureCommand(botHomeId, commandId, label) {
    return postSecureObject('/api/secure_command', botHomeId, commandId, label);
}

function secureReaction(event, botHomeId, reactionId) {
    postSecureReaction(botHomeId, reactionId, event.currentTarget.dataset.label);
}

async function postSecureReaction(botHomeId, reactionId, label) {
    return postSecureObject('/api/secure_reaction', botHomeId, reactionId, label);
}

async function postSecureObject(endPoint, botHomeId, objectId, label) {
    let valueElement = document.getElementById(label + '-secured');
    const secure = valueElement.innerText != decodedLockedIcon;
    const parameters = {botHomeId: botHomeId, objectId: objectId, secure: secure};
    const responseElement = document.getElementById(label + '-secure-response');
    let response = await makePost(endPoint, parameters, [responseElement], false);
    if (response.ok) {
        setSecure(document.getElementById(label + '-row'), valueElement, await response.json());
    }
}

function setSecure(rowElement, iconElement, secure) {
    if (secure) {
        iconElement.innerHTML = lockedIcon;
        rowElement.classList.add('secure');
    } else {
        iconElement.innerHTML = unlockedIcon;
        rowElement.classList.remove('secure');
    }
}

function toggleCommandTwitch(botHomeId, commandId) {
    postSetCommandService(botHomeId, commandId, 'command-' + commandId, 'twitch', 1);
}

function toggleCommandDiscord(botHomeId, commandId) {
    postSetCommandService(botHomeId, commandId, 'command-' + commandId, 'discord', 2);
}

async function postSetCommandService(botHomeId, commandId, label, service, serviceType) {
    let imgElement = document.getElementById(label + '-' + service + '-img');
    const serviceValue = imgElement.src.endsWith('/images/' + service + '.ico');
    const parameters = {botHomeId: botHomeId, commandId: commandId, serviceType: serviceType, value: !serviceValue};
    const responseElement = document.getElementById(label + '-' + service + '-response');
    let response =
        await makePost('/api/set_command_service', parameters, [responseElement], false);
    if (response.ok) {
        if (serviceValue) {
            imgElement.src = '/images/no-' + service + '.ico';
        } else {
            imgElement.src = '/images/' + service + '.ico';
        }
    }
}

function updateCommandPermission(event, botHomeId, commandId) {
    postUpdateCommandPermission(botHomeId, commandId, event.target.value,
        document.getElementById('command-' + commandId + '-permission-updated'));
}

async function postUpdateCommandPermission(botHomeId, commandId, permission, responseElement) {
    const parameters = {botHomeId: botHomeId, commandId: commandId, permission: permission};
    makePost('/api/set_command_permission', parameters, [responseElement], true);
}

function updateTimeZone(event, botHomeId, responseElementId) {
    postTimeZone(botHomeId, event.target.value, document.getElementById(responseElementId));
}

async function postTimeZone(botHomeId, timeZone, responseElement) {
    const parameters = {botHomeId: botHomeId, timeZone: timeZone};
    makePost('/api/set_home_time_zone', parameters, [responseElement], true);
}

function deleteCommand(botHomeId, commandId) {
    const performDelete = window.confirm('Are you sure you want to delete the command?');
    if (performDelete) {
        const parameters = {botHomeId: botHomeId, objectId: commandId};
        postDelete('/api/delete_command', parameters, 'command-' + commandId + '-row');
    }
}

async function postDelete(endpoint, parameters, elementId) {
    let response = await makePost(endpoint, parameters, [], false);
    if (response.ok) {
        let element = document.getElementById(elementId);
        element.parentElement.removeChild(element);
    }
}

function deleteTrigger(botHomeId, triggerId) {
    const parameters = {botHomeId: botHomeId, objectId: triggerId};
    postDelete('/api/delete_trigger', parameters, 'trigger-' + triggerId);
}

function deleteReaction(botHomeId, reactionId) {
    const parameters = {botHomeId: botHomeId, objectId: reactionId};
    postDelete('/api/delete_reaction', parameters, 'reaction-' + reactionId + '-row');
}

function deletePattern(botHomeId, patternId) {
    const parameters = {botHomeId: botHomeId, objectId: patternId};
    postDelete('/api/delete_pattern', parameters, 'pattern-' + patternId);
}

function deleteStatement(event, botHomeId, bookId, statementId) {
    const parameters = {botHomeId: botHomeId, bookId: bookId, statementId: statementId};
    postDelete('/api/delete_statement', parameters, 'statement-' + statementId + '-row');
}

function editStatement(event, statementId) {
    hideElementById('statement-' + statementId + '-display');
    showElementById('statement-' + statementId + '-edit');
}

function modifyStatement(event, botHomeId, bookId, statementId) {
    let inputElement = document.getElementById('statement-' + statementId + '-input');

    let valueElement = document.getElementById('statement-' + statementId + '-value');

    if (valueElement.innerText != inputElement.value) {
        postModifyStatement(botHomeId, bookId, statementId, inputElement.value);
    } else {
        resetStatement(statementId);
    }
}

function resetStatement(statementId) {
    hideElementById('statement-' + statementId + '-edit');
    showElementById('statement-' + statementId + '-display');
}

async function postModifyStatement(botHomeId, bookId, statementId, text) {
    const parameters = {botHomeId: botHomeId, bookId: bookId, statementId: statementId, text: text};
    let response = await makePost('/api/modify_statement', parameters, [], false);
    if (response.ok) {
        let valueElement = document.getElementById('statement-' + statementId + '-value');
        valueElement.innerText = text;
        resetStatement(statementId);
    }
}

function startHome(botHomeId) {
    postStartHome(botHomeId);
}

async function postStartHome(botHomeId) {
    const parameters = {botHomeId: botHomeId};
    let statusElement = document.getElementById('status');
    statusElement.innerHTML = yellowCircleIcon;
    statusElement.title = 'Restarting';
    let response = await makePost('/api/start_home', parameters, [], false);
    if (response.ok) {
        showElementInlineById('stop-button');
        statusElement.innerHTML = checkmarkIcon;
        statusElement.title = 'Active';

        window.setTimeout(function () { location.reload(); }, 500);
    }
}

function stopHome(botHomeId) {
    postStopHome(botHomeId);
}

async function postStopHome(botHomeId) {
    const parameters = {botHomeId: botHomeId};
    let response = await makePost('/api/stop_home', parameters, [], false);
    if (response.ok) {
        hideElementById('stop-button');
        let statusElement = document.getElementById('status');
        statusElement.innerHTML = crossIcon;
        statusElement.title = 'Idle';
    }
}

function showAddTriggerForm(commandId) {
    const label = 'add-trigger-' + commandId;
    hideElementById(label + '-button');

    let textInputElement = document.getElementById(label + '-text-input');
    textInputElement.value = '';
    showElementInlineById(label + '-form');
    textInputElement.focus();
}

function addTrigger(botHomeId, commandId) {
    const text = document.getElementById('add-trigger-' + commandId + '-text-input').value;
    const triggerType = parseInt(document.getElementById('add-trigger-' + commandId + '-type-input').value);
    postAddTrigger(botHomeId, commandId, text, triggerType);
}

async function postAddTrigger(botHomeId, commandId, text, triggerType) {
    const label = 'add-trigger-' + commandId;
    const parameters = {botHomeId: botHomeId, commandId: commandId, text: text, triggerType: triggerType};
    let response = await makePost('/api/add_trigger', parameters, [], false);

    if (response.ok) {
        hideElementById(label + '-form');
        showElementInlineById(label + '-button');

        let addTriggerResponse = await response.json();
        addTriggerTable(addTriggerResponse.addedTrigger, botHomeId, commandId, text);
    }
}

function addTriggerTable(trigger, botHomeId, commandId, text) {
    let triggerType;
    switch (trigger.type) {
        case 1:
            triggerType = 'alias';
            break;
        case 2:
            triggerType = 'event';
            break;
        case 3:
            triggerType = 'alert';
            break;
    }
    const label = triggerType + '-triggers-' + commandId;
    let triggersSpan = document.getElementById(label);

    let triggerTable = document.createElement('table');
    triggerTable.classList.add(triggerType + '-label', 'label', 'label-table');
    triggerTable.id = 'trigger-' + trigger.id;
    let row = triggerTable.insertRow();
    let triggerCell = row.insertCell();
    triggerCell.innerHTML = text;

    let deleteCell = row.insertCell();
    deleteCell.classList.add('pseudo-link', triggerType + '-delete');
    deleteCell.innerHTML = 'x';
    deleteCell.onclick = function () {
        deleteTrigger(botHomeId, trigger.id);
    };

    triggersSpan.appendChild(triggerTable);
}

function triggerAlert(event, botHomeId, triggerId) {
    const alertToken = event.currentTarget.dataset.alertToken;
    postTriggerAlert(botHomeId, alertToken, triggerId);
}

async function postTriggerAlert(botHomeId, alertToken, triggerId) {
    const parameters = {botHomeId: botHomeId, alertToken: alertToken};
    const responseElement = document.getElementById('trigger-' + triggerId + '-alert-response');
    makePost('/api/trigger_alert', parameters, [responseElement], true);
}

function showAddStatementForm() {
    hideElementById('add-statement-button');

    let textInputElement = document.getElementById('add-statement-text-input');
    textInputElement.value = '';
    showElementInlineById('add-statement-form');
    textInputElement.focus();
}

function addStatement(botHomeId, bookId) {
    const text = document.getElementById('add-statement-text-input').value;
    postAddStatement(botHomeId, bookId, text);
}

async function postAddStatement(botHomeId, bookId, text) {
    const parameters = {botHomeId: botHomeId, bookId: bookId, text: text};
    let response = await makePost('/api/add_statement', parameters, [], false);

    if (response.ok) {
        hideElementById('add-statement-form');
        showElementById('add-statement-button');

        let statement = await response.json();

        addStatementRow(statement, botHomeId, bookId);
    }
}

function addStatementRow(statement, botHomeId, bookId) {
    let statementTable = document.getElementById('book-' + bookId + '-table');
    let newRow = statementTable.insertRow();

    const label = 'statement-' + statement.id;
    newRow.id = label + '-row';

    let displayDiv = document.createElement('div');
    displayDiv.id = label + '-display';
    let textSpan = document.createElement('span');
    textSpan.id = label + '-value';
    textSpan.innerHTML = statement.text;
    displayDiv.appendChild(textSpan);
    let editButtonSpan = document.createElement('span');
    editButtonSpan.classList.add('pseudo-link');
    editButtonSpan.onclick = function(event) {
        editStatement(event, statement.id);
    };
    editButtonSpan.innerHTML = penIcon;
    displayDiv.appendChild(editButtonSpan);

    let editDiv = document.createElement('div');
    editDiv.id = label + '-edit';
    editDiv.classList.add('hidden');

    let textInput = document.createElement('input');
    textInput.type = 'text';
    textInput.value = statement.text;
    textInput.size = statement.text.length;
    textInput.id = label + '-input';
    editDiv.appendChild(textInput);

    let modifyButtonSpan = document.createElement('span');
    modifyButtonSpan.classList.add('pseudo-link');
    modifyButtonSpan.onclick = function(event) {
        modifyStatement(event, botHomeId, bookId, statement.id);
    };
    modifyButtonSpan.innerHTML = bookIcon;
    editDiv.appendChild(modifyButtonSpan);

    let textCell = newRow.insertCell();
    textCell.appendChild(displayDiv);
    textCell.appendChild(editDiv);

    let deleteCell = newRow.insertCell();
    deleteCell.classList.add('pseudo-link');
    deleteCell.innerHTML = trashcanIcon;
    deleteCell.onclick = function (event) {
        deleteStatement(event, botHomeId, bookId, statement.id);
    };
}

function showAddCommandForm() {
    hideElementById('add-command-button');

    let typeSelect = document.getElementById('add-command-type-input');
    typeSelect.selectedIndex = 0;
    changeAddCommandType(typeSelect);
    document.getElementById('add-command-permissions-input').selectedIndex = 4;
    document.getElementById('add-command-secure-input').checked = false;
    document.getElementById('add-command-text-input').value = '';
    document.getElementById('add-command-text-2-input').value = '';
    document.getElementById('add-command-integer-input').value = 0;
    document.getElementById('add-command-book-input').selectedIndex = 0;
    document.getElementById('add-command-game-queue-input').selectedIndex = 0;
    document.getElementById('add-command-service-input').selectedIndex = 0;
    showElementInlineById('add-command-form');
}

function addAddCommandParameter(parameters, inputId, parameterName) {
    let inputElement = document.getElementById('add-command-' + inputId + '-input');
    if (parameterName === 'longParameter') {
        parameters[parameterName] = parseInt(inputElement.value);
    } if (inputElement.tagName === 'INPUT' && inputElement.type === 'checkbox') {
        parameters[parameterName] = inputElement.checked;
    } else {
        parameters[parameterName] = inputElement.value;
    }
}

function getParameterName(parameterId) {
    switch (parameterId) {
        case 'text':
            return 'stringParameter';
        case 'text-2':
            return 'stringParameter2';
        case 'service':
        case 'book':
        case 'game-queue':
        case 'integer':
            return 'longParameter';
    }
}

function getAddCommandFlags() {
    const secure = document.getElementById('add-command-secure-input').checked;
    return defaultCommandFlags + secure;
}

function addCommand(botHomeId) {
    const parameters = {botHomeId: botHomeId};
    const commandType = parseInt(document.getElementById('add-command-type-input').value);
    addAddCommandParameter(parameters, 'type', 'type');
    addAddCommandParameter(parameters, 'permissions', 'permission');
    parameters['flags'] = getAddCommandFlags();

    const data = commandData[commandType];
    for (let i = 0; i < data.parameters.length; i++) {
        let parameter = data.parameters[i];

        addAddCommandParameter(parameters, parameter.id, getParameterName(parameter.id));
    }
    if (!parameters.hasOwnProperty('longParameter')) {
        parameters['longParameter'] = 0;
    }

    postAddCommand(parameters);
}

async function postAddCommand(parameters) {
    console.log(parameters);
    let response = await makePost('/api/add_command', parameters, [], false);

    if (response.ok) {
        hideElementById('add-command-form');
        showElementById('add-command-button');

        let commandDescriptor = await response.json();

        addCommandRow(commandDescriptor, parameters.botHomeId);
    }
}

const commandData = [
    {},
    {name: 'Respond Command', parameters: [{id: 'text', name: 'Text'}]}, //1
    {name: 'Random Statement Command', parameters: [{id: 'book', name: 'Book'}]}, //2
    {name: 'Friendship Tier Command', parameters: []}, //3
    {name: 'Message Channel Command', parameters: [{id: 'text', name: 'Channel Name'}, {id: 'text-2', name: 'Text'},
        {id: 'service', name: 'Service'}]}, //4
    {name: 'Add Command', parameters: []}, //5
    {name: 'Delete Command', parameters: []}, //6
    {name: 'Game Queue Command', parameters: [{id: 'game-queue', name: 'Game Queue'}]}, //7
    {name: 'Join Game Queue Command', parameters: [{id: 'game-queue', name: 'Game Queue'}]}, //8
    {name: 'Show Game Queue Command', parameters: [{id: 'game-queue', name: 'Game Queue'}]}, //9
    {name: 'Remove From Game Queue Command', parameters: [{id: 'game-queue', name: 'Game Queue'}]}, //10
    {name: 'Set Arena Username Command', parameters: []}, //11
    {name: 'Show Arena Usernames Command', parameters: []}, //12
    {name: 'Set Role Command', parameters: [{id: 'text', name: 'Role Name'}]}, //13
    {name: 'Set Status Command', parameters: [{id: 'book', name: 'Book'}]}, //14
    {name: 'Add Statement Command', parameters: []}, //15
    {name: 'Delayed Alert Command',
        parameters: [{id: 'text', name: 'Alert Token'}, {id: 'integer', name: 'Delay (seconds)'}]}, //16
    {name: 'Show Value Command', parameters: []}, //17
    {name: 'Set Value Command', parameters: []}, //18
    {name: 'Math Command', parameters: []}, //19
    {name: 'Start Giveaway Command', parameters: []}, //20
    {name: 'Enter Giveaway Command', parameters: []}, //21
    {name: 'Giveaway Status Command', parameters: []}, //22
    {name: 'Select Giveaway Winner Command', parameters: []}, //23
    {name: 'Add Reaction Command', parameters: [{id: 'text', name: 'Emote Name'}]}, //23
];

const permissions = ['ADMIN', 'STREAMER', 'MOD', 'SUB', 'ANYONE'];

function changeAddCommandType(selectElement) {
    const commandType = parseInt(selectElement.value);
    const data = commandData[commandType];

    let elementIds = [];
    for (let i = 0; i < data.parameters.length; i++) {
        let parameter = data.parameters[i];
        setElementText('add-command-' +  parameter.id + '-label', parameter.name);
        elementIds.push('add-command-' +  parameter.id + '-div');
    }

    showAddCommandElements(elementIds);

    if (data.parameters.length > 0) {
        let parameter = data.parameters[0];
        document.getElementById('add-command-' +  parameter.id + '-input').focus();
    }
}

function setElementText(elementId, text) {
    document.getElementById(elementId).innerHTML = text;
}

const addCommandElements = ['add-command-text-div', 'add-command-text-2-div', 'add-command-service-div',
    'add-command-book-div', 'add-command-game-queue-div', 'add-command-integer-div'];

function showAddCommandElements(elementIds) {
    for (let i = 0; i < addCommandElements.length; i++) {
        showOrHideElement(elementIds.includes(addCommandElements[i]), addCommandElements[i]);
    }

    if (elementIds.length > 0) {
        showOrHideElement(elementIds.length > 0, 'add-command-break');
    }

    showOrHideElement(elementIds.includes('add-command-text-div'), 'add-command-break-2');
    showOrHideElement(elementIds.includes('add-command-text-2-div'), 'add-command-break-3');
}

function showOrHideElement(show, elementId) {
    if (show) {
        showElementInlineById(elementId);
    } else {
        hideElementById(elementId);
    }
}

function addCommandRow(commandDescriptor, botHomeId) {
    let commandTable = document.getElementById('command-table');
    let newRow = commandTable.insertRow();

    const label = 'command-' + commandDescriptor.command.id;
    newRow.id = label + '-row';
    if (commandDescriptor.command.secure) {
        newRow.classList.add('secure');
    }

    let idCell = newRow.insertCell();
    idCell.innerHTML = commandDescriptor.command.id;

    let typeCell = newRow.insertCell();
    typeCell.innerHTML = commandDescriptor.type;

    let descriptionCell = newRow.insertCell();
    descriptionCell.innerHTML = commandDescriptor.description;

    let triggersCell = newRow.insertCell();
    let aliasTriggersSpan = document.createElement('span');
    aliasTriggersSpan.id = 'alias-triggers-' + commandDescriptor.command.id;
    triggersCell.appendChild(aliasTriggersSpan);

    let addTriggerLabel = 'add-trigger-' + commandDescriptor.command.id;
    let addTriggerSpan = document.createElement('span');
    addTriggerSpan.classList.add('add-trigger');

    let addTriggerButtonDiv = document.createElement('div');
    addTriggerButtonDiv.id = addTriggerLabel + '-button';
    addTriggerButtonDiv.classList.add('pseudo-link', 'add-button');
    addTriggerButtonDiv.title = 'Add a trigger';
    addTriggerButtonDiv.onclick = function () {
        showAddTriggerForm(commandDescriptor.command.id);
    };
    addTriggerButtonDiv.innerHTML = '+';
    addTriggerSpan.appendChild(addTriggerButtonDiv);

    let addTriggerForm = document.createElement('form');
    addTriggerForm.id = addTriggerLabel + '-form';
    addTriggerForm.classList.add('add-trigger-form', 'hidden');
    addTriggerForm.onsubmit = function () {
        addTrigger(botHomeId, commandDescriptor.command.id);
        return false;
    };

    let addTriggerTextInput = document.createElement('input');
    addTriggerTextInput.id = addTriggerLabel + '-text-input';
    addTriggerTextInput.type = 'text';
    addTriggerTextInput.name = 'trigger';
    addTriggerTextInput.size = 5;
    addTriggerForm.appendChild(addTriggerTextInput);

    let addTriggerSubmitInput = document.createElement('input');
    addTriggerSubmitInput.type = 'submit';
    addTriggerSubmitInput.value = '+';
    addTriggerForm.appendChild(addTriggerSubmitInput);
    addTriggerSpan.appendChild(addTriggerForm);
    triggersCell.appendChild(addTriggerSpan);

    let iconCell = newRow.insertCell();

    let secureIconSpan = document.createElement('span');
    secureIconSpan.id = label + '-secured';
    secureIconSpan.classList.add('pseudo-link');
    secureIconSpan.onclick = function () {
        secureCommand(botHomeId, commandDescriptor.command.id);
    };
    secureIconSpan.innerHTML = commandDescriptor.command.secure ? lockedIcon : unlockedIcon;
    iconCell.appendChild(secureIconSpan);

    let secureResponseSpan = document.createElement('span');
    secureResponseSpan.id = label + '-secure-response';
    iconCell.appendChild(secureResponseSpan);

    let twitchIconSpan = document.createElement('span');
    twitchIconSpan.id = label + '-twitch';
    twitchIconSpan.classList.add('pseudo-link');
    twitchIconSpan.onclick = function () {
        toggleCommandTwitch(botHomeId, commandDescriptor.command.id);
    };
    let twitchImg = document.createElement('img');
    twitchImg.id = label + '-twitch-img';
    twitchImg.classList.add('icon');
    twitchImg.title = 'Toggle Twitch use';
    twitchImg.src = commandDescriptor.command.twitch ? '/images/twitch.ico' : '/images/no-twitch.ico';
    twitchIconSpan.appendChild(twitchImg);
    iconCell.appendChild(twitchIconSpan);

    let twitchResponseSpan = document.createElement('span');
    twitchResponseSpan.id = label + '-twitch-response';
    iconCell.appendChild(twitchResponseSpan);

    let discordIconSpan = document.createElement('span');
    discordIconSpan.id = label + '-discord';
    discordIconSpan.classList.add('pseudo-link');
    discordIconSpan.onclick = function () {
        toggleCommandDiscord(botHomeId, commandDescriptor.command.id);
    };
    let discordImg = document.createElement('img');
    discordImg.id = label + '-discord-img';
    discordImg.classList.add('icon');
    discordImg.title = 'Toggle Discord use';
    discordImg.src = commandDescriptor.command.discord ? '/images/discord.ico' : '/images/no-discord.ico';
    discordIconSpan.appendChild(discordImg);
    iconCell.appendChild(discordIconSpan);

    let discordResponseSpan = document.createElement('span');
    discordResponseSpan.id = label + '-discord-response';
    iconCell.appendChild(discordResponseSpan);

    let permissionsCell = newRow.insertCell();
    let permissionsSelect = document.createElement('select');
    permissionsSelect.onchange = function (event) {
        updateCommandPermission(event, botHomeId, commandDescriptor.command.id);
    };
    for (let i = 0; i < permissions.length; i++) {
        let option = document.createElement('option');
        option.text = permissions[i];
        option.value = permissions[i];
        if (commandDescriptor.command.permission == permissions[i]) {
            option.selected = true;
        }
        permissionsSelect.add(option);
    }
    permissionsCell.appendChild(permissionsSelect);

    let permissionsResponseSpan = document.createElement('span');
    permissionsResponseSpan.id = label + '-permission-updated';
    permissionsCell.appendChild(permissionsResponseSpan);

    let deletionCell = newRow.insertCell();
    deletionCell.classList.add('pseudo-link');
    deletionCell.onclick = function () {
        deleteCommand(botHomeId, commandDescriptor.command.id);
    };
    deletionCell.innerHTML = trashcanIcon;
}

function showAddPatternForm(reactionId) {
    const label = 'add-pattern-' + reactionId;
    hideElementById(label + '-button');

    let textInputElement = document.getElementById(label + '-text-input');
    textInputElement.value = '';
    showElementInlineById(label + '-form');
    textInputElement.focus();
}

function addPattern(botHomeId, reactionId) {
    const label = 'add-pattern-' + reactionId;
    const text = document.getElementById(label + '-text-input').value;
    postAddPattern(botHomeId, reactionId, text);
}

async function postAddPattern(botHomeId, reactionId, text) {
    const label = 'add-pattern-' + reactionId;
    const parameters = {botHomeId: botHomeId, reactionId: reactionId, pattern: text};
    let response = await makePost('/api/add_pattern', parameters, [], false);

    if (response.ok) {
        hideElementById(label + '-form');
        showElementInlineById(label + '-button');

        let pattern = await response.json();
        addPatternTable(pattern, botHomeId, reactionId);
    }
}

function addPatternTable(pattern, botHomeId, reactionId) {
    const label = 'patterns-' + reactionId;
    let patternsSpan = document.getElementById(label);

    let patternTable = document.createElement('table');
    patternTable.classList.add('pattern-label', 'label', 'label-table');
    patternTable.id = 'pattern-' + pattern.id;
    let row = patternTable.insertRow();
    let aliasCell = row.insertCell();
    aliasCell.innerHTML = pattern.patternString;

    let deleteCell = row.insertCell();
    deleteCell.classList.add('pseudo-link', 'pattern-delete');
    deleteCell.innerHTML = 'x';
    deleteCell.onclick = function () {
        deletePattern(botHomeId, reactionId, pattern.id);
    };

    patternsSpan.appendChild(patternTable);
}

function showAddReactionForm() {
    const label = 'add-reaction';
    hideElementById(label + '-button');

    let emoteInputElement = document.getElementById(label + '-emote-input');
    emoteInputElement.selectedIndex = 0;
    showElementInlineById(label + '-form');
    emoteInputElement.focus();
}

function addReaction(botHomeId) {
    const emote = document.getElementById('add-reaction-emote-input').value;
    const secure = document.getElementById('add-reaction-secure-input').checked;
    postAddReaction(botHomeId, emote, secure);
}

async function postAddReaction(botHomeId, emote, secure) {
    const label = 'add-reaction';
    const parameters = {botHomeId: botHomeId, emote: emote, secure: secure};
    let response = await makePost('/api/add_reaction', parameters, [], false);

    if (response.ok) {
        hideElementById(label + '-form');
        showElementInlineById(label + '-button');

        let reaction = await response.json();
        addReactionRow(reaction, botHomeId);
    }
}

function addReactionRow(reaction, botHomeId) {
    let reactionTable = document.getElementById('reaction-table');
    let newRow = reactionTable.insertRow();

    const label = 'reaction-' + reaction.id;
    newRow.id = label + '-row';
    let keywordCell = newRow.insertCell();
    keywordCell.innerHTML = reaction.emoteName;

    let patternsCell = newRow.insertCell();
    let patternsSpan = document.createElement('span');
    patternsSpan.id = 'patterns-' + reaction.id;
    patternsCell.appendChild(patternsSpan);

    let addPatternLabel = 'add-pattern-' + reaction.id;
    let addPatternSpan = document.createElement('span');
    addPatternSpan.classList.add('add-pattern');

    let addPatternButtonDiv = document.createElement('div');
    addPatternButtonDiv.id = addPatternLabel + '-button';
    addPatternButtonDiv.classList.add('pseudo-link', 'add-button');
    addPatternButtonDiv.title = 'Add a pattern';
    addPatternButtonDiv.onclick = function () {
        showAddPatternForm(reaction.id);
    };
    addPatternButtonDiv.innerHTML = '+';
    addPatternSpan.appendChild(addPatternButtonDiv);

    let addPatternForm = document.createElement('form');
    addPatternForm.id = addPatternLabel + '-form';
    addPatternForm.classList.add('add-pattern-form', 'hidden');
    addPatternForm.onsubmit = function () {
        addPattern(botHomeId, reaction.id);
        return false;
    };

    let addPatternTextInput = document.createElement('input');
    addPatternTextInput.id = addPatternLabel + '-text-input';
    addPatternTextInput.type = 'text';
    addPatternTextInput.name = 'pattern';
    addPatternForm.appendChild(addPatternTextInput);

    let addPatternSubmitInput = document.createElement('input');
    addPatternSubmitInput.type = 'submit';
    addPatternSubmitInput.value = '+';
    addPatternForm.appendChild(addPatternSubmitInput);
    addPatternSpan.appendChild(addPatternForm);
    patternsCell.appendChild(addPatternSpan);

    let secureCell = newRow.insertCell();
    secureCell.classList.add('pseudo-link');
    secureCell.onclick = function (event) {
        secureReaction(event, botHomeId, reaction.id);
    };

    let secureIconSpan = document.createElement('span');
    secureIconSpan.id = label + '-secured';
    secureIconSpan.innerHTML = reaction.secure ? lockedIcon : unlockedIcon;
    secureCell.appendChild(secureIconSpan);

    let secureResponseSpan = document.createElement('span');
    secureResponseSpan.id = label + '-response';
    secureCell.appendChild(secureResponseSpan);

    let deleteCell = newRow.insertCell();
    deleteCell.classList.add('pseudo-link');
    deleteCell.innerHTML = trashcanIcon;
    deleteCell.onclick = function () {
        deleteReaction(botHomeId, reaction.id);
    };
}

function showAddRewardForm() {
    const label = 'add-reward';
    hideElementById(label + '-button');

    let prizeInputElement = document.getElementById(label + '-prize-input');
    prizeInputElement.value = '';
    prizeInputElement.style.minWidth = "5em";
    prizeInputElement.size = 30;
    showElementInlineById(label + '-form');
}

function addReward(botHomeId) {
    const prize = document.getElementById('add-reward-prize-input').value;
    postAddReward(botHomeId, prize);
}

async function postAddReward(botHomeId, prize) {
    const label = 'add-reward';
    const parameters = {botHomeId: botHomeId, prize: prize};
    let response = await makePost('/api/add_reward', parameters, [], false);

    if (response.ok) {
        hideElementById(label + '-form');
        showElementInlineById(label + '-button');

        let reward = await response.json();
        addRewardRow(botHomeId, reward);
    }
}

function addRewardRow(botHomeId, reward) {
    let rewardTable = document.getElementById('reward-table');
    let newRow = rewardTable.insertRow();

    const label = 'reward-' + reward.id;
    newRow.id = label + '-row';
    let prizeCell = newRow.insertCell();
    prizeCell.innerHTML = reward.prize;

    let statusCell = newRow.insertCell();
    statusCell.innerHTML = reward.status;

    let timeRemainingCell = newRow.insertCell();

    let entrantsCell = newRow.insertCell();
    entrantsCell.innerHTML = "0";

    let actionsCell = newRow.insertCell();
    actionsCell.innerHTML = "";

    let winnerCell = newRow.insertCell();
    if (reward.winner) {
        winnerCell.innerHTML = reward.winner;
    } else {
        winnerCell.innerHTML = '&#x2205';
    }

    let deleteCell = newRow.insertCell();
    deleteCell.classList.add('pseudo-link');
    deleteCell.innerHTML = trashcanIcon;
    deleteCell.onclick = function () {
        deleteReward(botHomeId, reward.id);
    };
}

function deleteReward(botHomeId, rewardId) {
    const parameters = {botHomeId: botHomeId, objectId: rewardId};
    postDelete('/api/delete_reward', parameters, 'reward-' + rewardId + '-row');
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

function bestowReward(botHomeId, rewardId) {
    postBestowReward(botHomeId, rewardId);
}

async function postBestowReward(botHomeId, rewardId) {
    const parameters = {botHomeId: botHomeId, objectId: rewardId};
    let response = await makePost('/api/bestow_reward', parameters, [], false);

    if (response.ok) {
        document.getElementById('reward-' + rewardId + '-status').innerText = 'BESTOWED';
    }
}

