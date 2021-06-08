function initTournament() {
    hideColumn('follow-column', 2);
    if (getCookie('follows') == null) {
        stylePlayerTable();
        return;
    }

    let storedFollows = getFollows();
    let playerTable = document.getElementById('player-table');
    for (let i = 1, row; row = playerTable.rows[i]; i++) {
        let follow = storedFollows[row.dataset.arenaName];
        row.getElementsByTagName('input').item(0).checked = follow;
    }

    stylePlayerTable();
}

function showRow(row) {
    row.style.display = 'table-row';
    row.style.visibility = 'visible';
}

function hideRow(row) {
    row.style.display = 'none';
    row.style.visibility = 'visible';
    //row.style.visibility = 'collapse';
}

function stylePlayerTable() {
    let lastVisible = 0;
    let playerTable = document.getElementById('player-table');

    for (let i = 1, row; row = playerTable.rows[i]; i++) {
        let leader = row.dataset.leader == 'true';
        let follow = row.getElementsByTagName('input').item(0).checked;
        if (leader || follow) {
            showRow(row);
        } else {
            hideRow(row);
        }

        if (lastVisible != 0 && parseInt(row.dataset.points) < parseInt(playerTable.rows[lastVisible].dataset.points)) {
            row.classList.add('pad-row-top');
        } else {
            row.classList.remove('pad-row-top');
        }

        if (leader || follow) {
            lastVisible = i;
        }

        if (follow) {
            row.classList.add('follow-row');
        } else {
            row.classList.remove('follow-row');
        }
    }
}

function printDebug(statement) {
    /*
    let debug = document.getElementById('debug');
    debug.appendChild(document.createTextNode(statement));
    debug.appendChild(document.createElement('br'));
     */
}

function hideColumn(columnId, columnIndex) {
    const browser = getBrowser();
    if (browser == 'chrome' || browser == 'safari') {
        let playerTable = document.getElementById('player-table');
        for (let i = 0, row; row = playerTable.rows[i]; i++) {
            row.cells[columnIndex].style.display = 'none';
        }
    } else {
        document.getElementById(columnId).style.visibility = 'collapse';
    }
}

function showColumn(columnId, columnIndex) {
    const browser = getBrowser();
    if (browser == 'chrome' || browser == 'safari') {
        let playerTable = document.getElementById('player-table');
        for (let i = 0, row; row = playerTable.rows[i]; i++) {
            row.cells[columnIndex].style.display = 'table-cell';
        }
    } else {
        document.getElementById(columnId).style.visibility = 'visible';
    }
}

function startSelecting() {
    let playerTable = document.getElementById('player-table');
    for (let i = 0, row; row = playerTable.rows[i]; i++) {
        showRow(row);

        if (i > 1 && parseInt(row.dataset.points) < parseInt(playerTable.rows[i - 1].dataset.points)) {
            row.classList.add('pad-row-top');
        } else {
            row.classList.remove('pad-row-top');
        }
    }

    showColumn('follow-column', 2);
    document.getElementById('select-players-button').style.display = 'none';
    document.getElementById('done-selecting-players-button').style.display = 'block';
}

function showSelected() {
    hideColumn('follow-column', 2);

    let storedFollows = getFollows();
    let playerTable = document.getElementById('player-table');
    for (let i = 1, row; row = playerTable.rows[i]; i++) {
        let follow = row.getElementsByTagName('input').item(0).checked;
        if (follow) {
            storedFollows[row.dataset.arenaName] = true;
        } else {
            storedFollows[row.dataset.arenaName] = false;
        }
    }
    stylePlayerTable();

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
    setCookie('follows', follows.join(','), 2);
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

let browser = null;

function getBrowser() {
    if (browser) {
        return browser;
    }

    browser = computeBrowser();
    printDebug('is ' + browser);

    return browser;
}

function computeBrowser() {
    let ua = navigator.userAgent.toLowerCase();
    if (ua.indexOf('safari') != -1) {
        if (ua.indexOf('chrome') > -1) {
            return 'chrome';
        } else {
            return 'safari';
        }
    }
    if (ua.indexOf('firefox') != -1) {
        return 'firefox';
    }
    return '?';
}

