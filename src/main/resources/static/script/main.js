function logout(formName) {
    document.getElementById(formName).submit();
}

function decodeHtmlEntity(html) {
	let txt = document.createElement('textarea');
	txt.innerHTML = html;
	return txt.value;
}

const lockedIcon = '&#x1F512;';
const unlockedIcon = '&#x1F511;';
const decodedLockedIcon = decodeHtmlEntity('&#x1F512;');

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
    let response = await makePost(endPoint,parameters, responseElements, false);
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

async function makePost(endpoint, parameters, responseElements, showOk) {
    const settings = getPostSettings(parameters);

    let response = await fetch(endpoint, settings);
    let set = false;
    if (response.ok) {
        if(showOk) {
            Array.from(responseElements).forEach((responseElement) => responseElement.innerHTML = '&#x2705;');
            set = true;
        }
    } else {
        Array.from(responseElements).forEach((responseElement) => responseElement.innerHTML = '&#x274C;');
        set = true;
    }
    if (set) {
        window.setTimeout(function () {
            Array.from(responseElements).forEach((responseElement) => responseElement.innerHTML = '');
        }, 1500);
    }
    return response;
}

function deleteCommand(event, botHomeId) {
    const commandName = event.currentTarget.dataset.alias;
    const performDelete = window.confirm('Are you sure you want to delete the ' + commandName + ' command?');
    if (performDelete) {
        postDeleteCommand(botHomeId, commandName);
    }
}

async function postDeleteCommand(botHomeId, commandName) {
    const parameters = {botHomeId: botHomeId, commandName: commandName};
    let response = await makePost('/api/delete_command', parameters, [], false);
    if (response.ok) {
        let rowElement = document.getElementById('command-' + commandName);
        rowElement.parentElement.removeChild(rowElement);
    }
}

function getPostSettings(parameters) {
    let settings = {
        method: 'POST',
        headers: {
            Accept: 'application/json',
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(parameters)
    };

    const securityElement = document.getElementById('security-token');
    const security = securityElement.dataset;

    settings.headers[security.header] = security.token;
    return settings;
}
