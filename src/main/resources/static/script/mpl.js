function showTab(tabId) {
    var tabs = document.getElementsByClassName('main-tab');
    for (var i = 0; i < tabs.length; i++) {
        var tab = tabs[i];
        if (tab.id == tabId) {
            showElement(tab);
        } else {
            hideElement(tab);
        }
    }
}

