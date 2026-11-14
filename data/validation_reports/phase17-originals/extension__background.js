chrome.sidePanel.setPanelBehavior({openPanelOnActionClick: true});

// Remove the opt-in stylesheet when an SPA route leaves the reviewed page.
// This only removes our packaged CSS; it never injects on an unreviewed route.
chrome.tabs.onUpdated.addListener(async (tabId, change) => {
  if (!change.url) return;
  let reviewed = false;
  try {
    const url = new URL(change.url);
    reviewed = url.protocol === 'https:' && url.hostname === 'scholarships.gov.in' &&
      url.pathname === '/home' && !url.search && !url.port && !url.username && !url.password;
  } catch {}
  if (!reviewed) {
    await chrome.scripting.removeCSS({target:{tabId},files:['readability.css'],origin:'USER'}).catch(() => {});
  }
});
