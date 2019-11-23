function logout(formName) {
    document.getElementById(formName).submit();
}

function updateTimeZone(event, botHomeId, resultElementId) {
    var element = event.target;
    var dataset = element.dataset;
    postTimeZone(botHomeId, element.value, dataset.header, dataset.token, document.getElementById(resultElementId));
}

async function postTimeZone(botHomeId, timeZone, header, token, resultElement) {
    const settings = {
        method: 'POST',
        headers: {
            Accept: 'application/json',
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ botHomeId: botHomeId, timeZone: timeZone})
    };
    settings.headers[header] = token;
    var response = await fetch('/api/set_home_time_zone', settings);
    resultElement.innerHTML = 'Saved';
    resultElement.style.color = 'red';
    window.setTimeout(function () {
        resultElement.innerHTML = '';
    }, 1500);
}
