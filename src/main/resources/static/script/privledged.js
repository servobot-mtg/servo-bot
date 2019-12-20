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

function secureCommand(event, botHomeId, commandId) {
    postSecureCommand(botHomeId, commandId, event.currentTarget.dataset.label);
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
    let valueElements = document.getElementsByClassName(label + '-secured');
    const secure = valueElements[0].innerText != decodedLockedIcon;
    const parameters = {botHomeId: botHomeId, objectId: objectId, secure: secure};
    const responseElements = document.getElementById(label + '-response');
    let response = await makePost(endPoint, parameters, responseElements, false);
    if (response.ok) {
        setSecure(document.getElementsByClassName(label + '-row'), valueElements, await response.json());
    }
}

function setSecure(rowElements, iconElements, secure) {
    if (secure) {
        Array.from(iconElements).forEach((iconElement) => iconElement.innerHTML = lockedIcon);
        Array.from(rowElements).forEach((rowElement) => rowElement.classList.add('secure'));
    } else {
        Array.from(iconElements).forEach((iconElement) => iconElement.innerHTML = unlockedIcon);
        Array.from(rowElements).forEach((rowElement) => rowElement.classList.remove('secure'));
    }
}

function updateCommandPermission(event, botHomeId, commandId) {
    postUpdateCommandPermission(botHomeId, commandId, event.target.value,
        document.getElementById(event.target.dataset.label));
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

    let displayDiv = document.createElement("div");
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

    let editDiv = document.createElement("div");
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

