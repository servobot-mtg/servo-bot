function runAdminTask() {
    const responseElement = document.getElementById('run-task-response');
    postRunAdminTask(responseElement);
}

async function postRunAdminTask(responseElement) {
    await makePost('/admin/run', {}, [responseElement], true);
}

let selectedUsers = [];

function mergeUsers() {
    const responseElement = document.getElementById('merge-users-response');
    postMergeUsers(responseElement);
}

const deleteIcon = '&#x1F5D1;';

async function postMergeUsers(responseElement) {
    const parameters = { userIds: selectedUsers};
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

