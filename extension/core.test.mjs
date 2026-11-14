import assert from 'node:assert/strict';
import {readFileSync} from 'node:fs';
import {test} from 'node:test';
import {searchSites, siteForUrl, verifiedSites, listedSites, destinationLabel} from './core.mjs';

const catalog = JSON.parse(readFileSync(new URL('./catalog.json', import.meta.url), 'utf8'));
test('dated response and a newer inconclusive check have distinct labels', () => {
  const site = {healthCheckedAt:'2026-10-14T12:00:00Z',latestHealthStatus:'SERVER_ERROR',latestHealthCheckedAt:'2026-10-15T12:00:00Z'};
  assert.match(destinationLabel(site), /Latest check 2026-10-15 was unavailable or inconclusive/);
  assert.match(destinationLabel(site), /Last recorded response 2026-10-14/);
  assert.match(destinationLabel({...site,latestHealthStatus:'RESPONDING'}), /dated observation/);
});
test('only verified HTTPS records appear', () => {
  assert.equal(verifiedSites(catalog).length, 9);
  assert.equal(verifiedSites({...catalog, websites:[{...catalog.websites[0], verificationStatus:'candidate'}]}).length, 0);
});
test('only exact official host is recognized', () => {
  assert.equal(siteForUrl(catalog, 'https://www.digilocker.gov.in/dashboard')?.id, 'digilocker');
  assert.equal(siteForUrl(catalog, 'https://www.digilocker.gov.in.evil.example/'), null);
  assert.equal(siteForUrl(catalog, 'http://www.digilocker.gov.in/'), null);
});
test('search does not claim unavailable services', () => {
  const curated = {...catalog, websites:verifiedSites(catalog)};
  assert.equal(searchSites(curated, 'file a complaint')[0]?.id, 'cpgrams');
  assert.equal(searchSites(curated, 'income certificate').length, 0);
  assert.equal(searchSites(catalog, 'not-a-real-portal-991299').length, 0);
});
test('directory data stays attributed', () => {
  const sites = listedSites(catalog);
  assert.equal(sites.length, catalog.websites.length);
  for (const site of sites.filter(s => s.verificationStatus === 'directory_listed')) {
    assert.ok(site.sourceEvidence.length);
  }
  const local = sites.find(s => s.verificationStatus === 'directory_listed');
  if (local) {
    const unsupported = {...catalog, websites:[{...local, sourceEvidence:[]}]};
    assert.equal(listedSites(unsupported).length, 0);
    assert.equal(listedSites({...catalog, websites:[{...local, publicationScope:'public'}]}).length, 0);
    assert.ok(searchSites(catalog, local.title).some(s => s.id === local.id));
  }
});
test('the panel summary matches only exact listed HTTPS hosts', () => {
  assert.equal(siteForUrl(catalog, 'https://scholarships.gov.in/home')?.id, 'national-scholarship-portal');
  assert.equal(siteForUrl(catalog, 'https://www.india.gov.in/login')?.id, 'india-portal');
  for (const url of ['https://www.india.gov.in.evil.example/', 'http://www.india.gov.in/', 'https://scholarships.gov.in.evil.example/home',
    'javascript:alert(1)', 'not a url']) assert.equal(siteForUrl(catalog, url), null, url);
});
