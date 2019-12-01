function runAdminTask() {
    const responseElement = document.getElementById('run-task-response')
    postRunAdminTask(responseElement);
}

async function postRunAdminTask(responseElement) {
    await makePost('/admin/run', {}, [responseElement], true);
}
