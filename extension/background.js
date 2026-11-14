// Opens the JanSetu side panel when the toolbar icon is selected. The extension has no other background behaviour:
// it never injects CSS or scripts and does not watch tab navigation.
chrome.sidePanel.setPanelBehavior({openPanelOnActionClick: true});
