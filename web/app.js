// JanSetu Web Application Logic
let currentLang = 'en';
let currentTab = 'services';
let currentCategory = 'all';
let currentLevel = 'all';
let searchQuery = '';
let activeModalService = null;
let checkedDocIndices = new Set();
let universalDocs = new Set(['Aadhaar Card (UIDAI)', 'Permanent Account Number (PAN Card)']);

// Common Indian documents for Universal Vault
const COMMON_DOCS = [
  { id: 'aadhaar', name: 'Aadhaar Card (UIDAI)', desc: 'Identity & Address Proof' },
  { id: 'pan', name: 'Permanent Account Number (PAN Card)', desc: 'Tax & Financial Identity' },
  { id: 'income', name: 'Income Certificate (Tehsildar/Revenue)', desc: 'Fee Waivers & Scheme Eligibility' },
  { id: 'domicile', name: 'Domicile / Residential Certificate', desc: 'State-Level Benefits & Quotas' },
  { id: 'caste', name: 'Caste / Community Certificate (SC/ST/OBC/EWS)', desc: 'Reservation & Scholarships' },
  { id: 'marksheet', name: '10th / 12th Educational Marksheets', desc: 'Educational & Job Admissions' },
  { id: 'photo', name: 'Passport Size Photograph (White Background)', desc: 'Universal Application Requirement' },
  { id: 'passbook', name: 'Bank Passbook / Cancelled Cheque', desc: 'Direct Benefit Transfer (DBT)' },
  { id: 'voter', name: 'Voter ID Card (EPIC)', desc: 'Electoral Identity & Address Proof' },
  { id: 'ration', name: 'Ration Card / PDS Card', desc: 'Food Security & Household Proof' }
];

// Initialize on DOM load
document.addEventListener('DOMContentLoaded', () => {
  renderCategoryRibbon();
  renderContent();
  renderCommonDocs();

  // Close modals when clicking on background backdrop
  const sModal = document.getElementById('serviceModal');
  if (sModal) {
    sModal.addEventListener('click', (e) => {
      if (e.target.id === 'serviceModal') closeModal();
    });
  }

  const rModal = document.getElementById('readinessModal');
  if (rModal) {
    rModal.addEventListener('click', (e) => {
      if (e.target.id === 'readinessModal') closeReadinessModal();
    });
  }

  // Close modals on Escape key
  document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape') {
      closeModal();
      closeReadinessModal();
    }
  });

  if (window.lucide) {
    window.lucide.createIcons();
  }
});

// Render Category Ribbon
function renderCategoryRibbon() {
  const ribbon = document.getElementById('categoryRibbon');
  if (!ribbon || !window.JANSETU_CATEGORIES) return;

  let html = `
    <button onclick="selectCategory('all')" class="flex-shrink-0 px-3 py-1.5 rounded-full text-xs font-semibold transition ${currentCategory === 'all' ? 'bg-brand-600 text-white shadow-sm' : 'bg-slate-100 text-slate-700 hover:bg-slate-200'}">
      <span>${currentLang === 'hi' ? 'सभी श्रेणियां' : 'All Categories'}</span>
      <span class="ml-1.5 px-1.5 py-0.2 rounded-full text-[10px] ${currentCategory === 'all' ? 'bg-brand-700' : 'bg-slate-200'}">${window.JANSETU_SERVICES ? window.JANSETU_SERVICES.length : 0}</span>
    </button>
  `;

  window.JANSETU_CATEGORIES.forEach(cat => {
    const isSelected = currentCategory === cat.id;
    const count = (window.JANSETU_SERVICES || []).filter(s => s.categories && s.categories.includes(cat.id)).length;
    const title = currentLang === 'hi' && cat.titleHi ? cat.titleHi : cat.title;

    html += `
      <button onclick="selectCategory('${cat.id}')" class="flex-shrink-0 flex items-center space-x-1.5 px-3 py-1.5 rounded-full text-xs font-medium transition ${isSelected ? 'bg-brand-600 text-white shadow-sm' : 'bg-slate-100 text-slate-700 hover:bg-slate-200'}">
        <span class="font-bold">${cat.symbol || '•'}</span>
        <span>${title}</span>
        ${count > 0 ? `<span class="ml-1 px-1.5 py-0.2 rounded-full text-[10px] ${isSelected ? 'bg-brand-700' : 'bg-slate-200'}">${count}</span>` : ''}
      </button>
    `;
  });

  ribbon.innerHTML = html;
}

