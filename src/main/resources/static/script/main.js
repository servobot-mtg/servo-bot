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


function hideElementById(elementId) {
    document.getElementById(elementId).style.display = 'none';
}

function showElementById(elementId) {
    document.getElementById(elementId).style.display = 'block';
}

function showElementInlineById(elementId) {
    document.getElementById(elementId).style.display = 'inline-block';
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
