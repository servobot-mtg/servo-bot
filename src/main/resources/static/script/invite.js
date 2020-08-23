function submitInvite() {
    let botName = document.getElementById('bot-name').innerText;
    let timeZone = document.getElementById('time-zone-select').value;

    let useAddCommand = document.getElementById('use-add-command-name').checked;
    let addCommandName = useAddCommand ? document.getElementById('add-command-name-input').value : null;
    let useDeleteCommand = document.getElementById('use-delete-command').checked;
    let deleteCommandName =
        useDeleteCommand ? document.getElementById('delete-command-name-input').value : null;
    let useShowCommands = document.getElementById('use-show-commands').checked;
    let showCommandsName = useShowCommands ? document.getElementById('show-commands-name-input').value : null;

    let textCommands = [];

    var textCommandRows = document.getElementsByClassName('text-command-row');
    for (var i = 0; i < textCommandRows.length; i++) {
        let row = textCommandRows[i];
        let useTextCommand = row.getElementsByClassName('use-text-command').checked;
        if (useTextCommand) {
            let textCommandName = row.getElementsByClassName('text-commands-name-input')[0].value;
            let textCommandValue = row.getElementsByClassName('text-commands-value-input')[0].value;
            textCommands.push({name: textCommandName, value: textCommandValue});
        }
    }

    postSubmitInvite(botName, timeZone, addCommandName, deleteCommandName, showCommandsName, textCommands);
}

async function postSubmitInvite(botName, timeZone, addCommandName, deleteCommandName, showCommandsName, textCommands) {
    const parameters = {timeZone: timeZone, addCommandName: addCommandName, deleteCommandName: deleteCommandName,
        showCommandsName:showCommandsName, textCommands: textCommands, botName: botName};

    let response = await makePost('/api/create_bot_home', parameters, [], false);
    if (response.ok) {
        let botHome = await response.json();
        window.location.href = '/home/' + botHome.name;
    }
}
