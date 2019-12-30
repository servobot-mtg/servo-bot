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
    const responseElement = document.getElementById(label + '-response');
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

function deleteAlias(botHomeId, aliasId) {
    const parameters = {botHomeId: botHomeId, objectId: aliasId};
    postDelete('/api/delete_alias', parameters, 'alias-' + aliasId);
}

function deleteEvent(botHomeId, eventId) {
    const parameters = {botHomeId: botHomeId, objectId: eventId};
    postDelete('/api/delete_event', parameters, 'event-' + eventId);
}

function deleteAlert(botHomeId, alertId) {
    const parameters = {botHomeId: botHomeId, objectId: alertId};
    postDelete('/api/delete_alert', parameters, 'alert-' + alertId);
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
    document.getElementById('status').innerHTML = yellowCircleIcon;
    let response = await makePost('/api/start_home', parameters, [], false);
    if (response.ok) {
        document.getElementById('stop-button').style.display = 'inline-block';
        document.getElementById('status').innerHTML = checkmarkIcon;
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
        document.getElementById('status').innerHTML = crossIcon;
    }
}

function showAddTriggerForm(commandId) {
    const label = 'add-trigger-' + commandId;
    hideElementById(label + '-button');

    document.getElementById(label + '-text-input').value = '';
    showElementInlineById(label + '-form');
}

function addTrigger(botHomeId, commandId) {
    const text = document.getElementById('add-trigger-' + commandId + '-text-input').value;
    postAddTrigger(botHomeId, commandId, text);
}

async function postAddTrigger(botHomeId, commandId, text) {
    const label = 'add-trigger-' + commandId;
    const parameters = {botHomeId: botHomeId, commandId: commandId, text: text};
    let response = await makePost('/api/add_trigger', parameters, [], false);

    if (response.ok) {
        hideElementById(label + '-form');
        showElementInlineById(label + '-button');

        let addTriggerResponse = await response.json();
        addTriggerTable(addTriggerResponse.addedTrigger, botHomeId, commandId);
    }
}

function addTriggerTable(trigger, botHomeId, commandId) {
    const label = 'alias-triggers-' + commandId;
    let aliasTriggersSpan = document.getElementById(label);

    let triggerTable = document.createElement('table');
    triggerTable.classList.add('alias-label', 'label', 'label-table');
    triggerTable.id = 'alias-' + trigger.id;
    let row = triggerTable.insertRow();
    let aliasCell = row.insertCell();
    aliasCell.innerHTML = trigger.alias;

    let deleteCell = row.insertCell();
    deleteCell.classList.add('pseudo-link', 'alias-delete');
    deleteCell.innerHTML = 'x';
    deleteCell.onclick = function () {
        deleteAlias(botHomeId, trigger.id);
    };

    aliasTriggersSpan.appendChild(triggerTable);
}

function showAddStatementForm() {
    hideElementById('add-statement-button');

    document.getElementById('add-statement-text-input').value = '';
    showElementInlineById('add-statement-form');
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
    document.getElementById('add-command-permissions-input').selectedIndex = 0;
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

function addCommand(botHomeId) {
    const parameters = {botHomeId: botHomeId};
    const commandType = parseInt(document.getElementById('add-command-type-input').value);
    addAddCommandParameter(parameters, 'type', 'type');
    addAddCommandParameter(parameters, 'permissions', 'permission');
    addAddCommandParameter(parameters, 'secure', 'secure');

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
    let addTriggerSpan = document.createElement('span');

    let addTriggerButtonDiv = document.createElement('div');
    addTriggerButtonDiv.id = label + '-button';
    addTriggerButtonDiv.classList.add('pseudo-link', 'add-button');
    addTriggerButtonDiv.title = 'Add a trigger';
    addTriggerButtonDiv.onclick = function () {
        showAddTriggerForm(commandDescriptor.command.id);
    };
    addTriggerButtonDiv.innerHTML = '+';
    addTriggerSpan.appendChild(addTriggerButtonDiv);
    triggersCell.appendChild(addTriggerSpan);

    let addTriggerForm = document.createElement('form');
    addTriggerForm.id = label + '-form';
    addTriggerForm.classList.add('add-trigger-form', 'hidden');
    addTriggerForm.onsubmit = function () {
        addTrigger(botHomeId, commandDescriptor.command.id);
        return false;
    };

    let addTriggerTextInput = document.createElement('input');
    addTriggerTextInput.id = label + '-text-input';
    addTriggerTextInput.type = 'text';
    addTriggerTextInput.name = 'trigger';
    addTriggerTextInput.size = 5;
    addTriggerForm.appendChild(addTriggerTextInput);

    let addTriggerSubmitInput = document.createElement('input');
    addTriggerSubmitInput.type = 'submit';
    addTriggerSubmitInput.value = '+';
    addTriggerForm.appendChild(addTriggerSubmitInput);
    triggersCell.appendChild(addTriggerForm);

    let secureCell = newRow.insertCell();
    secureCell.classList.add('pseudo-link');
    secureCell.onclick = function () {
        secureCommand(botHomeId, commandDescriptor.command.id);
    };

    let secureIconSpan = document.createElement('span');
    secureIconSpan.id = label + '-secured';
    secureIconSpan.innerHTML = commandDescriptor.command.secure ? lockedIcon : unlockedIcon;
    secureCell.appendChild(secureIconSpan);

    let secureResponseSpan = document.createElement('span');
    secureResponseSpan.id = label + '-response';
    secureCell.appendChild(secureResponseSpan);

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