// Select Category
function selectCategory(catId) {
  currentCategory = catId;
  renderCategoryRibbon();
  renderContent();
}

// Handle Search
function handleSearch(val) {
  searchQuery = (val || '').trim().toLowerCase();
  const clearBtn = document.getElementById('clearSearchBtn');
  if (clearBtn) {
    if (searchQuery.length > 0) clearBtn.classList.remove('hidden');
    else clearBtn.classList.add('hidden');
  }
  renderContent();
}

// Clear Search
function clearSearch() {
  const input = document.getElementById('searchInput');
  if (input) input.value = '';
  searchQuery = '';
  document.getElementById('clearSearchBtn')?.classList.add('hidden');
  renderContent();
}

// Handle Administrative Level Change
function handleLevelChange(val) {
  currentLevel = val;
  renderContent();
}

// Switch Tab
function switchTab(tab) {
  currentTab = tab;
  const sBtn = document.getElementById('tabServicesBtn');
  const pBtn = document.getElementById('tabPortalsBtn');

  if (sBtn && pBtn) {
    if (tab === 'services') {
      sBtn.className = 'px-4 py-2 rounded-lg text-xs font-semibold bg-white text-slate-900 shadow-sm transition';
      pBtn.className = 'px-4 py-2 rounded-lg text-xs font-semibold text-slate-600 hover:text-slate-900 transition';
    } else {
      pBtn.className = 'px-4 py-2 rounded-lg text-xs font-semibold bg-white text-slate-900 shadow-sm transition';
      sBtn.className = 'px-4 py-2 rounded-lg text-xs font-semibold text-slate-600 hover:text-slate-900 transition';
    }
  }

  renderContent();
}

// Filter Services
function getFilteredServices() {
  if (!window.JANSETU_SERVICES) return [];

  return window.JANSETU_SERVICES.filter(service => {
    // Category filter
    if (currentCategory !== 'all' && (!service.categories || !service.categories.includes(currentCategory))) {
      return false;
    }

    // Level filter
    if (currentLevel !== 'all' && service.level !== currentLevel) {
      return false;
    }

    // Search query
    if (searchQuery) {
      const q = searchQuery;
      const titleMatch = (service.title || '').toLowerCase().includes(q);
      const descMatch = (service.description || '').toLowerCase().includes(q);
      const deptMatch = (service.department || '').toLowerCase().includes(q);
      const stateMatch = (service.state || '').toLowerCase().includes(q);
      const intentsMatch = (service.citizenIntents || []).some(i => (i || '').toLowerCase().includes(q));
      const synonymsMatch = (service.synonyms || []).some(s => (s || '').toLowerCase().includes(q));
      const docsMatch = (service.documents || []).some(d => (d || '').toLowerCase().includes(q));

      if (!titleMatch && !descMatch && !deptMatch && !stateMatch && !intentsMatch && !synonymsMatch && !docsMatch) {
        return false;
      }
    }

    return true;
  });
}

// Filter Portals
function getFilteredPortals() {
  if (!window.JANSETU_WEBSITES) return [];

  return window.JANSETU_WEBSITES.filter(portal => {
    if (currentCategory !== 'all' && (!portal.categories || !portal.categories.includes(currentCategory))) {
      return false;
    }

    if (currentLevel !== 'all' && portal.level !== currentLevel) {
      return false;
    }

    if (searchQuery) {
      const q = searchQuery;
      const titleMatch = (portal.title || '').toLowerCase().includes(q);
      const descMatch = (portal.description || '').toLowerCase().includes(q);
      const orgMatch = (portal.organization || '').toLowerCase().includes(q);
      const urlMatch = (portal.url || '').toLowerCase().includes(q);

      if (!titleMatch && !descMatch && !orgMatch && !urlMatch) {
        return false;
      }
    }

    return true;
  });
}

