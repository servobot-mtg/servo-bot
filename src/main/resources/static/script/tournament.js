function initTournament() {
    if (getCookie('follows') == null) {
        let lastVisible = 0;
        let playerTable = document.getElementById('player-table');

        for (let i = 1, row; row = playerTable.rows[i]; i++) {
            let leader = row.dataset.leader == 'true';
            let follow = row.getElementsByTagName('input').item(0).checked;

            if (lastVisible != 0 && row.dataset.points < playerTable.rows[lastVisible].dataset.points) {
                row.classList.add('pad-row-top');
            } else {
                row.classList.remove('pad-row-top');
            }

            if (leader || follow) {
                lastVisible = i;
            }
        }

        return;
    }

    let storedFollows = getFollows();
    let playerTable = document.getElementById('player-table');
    let lastVisible = 0;
    for (let i = 1, row; row = playerTable.rows[i]; i++) {
        let leader = row.dataset.leader == 'true';
        let follow = storedFollows[row.dataset.arenaName];
        row.getElementsByTagName('input').item(0).checked = follow;
        if (leader || follow) {
            row.style.visibility = 'visible';
        } else {
            row.style.visibility = 'collapse';
        }

        if (lastVisible != 0 && row.dataset.points < playerTable.rows[lastVisible].dataset.points) {
            row.classList.add('pad-row-top');
        } else {
            row.classList.remove('pad-row-top');
        }

        if (leader || follow) {
            lastVisible = i;
        }
    }
}

function startSelecting() {
    let playerTable = document.getElementById('player-table');
    for (let i = 0, row; row = playerTable.rows[i]; i++) {
        row.style.visibility = 'visible';

        if (i > 1 && row.dataset.points < playerTable.rows[i - 1].dataset.points) {
            row.classList.add('pad-row-top');
        } else {
            row.classList.remove('pad-row-top');
        }
    }

    document.getElementById('follow-column').style.visibility = 'visible';
    document.getElementById('select-players-button').style.display = 'none';
    document.getElementById('done-selecting-players-button').style.display = 'block';
}

function showSelected() {
    document.getElementById('follow-column').style.visibility = 'collapse';

    let storedFollows = getFollows();
    let playerTable = document.getElementById('player-table');
    let lastVisible = 0;
    for (let i = 1, row; row = playerTable.rows[i]; i++) {
        let leader = row.dataset.leader == 'true';
        let follow = row.getElementsByTagName('input').item(0).checked;
        if (follow) {
            storedFollows[row.dataset.arenaName] = true;
        } else {
            storedFollows[row.dataset.arenaName] = false;
            if (!leader) {
                row.style.visibility = 'collapse';
            }
        }

        if (lastVisible != 0 && row.dataset.points < playerTable.rows[lastVisible].dataset.points) {
            row.classList.add('pad-row-top');
        } else {
            row.classList.remove('pad-row-top');
        }

        if (leader || follow) {
            lastVisible = i;
        }
    }

    let follows = [];
    for (const [key, value] of Object.entries(storedFollows)) {
        if (value) {
            follows.push(key);
        }
    }
    setFollows(follows);

    document.getElementById('select-players-button').style.display = 'block';
    document.getElementById('done-selecting-players-button').style.display = 'none';
}

function getFollows() {
    let cookie = getCookie('follows');
    let follows = {};
    if (cookie == null) {
        return follows;
    }

    let splits = cookie.split(',');
    for (let i = 0; i < splits.length; i++) {
        follows[splits[i]] = true;
    }
    return follows;
}

function setFollows(follows) {
    setCookie('follows', follows.join(','), 365);
}

function getCookie(cname) {
    let name = cname + '=';
    let decodedCookie = decodeURIComponent(document.cookie);
    let ca = decodedCookie.split(';');
    for(let i = 0; i <ca.length; i++) {
        let c = ca[i];
        while (c.charAt(0) == ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) == 0) {
            return c.substring(name.length, c.length);
        }
    }
    return null;
}

function setCookie(cname, cvalue, exdays) {
    let d = new Date();
    d.setTime(d.getTime() + (exdays*24*60*60*1000));
    let expires = 'expires=' + d.toUTCString();
    document.cookie = cname + '=' + cvalue + ';' + expires + ';path=/;SameSite=Strict';
}
