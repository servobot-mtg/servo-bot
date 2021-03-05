async function addChatDraft(botHomeId) {
    const parameters = {botHomeId: botHomeId};
    let response = await makePost('/api/add_chat_draft', parameters, [], false);

    if (response.ok) {
        location.reload();
    }
}


