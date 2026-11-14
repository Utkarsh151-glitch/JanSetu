import {searchSites, siteForUrl, destinationLabel} from './core.mjs';

const current = document.querySelector('#current');
const input = document.querySelector('#search');
const results = document.querySelector('#results');
const count = document.querySelector('#count');

function text(tag, value, className) {
  const node = document.createElement(tag);
  node.textContent = value;
  if (className) node.className = className;
  return node;
}

function link(label, url) {
  const anchor = text('a', label);
  anchor.href = url;
  anchor.target = '_blank';
  anchor.rel = 'noopener noreferrer';
  return anchor;
}

function renderCard(site) {
  const card = document.createElement('article');
  card.className = 'card';
  const listed = site.verificationStatus === 'directory_listed';
  card.append(text('div', listed ? 'LISTED BY IGOD' : 'VERIFIED SOURCE', 'eyebrow'), text('h2', site.title),
    text('p', site.description), text('p', new URL(site.url).hostname, 'domain muted'),
    link('Open official portal ↗', site.url), text('p', `Source checked ${listed ? site.sourceRetrievedOn : site.verifiedOn}`, 'date'),
    link(listed ? 'IGOD listing ↗' : 'View source ↗', site.sourceUrl));
  if (listed) card.append(text('p', `${destinationLabel(site)} Ownership and services have not been manually reviewed.`, 'muted'));
  return card;
}

async function init() {
  try {
    const response = await fetch(chrome.runtime.getURL('catalog.json'));
    if (!response.ok) throw new Error('Catalog unavailable');
    const catalog = await response.json();
    const [tab] = await chrome.tabs.query({active: true, currentWindow: true}).catch(() => []);
    const matching = siteForUrl(catalog, tab?.url);
    current.replaceChildren();
    if (matching) {
      current.append(text('div', 'ON THIS WEBSITE', 'eyebrow'), text('h2', matching.title),
        text('p', matching.description), text('p', `${matching.verificationStatus === 'directory_listed' ? 'Listed by IGOD ' + matching.sourceRetrievedOn : 'Verified ' + matching.verifiedOn} · ${new URL(matching.url).hostname}`, 'muted'),
        link('View source ↗', matching.sourceUrl));
      const guide = (catalog.guides || []).find(item => item.websiteId === matching.id);
      if (guide) {
        current.append(text('h3', guide.title));
        const steps = document.createElement('ol');
        for (const step of guide.steps) steps.append(text('li', step));
        current.append(steps, text('p', guide.note, 'muted'),
          text('p', `Guide reviewed ${guide.reviewedOn}`, 'date'), link('Guide source ↗', guide.sourceUrl));
      } else {
        current.append(text('p', 'No verified step-by-step guide is available for this portal yet.', 'muted'));
      }
    } else {
      current.append(text('div', 'COMPANION PANEL', 'eyebrow'),
        text('p', 'Open this panel on a listed official portal to see its JanSetu summary. You can also search below.'));
    }
    const render = () => {
      const sites = searchSites(catalog, input.value);
      const listed = sites.filter(s => s.verificationStatus === 'directory_listed').length;
      count.textContent = `${sites.length} portal${sites.length === 1 ? '' : 's'} · ${listed} directory listed`;
      results.replaceChildren();
      if (!sites.length) {
        results.append(text('p', 'No matching portal. Try another term; coverage is incomplete.', 'empty'));
        return;
      }
      let shown = 0;
      const more = text('button', 'Show more portals');
      more.type = 'button';
      const append = () => {
        more.remove();
        results.append(...sites.slice(shown, shown + 30).map(renderCard));
        shown += 30;
        if (shown < sites.length) results.append(more);
      };
      more.addEventListener('click', append);
      append();
    };
    input.addEventListener('input', render);
    render();
  } catch {
    current.replaceChildren(text('p', 'The local catalog could not be loaded. Reload the extension to try again.'));
    count.textContent = '';
  }
}

init();