// Render Content (Services or Portals)
function renderContent() {
  const container = document.getElementById('servicesContainer');
  const emptyState = document.getElementById('emptyState');
  const countBadge = document.getElementById('resultCountBadge');
  if (!container) return;

  if (currentTab === 'services') {
    const list = getFilteredServices();
    if (countBadge) countBadge.innerText = `Showing ${list.length} source-reviewed services`;

    if (list.length === 0) {
      container.innerHTML = '';
      if (emptyState) emptyState.classList.remove('hidden');
      return;
    }

    if (emptyState) emptyState.classList.add('hidden');
    let html = '';

    list.forEach(item => {
      const catObj = window.JANSETU_CATEGORIES?.find(c => item.categories && item.categories.includes(c.id));
      const catTitle = catObj ? (currentLang === 'hi' && catObj.titleHi ? catObj.titleHi : catObj.title) : 'Public Service';
      const catSymbol = catObj?.symbol || '•';
      const docCount = (item.documents || []).length;
      const appUrl = item.applicationUrl || item.informationUrl || item.sourceUrl || '#';

      html += `
        <div class="bg-white rounded-2xl border border-slate-200 hover:border-brand-300 hover:shadow-md transition-all duration-200 flex flex-col overflow-hidden group">
          <div class="p-6 flex-grow flex flex-col">
            <!-- Badges -->
            <div class="flex items-center justify-between mb-3 text-[11px]">
              <span class="inline-flex items-center space-x-1 font-semibold px-2.5 py-0.5 rounded-full bg-brand-50 text-brand-700 border border-brand-100">
                <span>${catSymbol}</span>
                <span>${catTitle}</span>
              </span>
              <span class="font-medium px-2 py-0.5 rounded-full ${item.level === 'Central' ? 'bg-indigo-50 text-indigo-700' : 'bg-amber-50 text-amber-700'}">
                ${item.level || 'Central'} ${item.state ? `&bull; ${item.state}` : ''}
              </span>
            </div>

            <!-- Title & Department -->
            <h3 onclick="openServiceModal('${item.id}')" class="cursor-pointer font-bold text-slate-900 text-lg group-hover:text-brand-600 transition leading-snug">
              ${item.title}
            </h3>
            <p class="text-xs font-medium text-slate-500 mt-1 line-clamp-1">
              ${item.department || 'Government of India'}
            </p>

            <!-- Description -->
            <p class="text-xs text-slate-600 mt-3 line-clamp-3 leading-relaxed flex-grow">
              ${item.description || 'Verified official public service record.'}
            </p>

            <!-- Metadata Pills -->
            <div class="mt-4 pt-3 border-t border-slate-100 grid grid-cols-2 gap-2 text-[11px] text-slate-500">
              <div class="flex items-center space-x-1">
                <i data-lucide="file-text" class="w-3.5 h-3.5 text-brand-500"></i>
                <span>${docCount} Documents Required</span>
              </div>
              <div class="flex items-center space-x-1">
                <i data-lucide="clock" class="w-3.5 h-3.5 text-amber-500"></i>
                <span class="truncate">${item.processing || 'Standard timeframe'}</span>
              </div>
            </div>
          </div>

          <!-- Card Actions -->
          <div class="bg-slate-50 px-6 py-3 border-t border-slate-100 flex items-center justify-between">
            <button onclick="openServiceModal('${item.id}')" class="text-xs font-semibold text-brand-600 hover:text-brand-800 flex items-center space-x-1 cursor-pointer">
              <span>View Checklist &amp; Guide</span>
              <i data-lucide="chevron-right" class="w-3.5 h-3.5"></i>
            </button>
            <a href="${appUrl}" target="_blank" rel="noopener noreferrer" class="p-1.5 rounded-lg text-slate-400 hover:text-brand-600 hover:bg-brand-50 transition" title="Direct Official Portal">
              <i data-lucide="external-link" class="w-4 h-4"></i>
            </a>
          </div>
        </div>
      `;
    });

    container.innerHTML = html;
  } else {
    // Portals directory tab
    const list = getFilteredPortals();
    if (countBadge) countBadge.innerText = `Showing ${list.length} official government portals`;

    if (list.length === 0) {
      container.innerHTML = '';
      if (emptyState) emptyState.classList.remove('hidden');
      return;
    }

    if (emptyState) emptyState.classList.add('hidden');
    let html = '';

    list.forEach(p => {
      const catObj = window.JANSETU_CATEGORIES?.find(c => p.categories && p.categories.includes(c.id));
      const catTitle = catObj ? (currentLang === 'hi' && catObj.titleHi ? catObj.titleHi : catObj.title) : 'Government Directory';

      html += `
        <div class="bg-white rounded-2xl border border-slate-200 hover:border-brand-300 hover:shadow-md transition-all duration-200 flex flex-col p-6 overflow-hidden">
          <div class="flex items-center justify-between mb-3 text-[11px]">
            <span class="inline-flex items-center font-semibold px-2 py-0.5 rounded-full bg-slate-100 text-slate-700">
              ${catTitle}
            </span>
            <span class="font-medium px-2 py-0.5 rounded-full bg-emerald-50 text-emerald-700">
              ${p.level || 'Central'} Portal
            </span>
          </div>

          <h3 class="font-bold text-slate-900 text-base leading-snug">
            ${p.title}
          </h3>
          <p class="text-xs text-slate-500 mt-1 line-clamp-1 font-medium">
            ${p.organization || 'Official Government Department'}
          </p>

          <p class="text-xs text-slate-600 mt-3 line-clamp-2 leading-relaxed flex-grow">
            ${p.description || 'Official government portal.'}
          </p>

          <div class="mt-4 pt-3 border-t border-slate-100 flex items-center justify-between">
            <span class="text-[11px] font-mono text-slate-400 truncate max-w-[200px]">${p.url}</span>
            <a href="${p.url}" target="_blank" rel="noopener noreferrer" class="inline-flex items-center space-x-1 px-3 py-1.5 rounded-lg bg-brand-50 text-brand-700 hover:bg-brand-100 text-xs font-semibold transition">
              <span>Open Portal</span>
              <i data-lucide="external-link" class="w-3.5 h-3.5"></i>
            </a>
          </div>
        </div>
      `;
    });

    container.innerHTML = html;
  }

  if (window.lucide) {
    window.lucide.createIcons();
  }
}

