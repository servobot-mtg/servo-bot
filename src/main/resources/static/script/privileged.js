function decodeHtmlEntity(html) {
    let txt = document.createElement('textarea');
    txt.innerHTML = html;
    return txt.value;
}

const lockedIcon = '&#x1F512;';
const unlockedIcon = '&#x1F511;';
const decodedLockedIcon = decodeHtmlEntity(lockedIcon);
const tvIcon = '&#x1F4FA;';
const decodedTvIcon = decodeHtmlEntity(tvIcon);
const clockIcon = '&#x1F570;&#xFE0F;';
const checkmarkIcon = '&#x2714;&#xFE0F;';
const crossIcon = '&#x274C;';
const yellowCircleIcon = '&#x1F7E1;';
const trashcanIcon = '&#x1F5D1;&#xFE0F;';
const penIcon = '&#x270F;&#xFE0F;';
const bookIcon = '&#x1F4BE;';
const emptySetIcon = '&#x2205';
const bellIcon = '&#x1F6CE;&#xFE0F;';
const defaultCommandFlags = 2+4;

function getInputId(label, inputName) {
    return label + '-' + inputName + '-input';
}

function getInputDiv(label, inputName) {
    return label + '-' + inputName + '-div';
}

const addTriggerFormData = {
    focus: 'text',
    getInputBlock: getInputId,
    inputs: [{name: 'text', type: 'value', value: '', hide: false},
        {name: 'type', type: 'value', value: '1', hide: false},
        {name: 'event', type: 'value', value: 'STREAM_START', hide: true},
    ],
};

const addStatementFormData = {
    focus: 'text',
    getInputBlock: getInputDiv,
    inputs: [{name: 'text', type: 'text', value: '', hide: false}],
};

const addCommandFormData = {
    focus: 'text',
    getInputBlock: getInputDiv,
    inputs: [{name: 'type', type: 'select', value: 0, hide: false},
             {name: 'permissions', type: 'select', value: 4, hide: false},
             {name: 'secure', type: 'checkbox', value: false, hide: false},
             {name: 'text', type: 'value', value: '', hide: false},
             {name: 'text-2', type: 'value', value: '', hide: true},
             {name: 'integer', type: 'value', value: 0, hide: true},
             {name: 'book', type: 'select', value: 0, hide: true},
             {name: 'emote', type: 'select', value: 0, hide: true},
             {name: 'role', type: 'select', value: 0, hide: true},
             {name: 'game-queue', type: 'select', value: 0, hide: true},
             {name: 'service', type: 'select', value: 0, hide: true},
    ],
};

const addPatternFormData = {
    focus: 'text',
    getInputBlock: getInputId,
    inputs: [{name: 'text', type: 'value', value: '', hide: false}],
};

const addReactionFormData = {
    focus: 'emote',
    getInputBlock: getInputId,
    inputs: [{name: 'emote', type: 'select', value: 0, hide: false},
             {name: 'secure', type: 'checkbox', value: false, hide: false},
    ],
};

const addAlertFormData = {
    focus: 'type',
    getInputBlock: getInputDiv,
    inputs: [{name: 'type', type: 'select', value: 0, hide: false},
             {name: 'time', type: 'value', value: 60, hide: false},
             {name: 'keyword', type: 'value', value: '', hide: false},
    ],
};

const addBookFormData = {
    focus: 'name',
    getInputBlock: getInputDiv,
    inputs: [{name: 'name', type: 'value', value: '', hide: false},
             {name: 'statement', type: 'value', value: '', hide: false},
    ],
};

function createElement(type, {id = null, classType = null, classList = null, title = null, value = null,
        clickFunction = null}) {
    let element = document.createElement(type);
    if (id) {
        element.id = id;
    }
    if (classType) {
        element.classList.add(classType);
    } else if(classList) {
        element.classList.add(...classList);
    }
    if (title != null) {
        element.title = title;
    }
    if (value != null) {
        element.innerHTML = value;
    }
    if (clickFunction) {
        element.onclick = clickFunction;
    }
    return element;
}

function createSpan(parameters) {
    return createElement('span', parameters);
}

