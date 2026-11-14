// The extension's page-assistance boundary after Phase 17: the withdrawn NSP "Readable text" pilot is removed
// completely, so the extension has no way to change any website. Static checks and mocked Chrome APIs only;
// no live page is opened. Run with core.test.mjs in the gate.
import assert from 'node:assert/strict';
import {readdirSync, readFileSync} from 'node:fs';
import {test} from 'node:test';
import vm from 'node:vm';
import * as core from './core.mjs';

const read = name => readFileSync(new URL(`./${name}`, import.meta.url), 'utf8');
const manifest = JSON.parse(read('manifest.json'));
const registry = JSON.parse(readFileSync(new URL('../data/site_interventions.json', import.meta.url), 'utf8'));

test('permissions are only the side panel and the user-invoked active-tab check', () => {
  assert.deepEqual(manifest.permissions, ['sidePanel', 'activeTab']);
  assert.equal(manifest.host_permissions, undefined, 'No host permissions');
  assert.equal(manifest.optional_permissions, undefined, 'No optional permissions');
  assert.equal(manifest.optional_host_permissions, undefined);
  assert.equal(manifest.content_scripts, undefined, 'No automatic content scripts');
  assert.equal(manifest.web_accessible_resources, undefined);
  assert.equal(manifest.externally_connectable, undefined, 'Nothing outside the extension can message it');
  assert.doesNotMatch(manifest.description, /readab|appearance|style/i);
});

test('no stylesheet or script can be injected into a website', () => {
  const files = readdirSync(new URL('./', import.meta.url));
  assert.deepEqual(files.filter(name => name.endsWith('.css')), ['panel.css'], 'Only the panel’s own stylesheet is packaged');
  for (const name of files.filter(name => /\.m?js$/.test(name) && !name.endsWith('.test.mjs')).concat('panel.html')) {
    const source = read(name);
    assert.doesNotMatch(source, /chrome\.scripting|insertCSS|removeCSS|executeScript|registerContentScripts|readab/i, name);
  }
  assert.equal('readableSiteForUrl' in core, false, 'The styling lookup is gone, not just empty');
  // The privacy page may mention the withdrawal, but must not describe a style the extension can still apply or remove.
  assert.doesNotMatch(read('privacy.html'), /extension removes it|you may choose Readable text|Restore original/);
});

test('the background worker only opens the side panel and watches no navigation', () => {
  const calls = [];
  const listeners = [];
  vm.runInNewContext(read('background.js'), {
    chrome: {
      sidePanel: {setPanelBehavior(options) { calls.push(options); }},
      tabs: {onUpdated: {addListener(fn) { listeners.push(fn); }}},
      runtime: {onMessage: {addListener(fn) { listeners.push(fn); }}, onMessageExternal: {addListener(fn) { listeners.push(fn); }}}
    }
  });
  assert.equal(JSON.stringify(calls), JSON.stringify([{openPanelOnActionClick: true}]));
  assert.equal(listeners.length, 0, 'No tab, message or external listener remains');
});

test('the registry has no live extension styling and keeps the retired pilot only as archived evidence', () => {
  const styling = ['readable_text_css', 'overflow_scroll_css', 'reading_view'];
  const live = registry.pages.filter(page => page.controls.some(c => c.context === 'chrome_extension' &&
    styling.includes(c.control) && ['active', 'recommended_withdrawal'].includes(c.state)));
  assert.deepEqual(live, []);
  const nsp = registry.pages.find(page => page.pageId === 'scholarship-home');
  assert.equal(nsp.status, 'retired');
  for (const control of nsp.controls.filter(c => c.stylesheet)) {
    assert.equal(control.state, 'retired');
    assert.ok(!control.stylesheet.startsWith('extension/'), 'The retired stylesheet is not packaged');
  }
});

test('sensitive (E) and repair (F) pages carry no presentation-altering control', () => {
  const altering = new Set(Object.entries(registry.controls).filter(([, v]) => v.altersPresentation).map(([k]) => k));
  for (const page of registry.pages.filter(p => ['E', 'F'].includes(p.class))) {
    assert.ok(page.controls.every(c => !altering.has(c.control)), page.pageId);
  }
});