// Open Service Modal
function openServiceModal(serviceId) {
  const service = (window.JANSETU_SERVICES || []).find(s => s.id === serviceId);
  if (!service) {
    console.warn("Service not found for id:", serviceId);
    return;
  }

  activeModalService = service;
  checkedDocIndices.clear();

  const modal = document.getElementById('serviceModal');
  if (!modal) return;

  const catObj = window.JANSETU_CATEGORIES?.find(c => service.categories && service.categories.includes(c.id));

  const setElText = (id, text) => {
    const el = document.getElementById(id);
    if (el) el.innerText = text;
  };

  setElText('modalTitle', service.title || '');
  setElText('modalDepartment', service.department || 'Government of India');
  setElText('modalDescription', service.description || '');
  setElText('modalCategoryBadge', catObj?.title || 'Public Service');
  setElText('modalLevelBadge', `${service.level || 'Central'} ${service.state ? `• ${service.state}` : ''}`);
  setElText('modalProcessing', service.processing || 'Standard department timeframe');
  setElText('modalFees', service.fees || 'Free / Nil');

  const actionBtn = document.getElementById('modalActionBtn');
  if (actionBtn) {
    actionBtn.href = service.applicationUrl || service.informationUrl || service.sourceUrl || '#';
  }

  // Eligibility
  const eligList = document.getElementById('modalEligibilityList');
  if (eligList) {
    eligList.innerHTML = '';
    if (service.eligibility && service.eligibility.length > 0) {
      service.eligibility.forEach(e => {
        eligList.innerHTML += `
          <li class="flex items-start space-x-2">
            <i data-lucide="check" class="w-4 h-4 text-emerald-600 flex-shrink-0 mt-0.5"></i>
            <span>${e}</span>
          </li>
        `;
      });
    } else {
      eligList.innerHTML = '<li class="text-xs text-slate-400">Open to eligible citizens meeting statutory criteria.</li>';
    }
  }

  // Documents Checklist
  renderModalDocuments();

  // Show modal using display flex
  modal.style.display = 'flex';
  document.body.classList.add('overflow-hidden');

  if (window.lucide) {
    window.lucide.createIcons();
  }
}

