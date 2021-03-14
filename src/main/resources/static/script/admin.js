const deleteIcon = '&#x1F5D1;';
const envelopeIcon = '&#x2709;&#xFE0F;';
const movieCameraIcon = '&#x1F3A5;';

function runAdminTask() {
    const responseElement = document.getElementById('run-task-response');
    postRunAdminTask(responseElement);
}

async function postRunAdminTask(responseElement) {
    await makePost('/admin/run', {}, [responseElement], true);
}

let selectedUsers = [];

function giveInvite(userId) {
    postGiveInvite(userId);
}

async function postGiveInvite(userId) {
    const parameters = {userId: userId};
    let response = await makePost('/admin/give_invite', parameters, [], false);
    if (response.ok) {
        let inviteElement = document.getElementById('user-' + userId + '-invite');
        inviteElement.innerHTML = envelopeIcon;
        inviteElement.onclick = null;
    }
}

async function makeEditor(userId) {
    const parameters = {userId: userId};
    let response = await makePost('/admin/make_editor', parameters, [], false);
    if (response.ok) {
        let inviteElement = document.getElementById('user-' + userId + '-make-editor');
        inviteElement.innerHTML = movieCameraIcon;
        inviteElement.onclick = null;
    }
}

function mergeUsers() {
    const responseElement = document.getElementById('merge-users-response');
    postMergeUsers(responseElement);
}

async function postMergeUsers(responseElement) {
    const parameters = {userIds: selectedUsers};
    let response = await makePost('/admin/merge_users', parameters, [responseElement], true);
    if (response.ok) {
        const mergedUser = await response.json();
        const mergedId = mergedUser.id;

        for (let i = 0; i < selectedUsers.length; i++) {
            if (selectedUsers[i] != mergedId) {
                let rowElement = document.getElementById('user-' + selectedUsers[i] + '-row');
                rowElement.parentElement.removeChild(rowElement);
            }
        }

        const label = 'user-' + mergedId;

        setElementValue(label + '-twitch-username', mergedUser.twitchUsername);
        setElementValue(label + '-twitch-id', mergedUser.twitchId);
        setElementValue(label + '-discord-username', mergedUser.discordUsername);
        setElementValue(label + '-discord-id', mergedUser.discordId);
        setElementValue(label + '-arena-username', mergedUser.arenaUsername);
        if (mergedUser.arenaUsername) {
            setElementValue(label + '-arena-username-delete-icon', deleteIcon);
        }
        setElementValue(label + '-admin', mergedUser.admin ? '&#x1F477;' : '');
    }
}

function setElementValue(elementId, value) {
    document.getElementById(elementId).innerHTML = value;
}

function selectUser(event, userId) {
    if (event.currentTarget.checked) {
        selectedUsers.push(userId);
    } else {
        let index = selectedUsers.indexOf(userId);
        selectedUsers.splice(index, 1);
    }
    let mergeUsersElement = document.getElementById('merge-users');
    if (selectedUsers.length > 1) {
        mergeUsersElement.style.display = 'block';
    } else {
        mergeUsersElement.style.display = 'none';
    }
}

function deleteArenaUsername(userId) {
    postDeleteArenaUsername(userId);

}

async function postDeleteArenaUsername(userId) {
    let response = await makePost('/admin/delete_arena_username',{userId: userId}, [],
        false);
    if (response.ok) {
        const label = 'user-' + userId;
        setElementValue(label + '-arena-username', '');
        setElementValue(label + '-arena-username-delete-icon', '');
    }
}

const sendMessageParameterData = [
    {id: 'user', label: 'User', name: 'receiverId', type: 'integer'},
    {id: 'service', label: 'Service', name: 'serviceType', type: 'integer'},
    {id: 'text', label: 'Message', name: 'message', type: 'string'},
];

async function sendMessage() {
    let parameters = {};
    for (let i = 0; i < sendMessageParameterData.length; i++) {
        addFormParameter('send-message', parameters, sendMessageParameterData[i]);
    }

    let response = await makePost('/admin/send_message', parameters, [],
        false);
    if (response.ok) {
    }
}

function addFormParameter(label, parameters, parameterData) {
    const name = parameterData.name;
    let inputElement = document.getElementById(label + '-' + parameterData.id + '-input');
    switch (parameterData.id) {
        case 'checkbox':
            parameters[name] = inputElement.checked;
            return;
        case 'long':
            parameters[name] = parseInt(inputElement.value);
            return;
        case 'time':
            const hmsSplit = inputElement.value.split(':');
            if (hmsSplit == '') {
                const message = 'Invalid ' + parameterData.label;
                showErrorMessage(message);
                throw message;
            }
            parameters[name] = (+hmsSplit[0] * 60 + +hmsSplit[1]) * 60 + (+hmsSplit[2] || 0);
            return;
        default:
            parameters[name] = inputElement.value;
            return;
    }
}