function createDiv(parameters) {
    return createElement('div', parameters);
}

function createImg(parameters) {
    let img = createElement('img', parameters);
    if (parameters.hasOwnProperty('src')) {
        img.src = parameters.src;
    }
    return img;
}

function createInput({id = null, type, name = null, value = null, size = null}) {
    let input = document.createElement('input');
    if (id) {
        input.id = id;
    }
    input.type = type;
    if (name != null) {
        input.name = name;
    }
    if (value != null) {
        input.value = value;
    }
    if (size != null) {
        input.size = size;
    }
    return input;
}

function createOption(text, value, selected) {
    let option = document.createElement('option');
    option.text = text;
    option.value = value;
    if (selected) {
        option.selected = true;
    }
    return option;
}

function createLabelTable(tableId, tableClass, text, deleteFunction) {
    let table = document.createElement('table');
    table.classList.add(tableClass + '-label', 'label', 'label-table');
    table.id = tableId;

    let row = table.insertRow();
    addTextCell(row, text);

    let deleteCell = addDeleteCell(row, 'x', deleteFunction);
    deleteCell.classList.add(tableClass + '-delete');
    return table;
}

function addEditableDiv(parentElement, label, value, editFunction, modifyFunction) {
    let displayDiv = createDiv({id: label + '-display'});

    let valueSpan = createSpan({id: label + '-value', value: value});
    displayDiv.appendChild(valueSpan);

    displayDiv.appendChild(createSpan({classType: 'pseudo-link', value: penIcon, clickFunction: editFunction}));

    let editDiv = createDiv({id: label + '-edit', classType: 'hidden'});

    let textInput = createInput({id: label + '-input', type: 'text', value: value, size: value.length});
    editDiv.appendChild(textInput);

    editDiv.appendChild(createSpan({classType: 'pseudo-link', value: bookIcon, clickFunction: modifyFunction}));

    parentElement.appendChild(displayDiv);
    parentElement.appendChild(editDiv);
}

function addTextCell(row, text) {
    let textCell = row.insertCell();
    textCell.innerHTML = text;
}

function addDeleteCell(row, text, deleteFunction) {
    let deleteCell = row.insertCell();
    deleteCell.classList.add('pseudo-link');
    deleteCell.innerHTML = text;
    deleteCell.onclick = deleteFunction;
    return deleteCell;
}

function editBotName() {
    hideElementById('bot-name-display');
    showElementById('bot-name-edit');
}

function modifyBotName(botHomeId) {
    let inputElement = document.getElementById('bot-name-input');
    let valueElement = document.getElementById('bot-name-value');

    if (valueElement.innerText != inputElement.value) {
        postModifyBotName(botHomeId, inputElement.value);
    } else {
        resetBotName();
    }
}

async function postModifyBotName(botHomeId, text) {
    const parameters = {botHomeId: botHomeId, text: text};
    let response = await makePost('/api/modify_bot_name', parameters, [], false);
    if (response.ok) {
        let valueElement = document.getElementById('bot-name-value');
        valueElement.innerText = text;
        resetBotName();
    }
}

function resetBotName() {
    hideElementById('bot-name-edit');
    showElementById('bot-name-display');
}

function secureCommand(botHomeId, commandId) {
    postSecureCommand(botHomeId, commandId, 'command-' + commandId);
}

async function postSecureCommand(botHomeId, commandId, label) {
    return postSecureObject('/api/secure_command', botHomeId, commandId, label);
}

function secureReaction(botHomeId, reactionId) {
    postSecureReaction(botHomeId, reactionId);
}