// Render Modal Document Checklist using numeric indices
function renderModalDocuments() {
  const docContainer = document.getElementById('modalDocumentsList');
  const scoreBadge = document.getElementById('checklistScore');
  const progressBar = document.getElementById('checklistProgressBar');

  if (!activeModalService || !docContainer) return;

  const docs = activeModalService.documents || [];
  const total = docs.length;
  const readyCount = checkedDocIndices.size;

  if (scoreBadge) scoreBadge.innerText = `${readyCount} of ${total} Ready`;
  const pct = total > 0 ? (readyCount / total) * 100 : 0;
  if (progressBar) progressBar.style.width = `${pct}%`;

  let html = '';
  if (docs.length === 0) {
    html = '<p class="text-xs text-slate-400">No mandatory pre-requisite documents declared.</p>';
  } else {
    docs.forEach((doc, idx) => {
      const isChecked = checkedDocIndices.has(idx);
      html += `
        <label class="flex items-start space-x-2.5 p-2 rounded-xl border transition cursor-pointer ${isChecked ? 'bg-emerald-50/70 border-emerald-300 text-emerald-950 font-medium' : 'bg-white border-slate-200 text-slate-700 hover:bg-slate-50'}">
          <input type="checkbox" ${isChecked ? 'checked' : ''} onchange="toggleDocumentCheckByIndex(${idx})" class="mt-0.5 w-4 h-4 rounded text-emerald-600 focus:ring-emerald-500 border-slate-300" />
          <span class="text-xs leading-relaxed flex-grow">${doc}</span>
        </label>
      `;
    });
  }

  docContainer.innerHTML = html;
}

// Toggle Document Check by numeric index
function toggleDocumentCheckByIndex(idx) {
  if (checkedDocIndices.has(idx)) {
    checkedDocIndices.delete(idx);
  } else {
    checkedDocIndices.add(idx);
  }
  renderModalDocuments();
}

// Close Modal
function closeModal() {
  const modal = document.getElementById('serviceModal');
  if (modal) modal.style.display = 'none';
  document.body.classList.remove('overflow-hidden');
  activeModalService = null;
}

// Open Readiness Modal
function openReadinessModal() {
  const rModal = document.getElementById('readinessModal');
  if (rModal) rModal.style.display = 'flex';
  document.body.classList.add('overflow-hidden');
  if (window.lucide) {
    window.lucide.createIcons();
  }
}

function closeReadinessModal() {
  const rModal = document.getElementById('readinessModal');
  if (rModal) rModal.style.display = 'none';
  document.body.classList.remove('overflow-hidden');
}

// Render Universal Common Documents
function renderCommonDocs() {
  const container = document.getElementById('commonDocChecklist');
  if (!container) return;

  let html = '';
  COMMON_DOCS.forEach(doc => {
    const isChecked = universalDocs.has(doc.name);
    html += `
      <label class="flex items-center space-x-3 p-3 rounded-xl border transition cursor-pointer ${isChecked ? 'bg-emerald-50 border-emerald-300' : 'bg-white border-slate-200 hover:bg-slate-50'}">
        <input type="checkbox" ${isChecked ? 'checked' : ''} onchange="toggleUniversalDoc('${doc.id}')" class="w-4 h-4 rounded text-emerald-600 focus:ring-emerald-500 border-slate-300" />
        <div class="flex-grow">
          <div class="text-xs font-bold text-slate-800">${doc.name}</div>
          <div class="text-[11px] text-slate-500">${doc.desc}</div>
        </div>
      </label>
    `;
  });

  container.innerHTML = html;
}

