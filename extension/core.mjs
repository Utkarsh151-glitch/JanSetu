const stop = new Set(['i', 'a', 'an', 'the', 'to', 'for', 'need', 'find', 'show', 'apply', 'check', 'file', 'get', 'my', 'in']);
const synonyms = {govt:'government',naukri:'jobs',job:'jobs',complaints:'complaint',docs:'documents'};
const words = value => (value.toLocaleLowerCase('en').match(/[\p{L}\p{N}]+/gu) || []);

export function verifiedSites(catalog) {
  return (catalog.websites || []).filter(site => site.verificationStatus === 'verified' &&
    site.sourceUrl?.startsWith('https://') && site.url?.startsWith('https://'));
}

const secure = raw => {
  try { const u = new URL(raw); return u.protocol === 'https:' && !u.username && !u.password && (!u.port || u.port === '443'); }
  catch { return false; }
};

const listings = new WeakMap();
export function destinationLabel(site) {
  const responded = site.healthCheckedAt?.slice(0, 10) || 'unknown date';
  if (site.latestHealthStatus && site.latestHealthStatus !== 'RESPONDING') {
    return `Latest check ${site.latestHealthCheckedAt?.slice(0, 10) || 'unknown date'} was unavailable or inconclusive. Last recorded response ${responded}.`;
  }
  return `Destination responded ${responded}; this is a dated observation.`;
}

export function listedSites(catalog) {
  if (listings.has(catalog)) return listings.get(catalog);
  const sites = (catalog.websites || []).filter(site => {
    if (!secure(site.url) || !secure(site.sourceUrl)) return false;
    if (site.verificationStatus === 'verified') return true;
    return site.verificationStatus === 'directory_listed' && site.sourceId === 'igod' &&
      site.publicationScope === 'local_development' && site.healthStatus === 'RESPONDING' &&
      !!site.healthCheckedAt && /^\d{4}-\d{2}-\d{2}$/.test(site.sourceRetrievedOn || '') &&
      site.sourceEvidence?.length && site.sourceEvidence.every(e => e.sourceId === 'igod' &&
        secure(e.sourcePage) && new URL(e.sourcePage).hostname === 'igod.gov.in' && secure(e.originalUrl) && e.retrievedAt);
  });
  listings.set(catalog, sites);
  return sites;
}

const hosts = new WeakMap();
export function siteForUrl(catalog, rawUrl) {
  try {
    const current = new URL(rawUrl);
    if (current.protocol !== 'https:') return null;
    const host = current.hostname.toLowerCase().replace(/^www\./, '');
    if (!hosts.has(catalog)) {
      const matching = new Map();
      for (const site of listedSites(catalog)) {
        const key = new URL(site.url).hostname.toLowerCase().replace(/^www\./, '');
        if (!matching.has(key)) matching.set(key, site);
      }
      hosts.set(catalog, matching);
    }
    return hosts.get(catalog).get(host) || null;
  } catch { return null; }
}

const indexes = new WeakMap();
function index(catalog) {
  if (!indexes.has(catalog)) indexes.set(catalog, listedSites(catalog).map(site => ({site,
    fields: [[site.title,8],[(site.aliases || []).join(' '),7],[site.keywords.join(' '),6],
      [[site.state,site.district].filter(Boolean).join(' '),6],[site.description,3],[site.organization,2]]
      .map(([value,weight]) => [new Set(words(value).map(w => synonyms[w] || w)),weight])
  })));
  return indexes.get(catalog);
}

export function searchSites(catalog, query) {
  const tokens = [...new Set(words(query).map(token => synonyms[token] || token).filter(token => !stop.has(token)))];
  return index(catalog).map(({site,fields}) => {
    const scores = tokens.map(token => Math.max(...fields.map(([terms, weight]) =>
      terms.has(token) || (token.length >= 4 && [...terms].some(word => word.startsWith(token))) ? weight : 0)));
    return {site, score: scores.every(Boolean) ? scores.reduce((a,b) => a+b, 0) : 0};
  }).filter(item => !tokens.length || item.score).sort((a,b) => b.score-a.score || a.site.title.localeCompare(b.site.title)).map(item => item.site);
}