async function postSecureReaction(botHomeId, reactionId) {
    return postSecureObject('/api/secure_reaction', botHomeId, reactionId, 'reaction-' + reactionId);
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

function setOnlyWhileStreaming(botHomeId, commandId) {
    postSetOnlyWhileStreaming(botHomeId, commandId);
}

async function postSetOnlyWhileStreaming(botHomeId, commandId) {
    const elementId = 'command-' + commandId + '-while-streaming';
    let valueElement = document.getElementById(elementId);
    const onlyWhileStreamingValue = valueElement.innerText != decodedTvIcon;
    const parameters = {botHomeId: botHomeId, commandId: commandId, onlyWhileStreaming: onlyWhileStreamingValue};
    let response = await makePost('/api/set_command_only_while_streaming', parameters, [], false);
    if (response.ok) {
        if (onlyWhileStreamingValue) {
            valueElement.innerHTML = tvIcon;
            valueElement.title = 'Allow only while streaming';
        } else {
            valueElement.innerHTML = clockIcon;
            valueElement.title = 'Allow anytime';
        }
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

function deletePattern(botHomeId, reactionId, patternId) {
    const parameters = {botHomeId: botHomeId, reactionId: reactionId, patternId: patternId};
    postDelete('/api/delete_pattern', parameters, 'pattern-' + patternId);
}

function deleteStatement(botHomeId, bookId, statementId) {
    const parameters = {botHomeId: botHomeId, bookId: bookId, statementId: statementId};
    postDelete('/api/delete_statement', parameters, 'statement-' + statementId + '-row');
}

function editStatement(statementId) {
    hideElementById('statement-' + statementId + '-display');
    showElementById('statement-' + statementId + '-edit');
}

function modifyStatement(botHomeId, bookId, statementId) {
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

function showForm(label, data) {
    hideElementById(label + '-button');

    for (let i = 0; i < data.inputs.length; i++) {
        let input = data.inputs[i];
        let inputElement = document.getElementById(data.getInputBlock(label, input.name));
        if (input.hide)  {
            hideElement(inputElement);
        } else {
            showElementInline(inputElement);
        }
        switch (input.type) {
            case 'value':
                inputElement.value = input.value;
                break;
            case 'select':
                inputElement.selectedIndex = input.value;
                break;
            case 'checkbox':
                inputElement.checked = input.value;
                break;
        }
    }
    showElementInlineById(label + '-form');
    document.getElementById(getInputId(label, data.focus)).focus();
    document.getElementById(label + '-form').scrollIntoView(false);
}

function showAddTriggerForm(commandId) {
    const label = 'add-trigger-' + commandId;
    showForm(label, addTriggerFormData);
}

function updateAddTriggerType(commandId) {
    const label = 'add-trigger-' + commandId;
    let type = document.getElementById(label + '-type-input').value;
    if (type == 2) {
        showElementInlineById(label + '-event-input');
        hideElementById(label + '-text-input');
    } else {
        showElementInlineById(label + '-text-input');
        hideElementById(label + '-event-input');
    }
}

function addTrigger(botHomeId, commandId) {
    const label = 'add-trigger-' + commandId;
    let text = document.getElementById(label + '-text-input').value;
    const triggerType = parseInt(document.getElementById(label + '-type-input').value);
    if (triggerType == 2) {
        text = document.getElementById(label + '-event-input').value;
    }
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
    const triggerTypeArray = ['', 'alias', 'event', 'alert'];
    let triggerType = triggerTypeArray[trigger.type];

    let triggersSpan = document.getElementById(triggerType + '-triggers-' + commandId);
    let triggerTable = createLabelTable('trigger-' + trigger.id, triggerType, text, function () {
        deleteTrigger(botHomeId, trigger.id);
    });
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
    showForm('add-statement', addStatementFormData);
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
    let row = statementTable.insertRow();

    const label = 'statement-' + statement.id;
    row.id = label + '-row';

    addEditableDiv(row.insertCell(), label, statement.text, function() {
        editStatement(statement.id);
    }, function() {
        modifyStatement(botHomeId, bookId, statement.id);
    });

    addDeleteCell(row, trashcanIcon, function () {
        deleteStatement(botHomeId, bookId, statement.id);
    });
}

function showAddCommandForm() {
    showForm('add-command', addCommandFormData);
    changeAddCommandType(document.getElementById('add-command-type-input'));
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
        case 'emote':
        case 'role':
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
    {name: 'Set Role Command', parameters: [{id: 'role', name: 'Role Name'}]}, //13
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
    {name: 'Add Reaction Command', parameters: [{id: 'emote', name: 'Emote'}]}, //24
    {name: 'Jail Command', parameters: [{id: 'role', name: 'Role Name'}, {id: 'text-2', name: 'Variable Name'},
            {id: 'integer', name: 'Strikes'}]}, //25
    {name: 'Jail Break Command', parameters: [{id: 'role', name: 'Prison Role Name'}]}, //26
    {name: 'Set User Role Command', parameters: [{id: 'role', name: 'Role Name'},
            {id: 'text-2', name: 'Response message'}]}, //27
    {}, //28, Prize Request Command
    {name: 'Jail Release Command', parameters: [{id: 'role', name: 'Prison Role Name'}]}, //29
    {name: 'Arrest Command', parameters: [{id: 'role', name: 'Prison Role Name'},
            {id: 'text-2', name: 'Response message'}]}, //30
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
    'add-command-book-div', 'add-command-role-div', 'add-command-emote-div', 'add-command-game-queue-div',
    'add-command-integer-div'];

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
    let row = commandTable.insertRow();

    const label = 'command-' + commandDescriptor.command.id;
    row.id = label + '-row';
    if (commandDescriptor.command.secure) {
        row.classList.add('secure');
    }

    addTextCell(row, commandDescriptor.command.id);
    addTextCell(row, commandDescriptor.type);
    addTextCell(row, commandDescriptor.description);

    let triggersCell = row.insertCell();
    triggersCell.appendChild(createSpan({id: 'alias-triggers-' + commandDescriptor.command.id}));
    triggersCell.appendChild(createSpan({id: 'event-triggers-' + commandDescriptor.command.id}));
    triggersCell.appendChild(createSpan({id: 'alert-triggers-' + commandDescriptor.command.id}));

    let addTriggerLabel = 'add-trigger-' + commandDescriptor.command.id;
    let addTriggerSpan = createSpan({classType: 'add-trigger'});

    let addTriggerButtonDiv = createDiv({id: addTriggerLabel + '-button',
        classList: ['pseudo-link', 'add-button'], title: 'Add a trigger', value: '+', clickFunction: function () {
            showAddTriggerForm(commandDescriptor.command.id);
        }});
    addTriggerSpan.appendChild(addTriggerButtonDiv);

    let addTriggerForm = document.createElement('form');
    addTriggerForm.id = addTriggerLabel + '-form';
    addTriggerForm.classList.add('add-trigger-form', 'hidden');
    addTriggerForm.onsubmit = function () {
        addTrigger(botHomeId, commandDescriptor.command.id);
        return false;
    };

    addTriggerForm.appendChild(
        createInput({id: addTriggerLabel + '-text-input', type: 'text', name: 'trigger', size: 9}));

    let addTriggerEventSelect = document.createElement('select');
    addTriggerEventSelect.classList.add('hidden');
    addTriggerEventSelect.id = addTriggerLabel + '-event-input';

    addTriggerEventSelect.add(createOption('STREAM_START', 'STREAM_START', true));
    addTriggerEventSelect.add(createOption('SUBSCRIBE', 'SUBSCRIBE', false));
    addTriggerEventSelect.add(createOption('RAID', 'RAID', false));
    addTriggerEventSelect.add(createOption('NEW_USER', 'NEW_USER', false));
    addTriggerForm.appendChild(addTriggerEventSelect);

    let addTriggerTypeSelect = document.createElement('select');
    addTriggerTypeSelect.id = addTriggerLabel + '-type-input';

    addTriggerTypeSelect.add(createOption('Message', 1, true));
    addTriggerTypeSelect.add(createOption('Event', 2, false));
    addTriggerTypeSelect.add(createOption('Alert', 3, false));
    addTriggerTypeSelect.onchange = function () {
        updateAddTriggerType(commandDescriptor.command.id);
    };

    addTriggerForm.appendChild(addTriggerTypeSelect);

    addTriggerForm.appendChild(createInput({type: 'submit', value: '+'}));
    addTriggerSpan.appendChild(addTriggerForm);
    triggersCell.appendChild(addTriggerSpan);

    let iconCell = row.insertCell();

    let secureIcon = commandDescriptor.command.secure ? lockedIcon : unlockedIcon;
    let secureIconSpan = createSpan({id: label + '-secured', classType: 'pseudo-link', value: secureIcon,
        clickFunction: function () {
            secureCommand(botHomeId, commandDescriptor.command.id);
        }});
    iconCell.appendChild(secureIconSpan);
    iconCell.appendChild(createSpan({id: label + '-secure-response'}));

    let onlyWhileStreamingIcon = commandDescriptor.command.onlyWhileStreaming ? tvIcon : clockIcon;
    let onlyWhileStreamingIconSpan = createSpan({id: label + '-while-streaming', classType: 'pseudo-link',
        value: onlyWhileStreamingIcon, clickFunction: function () {
            setOnlyWhileStreaming(botHomeId, commandDescriptor.command.id);
        }});
    iconCell.appendChild(onlyWhileStreamingIconSpan);

    let twitchIconSpan = createSpan({id: label + '-twitch', classType: 'pseudo-link',
        clickFunction: function () {
            toggleCommandTwitch(botHomeId, commandDescriptor.command.id);
        }});
    let twitchImgSrc = commandDescriptor.command.twitch ? '/images/twitch.ico' : '/images/no-twitch.ico';
    let twitchImg = createImg({id: label + '-twitch-img', classType: 'icon', title: 'Toggle Twitch use',
        src: twitchImgSrc});
    twitchIconSpan.appendChild(twitchImg);
    iconCell.appendChild(twitchIconSpan);
    iconCell.appendChild(createSpan({id: label + '-twitch-response'}));

    let discordIconSpan = createSpan({id: label + '-discord', classType: 'pseudo-link',
        clickFunction: function () {
            toggleCommandDiscord(botHomeId, commandDescriptor.command.id);
        }});
    let discordImgSrc = commandDescriptor.command.discord ? '/images/discord.ico' : '/images/no-discord.ico';
    let discordImg = createImg({id: label + '-discord-img', classType: 'icon', title: 'Toggle Discord use',
        src: discordImgSrc});
    discordIconSpan.appendChild(discordImg);
    iconCell.appendChild(discordIconSpan);
    iconCell.appendChild(createSpan({id: label + '-discord-response'}));

    let permissionsCell = row.insertCell();
    let permissionsSelect = document.createElement('select');
    permissionsSelect.onchange = function (event) {
        updateCommandPermission(event, botHomeId, commandDescriptor.command.id);
    };
    for (let i = 0; i < permissions.length; i++) {
        permissionsSelect.add(
            createOption(permissions[i], permissions[i], permissions[i] == commandDescriptor.command.permission));
    }
    permissionsCell.appendChild(permissionsSelect);
    permissionsCell.appendChild(createSpan({id: label + '-permission-updated'}));

    addDeleteCell(row, trashcanIcon, function () {
        deleteCommand(botHomeId, commandDescriptor.command.id);
    });
}

function showAddPatternForm(reactionId) {
    showForm('add-pattern-' + reactionId, addPatternFormData);
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
    let patternsSpan = document.getElementById('patterns-' + reactionId);
    let patternTable = createLabelTable('pattern-' + pattern.id, 'pattern', pattern.patternString,
        function () {
            deletePattern(botHomeId, reactionId, pattern.id);
        });
    patternsSpan.appendChild(patternTable);
}

function showAddReactionForm() {
    showForm('add-reaction', addReactionFormData);
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
    let row = reactionTable.insertRow();

    const label = 'reaction-' + reaction.id;
    row.id = label + '-row';

    addTextCell(row, reaction.emoteName);

    let patternsCell = row.insertCell();
    patternsCell.appendChild(createSpan({id: 'patterns-' + reaction.id}));

    let addPatternLabel = 'add-pattern-' + reaction.id;
    let addPatternSpan = createSpan({classType: 'add-pattern'});

    let addPatternButtonDiv = createDiv({id: addPatternLabel + '-button', title: 'Add a pattern',
        classList: ['pseudo-link', 'add-button'], value: '+', clickFunction: function () {
            showAddPatternForm(reaction.id);
        }});
    addPatternSpan.appendChild(addPatternButtonDiv);

    let addPatternForm = document.createElement('form');
    addPatternForm.id = addPatternLabel + '-form';
    addPatternForm.classList.add('add-pattern-form', 'hidden');
    addPatternForm.onsubmit = function () {
        addPattern(botHomeId, reaction.id);
        return false;
    };

    addPatternForm.appendChild(createInput({id: addPatternLabel + '-text-input', type: 'text', name: 'pattern'}));
    addPatternForm.appendChild(createInput({type: 'submit', value: '+'}));

    addPatternSpan.appendChild(addPatternForm);
    patternsCell.appendChild(addPatternSpan);

    let secureIconCell = row.insertCell();
    let secureIcon = reaction.secure ? lockedIcon : unlockedIcon;
    let secureIconSpan = createSpan({id: label + '-secured', classType: 'pseudo-link', value: secureIcon,
        clickFunction: function () {
            secureReaction(botHomeId, reaction.id);
        }});
    secureIconCell.appendChild(secureIconSpan);
    secureIconCell.appendChild(createSpan({id: label + '-secure-response'}));

    addDeleteCell(row, trashcanIcon, function () {
        deleteReaction(botHomeId, reaction.id);
    });
}

function showAddAlertForm() {
    showForm('add-alert', addAlertFormData);
}

function addAlert(botHomeId) {
    const type = document.getElementById('add-alert-type-input').value;
    const time = document.getElementById('add-alert-time-input').value;
    const keyword = document.getElementById('add-alert-keyword-input').value;
    postAddAlert(botHomeId, type, keyword, time);
}

async function postAddAlert(botHomeId, type, keyword, time) {
    const label = 'add-alert';
    const parameters = {botHomeId: botHomeId, type: type, keyword: keyword, time: time};
    let response = await makePost('/api/add_alert', parameters, [], false);

    if (response.ok) {
        hideElementById(label + '-form');
        showElementInlineById(label + '-button');

        let alert = await response.json();
        addAlertRow(alert, botHomeId);
    }
}

function addAlertRow(alert, botHomeId) {
    let alertTable = document.getElementById('alert-table');
    let row = alertTable.insertRow();

    const label = 'alert-' + alert.id;
    row.id = label + '-row';

    addTextCell(row, alert.alertToken);
    addTextCell(row, alert.description);

    let triggerCell = row.insertCell();
    triggerCell.classList.add('pseudo-link');
    triggerCell.setAttribute('data-alert-token', alert.alertToken);
    triggerCell.onclick = function () {
        triggerAlert(botHomeId, alert.id);
    };
    triggerCell.innerHTML = bellIcon;

    addDeleteCell(row, trashcanIcon, function () {
        deleteAlert(botHomeId, alert.id);
    });
}

function deleteAlert(botHomeId, alertId) {
    const parameters = {botHomeId: botHomeId, objectId: alertId};
    postDelete('/api/delete_alert', parameters, 'alert-' + alertId + '-row');
}

function showAddBookForm() {
    showForm('add-book', addBookFormData);
}

function addBook(botHomeId) {
    const name = document.getElementById('add-book-name-input').value;
    const statement = document.getElementById('add-book-statement-input').value;
    postAddBook(botHomeId, name, statement);
}

async function postAddBook(botHomeId, name, statement) {
    const label = 'add-book';
    const parameters = {botHomeId: botHomeId, name: name, statement: statement};
    let response = await makePost('/api/add_book', parameters, [], false);

    if (response.ok) {
        hideElementById(label + '-form');
        showElementInlineById(label + '-button');

        let book = await response.json();
        addBookItem(book, botHomeId);
    }
}

function addBookItem(book, botHomeId) {
    let bookList = document.getElementById('book-list');
    let listItem = document.createElement('li');
    let link = document.createElement('a');
    link.href = 'book/' + book.name;
    link.innerHTML = book.name;
    listItem.appendChild(link);
    bookList.appendChild(listItem);
}