function toggleUniversalDoc(docId) {
  const doc = COMMON_DOCS.find(d => d.id === docId);
  if (!doc) return;
  if (universalDocs.has(doc.name)) universalDocs.delete(doc.name);
  else universalDocs.add(doc.name);
  renderCommonDocs();
}

// Language Switch
function toggleLanguage() {
  currentLang = currentLang === 'en' ? 'hi' : 'en';
  const langLabel = document.getElementById('langLabel');
  if (langLabel) langLabel.innerText = currentLang === 'en' ? 'हिंदी' : 'English';

  const titleEl = document.getElementById('heroTitle');
  const subtitleEl = document.getElementById('heroSubtitle');
  const searchInput = document.getElementById('searchInput');
  const sLabel = document.getElementById('tabServicesLabel');
  const pLabel = document.getElementById('tabPortalsLabel');

  if (currentLang === 'hi') {
    if (titleEl) titleEl.innerHTML = 'नागरिक सेवाओं तक पहुँचें <span class="text-transparent bg-clip-text bg-gradient-to-r from-brand-700 via-brand-600 to-ashoka-600">सहजता और स्पष्टता से</span>';
    if (subtitleEl) subtitleEl.innerText = 'अपनी आवश्यकता के अनुसार आधिकारिक सरकारी सेवाएँ खोजें। सरल सारांश, सत्यापित चेकलिस्ट, आवश्यक दस्तावेज़ और आधिकारिक पोर्टलों का सुरक्षित मार्ग।';
    if (searchInput) searchInput.placeholder = 'सेवा खोजें, जैसे "छात्रवृत्ति", "पैन कार्ड", "आरटीआई", "पासपोर्ट", "वोटर आईडी"...';
    if (sLabel) sLabel.innerText = 'नागरिक सेवाएँ (52)';
    if (pLabel) pLabel.innerText = 'आधिकारिक पोर्टल निर्देशिका';
  } else {
    if (titleEl) titleEl.innerHTML = 'Access Indian Public Services with <span class="text-transparent bg-clip-text bg-gradient-to-r from-brand-700 via-brand-600 to-ashoka-600">Clarity &amp; Ease</span>';
    if (subtitleEl) subtitleEl.innerText = 'Find official government portals by what you need to do. Plain-language summaries, verified checklists, required documents, and direct safe routes to government portals.';
    if (searchInput) searchInput.placeholder = "Search service, e.g. 'Scholarship', 'PAN card', 'RTI', 'Passport', 'Voter ID'...";
    if (sLabel) sLabel.innerText = 'Citizen Services (52)';
    if (pLabel) pLabel.innerText = 'Official Portals Directory';
  }

  renderCategoryRibbon();
  renderContent();
}

// Reset Filters
function resetFilters() {
  currentCategory = 'all';
  currentLevel = 'all';
  searchQuery = '';
  const searchInput = document.getElementById('searchInput');
  if (searchInput) searchInput.value = '';
  const levelFilter = document.getElementById('levelFilter');
  if (levelFilter) levelFilter.value = 'all';
  document.getElementById('clearSearchBtn')?.classList.add('hidden');
  renderCategoryRibbon();
  renderContent();
}

// Explicit window bindings
window.openServiceModal = openServiceModal;
window.closeModal = closeModal;
window.openReadinessModal = openReadinessModal;
window.closeReadinessModal = closeReadinessModal;
window.toggleDocumentCheckByIndex = toggleDocumentCheckByIndex;
window.toggleUniversalDoc = toggleUniversalDoc;
window.toggleLanguage = toggleLanguage;
window.resetFilters = resetFilters;
window.switchTab = switchTab;
window.selectCategory = selectCategory;
window.handleSearch = handleSearch;
window.clearSearch = clearSearch;
window.handleLevelChange = handleLevelChange;
