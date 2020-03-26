function logout(formName) {
    document.getElementById(formName).submit();
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
        let json = await response.json();
        if (json.hasOwnProperty('message')) {
            showErrorMessage(json.message);
        }

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

async function makeGet(endpoint, parameters) {
    let url = new URL(endpoint, location.origin);
    url.search = new URLSearchParams(parameters).toString();
    const settings = getGetSettings();

    return await fetch(url, settings);
}

function hideElement(element) {
    element.style.display = 'none';
}

function hideElementById(elementId) {
    hideElement(document.getElementById(elementId));
}

function showElement(element) {
    element.style.display = 'block';
}

function showElementById(elementId) {
    showElement(document.getElementById(elementId));
}

function showElementInline(element) {
    element.style.display = 'inline-block';
}

function showElementInlineById(elementId) {
    showElementInline(document.getElementById(elementId));
}

function dismissErrorMessage() {
    hideElementById('error-banner');
}

function showErrorMessage(message) {
    document.getElementById('error-message').innerText = message;
    showElementInlineById('error-banner');
}

function dismissWarningMessage() {
    hideElementById('warning-banner');
}

function showWarningMessage(message) {
    document.getElementById('warning-message').innerText = message;
    showElementInlineById('warning-banner');
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

function getGetSettings() {
    let settings = {
        method: 'GET',
        headers: {
            Accept: 'application/json',
        },
    };
    return settings;
}
