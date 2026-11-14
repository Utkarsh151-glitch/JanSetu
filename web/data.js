// JanSetu Public Services Data Bundle
window.JANSETU_CATEGORIES = [
  {
    "id": "documents",
    "parentId": "people",
    "symbol": "▣",
    "title": "Documents & identity",
    "titleHi": "दस्तावेज़ और पहचान"
  },
  {
    "id": "grievances",
    "parentId": "rights",
    "symbol": "◎",
    "title": "Complaints & grievances",
    "titleHi": "शिकायतें"
  },
  {
    "id": "jobs",
    "parentId": "learning_work",
    "symbol": "◈",
    "title": "Jobs & careers",
    "titleHi": "नौकरी और करियर"
  },
  {
    "id": "information",
    "parentId": "rights",
    "symbol": "◇",
    "title": "Government information",
    "titleHi": "सरकारी जानकारी"
  },
  {
    "id": "education",
    "parentId": "learning_work",
    "symbol": "✦",
    "title": "Education & scholarships",
    "titleHi": "शिक्षा और छात्रवृत्ति"
  },
  {
    "id": "business",
    "parentId": "learning_work",
    "symbol": "▤",
    "title": "Business & registration",
    "titleHi": "व्यवसाय और पंजीकरण"
  },
  {
    "id": "taxation",
    "parentId": "rights",
    "symbol": "₹",
    "title": "Taxes & finance",
    "titleHi": "कर और वित्त"
  },
  {
    "id": "transparency",
    "parentId": "rights",
    "symbol": "◉",
    "title": "Information requests",
    "titleHi": "सूचना अनुरोध"
  },
  {
    "id": "agriculture",
    "parentId": "land_mobility",
    "symbol": "❀",
    "title": "Agriculture & farmers",
    "titleHi": "कृषि और किसान"
  },
  {
    "id": "transport",
    "parentId": "land_mobility",
    "symbol": "↗",
    "title": "Transport & licences",
    "titleHi": "परिवहन और लाइसेंस"
  },
  {
    "id": "courts",
    "parentId": "rights",
    "symbol": "⚖",
    "title": "Courts & legal services",
    "titleHi": "न्यायालय और कानूनी सेवाएँ"
  },
  {
    "id": "health",
    "parentId": "people",
    "symbol": "+",
    "title": "Health & family welfare",
    "titleHi": "????????? ?? ?????? ??????"
  },
  {
    "id": "welfare",
    "parentId": "people",
    "symbol": "?",
    "title": "Welfare & benefits",
    "titleHi": "?????? ?? ???"
  },
  {
    "id": "land",
    "parentId": "land_mobility",
    "symbol": "?",
    "title": "Land, property & housing",
    "titleHi": "????, ??????? ?? ????"
  },
  {
    "id": "utilities",
    "parentId": "people",
    "symbol": "?",
    "title": "Water, power & sanitation",
    "titleHi": "????, ????? ?? ????????"
  },
  {
    "id": "civic",
    "parentId": "people",
    "symbol": "?",
    "title": "Local government & civic services",
    "titleHi": "??????? ????? ?? ?????? ??????"
  },
  {
    "id": "environment",
    "parentId": "land_mobility",
    "symbol": "?",
    "title": "Environment & forests",
    "titleHi": "???????? ?? ??"
  },
  {
    "id": "industry",
    "parentId": "learning_work",
    "symbol": "?",
    "title": "Industry, trade & commerce",
    "titleHi": "??????, ??????? ?? ???????"
  },
  {
    "id": "safety",
    "parentId": "rights",
    "symbol": "!",
    "title": "Police, safety & disasters",
    "titleHi": "?????, ??????? ?? ????"
  }
];

window.JANSETU_SERVICES = [
  {
    "applicationMode": "online",
    "applicationUrl": "https://scholarships.gov.in/",
    "categories": [
      "education"
    ],
    "citizenIntents": [
      "I need a scholarship for engineering",
      "apply for pragati scholarship"
    ],
    "conditionalDocuments": null,
    "deadline": null,
    "department": "All India Council for Technical Education",
    "description": "Scholarship support for eligible technical degree students.",
    "district": null,
    "documents": [
      {
        "evidenceIds": [
          "pragati"
        ],
        "text": "State/UT income certificate."
      }
    ],
    "eligibility": {
      "evidenceIds": [
        "pragati"
      ],
      "text": "Selected conditions: girls entering year one, or year two through lateral entry, of an AICTE-approved degree; family income up to INR 8 lakh; at most two girls per family. Read the guidelines for exclusions."
    },
    "evidence": [
      {
        "checkedOn": "2026-10-16",
        "contentSha256": "1e493333bc25f9adf9192624a0f92104845f5c5e3b3a00e1ec52af5302a4b10f",
        "id": "pragati",
        "locator": "Sections 2, 6, 10 and renewals; July 2020 guidelines linked by current NSP",
        "publisher": "AICTE",
        "summary": "Degree scholarship conditions and application procedure.",
        "url": "https://scholarships.gov.in/public/schemeGuidelines/AICTE/AICTE_2010_G.pdf"
      },
      {
        "checkedOn": "2026-10-16",
        "contentSha256": "6168af5cf69ad11807dcfdbe8af6a490743e720eb7752daef4250633e32a626f",
        "id": "nsp",
        "locator": "Current AICTE scheme list",
        "publisher": "National Scholarship Portal",
        "summary": "The degree scheme is listed on the current NSP page.",
        "url": "https://scholarships.gov.in/All-Scholarships"
      }
    ],
    "fees": null,
    "id": "aicte-pragati-degree",
    "informationUrl": "https://scholarships.gov.in/public/schemeGuidelines/AICTE/AICTE_2010_G.pdf",
    "level": "Central",
    "linkEvidenceIds": [
      "pragati",
      "nsp"
    ],
    "organizationId": "org-7077af4eaac38e33",
    "procedure": [
      {
        "evidenceIds": [
          "pragati"
        ],
        "text": "Use the NSP student application process for this scheme; institution and State/UT scrutiny follow."
      }
    ],
    "processing": null,
    "publicationStatus": "published",
    "reviewNotes": "Official service information reviewed by Codex on 2026-10-16; not an independently completed transaction. The linked organization identifies the existing website operator; the responsible department is listed separately. Unknown fields stay null. Guideline publication year differs from review date; it remains linked on current NSP. Reconfirm the applicable academic year and complete conditions. Document list is partial.",
    "sourceUrl": "https://scholarships.gov.in/public/schemeGuidelines/AICTE/AICTE_2010_G.pdf",
    "state": null,
    "synonyms": [
      "pragati",
      "engineering scholarship",
      "technical education",
      "student financial assistance"
    ],
    "title": "AICTE Pragati Scholarship for Girl Students (Degree)",
    "verificationStatus": "verified",
    "verifiedOn": "2026-10-16",
    "websiteId": "national-scholarship-portal"
  },
  {
    "applicationMode": "online",
    "applicationUrl": "https://scholarships.gov.in/",
    "categories": [
      "education"
    ],
    "citizenIntents": [
      "I need a scholarship for engineering",
      "apply for saksham scholarship"
    ],
    "conditionalDocuments": null,
    "deadline": null,
    "department": "All India Council for Technical Education",
    "description": "Scholarship support for eligible technical degree students.",
    "district": null,
    "documents": [
      {
        "evidenceIds": [
          "saksham"
        ],
        "text": "State/UT income certificate."
      }
    ],
    "eligibility": {
      "evidenceIds": [
        "saksham"
      ],
      "text": "Selected conditions: AICTE-approved degree admission in year one or lateral entry in year two; disability at least 40%; family income up to INR 8 lakh. Other conditions apply."
    },
    "evidence": [
      {
        "checkedOn": "2026-10-16",
        "contentSha256": "06ebed9e50a47ae76cba985ddde78cd65f19f2193b0e4c9c164fff529580474c",
        "id": "saksham",
        "locator": "Sections 2, 6, 7 and 8; July 2020 guidelines linked by current NSP",
        "publisher": "AICTE",
        "summary": "Degree scholarship conditions and application procedure.",
        "url": "https://scholarships.gov.in/public/schemeGuidelines/AICTE/AICTE_2012_G.pdf"
      },
      {
        "checkedOn": "2026-10-16",
        "contentSha256": "6168af5cf69ad11807dcfdbe8af6a490743e720eb7752daef4250633e32a626f",
        "id": "nsp",
        "locator": "Current AICTE scheme list",
        "publisher": "National Scholarship Portal",
        "summary": "The degree scheme is listed on the current NSP page.",
        "url": "https://scholarships.gov.in/All-Scholarships"
      }
    ],
    "fees": null,
    "id": "aicte-saksham-degree",
    "informationUrl": "https://scholarships.gov.in/public/schemeGuidelines/AICTE/AICTE_2012_G.pdf",
    "level": "Central",
    "linkEvidenceIds": [
      "saksham",
      "nsp"
    ],
    "organizationId": "org-7077af4eaac38e33",
    "procedure": [
      {
        "evidenceIds": [
          "saksham"
        ],
        "text": "Use the NSP student application process for this scheme; institution and State/UT scrutiny follow."
      }
    ],
    "processing": null,
    "publicationStatus": "published",
    "reviewNotes": "Official service information reviewed by Codex on 2026-10-16; not an independently completed transaction. The linked organization identifies the existing website operator; the responsible department is listed separately. Unknown fields stay null. Guideline publication year differs from review date; it remains linked on current NSP. Reconfirm the applicable academic year and complete conditions. Document list is partial.",
    "sourceUrl": "https://scholarships.gov.in/public/schemeGuidelines/AICTE/AICTE_2012_G.pdf",
    "state": null,
    "synonyms": [
      "saksham",
      "engineering scholarship",
      "technical education",
      "student financial assistance"
    ],
    "title": "AICTE Saksham Scholarship for Specially-Abled Students (Degree)",
    "verificationStatus": "verified",
    "verifiedOn": "2026-10-16",
    "websiteId": "national-scholarship-portal"
  },
  {
    "applicationMode": "online",
    "applicationUrl": "https://scholarships.gov.in/",
    "categories": [
      "education"
    ],
    "citizenIntents": [
      "I need a scholarship for engineering",
      "apply for swanath scholarship"
    ],
    "conditionalDocuments": null,
    "deadline": null,
    "department": "All India Council for Technical Education",
    "description": "Scholarship support for eligible technical degree students.",
    "district": null,
    "documents": [
      {
        "evidenceIds": [
          "swanath"
        ],
        "text": "Institution bonafide certificate and applicable income evidence; see category-specific checklist."
      }
    ],
    "eligibility": {
      "evidenceIds": [
        "swanath"
      ],
      "text": "Qualifying family circumstances include orphanhood, parental death due to COVID-19, a parent martyred in specified forces, or the specified critical illness/accident category. Income up to INR 8 lakh, regular AICTE-approved study and no other government/AICTE scholarship are also required."
    },
    "evidence": [
      {
        "checkedOn": "2026-10-16",
        "contentSha256": "d9986d39167b660b814f871feca5b0accbc8ce08b6a7f95f7c5a07e4c8f6ee04",
        "id": "swanath",
        "locator": "May 2025 guidelines, sections 2, 7 and 10",
        "publisher": "AICTE",
        "summary": "Degree scholarship conditions and application procedure.",
        "url": "https://scholarships.gov.in/public/schemeGuidelines/AICTE/AICTE_3039_G.pdf"
      },
      {
        "checkedOn": "2026-10-16",
        "contentSha256": "6168af5cf69ad11807dcfdbe8af6a490743e720eb7752daef4250633e32a626f",
        "id": "nsp",
        "locator": "Current AICTE scheme list",
        "publisher": "National Scholarship Portal",
        "summary": "The degree scheme is listed on the current NSP page.",
        "url": "https://scholarships.gov.in/All-Scholarships"
      }
    ],
    "fees": null,
    "id": "aicte-swanath-degree",
    "informationUrl": "https://scholarships.gov.in/public/schemeGuidelines/AICTE/AICTE_3039_G.pdf",
    "level": "Central",
    "linkEvidenceIds": [
      "swanath",
      "nsp"
    ],
    "organizationId": "org-7077af4eaac38e33",
    "procedure": [
      {
        "evidenceIds": [
          "swanath"
        ],
        "text": "Use the NSP student application process for this scheme; institution and State/UT scrutiny follow."
      }
    ],
    "processing": null,
    "publicationStatus": "published",
    "reviewNotes": "Official service information reviewed by Codex on 2026-10-16; not an independently completed transaction. The linked organization identifies the existing website operator; the responsible department is listed separately. Unknown fields stay null. Guideline publication year differs from review date; it remains linked on current NSP. Reconfirm the applicable academic year and complete conditions. Document list is partial.",
    "sourceUrl": "https://scholarships.gov.in/public/schemeGuidelines/AICTE/AICTE_3039_G.pdf",
    "state": null,
    "synonyms": [
      "swanath",
      "engineering scholarship",
      "technical education",
      "student financial assistance"
    ],
    "title": "AICTE Swanath Scholarship (Technical Degree)",
    "verificationStatus": "verified",
    "verifiedOn": "2026-10-16",
    "websiteId": "national-scholarship-portal"
  },
  {
    "applicationMode": "assisted",
    "applicationUrl": null,
    "categories": [
      "documents"
    ],
    "citizenIntents": [
      "How can I get a caste certificate"
    ],
    "conditionalDocuments": [
      {
        "evidenceIds": [
          "tn-community"
        ],
        "text": "A parent or sibling community certificate, where applicable."
      }
    ],
    "deadline": null,
    "department": "Revenue Administration, Government of Tamil Nadu",
    "description": "Community certification.",
    "district": null,
    "documents": [
      {
        "evidenceIds": [
          "tn-community"
        ],
        "text": "Applicant photo, address evidence and self-declaration."
      }
    ],
    "eligibility": null,
    "evidence": [
      {
        "checkedOn": "2026-10-16",
        "contentSha256": "b13c8222ab52490c67c4d5d700f4446f108ad2e6d0ea3edb527f0ca55ed01e51",
        "id": "tn-community",
        "locator": "Description, access steps and service-specific documents",
        "publisher": "Tamil Nadu e-Governance Agency / ServicePlus",
        "summary": "community certificate service.",
        "url": "https://serviceonline.gov.in/configuretn/login.do"
      }
    ],
    "fees": null,
    "id": "tn-community-certificate",
    "informationUrl": "https://serviceonline.gov.in/configuretn/login.do",
    "level": "State",
    "linkEvidenceIds": [
      "tn-community"
    ],
    "organizationId": "org-05a96313c65f060b",
    "procedure": [
      {
        "evidenceIds": [
          "tn-community"
        ],
        "text": "Register/login on the official service platform and select the relevant service."
      }
    ],
    "processing": null,
    "publicationStatus": "published",
    "reviewNotes": "Official service information reviewed by Codex on 2026-10-16; not an independently completed transaction. The linked organization identifies the existing website operator; the responsible department is listed separately. Unknown fields stay null. Centre-assisted listing; no direct citizen application URL or complete eligibility checklist confirmed. Fees and processing-time columns may differ by service and are not reproduced. Birth printing is not birth registration.",
    "sourceUrl": "https://serviceonline.gov.in/configuretn/login.do",
    "state": "Tamil Nadu",
    "synonyms": [
      "community certificate",
      "caste certificate",
      "जाति प्रमाणपत्र"
    ],
    "title": "Community Certificate (Tamil Nadu)",
    "verificationStatus": "verified",
    "verifiedOn": "2026-10-16",
    "websiteId": "igod-2715637a8f399700"
  },
  {
    "applicationMode": "assisted",
    "applicationUrl": null,
    "categories": [
      "documents"
    ],
    "citizenIntents": [
      "apply for nativity certificate"
    ],
    "conditionalDocuments": [
      {
        "evidenceIds": [
          "tn-community"
        ],
        "text": "Birth certificate or school/employment/residence-history proof, as applicable."
      }
    ],
    "deadline": null,
    "department": "Revenue Administration, Government of Tamil Nadu",
    "description": "Nativity certification.",
    "district": null,
    "documents": [
      {
        "evidenceIds": [
          "tn-community"
        ],
        "text": "Applicant photo, address evidence and self-declaration."
      }
    ],
    "eligibility": null,
    "evidence": [
      {
        "checkedOn": "2026-10-16",
        "contentSha256": "b13c8222ab52490c67c4d5d700f4446f108ad2e6d0ea3edb527f0ca55ed01e51",
        "id": "tn-community",
        "locator": "Description, access steps and service-specific documents",
        "publisher": "Tamil Nadu e-Governance Agency / ServicePlus",
        "summary": "nativity certificate service.",
        "url": "https://serviceonline.gov.in/configuretn/login.do"
      }
    ],
    "fees": null,
    "id": "tn-nativity-certificate",
    "informationUrl": "https://serviceonline.gov.in/configuretn/login.do",
    "level": "State",
    "linkEvidenceIds": [
      "tn-community"
    ],
    "organizationId": "org-05a96313c65f060b",
    "procedure": [
      {
        "evidenceIds": [
          "tn-community"
        ],
        "text": "Register/login on the official service platform and select the relevant service."
      }
    ],
    "processing": null,
    "publicationStatus": "published",
    "reviewNotes": "Official service information reviewed by Codex on 2026-10-16; not an independently completed transaction. The linked organization identifies the existing website operator; the responsible department is listed separately. Unknown fields stay null. Centre-assisted listing; no direct citizen application URL or complete eligibility checklist confirmed. Fees and processing-time columns may differ by service and are not reproduced. Birth printing is not birth registration.",
    "sourceUrl": "https://serviceonline.gov.in/configuretn/login.do",
    "state": "Tamil Nadu",
    "synonyms": [
      "nativity certificate",
      "residence origin"
    ],
    "title": "Nativity Certificate (Tamil Nadu)",
    "verificationStatus": "verified",
    "verifiedOn": "2026-10-16",
    "websiteId": "igod-2715637a8f399700"
  },
  {
    "applicationMode": "assisted",
    "applicationUrl": null,
    "categories": [
      "documents"
    ],
    "citizenIntents": [
      "apply for income certificate"
    ],
    "conditionalDocuments": null,
    "deadline": null,
    "department": "Revenue Administration, Government of Tamil Nadu",
    "description": "Income certification.",
    "district": null,
    "documents": null,
    "eligibility": null,
    "evidence": [
      {
        "checkedOn": "2026-10-16",
        "contentSha256": "119b1d4bb5706be06ae5575a9ff7cce5212462a79351a5e92980974af33203e9",
        "id": "tn",
        "locator": "Named service row in official eSevai centre list",
        "publisher": "Tamil Nadu e-Governance Agency",
        "summary": "income certificate service.",
        "url": "https://www.tnesevai.tn.gov.in/Pages/ServiceList.aspx"
      }
    ],
    "fees": null,
    "id": "tn-income-certificate",
    "informationUrl": "https://www.tnesevai.tn.gov.in/Pages/ServiceList.aspx",
    "level": "State",
    "linkEvidenceIds": [
      "tn"
    ],
    "organizationId": "org-05a96313c65f060b",
    "procedure": null,
    "processing": null,
    "publicationStatus": "published",
    "reviewNotes": "Official service information reviewed by Codex on 2026-10-16; not an independently completed transaction. The linked organization identifies the existing website operator; the responsible department is listed separately. Unknown fields stay null. Centre-assisted listing; no direct citizen application URL or complete eligibility checklist confirmed. Fees and processing-time columns may differ by service and are not reproduced. Birth printing is not birth registration.",
    "sourceUrl": "https://www.tnesevai.tn.gov.in/Pages/ServiceList.aspx",
    "state": "Tamil Nadu",
    "synonyms": [
      "income certificate",
      "earnings proof"
    ],
    "title": "Income Certificate (Tamil Nadu)",
    "verificationStatus": "verified",
    "verifiedOn": "2026-10-16",
    "websiteId": "igod-2715637a8f399700"
  },
  {
    "applicationMode": "assisted",
    "applicationUrl": null,
    "categories": [
      "documents"
    ],
    "citizenIntents": [
      "How do I obtain a birth certificate"
    ],
    "conditionalDocuments": null,
    "deadline": null,
    "department": "Directorate of Public Health, Government of Tamil Nadu",
    "description": "Print an existing birth record.",
    "district": null,
    "documents": null,
    "eligibility": null,
    "evidence": [
      {
        "checkedOn": "2026-10-16",
        "contentSha256": "119b1d4bb5706be06ae5575a9ff7cce5212462a79351a5e92980974af33203e9",
        "id": "tn",
        "locator": "Named service row in official eSevai centre list",
        "publisher": "Tamil Nadu e-Governance Agency",
        "summary": "birth certificate service.",
        "url": "https://www.tnesevai.tn.gov.in/Pages/ServiceList.aspx"
      }
    ],
    "fees": null,
    "id": "tn-birth-certificate-print",
    "informationUrl": "https://www.tnesevai.tn.gov.in/Pages/ServiceList.aspx",
    "level": "State",
    "linkEvidenceIds": [
      "tn"
    ],
    "organizationId": "org-05a96313c65f060b",
    "procedure": null,
    "processing": null,
    "publicationStatus": "published",
    "reviewNotes": "Official service information reviewed by Codex on 2026-10-16; not an independently completed transaction. The linked organization identifies the existing website operator; the responsible department is listed separately. Unknown fields stay null. Centre-assisted listing; no direct citizen application URL or complete eligibility checklist confirmed. Fees and processing-time columns may differ by service and are not reproduced. Birth printing is not birth registration.",
    "sourceUrl": "https://www.tnesevai.tn.gov.in/Pages/ServiceList.aspx",
    "state": "Tamil Nadu",
    "synonyms": [
      "birth certificate",
      "birth record copy",
      "जन्म प्रमाणपत्र"
    ],
    "title": "Printing of Birth Certificate (Tamil Nadu)",
    "verificationStatus": "verified",
    "verifiedOn": "2026-10-16",
    "websiteId": "igod-2715637a8f399700"
  },
  {
    "applicationMode": "assisted",
    "applicationUrl": null,
    "categories": [
      "welfare"
    ],
    "citizenIntents": [
      "I want to apply for a pension"
    ],
    "conditionalDocuments": null,
    "deadline": null,
    "department": "Revenue Administration, Government of Tamil Nadu",
    "description": "Old-age pension application access.",
    "district": null,
    "documents": null,
    "eligibility": null,
    "evidence": [
      {
        "checkedOn": "2026-10-16",
        "contentSha256": "119b1d4bb5706be06ae5575a9ff7cce5212462a79351a5e92980974af33203e9",
        "id": "tn",
        "locator": "Named service row in official eSevai centre list",
        "publisher": "Tamil Nadu e-Governance Agency",
        "summary": "pension service.",
        "url": "https://www.tnesevai.tn.gov.in/Pages/ServiceList.aspx"
      }
    ],
    "fees": null,
    "id": "tn-old-age-pension",
    "informationUrl": "https://www.tnesevai.tn.gov.in/Pages/ServiceList.aspx",
    "level": "State",
    "linkEvidenceIds": [
      "tn"
    ],
    "organizationId": "org-05a96313c65f060b",
    "procedure": null,
    "processing": null,
    "publicationStatus": "published",
    "reviewNotes": "Official service information reviewed by Codex on 2026-10-16; not an independently completed transaction. The linked organization identifies the existing website operator; the responsible department is listed separately. Unknown fields stay null. Centre-assisted listing; no direct citizen application URL or complete eligibility checklist confirmed. Fees and processing-time columns may differ by service and are not reproduced. Birth printing is not birth registration.",
    "sourceUrl": "https://www.tnesevai.tn.gov.in/Pages/ServiceList.aspx",
    "state": "Tamil Nadu",
    "synonyms": [
      "pension",
      "old age pension",
      "senior citizen support"
    ],
    "title": "Indira Gandhi National Old Age Pension Scheme — Tamil Nadu access",
    "verificationStatus": "verified",
    "verifiedOn": "2026-10-16",
    "websiteId": "igod-2715637a8f399700"
  },
  {
    "applicationMode": "assisted",
    "applicationUrl": null,
    "categories": [
      "welfare"
    ],
    "citizenIntents": [
      "I want to apply for a pension"
    ],
    "conditionalDocuments": null,
    "deadline": null,
    "department": "Revenue Administration, Government of Tamil Nadu",
    "description": "Widow pension application access.",
    "district": null,
    "documents": null,
    "eligibility": null,
    "evidence": [
      {
        "checkedOn": "2026-10-16",
        "contentSha256": "119b1d4bb5706be06ae5575a9ff7cce5212462a79351a5e92980974af33203e9",
        "id": "tn",
        "locator": "Named service row in official eSevai centre list",
        "publisher": "Tamil Nadu e-Governance Agency",
        "summary": "widow pension service.",
        "url": "https://www.tnesevai.tn.gov.in/Pages/ServiceList.aspx"
      }
    ],
    "fees": null,
    "id": "tn-widow-pension",
    "informationUrl": "https://www.tnesevai.tn.gov.in/Pages/ServiceList.aspx",
    "level": "State",
    "linkEvidenceIds": [
      "tn"
    ],
    "organizationId": "org-05a96313c65f060b",
    "procedure": null,
    "processing": null,
    "publicationStatus": "published",
    "reviewNotes": "Official service information reviewed by Codex on 2026-10-16; not an independently completed transaction. The linked organization identifies the existing website operator; the responsible department is listed separately. Unknown fields stay null. Centre-assisted listing; no direct citizen application URL or complete eligibility checklist confirmed. Fees and processing-time columns may differ by service and are not reproduced. Birth printing is not birth registration.",
    "sourceUrl": "https://www.tnesevai.tn.gov.in/Pages/ServiceList.aspx",
    "state": "Tamil Nadu",
    "synonyms": [
      "widow pension",
      "pension benefits"
    ],
    "title": "Destitute Widow Pension Scheme (Tamil Nadu)",
    "verificationStatus": "verified",
    "verifiedOn": "2026-10-16",
    "websiteId": "igod-2715637a8f399700"
  },
  {
    "applicationMode": "assisted",
    "applicationUrl": null,
    "categories": [
      "welfare"
    ],
    "citizenIntents": [
      "I want to apply for a pension"
    ],
    "conditionalDocuments": null,
    "deadline": null,
    "department": "Revenue Administration, Government of Tamil Nadu",
    "description": "Disability pension application access.",
    "district": null,
    "documents": null,
    "eligibility": null,
    "evidence": [
      {
        "checkedOn": "2026-10-16",
        "contentSha256": "119b1d4bb5706be06ae5575a9ff7cce5212462a79351a5e92980974af33203e9",
        "id": "tn",
        "locator": "Named service row in official eSevai centre list",
        "publisher": "Tamil Nadu e-Governance Agency",
        "summary": "disability pension service.",
        "url": "https://www.tnesevai.tn.gov.in/Pages/ServiceList.aspx"
      }
    ],
    "fees": null,
    "id": "tn-disability-pension",
    "informationUrl": "https://www.tnesevai.tn.gov.in/Pages/ServiceList.aspx",
    "level": "State",
    "linkEvidenceIds": [
      "tn"
    ],
    "organizationId": "org-05a96313c65f060b",
    "procedure": null,
    "processing": null,
    "publicationStatus": "published",
    "reviewNotes": "Official service information reviewed by Codex on 2026-10-16; not an independently completed transaction. The linked organization identifies the existing website operator; the responsible department is listed separately. Unknown fields stay null. Centre-assisted listing; no direct citizen application URL or complete eligibility checklist confirmed. Fees and processing-time columns may differ by service and are not reproduced. Birth printing is not birth registration.",
    "sourceUrl": "https://www.tnesevai.tn.gov.in/Pages/ServiceList.aspx",
    "state": "Tamil Nadu",
    "synonyms": [
      "disability pension",
      "differently abled benefits"
    ],
    "title": "Differently Abled Pension Scheme (Tamil Nadu)",
    "verificationStatus": "verified",
    "verifiedOn": "2026-10-16",
    "websiteId": "igod-2715637a8f399700"
  },
  {
    "applicationMode": "assisted",
    "applicationUrl": null,
    "categories": [
      "documents"
    ],
    "citizenIntents": [
      "apply for unemployment certificate"
    ],
    "conditionalDocuments": null,
    "deadline": null,
    "department": "Revenue Administration, Government of Tamil Nadu",
    "description": "Unemployment certification.",
    "district": null,
    "documents": null,
    "eligibility": null,
    "evidence": [
      {
        "checkedOn": "2026-10-16",
        "contentSha256": "119b1d4bb5706be06ae5575a9ff7cce5212462a79351a5e92980974af33203e9",
        "id": "tn",
        "locator": "Named service row in official eSevai centre list",
        "publisher": "Tamil Nadu e-Governance Agency",
        "summary": "unemployment certificate service.",
        "url": "https://www.tnesevai.tn.gov.in/Pages/ServiceList.aspx"
      }
    ],
    "fees": null,
    "id": "tn-unemployment-certificate",
    "informationUrl": "https://www.tnesevai.tn.gov.in/Pages/ServiceList.aspx",
    "level": "State",
    "linkEvidenceIds": [
      "tn"
    ],
    "organizationId": "org-05a96313c65f060b",
    "procedure": null,
    "processing": null,
    "publicationStatus": "published",
    "reviewNotes": "Official service information reviewed by Codex on 2026-10-16; not an independently completed transaction. The linked organization identifies the existing website operator; the responsible department is listed separately. Unknown fields stay null. Centre-assisted listing; no direct citizen application URL or complete eligibility checklist confirmed. Fees and processing-time columns may differ by service and are not reproduced. Birth printing is not birth registration.",
    "sourceUrl": "https://www.tnesevai.tn.gov.in/Pages/ServiceList.aspx",
    "state": "Tamil Nadu",
    "synonyms": [
      "unemployment certificate",
      "jobless certificate"
    ],
    "title": "Unemployment Certificate (Tamil Nadu)",
    "verificationStatus": "verified",
    "verifiedOn": "2026-10-16",
    "websiteId": "igod-2715637a8f399700"
  },
  {
    "applicationMode": "assisted",
    "applicationUrl": null,
    "categories": [
      "documents"
    ],
    "citizenIntents": [
      "apply for legal heir certificate"
    ],
    "conditionalDocuments": null,
    "deadline": null,
    "department": "Revenue Administration, Government of Tamil Nadu",
    "description": "Legal heir certification.",
    "district": null,
    "documents": null,
    "eligibility": null,
    "evidence": [
      {
        "checkedOn": "2026-10-16",
        "contentSha256": "119b1d4bb5706be06ae5575a9ff7cce5212462a79351a5e92980974af33203e9",
        "id": "tn",
        "locator": "Named service row in official eSevai centre list",
        "publisher": "Tamil Nadu e-Governance Agency",
        "summary": "legal heir certificate service.",
        "url": "https://www.tnesevai.tn.gov.in/Pages/ServiceList.aspx"
      }
    ],
    "fees": null,
    "id": "tn-legal-heir-certificate",
    "informationUrl": "https://www.tnesevai.tn.gov.in/Pages/ServiceList.aspx",
    "level": "State",
    "linkEvidenceIds": [
      "tn"
    ],
    "organizationId": "org-05a96313c65f060b",
    "procedure": null,
    "processing": null,
    "publicationStatus": "published",
    "reviewNotes": "Official service information reviewed by Codex on 2026-10-16; not an independently completed transaction. The linked organization identifies the existing website operator; the responsible department is listed separately. Unknown fields stay null. Centre-assisted listing; no direct citizen application URL or complete eligibility checklist confirmed. Fees and processing-time columns may differ by service and are not reproduced. Birth printing is not birth registration.",
    "sourceUrl": "https://www.tnesevai.tn.gov.in/Pages/ServiceList.aspx",
    "state": "Tamil Nadu",
    "synonyms": [
      "legal heir certificate",
      "succession proof"
    ],
    "title": "Legal Heir Certificate (Tamil Nadu)",
    "verificationStatus": "verified",
    "verifiedOn": "2026-10-16",
    "websiteId": "igod-2715637a8f399700"
  },
  {
    "applicationMode": "online",
    "applicationUrl": "https://ncs.gov.in/job-listing?isGovernmentJob=true",
    "categories": [
      "jobs"
    ],
    "citizenIntents": [
      "Find government jobs",
      "search govt jobs"
    ],
    "conditionalDocuments": null,
    "deadline": null,
    "department": "Directorate General of Employment, Ministry of Labour & Employment",
    "description": "Find published government-sector job listings; this is not a vacancy or selection guarantee.",
    "district": null,
    "documents": null,
    "eligibility": null,
    "evidence": [
      {
        "checkedOn": "2026-10-16",
        "contentSha256": "0cc583a2e8ad712ff1d9159d8f9a7663bda99efdb9e443d30301ab6662289bd8",
        "id": "ncs",
        "locator": "Jobseeker navigation and general FAQ",
        "publisher": "Directorate General of Employment",
        "summary": "Government-jobs search is linked in jobseeker navigation.",
        "url": "https://ncs.gov.in/faq"
      }
    ],
    "fees": null,
    "id": "ncs-government-job-search",
    "informationUrl": "https://ncs.gov.in/faq",
    "level": "Central",
    "linkEvidenceIds": [
      "ncs"
    ],
    "organizationId": "org-7ed359018f05059a",
    "procedure": [
      {
        "evidenceIds": [
          "ncs"
        ],
        "text": "Open government-sector search and inspect the employer’s vacancy notice."
      }
    ],
    "processing": null,
    "publicationStatus": "published",
    "reviewNotes": "Official service information reviewed by Codex on 2026-10-16; not an independently completed transaction. The linked organization identifies the existing website operator; the responsible department is listed separately. Unknown fields stay null.",
    "sourceUrl": "https://ncs.gov.in/faq",
    "state": null,
    "synonyms": [
      "government jobs",
      "naukri",
      "recruitment vacancies"
    ],
    "title": "Search government-sector jobs on NCS",
    "verificationStatus": "verified",
    "verifiedOn": "2026-10-16",
    "websiteId": "national-career-service"
  },
  {
    "applicationMode": "online",
    "applicationUrl": "https://ncs.gov.in/",
    "categories": [
      "jobs"
    ],
    "citizenIntents": [
      "register as a jobseeker"
    ],
    "conditionalDocuments": null,
    "deadline": null,
    "department": "Directorate General of Employment, Ministry of Labour & Employment",
    "description": "Create a jobseeker profile on the official employment platform.",
    "district": null,
    "documents": null,
    "eligibility": null,
    "evidence": [
      {
        "checkedOn": "2026-10-16",
        "id": "ncs-register",
        "locator": "One-page jobseeker registration flowchart",
        "publisher": "Directorate General of Employment",
        "summary": "Official jobseeker registration flowchart.",
        "url": "https://ncs.gov.in/assets/pdf/Jobseeker%20registration%20flowchart%201.pdf"
      },
      {
        "checkedOn": "2026-10-16",
        "contentSha256": "0cc583a2e8ad712ff1d9159d8f9a7663bda99efdb9e443d30301ab6662289bd8",
        "id": "ncs",
        "locator": "Jobseeker navigation and general FAQ",
        "publisher": "Directorate General of Employment",
        "summary": "Jobseeker registration is provided.",
        "url": "https://ncs.gov.in/faq"
      }
    ],
    "fees": null,
    "id": "ncs-jobseeker-registration",
    "informationUrl": "https://ncs.gov.in/assets/pdf/Jobseeker%20registration%20flowchart%201.pdf",
    "level": "Central",
    "linkEvidenceIds": [
      "ncs-register",
      "ncs"
    ],
    "organizationId": "org-7ed359018f05059a",
    "procedure": [
      {
        "evidenceIds": [
          "ncs-register"
        ],
        "text": "Choose jobseeker registration; follow the official flowchart."
      }
    ],
    "processing": null,
    "publicationStatus": "published",
    "reviewNotes": "Official service information reviewed by Codex on 2026-10-16; not an independently completed transaction. The linked organization identifies the existing website operator; the responsible department is listed separately. Unknown fields stay null.",
    "sourceUrl": "https://ncs.gov.in/assets/pdf/Jobseeker%20registration%20flowchart%201.pdf",
    "state": null,
    "synonyms": [
      "employment registration",
      "ncs account"
    ],
    "title": "NCS Jobseeker Registration",
    "verificationStatus": "verified",
    "verifiedOn": "2026-10-16",
    "websiteId": "national-career-service"
  },
  {
    "applicationMode": "online",
    "applicationUrl": "https://ncs.gov.in/career-counsellors",
    "categories": [
      "jobs"
    ],
    "citizenIntents": [
      "find career counselling"
    ],
    "conditionalDocuments": null,
    "deadline": null,
    "department": "Directorate General of Employment, Ministry of Labour & Employment",
    "description": "Explore official career-counselling access.",
    "district": null,
    "documents": null,
    "eligibility": null,
    "evidence": [
      {
        "checkedOn": "2026-10-16",
        "contentSha256": "0cc583a2e8ad712ff1d9159d8f9a7663bda99efdb9e443d30301ab6662289bd8",
        "id": "ncs",
        "locator": "Jobseeker navigation and general FAQ",
        "publisher": "Directorate General of Employment",
        "summary": "Counsellor search is linked under jobseeker support.",
        "url": "https://ncs.gov.in/faq"
      }
    ],
    "fees": null,
    "id": "ncs-career-counselling",
    "informationUrl": "https://ncs.gov.in/faq",
    "level": "Central",
    "linkEvidenceIds": [
      "ncs"
    ],
    "organizationId": "org-7ed359018f05059a",
    "procedure": null,
    "processing": null,
    "publicationStatus": "published",
    "reviewNotes": "Official service information reviewed by Codex on 2026-10-16; not an independently completed transaction. The linked organization identifies the existing website operator; the responsible department is listed separately. Unknown fields stay null.",
    "sourceUrl": "https://ncs.gov.in/faq",
    "state": null,
    "synonyms": [
      "career advice",
      "counsellor"
    ],
    "title": "Find a Career Counsellor through NCS",
    "verificationStatus": "verified",
    "verifiedOn": "2026-10-16",
    "websiteId": "national-career-service"
  },
  {
    "applicationMode": "information",
    "applicationUrl": null,
    "categories": [
      "documents"
    ],
    "citizenIntents": [
      "download aadhaar",
      "e aadhaar"
    ],
    "conditionalDocuments": null,
    "deadline": null,
    "department": "Unique Identification Authority of India",
    "description": "Retrieve an electronic Aadhaar copy.",
    "district": null,
    "documents": null,
    "eligibility": {
      "evidenceIds": [
        "uidai"
      ],
      "text": "For Aadhaar holders; the listed online flow requires mobile OTP."
    },
    "evidence": [
      {
        "checkedOn": "2026-10-16",
        "contentSha256": "601897a311551ba4c0ff2bdf52a6cb4c802efc02298e5571aa0393ff70f2c4be",
        "id": "uidai",
        "locator": "Named My Aadhaar service card; access requires official authentication",
        "publisher": "UIDAI",
        "summary": "download aadhaar is listed.",
        "url": "https://www.uidai.gov.in/en/my-aadhaar"
      }
    ],
    "fees": null,
    "id": "aadhaar-download",
    "informationUrl": "https://www.uidai.gov.in/en/my-aadhaar",
    "level": "Central",
    "linkEvidenceIds": [
      "uidai"
    ],
    "organizationId": "org-e999f31ac201d555",
    "procedure": null,
    "processing": null,
    "publicationStatus": "published",
    "reviewNotes": "Official service information reviewed by Codex on 2026-10-16; not an independently completed transaction. The linked organization identifies the existing website operator; the responsible department is listed separately. Unknown fields stay null. UIDAI currently links a beta-branded service host. Direct application endpoint is held for route review; open the official information page and use its current service link. No OTP is entered into JanSetu.",
    "sourceUrl": "https://www.uidai.gov.in/en/my-aadhaar",
    "state": null,
    "synonyms": [
      "download aadhaar",
      "e aadhaar"
    ],
    "title": "Download Aadhaar",
    "verificationStatus": "verified",
    "verifiedOn": "2026-10-16",
    "websiteId": "uidai"
  },
  {
    "applicationMode": "information",
    "applicationUrl": null,
    "categories": [
      "documents"
    ],
    "citizenIntents": [
      "aadhaar address update",
      "change address"
    ],
    "conditionalDocuments": null,
    "deadline": null,
    "department": "Unique Identification Authority of India",
    "description": "Request an address change.",
    "district": null,
    "documents": null,
    "eligibility": {
      "evidenceIds": [
        "uidai"
      ],
      "text": "For Aadhaar holders; the listed online flow requires mobile OTP."
    },
    "evidence": [
      {
        "checkedOn": "2026-10-16",
        "contentSha256": "601897a311551ba4c0ff2bdf52a6cb4c802efc02298e5571aa0393ff70f2c4be",
        "id": "uidai",
        "locator": "Named My Aadhaar service card; access requires official authentication",
        "publisher": "UIDAI",
        "summary": "aadhaar address update is listed.",
        "url": "https://www.uidai.gov.in/en/my-aadhaar"
      }
    ],
    "fees": null,
    "id": "aadhaar-address-update",
    "informationUrl": "https://www.uidai.gov.in/en/my-aadhaar",
    "level": "Central",
    "linkEvidenceIds": [
      "uidai"
    ],
    "organizationId": "org-e999f31ac201d555",
    "procedure": null,
    "processing": null,
    "publicationStatus": "published",
    "reviewNotes": "Official service information reviewed by Codex on 2026-10-16; not an independently completed transaction. The linked organization identifies the existing website operator; the responsible department is listed separately. Unknown fields stay null. UIDAI currently links a beta-branded service host. Direct application endpoint is held for route review; open the official information page and use its current service link. No OTP is entered into JanSetu.",
    "sourceUrl": "https://www.uidai.gov.in/en/my-aadhaar",
    "state": null,
    "synonyms": [
      "aadhaar address update",
      "change address"
    ],
    "title": "Aadhaar Address Update",
    "verificationStatus": "verified",
    "verifiedOn": "2026-10-16",
    "websiteId": "uidai"
  },
  {
    "applicationMode": "information",
    "applicationUrl": null,
    "categories": [
      "documents"
    ],
    "citizenIntents": [
      "aadhaar pvc card",
      "order aadhaar"
    ],
    "conditionalDocuments": null,
    "deadline": null,
    "department": "Unique Identification Authority of India",
    "description": "Request a durable Aadhaar card.",
    "district": null,
    "documents": null,
    "eligibility": {
      "evidenceIds": [
        "uidai"
      ],
      "text": "For Aadhaar holders; the listed online flow requires mobile OTP."
    },
    "evidence": [
      {
        "checkedOn": "2026-10-16",
        "contentSha256": "601897a311551ba4c0ff2bdf52a6cb4c802efc02298e5571aa0393ff70f2c4be",
        "id": "uidai",
        "locator": "Named My Aadhaar service card; access requires official authentication",
        "publisher": "UIDAI",
        "summary": "aadhaar pvc card is listed.",
        "url": "https://www.uidai.gov.in/en/my-aadhaar"
      }
    ],
    "fees": null,
    "id": "aadhaar-pvc-card",
    "informationUrl": "https://www.uidai.gov.in/en/my-aadhaar",
    "level": "Central",
    "linkEvidenceIds": [
      "uidai"
    ],
    "organizationId": "org-e999f31ac201d555",
    "procedure": null,
    "processing": null,
    "publicationStatus": "published",
    "reviewNotes": "Official service information reviewed by Codex on 2026-10-16; not an independently completed transaction. The linked organization identifies the existing website operator; the responsible department is listed separately. Unknown fields stay null. UIDAI currently links a beta-branded service host. Direct application endpoint is held for route review; open the official information page and use its current service link. No OTP is entered into JanSetu.",
    "sourceUrl": "https://www.uidai.gov.in/en/my-aadhaar",
    "state": null,
    "synonyms": [
      "aadhaar pvc card",
      "order aadhaar"
    ],
    "title": "Order Aadhaar PVC Card",
    "verificationStatus": "verified",
    "verifiedOn": "2026-10-16",
    "websiteId": "uidai"
  },
  {
    "applicationMode": "information",
    "applicationUrl": null,
    "categories": [
      "documents"
    ],
    "citizenIntents": [
      "aadhaar biometric lock",
      "unlock fingerprint"
    ],
    "conditionalDocuments": null,
    "deadline": null,
    "department": "Unique Identification Authority of India",
    "description": "Manage biometric authentication locking.",
    "district": null,
    "documents": null,
    "eligibility": {
      "evidenceIds": [
        "uidai"
      ],
      "text": "For Aadhaar holders; the listed online flow requires mobile OTP."
    },
    "evidence": [
      {
        "checkedOn": "2026-10-16",
        "contentSha256": "601897a311551ba4c0ff2bdf52a6cb4c802efc02298e5571aa0393ff70f2c4be",
        "id": "uidai",
        "locator": "Named My Aadhaar service card; access requires official authentication",
        "publisher": "UIDAI",
        "summary": "aadhaar biometric lock is listed.",
        "url": "https://www.uidai.gov.in/en/my-aadhaar"
      }
    ],
    "fees": null,
    "id": "aadhaar-biometric-lock",
    "informationUrl": "https://www.uidai.gov.in/en/my-aadhaar",
    "level": "Central",
    "linkEvidenceIds": [
      "uidai"
    ],
    "organizationId": "org-e999f31ac201d555",
    "procedure": null,
    "processing": null,
    "publicationStatus": "published",
    "reviewNotes": "Official service information reviewed by Codex on 2026-10-16; not an independently completed transaction. The linked organization identifies the existing website operator; the responsible department is listed separately. Unknown fields stay null. UIDAI currently links a beta-branded service host. Direct application endpoint is held for route review; open the official information page and use its current service link. No OTP is entered into JanSetu.",
    "sourceUrl": "https://www.uidai.gov.in/en/my-aadhaar",
    "state": null,
    "synonyms": [
      "aadhaar biometric lock",
      "unlock fingerprint"
    ],
    "title": "Lock or Unlock Aadhaar Biometrics",
    "verificationStatus": "verified",
    "verifiedOn": "2026-10-16",
    "websiteId": "uidai"
  },
  {
    "applicationMode": "online",
    "applicationUrl": "https://pgportal.gov.in/Home/LodgeGrievance",
    "categories": [
      "grievances"
    ],
    "citizenIntents": [
      "file a complaint",
      "lodge grievance"
    ],
    "conditionalDocuments": null,
    "deadline": null,
    "department": "Department of Administrative Reforms and Public Grievances",
    "description": "Submit a grievance to the relevant public authority.",
    "district": null,
    "documents": null,
    "eligibility": {
      "evidenceIds": [
        "cpgrams"
      ],
      "text": "Excluded matters include RTI requests, court/subjudice matters, and personal/family disputes."
    },
    "evidence": [
      {
        "checkedOn": "2026-10-16",
        "contentSha256": "aae350bbde0fda78465e546099e7a2f9bf1cc4bd16941abe7c2faf089197e0da",
        "id": "cpgrams",
        "locator": "FAQ 3, 10 and 15",
        "publisher": "DARPG",
        "summary": "Grievance submission, exclusions and appeal route.",
        "url": "https://pgportal.gov.in/Home/Faq"
      }
    ],
    "fees": null,
    "id": "cpgrams-public-grievance",
    "informationUrl": "https://pgportal.gov.in/Home/Faq",
    "level": "Central",
    "linkEvidenceIds": [
      "cpgrams"
    ],
    "organizationId": "org-afff511c4bead63d",
    "procedure": null,
    "processing": null,
    "publicationStatus": "published",
    "reviewNotes": "Official service information reviewed by Codex on 2026-10-16; not an independently completed transaction. The linked organization identifies the existing website operator; the responsible department is listed separately. Unknown fields stay null.",
    "sourceUrl": "https://pgportal.gov.in/Home/Faq",
    "state": null,
    "synonyms": [
      "file a complaint",
      "lodge grievance"
    ],
    "title": "Lodge a Public Grievance through CPGRAMS",
    "verificationStatus": "verified",
    "verifiedOn": "2026-10-16",
    "websiteId": "cpgrams"
  },
  {
    "applicationMode": "online",
    "applicationUrl": "https://pgportal.gov.in/",
    "categories": [
      "grievances"
    ],
    "citizenIntents": [
      "appeal grievance",
      "unsatisfied complaint"
    ],
    "conditionalDocuments": null,
    "deadline": null,
    "department": "Department of Administrative Reforms and Public Grievances",
    "description": "Use the appeal process for eligible disposed central-government grievances.",
    "district": null,
    "documents": null,
    "eligibility": {
      "evidenceIds": [
        "cpgrams"
      ],
      "text": "The central grievance appeal provision follows mandatory feedback on disposal; see the official FAQ."
    },
    "evidence": [
      {
        "checkedOn": "2026-10-16",
        "contentSha256": "aae350bbde0fda78465e546099e7a2f9bf1cc4bd16941abe7c2faf089197e0da",
        "id": "cpgrams",
        "locator": "FAQ 3, 10 and 15",
        "publisher": "DARPG",
        "summary": "Grievance submission, exclusions and appeal route.",
        "url": "https://pgportal.gov.in/Home/Faq"
      }
    ],
    "fees": null,
    "id": "cpgrams-grievance-appeal",
    "informationUrl": "https://pgportal.gov.in/Home/Faq",
    "level": "Central",
    "linkEvidenceIds": [
      "cpgrams"
    ],
    "organizationId": "org-afff511c4bead63d",
    "procedure": null,
    "processing": null,
    "publicationStatus": "published",
    "reviewNotes": "Official service information reviewed by Codex on 2026-10-16; not an independently completed transaction. The linked organization identifies the existing website operator; the responsible department is listed separately. Unknown fields stay null.",
    "sourceUrl": "https://pgportal.gov.in/Home/Faq",
    "state": null,
    "synonyms": [
      "appeal grievance",
      "unsatisfied complaint"
    ],
    "title": "Appeal a Dissatisfied CPGRAMS Grievance",
    "verificationStatus": "verified",
    "verifiedOn": "2026-10-16",
    "websiteId": "cpgrams"
  },
  {
    "applicationMode": "online",
    "applicationUrl": "https://rtionline.gov.in/",
    "categories": [
      "transparency"
    ],
    "citizenIntents": [
      "right to information",
      "rti request"
    ],
    "conditionalDocuments": [
      {
        "evidenceIds": [
          "rti",
          "rti-rules-2012"
        ],
        "text": "If you are below the poverty line: a copy of the BPL certificate issued by the appropriate government, attached with the application, to be exempt from the fee."
      }
    ],
    "deadline": null,
    "department": "Department of Personnel and Training",
    "description": "Request government information.",
    "district": null,
    "documents": null,
    "eligibility": {
      "evidenceIds": [
        "rti"
      ],
      "text": "Indian citizens requesting information from covered Government of India ministries/departments."
    },
    "evidence": [
      {
        "checkedOn": "2026-10-18",
        "contentSha256": "f0ede7ab2e561ca32567c638a087a2ba4d6255955ca00acd42efa726bc1086ca",
        "id": "rti",
        "locator": "Guidelines page: \"Do not upload Aadhar Card or PAN Card…\"; payment modes (a)–(d); \"After making payment… 24-48 working hours\"; \"No RTI fee… below poverty line\"; \"Submit First Appeal\"",
        "publisher": "Department of Personnel and Training",
        "summary": "RTI Online guidelines for requests to central public authorities: attachments, payment modes, payment reconciliation, the BPL fee exemption and the first appeal.",
        "url": "https://rtionline.gov.in/guidelines.php?request"
      },
      {
        "checkedOn": "2026-10-18",
        "contentSha256": "8d73989088febe131299d4b9a2f17b83002a576fab15084882c8f30d3424bc20",
        "id": "rti-rules-2012",
        "locator": "Rules 3 and 5",
        "publisher": "Central Information Commission (published copy of the Right to Information Rules, 2012, G.S.R. 603(E))",
        "summary": "Rule 3: a request is accompanied by a fee of rupees ten. Rule 5: no fee under rules 3 and 4 for a person below the poverty line who submits a copy of the appropriate Government's certificate with the application.",
        "url": "https://cic.gov.in/sites/default/files/RTI/RTIRules2012.pdf"
      }
    ],
    "fees": {
      "evidenceIds": [
        "rti",
        "rti-rules-2012"
      ],
      "text": "₹10 per request (Right to Information Rules, 2012, rule 3). RTI Online accepts internet banking, Mastercard or Visa credit and debit cards, UPI and RuPay cards. Applicants below the poverty line pay no fee if they attach a copy of their BPL certificate (rule 5)."
    },
    "id": "rti-central-request",
    "informationUrl": "https://rtionline.gov.in/guidelines.php?request",
    "level": "Central",
    "linkEvidenceIds": [
      "rti"
    ],
    "organizationId": "org-7f72318d5949adac",
    "procedure": [
      {
        "evidenceIds": [
          "rti"
        ],
        "text": "Do not upload an Aadhaar card, PAN card or any other personal identification. The only exception is a BPL card when you claim the fee exemption."
      },
      {
        "evidenceIds": [
          "rti"
        ],
        "text": "If you have paid but no registration number appears, wait 24–48 working hours while the payment is reconciled, and do not pay again. If it has still not appeared, email the RTI Online helpdesk (helprtionline-dopt[at]nic[dot]in) with the transaction details."
      }
    ],
    "processing": null,
    "publicationStatus": "published",
    "reviewNotes": "Official guidance re-checked on 2026-10-18 by Claude Code (automated agent) under the project owner's Phase 17 authorisation, against the RTI Online guidelines and the Right to Information Rules, 2012 (earlier check by Codex on 2026-10-16). Not a human editorial review and not a completed transaction. Applies to requests to central public authorities through RTI Online; state public authorities follow their own RTI rules. The linked organization identifies the existing website operator; the responsible department is listed separately. Unknown fields stay null.",
    "sourceUrl": "https://rtionline.gov.in/guidelines.php?request",
    "state": null,
    "synonyms": [
      "right to information",
      "rti request"
    ],
    "title": "Submit an RTI Request to a Central Public Authority",
    "verificationStatus": "verified",
    "verifiedOn": "2026-10-18",
    "websiteId": "rti-online"
  },
  {
    "applicationMode": "online",
    "applicationUrl": "https://rtionline.gov.in/",
    "categories": [
      "transparency"
    ],
    "citizenIntents": [
      "rti first appeal",
      "information appeal"
    ],
    "conditionalDocuments": null,
    "deadline": null,
    "department": "Department of Personnel and Training",
    "description": "Appeal an online RTI request.",
    "district": null,
    "documents": null,
    "eligibility": {
      "evidenceIds": [
        "rti"
      ],
      "text": "Indian citizens requesting information from covered Government of India ministries/departments."
    },
    "evidence": [
      {
        "checkedOn": "2026-10-16",
        "contentSha256": "09c1522a2577441cdc2fd9ec1734271453e7f0f7fcf4b363fe91d2a3d2326888",
        "id": "rti",
        "locator": "Guidelines 1–2, 6, 12, 16–18",
        "publisher": "Department of Personnel and Training",
        "summary": "Online central RTI request and first-appeal instructions.",
        "url": "https://rtionline.gov.in/guidelines.php?request"
      }
    ],
    "fees": {
      "evidenceIds": [
        "rti"
      ],
      "text": "No fee for the first appeal."
    },
    "id": "rti-first-appeal",
    "informationUrl": "https://rtionline.gov.in/guidelines.php?request",
    "level": "Central",
    "linkEvidenceIds": [
      "rti"
    ],
    "organizationId": "org-7f72318d5949adac",
    "procedure": [
      {
        "evidenceIds": [
          "rti"
        ],
        "text": "Choose Submit First Appeal and reference the original registration number."
      }
    ],
    "processing": null,
    "publicationStatus": "published",
    "reviewNotes": "Official service information reviewed by Codex on 2026-10-16; not an independently completed transaction. The linked organization identifies the existing website operator; the responsible department is listed separately. Unknown fields stay null.",
    "sourceUrl": "https://rtionline.gov.in/guidelines.php?request",
    "state": null,
    "synonyms": [
      "rti first appeal",
      "information appeal"
    ],
    "title": "Submit an RTI First Appeal",
    "verificationStatus": "verified",
    "verifiedOn": "2026-10-16",
    "websiteId": "rti-online"
  },
  {
    "applicationMode": "online",
    "applicationUrl": "https://udyamregistration.gov.in/",
    "categories": [
      "business"
    ],
    "citizenIntents": [
      "register my small business",
      "msme registration"
    ],
    "conditionalDocuments": null,
    "deadline": null,
    "department": "Ministry of Micro, Small and Medium Enterprises",
    "description": "Register an eligible micro, small or medium enterprise.",
    "district": null,
    "documents": [
      {
        "evidenceIds": [
          "udyam"
        ],
        "text": "Registration requires no document uploads; Aadhaar and applicable PAN/GST details are handled by the official portal."
      }
    ],
    "eligibility": null,
    "evidence": [
      {
        "checkedOn": "2026-10-16",
        "id": "udyam",
        "locator": "New registration, Must Follow and Must Know sections",
        "publisher": "Ministry of MSME",
        "summary": "Free paperless registration and official new-registration route.",
        "url": "https://udyamregistration.gov.in/"
      }
    ],
    "fees": {
      "evidenceIds": [
        "udyam"
      ],
      "text": "Registration is free."
    },
    "id": "udyam-new-registration",
    "informationUrl": "https://udyamregistration.gov.in/",
    "level": "Central",
    "linkEvidenceIds": [
      "udyam"
    ],
    "organizationId": "org-caf4f9732b6768f4",
    "procedure": null,
    "processing": null,
    "publicationStatus": "published",
    "reviewNotes": "Official service information reviewed by Codex on 2026-10-16; not an independently completed transaction. The linked organization identifies the existing website operator; the responsible department is listed separately. Unknown fields stay null.",
    "sourceUrl": "https://udyamregistration.gov.in/",
    "state": null,
    "synonyms": [
      "udyam",
      "business registration"
    ],
    "title": "Udyam Registration for a New MSME",
    "verificationStatus": "verified",
    "verifiedOn": "2026-10-16",
    "websiteId": "udyam-registration"
  },
  {
    "applicationMode": "information",
    "applicationRouteEvidence": {
      "checkedOn": "2026-10-16",
      "evidenceIds": [
        "pmegp-route"
      ],
      "notes": "Canonical homepage responds, but fresh-intake notice says submission is unavailable. A reachable homepage is not an active application route.",
      "status": "unconfirmed"
    },
    "applicationUrl": null,
    "categories": [
      "business"
    ],
    "citizenIntents": [
      "I need a subsidy for my small business",
      "financial support for new micro enterprise"
    ],
    "conditionalDocuments": null,
    "deadline": null,
    "department": "Ministry of MSME / Khadi and Village Industries Commission",
    "description": "Bank-linked margin-money subsidy for eligible new non-farm micro enterprises.",
    "district": null,
    "documents": null,
    "eligibility": null,
    "evidence": [
      {
        "checkedOn": "2026-10-16",
        "id": "pmegp",
        "locator": "23 March 2026 official PMEGP release",
        "publisher": "Press Information Bureau / Ministry of MSME",
        "summary": "Official release confirms the enterprise-support programme.",
        "url": "https://www.pib.gov.in/newsite/archiveContent.aspx?lang=2&reg=48&relid=286631"
      },
      {
        "checkedOn": "2026-10-16",
        "contentSha256": "55f14d849524195ade2c25f5363da80304980199297e6c576db19f70c8aa5874",
        "id": "pmegp-route",
        "locator": "Homepage; fresh-intake notice in public JavaScript (window.NewApplicationAccept)",
        "publisher": "PMEGP / Ministry of MSME",
        "summary": "Canonical portal and public intake notice; application availability must be rechecked.",
        "url": "https://common-pmegp.msme.gov.in/"
      }
    ],
    "fees": null,
    "id": "pmegp-enterprise-support",
    "informationUrl": "https://common-pmegp.msme.gov.in/",
    "level": "Central",
    "linkEvidenceIds": [
      "pmegp-route"
    ],
    "organizationId": "org-5763d05f09e5a8be",
    "procedure": null,
    "processing": null,
    "publicationStatus": "published",
    "reviewNotes": "Official service information reviewed by Codex on 2026-10-16; not an independently completed transaction. The linked organization identifies the existing website operator; the responsible department is listed separately. Unknown fields stay null. Information-only record. A candidate application host failed TLS verification on this machine, so no application button is published. Current eligibility, documents, funding window and route require further review. Phase 3C: the canonical non-www host responds with valid TLS. Its public fresh-intake notice says new submissions are unavailable pending approval of continuation/revised guidelines. The www host still fails TLS. No application button or open-window claim is published.",
    "sourceUrl": "https://common-pmegp.msme.gov.in/",
    "state": null,
    "synonyms": [
      "pmegp",
      "business subsidy",
      "small business subsidy"
    ],
    "title": "Prime Minister’s Employment Generation Programme (PMEGP)",
    "verificationStatus": "verified",
    "verifiedOn": "2026-10-16",
    "websiteId": "igod-1d591544b1c2e25d"
  },
  {
    "applicationMode": "online",
    "applicationRouteEvidence": {
      "checkedOn": "2026-10-16",
      "entryReviewed": true,
      "evidenceIds": [
        "delhi-income",
        "delhi-list"
      ],
      "finalUrl": "https://edistrict.delhigovt.nic.in/Account/Login",
      "httpStatus": 200,
      "linkedFromSource": true,
      "notes": "Normal TLS public login entry reviewed. This opens the shared official sign-in, not a prefilled service form. Authentication, service selection, acceptance and submission were not tested; availability can change.",
      "redirectReviewed": false,
      "status": "confirmed",
      "tlsVerified": true,
      "url": "https://edistrict.delhigovt.nic.in/Account/Login"
    },
    "applicationUrl": "https://edistrict.delhigovt.nic.in/Account/Login",
    "beneficiaryGroups": null,
    "categories": [
      "documents"
    ],
    "citizenIntents": [
      "get income certificate in Delhi",
      "Delhi income certificate"
    ],
    "conditionalDocuments": [
      {
        "evidenceIds": [
          "delhi-income"
        ],
        "text": "An earlier income certificate is optional; BPL/NFS evidence applies where available."
      }
    ],
    "deadline": null,
    "department": "Revenue Department, Government of NCT of Delhi",
    "description": "Official information about delhi income certificate.",
    "district": null,
    "documents": [
      {
        "evidenceIds": [
          "delhi-income"
        ],
        "text": "Identity and address evidence from the guideline alternatives; family-income evidence and applicable expenditure evidence."
      },
      {
        "evidenceIds": [
          "delhi-income"
        ],
        "text": "Signed self-declaration and a recent passport-size photograph."
      }
    ],
    "eligibility": {
      "evidenceIds": [
        "delhi-income"
      ],
      "text": "Selected conditions: an Indian individual permanently resident in NCT Delhi; confirm the current form and full conditions."
    },
    "evidence": [
      {
        "checkedOn": "2026-10-16",
        "contentSha256": "a55af811391026adbc1aef7cdc5ed565bd9bc74e8a07ce5e06347b2902210650",
        "id": "delhi-income",
        "locator": "Certificate guideline: eligibility, mandatory/conditional documents and important notes",
        "publisher": "Revenue Department, Government of NCT of Delhi",
        "summary": "Selected certificate requirements and conditional evidence.",
        "url": "https://edistrict.delhigovt.nic.in/Public/ViewUploadScanDocumentTemplate?q=riKGp6qbpouao2hGbLw4dJUtjAFtwDFpZutrIuogId0="
      },
      {
        "checkedOn": "2026-10-16",
        "contentSha256": "b73bb65c3354c6d2352ba7bb8ec8a5b1b69824050b86f807bec11c85047930b3",
        "id": "delhi-list",
        "locator": "Guidelines & Eligibility Criteria, Revenue and Social Welfare rows",
        "publisher": "eDistrict Delhi / Revenue Department",
        "summary": "Service names, departments and public sign-in links.",
        "url": "https://edistrict.delhigovt.nic.in/in/en/Public/ServiceGuideline.html"
      }
    ],
    "fees": null,
    "id": "dl-income-certificate",
    "informationUrl": "https://edistrict.delhigovt.nic.in/Public/ViewUploadScanDocumentTemplate?q=riKGp6qbpouao2hGbLw4dJUtjAFtwDFpZutrIuogId0=",
    "level": "State",
    "linkEvidenceIds": [
      "delhi-income",
      "delhi-list"
    ],
    "organizationId": "org-f766679368dbf7d4",
    "procedure": [
      {
        "evidenceIds": [
          "delhi-income"
        ],
        "text": "Use the official citizen login/application service, select the certificate and follow the current form. Document verification or an original declaration may also be required."
      }
    ],
    "processing": null,
    "publicationStatus": "published",
    "reviewNotes": "Source reviewed by Codex on 2026-10-16. Selected facts only; unknown fields remain null. Document lists are partial and case-dependent. The website operator and responsible department are recorded separately. No transaction was submitted. ",
    "sourceUrl": "https://edistrict.delhigovt.nic.in/Public/ViewUploadScanDocumentTemplate?q=riKGp6qbpouao2hGbLw4dJUtjAFtwDFpZutrIuogId0=",
    "state": "Delhi",
    "synonyms": [
      "income certificate",
      "Delhi certificate"
    ],
    "title": "Delhi Income Certificate",
    "verificationStatus": "verified",
    "verifiedOn": "2026-10-16",
    "websiteId": "igod-9908672336cdd912"
  },
  {
    "applicationMode": "online",
    "applicationRouteEvidence": {
      "checkedOn": "2026-10-16",
      "entryReviewed": true,
      "evidenceIds": [
        "delhi-domicile",
        "delhi-list"
      ],
      "finalUrl": "https://edistrict.delhigovt.nic.in/Account/Login",
      "httpStatus": 200,
      "linkedFromSource": true,
      "notes": "Normal TLS public login entry reviewed. This opens the shared official sign-in, not a prefilled service form. Authentication, service selection, acceptance and submission were not tested; availability can change.",
      "redirectReviewed": false,
      "status": "confirmed",
      "tlsVerified": true,
      "url": "https://edistrict.delhigovt.nic.in/Account/Login"
    },
    "applicationUrl": "https://edistrict.delhigovt.nic.in/Account/Login",
    "beneficiaryGroups": null,
    "categories": [
      "documents"
    ],
    "citizenIntents": [
      "get domicile certificate in Delhi",
      "Delhi domicile certificate"
    ],
    "conditionalDocuments": [
      {
        "evidenceIds": [
          "delhi-domicile"
        ],
        "text": "For a minor, the guideline lists parent identity and school/birth evidence alternatives."
      }
    ],
    "deadline": null,
    "department": "Revenue Department, Government of NCT of Delhi",
    "description": "Official information about delhi domicile certificate.",
    "district": null,
    "documents": [
      {
        "evidenceIds": [
          "delhi-domicile"
        ],
        "text": "Identity evidence, proof of three years of residence, self-declaration and a photograph."
      }
    ],
    "eligibility": {
      "evidenceIds": [
        "delhi-domicile"
      ],
      "text": "Selected conditions: Indian citizenship and continuous residence in Delhi for the preceding three years; see the guideline for minor applicants."
    },
    "evidence": [
      {
        "checkedOn": "2026-10-16",
        "contentSha256": "01e895d37e83e95091d6cf77b6b4b4d0e139da75cb3e5a505292eb8ada1c08b3",
        "id": "delhi-domicile",
        "locator": "Certificate guideline: eligibility, mandatory/conditional documents and important notes",
        "publisher": "Revenue Department, Government of NCT of Delhi",
        "summary": "Selected certificate requirements and conditional evidence.",
        "url": "https://edistrict.delhigovt.nic.in/Public/ViewUploadScanDocumentTemplate?q=GDX0anvCbFI+Ydkd0X4TfOMzCufU1wuuYbY+d/MGkho="
      },
      {
        "checkedOn": "2026-10-16",
        "contentSha256": "b73bb65c3354c6d2352ba7bb8ec8a5b1b69824050b86f807bec11c85047930b3",
        "id": "delhi-list",
        "locator": "Guidelines & Eligibility Criteria, Revenue and Social Welfare rows",
        "publisher": "eDistrict Delhi / Revenue Department",
        "summary": "Service names, departments and public sign-in links.",
        "url": "https://edistrict.delhigovt.nic.in/in/en/Public/ServiceGuideline.html"
      }
    ],
    "fees": null,
    "id": "dl-domicile-certificate",
    "informationUrl": "https://edistrict.delhigovt.nic.in/Public/ViewUploadScanDocumentTemplate?q=GDX0anvCbFI+Ydkd0X4TfOMzCufU1wuuYbY+d/MGkho=",
    "level": "State",
    "linkEvidenceIds": [
      "delhi-domicile",
      "delhi-list"
    ],
    "organizationId": "org-f766679368dbf7d4",
    "procedure": [
      {
        "evidenceIds": [
          "delhi-domicile"
        ],
        "text": "Use the official citizen login/application service, select the certificate and follow the current form. Document verification or an original declaration may also be required."
      }
    ],
    "processing": null,
    "publicationStatus": "published",
    "reviewNotes": "Source reviewed by Codex on 2026-10-16. Selected facts only; unknown fields remain null. Document lists are partial and case-dependent. The website operator and responsible department are recorded separately. No transaction was submitted. ",
    "sourceUrl": "https://edistrict.delhigovt.nic.in/Public/ViewUploadScanDocumentTemplate?q=GDX0anvCbFI+Ydkd0X4TfOMzCufU1wuuYbY+d/MGkho=",
    "state": "Delhi",
    "synonyms": [
      "domicile certificate",
      "Delhi certificate"
    ],
    "title": "Delhi Domicile Certificate",
    "verificationStatus": "verified",
    "verifiedOn": "2026-10-16",
    "websiteId": "igod-9908672336cdd912"
  },
  {
    "applicationMode": "online",
    "applicationRouteEvidence": {
      "checkedOn": "2026-10-16",
      "entryReviewed": true,
      "evidenceIds": [
        "delhi-obc",
        "delhi-list"
      ],
      "finalUrl": "https://edistrict.delhigovt.nic.in/Account/Login",
      "httpStatus": 200,
      "linkedFromSource": true,
      "notes": "Normal TLS public login entry reviewed. This opens the shared official sign-in, not a prefilled service form. Authentication, service selection, acceptance and submission were not tested; availability can change.",
      "redirectReviewed": false,
      "status": "confirmed",
      "tlsVerified": true,
      "url": "https://edistrict.delhigovt.nic.in/Account/Login"
    },
    "applicationUrl": "https://edistrict.delhigovt.nic.in/Account/Login",
    "beneficiaryGroups": [
      {
        "evidenceIds": [
          "delhi-obc"
        ],
        "text": "Other Backward Classes (OBC)"
      }
    ],
    "categories": [
      "documents"
    ],
    "citizenIntents": [
      "get obc certificate in Delhi",
      "Delhi obc certificate"
    ],
    "conditionalDocuments": [
      {
        "evidenceIds": [
          "delhi-obc"
        ],
        "text": "Delhi/Central OBC and migration cases have different criteria and evidence. Read sections A/B and the non-creamy-layer conditions; this is not a single universal checklist."
      }
    ],
    "deadline": null,
    "department": "Revenue Department, Government of NCT of Delhi",
    "description": "Official information about delhi obc certificate.",
    "district": null,
    "documents": [
      {
        "evidenceIds": [
          "delhi-obc"
        ],
        "text": "Identity/address evidence, self-declaration and photograph; caste and family-link evidence depend on the case."
      }
    ],
    "eligibility": null,
    "evidence": [
      {
        "checkedOn": "2026-10-16",
        "contentSha256": "b59c49679a0a8851a8a8f522e12289f1b3fd16d40baf11de69136a45c5977104",
        "id": "delhi-obc",
        "locator": "Certificate guideline: eligibility, mandatory/conditional documents and important notes",
        "publisher": "Revenue Department, Government of NCT of Delhi",
        "summary": "Selected certificate requirements and conditional evidence.",
        "url": "https://edistrict.delhigovt.nic.in/Public/ViewUploadScanDocumentTemplate?q=JB43Aj/g9K8d1RKt88lGvwuLNCKqIbcj5IEnYbOVu3E="
      },
      {
        "checkedOn": "2026-10-16",
        "contentSha256": "b73bb65c3354c6d2352ba7bb8ec8a5b1b69824050b86f807bec11c85047930b3",
        "id": "delhi-list",
        "locator": "Guidelines & Eligibility Criteria, Revenue and Social Welfare rows",
        "publisher": "eDistrict Delhi / Revenue Department",
        "summary": "Service names, departments and public sign-in links.",
        "url": "https://edistrict.delhigovt.nic.in/in/en/Public/ServiceGuideline.html"
      }
    ],
    "fees": null,
    "id": "dl-obc-certificate",
    "informationUrl": "https://edistrict.delhigovt.nic.in/Public/ViewUploadScanDocumentTemplate?q=JB43Aj/g9K8d1RKt88lGvwuLNCKqIbcj5IEnYbOVu3E=",
    "level": "State",
    "linkEvidenceIds": [
      "delhi-obc",
      "delhi-list"
    ],
    "organizationId": "org-f766679368dbf7d4",
    "procedure": [
      {
        "evidenceIds": [
          "delhi-obc"
        ],
        "text": "Use the official citizen login/application service, select the certificate and follow the current form. Document verification or an original declaration may also be required."
      }
    ],
    "processing": null,
    "publicationStatus": "published",
    "reviewNotes": "Source reviewed by Codex on 2026-10-16. Selected facts only; unknown fields remain null. Document lists are partial and case-dependent. The website operator and responsible department are recorded separately. No transaction was submitted. ",
    "sourceUrl": "https://edistrict.delhigovt.nic.in/Public/ViewUploadScanDocumentTemplate?q=JB43Aj/g9K8d1RKt88lGvwuLNCKqIbcj5IEnYbOVu3E=",
    "state": "Delhi",
    "synonyms": [
      "obc certificate",
      "Delhi certificate"
    ],
    "title": "Delhi OBC Certificate",
    "verificationStatus": "verified",
    "verifiedOn": "2026-10-16",
    "websiteId": "igod-9908672336cdd912"
  },
  {
    "applicationMode": "online",
    "applicationRouteEvidence": {
      "checkedOn": "2026-10-16",
      "entryReviewed": true,
      "evidenceIds": [
        "delhi-sc",
        "delhi-list"
      ],
      "finalUrl": "https://edistrict.delhigovt.nic.in/Account/Login",
      "httpStatus": 200,
      "linkedFromSource": true,
      "notes": "Normal TLS public login entry reviewed. This opens the shared official sign-in, not a prefilled service form. Authentication, service selection, acceptance and submission were not tested; availability can change.",
      "redirectReviewed": false,
      "status": "confirmed",
      "tlsVerified": true,
      "url": "https://edistrict.delhigovt.nic.in/Account/Login"
    },
    "applicationUrl": "https://edistrict.delhigovt.nic.in/Account/Login",
    "beneficiaryGroups": [
      {
        "evidenceIds": [
          "delhi-sc"
        ],
        "text": "Scheduled Castes (SC)"
      }
    ],
    "categories": [
      "documents"
    ],
    "citizenIntents": [
      "get sc certificate in Delhi",
      "Delhi sc certificate"
    ],
    "conditionalDocuments": [
      {
        "evidenceIds": [
          "delhi-sc"
        ],
        "text": "The combined SC/ST guideline has distinct historical residence, family and migration cases. Read the complete applicable section."
      }
    ],
    "deadline": null,
    "department": "Revenue Department, Government of NCT of Delhi",
    "description": "Official information about delhi scheduled caste certificate.",
    "district": null,
    "documents": [
      {
        "evidenceIds": [
          "delhi-sc"
        ],
        "text": "Selected identity/address evidence and self-declaration; family/caste evidence depends on the applicable case."
      }
    ],
    "eligibility": null,
    "evidence": [
      {
        "checkedOn": "2026-10-16",
        "contentSha256": "55116455473da361eea2187d3e3a0d22fc0e18993664a7147a3bde034a79da9b",
        "id": "delhi-sc",
        "locator": "Certificate guideline: eligibility, mandatory/conditional documents and important notes",
        "publisher": "Revenue Department, Government of NCT of Delhi",
        "summary": "Selected certificate requirements and conditional evidence.",
        "url": "https://edistrict.delhigovt.nic.in/Public/ViewUploadScanDocumentTemplate?q=lC/pNp5TPkzmQmDopE7xNpVTQ95OIoVTdMKB66fUUIU="
      },
      {
        "checkedOn": "2026-10-16",
        "contentSha256": "b73bb65c3354c6d2352ba7bb8ec8a5b1b69824050b86f807bec11c85047930b3",
        "id": "delhi-list",
        "locator": "Guidelines & Eligibility Criteria, Revenue and Social Welfare rows",
        "publisher": "eDistrict Delhi / Revenue Department",
        "summary": "Service names, departments and public sign-in links.",
        "url": "https://edistrict.delhigovt.nic.in/in/en/Public/ServiceGuideline.html"
      }
    ],
    "fees": null,
    "id": "dl-sc-certificate",
    "informationUrl": "https://edistrict.delhigovt.nic.in/Public/ViewUploadScanDocumentTemplate?q=lC/pNp5TPkzmQmDopE7xNpVTQ95OIoVTdMKB66fUUIU=",
    "level": "State",
    "linkEvidenceIds": [
      "delhi-sc",
      "delhi-list"
    ],
    "organizationId": "org-f766679368dbf7d4",
    "procedure": [
      {
        "evidenceIds": [
          "delhi-sc"
        ],
        "text": "Use the official citizen login/application service, select the certificate and follow the current form. Document verification or an original declaration may also be required."
      }
    ],
    "processing": null,
    "publicationStatus": "published",
    "reviewNotes": "Source reviewed by Codex on 2026-10-16. Selected facts only; unknown fields remain null. Document lists are partial and case-dependent. The website operator and responsible department are recorded separately. No transaction was submitted. ",
    "sourceUrl": "https://edistrict.delhigovt.nic.in/Public/ViewUploadScanDocumentTemplate?q=lC/pNp5TPkzmQmDopE7xNpVTQ95OIoVTdMKB66fUUIU=",
    "state": "Delhi",
    "synonyms": [
      "sc certificate",
      "Delhi certificate"
    ],
    "title": "Delhi Scheduled Caste Certificate",
    "verificationStatus": "verified",
    "verifiedOn": "2026-10-16",
    "websiteId": "igod-9908672336cdd912"
  },
  {
    "applicationMode": "online",
    "applicationRouteEvidence": {
      "checkedOn": "2026-10-16",
      "entryReviewed": true,
      "evidenceIds": [
        "delhi-surviving",
        "delhi-list"
      ],
      "finalUrl": "https://edistrict.delhigovt.nic.in/Account/Login",
      "httpStatus": 200,
      "linkedFromSource": true,
      "notes": "Normal TLS public login entry reviewed. This opens the shared official sign-in, not a prefilled service form. Authentication, service selection, acceptance and submission were not tested; availability can change.",
      "redirectReviewed": false,
      "status": "confirmed",
      "tlsVerified": true,
      "url": "https://edistrict.delhigovt.nic.in/Account/Login"
    },
    "applicationUrl": "https://edistrict.delhigovt.nic.in/Account/Login",
    "beneficiaryGroups": null,
    "categories": [
      "documents"
    ],
    "citizenIntents": [
      "get surviving certificate in Delhi",
      "Delhi surviving certificate"
    ],
    "conditionalDocuments": [
      {
        "evidenceIds": [
          "delhi-surviving"
        ],
        "text": "Additional family-member list when there are more than four survivors. Original declaration/physical verification may be required."
      }
    ],
    "deadline": null,
    "department": "Revenue Department, Government of NCT of Delhi",
    "description": "Official information about delhi surviving member certificate.",
    "district": null,
    "documents": [
      {
        "evidenceIds": [
          "delhi-surviving"
        ],
        "text": "Applicant and surviving-family identity/address evidence, death certificate, signed declaration and photographs specified by the guideline."
      }
    ],
    "eligibility": {
      "evidenceIds": [
        "delhi-surviving"
      ],
      "text": "Selected conditions: an Indian individual resident in Delhi; the guideline defines the family evidence needed."
    },
    "evidence": [
      {
        "checkedOn": "2026-10-16",
        "contentSha256": "032fb024c1cf647068e50312fb4d0273f15b8161d5ccd62b38fd4deae998bdfa",
        "id": "delhi-surviving",
        "locator": "Certificate guideline: eligibility, mandatory/conditional documents and important notes",
        "publisher": "Revenue Department, Government of NCT of Delhi",
        "summary": "Selected certificate requirements and conditional evidence.",
        "url": "https://edistrict.delhigovt.nic.in/Public/ViewUploadScanDocumentTemplate?q=jWdPxdcDpwWlm4OG+ipyvwLqhz7b9TKwPuPuOKYWVcM="
      },
      {
        "checkedOn": "2026-10-16",
        "contentSha256": "b73bb65c3354c6d2352ba7bb8ec8a5b1b69824050b86f807bec11c85047930b3",
        "id": "delhi-list",
        "locator": "Guidelines & Eligibility Criteria, Revenue and Social Welfare rows",
        "publisher": "eDistrict Delhi / Revenue Department",
        "summary": "Service names, departments and public sign-in links.",
        "url": "https://edistrict.delhigovt.nic.in/in/en/Public/ServiceGuideline.html"
      }
    ],
    "fees": null,
    "id": "dl-surviving-certificate",
    "informationUrl": "https://edistrict.delhigovt.nic.in/Public/ViewUploadScanDocumentTemplate?q=jWdPxdcDpwWlm4OG+ipyvwLqhz7b9TKwPuPuOKYWVcM=",
    "level": "State",
    "linkEvidenceIds": [
      "delhi-surviving",
      "delhi-list"
    ],
    "organizationId": "org-f766679368dbf7d4",
    "procedure": [
      {
        "evidenceIds": [
          "delhi-surviving"
        ],
        "text": "Use the official citizen login/application service, select the certificate and follow the current form. Document verification or an original declaration may also be required."
      }
    ],
    "processing": null,
    "publicationStatus": "published",
    "reviewNotes": "Source reviewed by Codex on 2026-10-16. Selected facts only; unknown fields remain null. Document lists are partial and case-dependent. The website operator and responsible department are recorded separately. No transaction was submitted. ",
    "sourceUrl": "https://edistrict.delhigovt.nic.in/Public/ViewUploadScanDocumentTemplate?q=jWdPxdcDpwWlm4OG+ipyvwLqhz7b9TKwPuPuOKYWVcM=",
    "state": "Delhi",
    "synonyms": [
      "surviving certificate",
      "Delhi certificate"
    ],
    "title": "Delhi Surviving Member Certificate",
    "verificationStatus": "verified",
    "verifiedOn": "2026-10-16",
    "websiteId": "igod-9908672336cdd912"
  },
  {
    "applicationMode": "online",
    "applicationRouteEvidence": {
      "checkedOn": "2026-10-16",
      "entryReviewed": true,
      "evidenceIds": [
        "kerala-faq",
        "kerala-faq"
      ],
      "finalUrl": "https://edistrict.kerala.gov.in/edportalsignin.jsp?lang=en",
      "httpStatus": 200,
      "linkedFromSource": true,
      "notes": "Normal TLS public login entry reviewed. This opens the shared official sign-in, not a prefilled service form. Authentication, service selection, acceptance and submission were not tested; availability can change.",
      "redirectReviewed": false,
      "status": "confirmed",
      "tlsVerified": true,
      "url": "https://edistrict.kerala.gov.in/edportalsignin.jsp?lang=en"
    },
    "applicationUrl": "https://edistrict.kerala.gov.in/edportalsignin.jsp?lang=en",
    "beneficiaryGroups": null,
    "categories": [
      "documents"
    ],
    "citizenIntents": [
      "get income certificate in Kerala",
      "Kerala income certificate"
    ],
    "conditionalDocuments": [
      {
        "evidenceIds": [
          "kerala-faq"
        ],
        "text": "The FAQ says to verify the ration-card number with PDS instead of uploading a copy. Other supporting evidence depends on the certificate and current form."
      }
    ],
    "deadline": null,
    "department": "Revenue Department, Government of Kerala",
    "description": "Kerala eDistrict guidance for income certificate. Requirements differ from similarly named certificates in other states.",
    "district": null,
    "documents": [
      {
        "evidenceIds": [
          "kerala-faq"
        ],
        "text": "Income evidence appropriate to the applicant, such as salary evidence, tax returns or relevant income records; affidavit where required."
      }
    ],
    "eligibility": null,
    "evidence": [
      {
        "checkedOn": "2026-10-16",
        "contentSha256": "a784de47c2a54d154cfd366b543136cc8ba114ea6736109ede940198abfcb33d",
        "id": "kerala-faq",
        "locator": "General questions; supporting documents and time-limit tables",
        "publisher": "Kerala eDistrict / Kerala State IT Mission",
        "summary": "Certificate naming, account procedure, supporting-evidence and service-time tables.",
        "url": "https://edistrict.kerala.gov.in/generalService.htm?event=faq"
      },
      {
        "checkedOn": "2026-10-16",
        "contentSha256": "598b39f0f7c5c2e8b7624f5514d905b861a61b087f07f3cf9cc3cc8c16391774",
        "id": "kerala-list",
        "locator": "Revenue Department certificate services",
        "publisher": "Kerala eDistrict / Kerala State IT Mission",
        "summary": "Revenue and other department service list.",
        "url": "https://edistrict.kerala.gov.in/servicesNew.html"
      }
    ],
    "fees": null,
    "id": "kl-income-certificate",
    "informationUrl": "https://edistrict.kerala.gov.in/generalService.htm?event=faq",
    "level": "State",
    "linkEvidenceIds": [
      "kerala-faq",
      "kerala-faq"
    ],
    "organizationId": "org-af215f9440cbcc78",
    "procedure": [
      {
        "evidenceIds": [
          "kerala-faq"
        ],
        "text": "Create/sign in to the official eDistrict account, complete One Time Registration, then choose Apply for a Certificate and the appropriate applicant registration."
      }
    ],
    "processing": {
      "evidenceIds": [
        "kerala-faq"
      ],
      "text": "The FAQ lists 6 working days for this certificate; this is a published service standard, not a personal delivery guarantee."
    },
    "publicationStatus": "published",
    "reviewNotes": "Source reviewed by Codex on 2026-10-16. Selected facts only; unknown fields remain null. Document lists are partial and case-dependent. The website operator and responsible department are recorded separately. No transaction was submitted. ",
    "sourceUrl": "https://edistrict.kerala.gov.in/generalService.htm?event=faq",
    "state": "Kerala",
    "synonyms": [
      "income certificate",
      "Kerala certificate"
    ],
    "title": "Income Certificate (Kerala)",
    "verificationStatus": "verified",
    "verifiedOn": "2026-10-16",
    "websiteId": "igod-50b73c3937b8d29b"
  },
  {
    "applicationMode": "online",
    "applicationRouteEvidence": {
      "checkedOn": "2026-10-16",
      "entryReviewed": true,
      "evidenceIds": [
        "kerala-faq",
        "kerala-faq"
      ],
      "finalUrl": "https://edistrict.kerala.gov.in/edportalsignin.jsp?lang=en",
      "httpStatus": 200,
      "linkedFromSource": true,
      "notes": "Normal TLS public login entry reviewed. This opens the shared official sign-in, not a prefilled service form. Authentication, service selection, acceptance and submission were not tested; availability can change.",
      "redirectReviewed": false,
      "status": "confirmed",
      "tlsVerified": true,
      "url": "https://edistrict.kerala.gov.in/edportalsignin.jsp?lang=en"
    },
    "applicationUrl": "https://edistrict.kerala.gov.in/edportalsignin.jsp?lang=en",
    "beneficiaryGroups": null,
    "categories": [
      "documents"
    ],
    "citizenIntents": [
      "get nativity certificate in Kerala",
      "Kerala nativity certificate"
    ],
    "conditionalDocuments": [
      {
        "evidenceIds": [
          "kerala-faq"
        ],
        "text": "The FAQ says to verify the ration-card number with PDS instead of uploading a copy. Other supporting evidence depends on the certificate and current form."
      }
    ],
    "deadline": null,
    "department": "Revenue Department, Government of Kerala",
    "description": "Kerala eDistrict guidance for nativity certificate. Requirements differ from similarly named certificates in other states.",
    "district": null,
    "documents": [
      {
        "evidenceIds": [
          "kerala-faq"
        ],
        "text": "Selected birth/school evidence and affidavit; parent school evidence may apply."
      }
    ],
    "eligibility": null,
    "evidence": [
      {
        "checkedOn": "2026-10-16",
        "contentSha256": "a784de47c2a54d154cfd366b543136cc8ba114ea6736109ede940198abfcb33d",
        "id": "kerala-faq",
        "locator": "General questions; supporting documents and time-limit tables",
        "publisher": "Kerala eDistrict / Kerala State IT Mission",
        "summary": "Certificate naming, account procedure, supporting-evidence and service-time tables.",
        "url": "https://edistrict.kerala.gov.in/generalService.htm?event=faq"
      },
      {
        "checkedOn": "2026-10-16",
        "contentSha256": "598b39f0f7c5c2e8b7624f5514d905b861a61b087f07f3cf9cc3cc8c16391774",
        "id": "kerala-list",
        "locator": "Revenue Department certificate services",
        "publisher": "Kerala eDistrict / Kerala State IT Mission",
        "summary": "Revenue and other department service list.",
        "url": "https://edistrict.kerala.gov.in/servicesNew.html"
      }
    ],
    "fees": null,
    "id": "kl-nativity-certificate",
    "informationUrl": "https://edistrict.kerala.gov.in/generalService.htm?event=faq",
    "level": "State",
    "linkEvidenceIds": [
      "kerala-faq",
      "kerala-faq"
    ],
    "organizationId": "org-af215f9440cbcc78",
    "procedure": [
      {
        "evidenceIds": [
          "kerala-faq"
        ],
        "text": "Create/sign in to the official eDistrict account, complete One Time Registration, then choose Apply for a Certificate and the appropriate applicant registration."
      }
    ],
    "processing": {
      "evidenceIds": [
        "kerala-faq"
      ],
      "text": "The FAQ lists 5 working days for this certificate; this is a published service standard, not a personal delivery guarantee."
    },
    "publicationStatus": "published",
    "reviewNotes": "Source reviewed by Codex on 2026-10-16. Selected facts only; unknown fields remain null. Document lists are partial and case-dependent. The website operator and responsible department are recorded separately. No transaction was submitted. ",
    "sourceUrl": "https://edistrict.kerala.gov.in/generalService.htm?event=faq",
    "state": "Kerala",
    "synonyms": [
      "nativity certificate",
      "Kerala certificate"
    ],
    "title": "Nativity Certificate (Kerala)",
    "verificationStatus": "verified",
    "verifiedOn": "2026-10-16",
    "websiteId": "igod-50b73c3937b8d29b"
  },
  {
    "applicationMode": "online",
    "applicationRouteEvidence": {
      "checkedOn": "2026-10-16",
      "entryReviewed": true,
      "evidenceIds": [
        "kerala-faq",
        "kerala-faq"
      ],
      "finalUrl": "https://edistrict.kerala.gov.in/edportalsignin.jsp?lang=en",
      "httpStatus": 200,
      "linkedFromSource": true,
      "notes": "Normal TLS public login entry reviewed. This opens the shared official sign-in, not a prefilled service form. Authentication, service selection, acceptance and submission were not tested; availability can change.",
      "redirectReviewed": false,
      "status": "confirmed",
      "tlsVerified": true,
      "url": "https://edistrict.kerala.gov.in/edportalsignin.jsp?lang=en"
    },
    "applicationUrl": "https://edistrict.kerala.gov.in/edportalsignin.jsp?lang=en",
    "beneficiaryGroups": [
      {
        "evidenceIds": [
          "kerala-faq"
        ],
        "text": "Other Backward Classes (OBC)"
      }
    ],
    "categories": [
      "documents"
    ],
    "citizenIntents": [
      "get caste certificate (obc) in Kerala",
      "Kerala caste certificate (obc)"
    ],
    "conditionalDocuments": [
      {
        "evidenceIds": [
          "kerala-faq"
        ],
        "text": "The FAQ says to verify the ration-card number with PDS instead of uploading a copy. Other supporting evidence depends on the certificate and current form."
      }
    ],
    "deadline": null,
    "department": "Revenue Department, Government of Kerala",
    "description": "Kerala eDistrict guidance for caste certificate (obc). Requirements differ from similarly named certificates in other states.",
    "district": null,
    "documents": [
      {
        "evidenceIds": [
          "kerala-faq"
        ],
        "text": "Selected caste evidence, school certificate and affidavit."
      }
    ],
    "eligibility": null,
    "evidence": [
      {
        "checkedOn": "2026-10-16",
        "contentSha256": "a784de47c2a54d154cfd366b543136cc8ba114ea6736109ede940198abfcb33d",
        "id": "kerala-faq",
        "locator": "General questions; supporting documents and time-limit tables",
        "publisher": "Kerala eDistrict / Kerala State IT Mission",
        "summary": "Certificate naming, account procedure, supporting-evidence and service-time tables.",
        "url": "https://edistrict.kerala.gov.in/generalService.htm?event=faq"
      },
      {
        "checkedOn": "2026-10-16",
        "contentSha256": "598b39f0f7c5c2e8b7624f5514d905b861a61b087f07f3cf9cc3cc8c16391774",
        "id": "kerala-list",
        "locator": "Revenue Department certificate services",
        "publisher": "Kerala eDistrict / Kerala State IT Mission",
        "summary": "Revenue and other department service list.",
        "url": "https://edistrict.kerala.gov.in/servicesNew.html"
      }
    ],
    "fees": null,
    "id": "kl-caste-obc-certificate",
    "informationUrl": "https://edistrict.kerala.gov.in/generalService.htm?event=faq",
    "level": "State",
    "linkEvidenceIds": [
      "kerala-faq",
      "kerala-faq"
    ],
    "organizationId": "org-af215f9440cbcc78",
    "procedure": [
      {
        "evidenceIds": [
          "kerala-faq"
        ],
        "text": "Create/sign in to the official eDistrict account, complete One Time Registration, then choose Apply for a Certificate and the appropriate applicant registration."
      }
    ],
    "processing": {
      "evidenceIds": [
        "kerala-faq"
      ],
      "text": "The FAQ lists 3 working days for this certificate; this is a published service standard, not a personal delivery guarantee."
    },
    "publicationStatus": "published",
    "reviewNotes": "Source reviewed by Codex on 2026-10-16. Selected facts only; unknown fields remain null. Document lists are partial and case-dependent. The website operator and responsible department are recorded separately. No transaction was submitted. ",
    "sourceUrl": "https://edistrict.kerala.gov.in/generalService.htm?event=faq",
    "state": "Kerala",
    "synonyms": [
      "caste certificate (obc)",
      "Kerala certificate"
    ],
    "title": "Caste Certificate (OBC) (Kerala)",
    "verificationStatus": "verified",
    "verifiedOn": "2026-10-16",
    "websiteId": "igod-50b73c3937b8d29b"
  },
  {
    "applicationMode": "online",
    "applicationRouteEvidence": {
      "checkedOn": "2026-10-16",
      "entryReviewed": true,
      "evidenceIds": [
        "kerala-faq",
        "kerala-faq"
      ],
      "finalUrl": "https://edistrict.kerala.gov.in/edportalsignin.jsp?lang=en",
      "httpStatus": 200,
      "linkedFromSource": true,
      "notes": "Normal TLS public login entry reviewed. This opens the shared official sign-in, not a prefilled service form. Authentication, service selection, acceptance and submission were not tested; availability can change.",
      "redirectReviewed": false,
      "status": "confirmed",
      "tlsVerified": true,
      "url": "https://edistrict.kerala.gov.in/edportalsignin.jsp?lang=en"
    },
    "applicationUrl": "https://edistrict.kerala.gov.in/edportalsignin.jsp?lang=en",
    "beneficiaryGroups": [
      {
        "evidenceIds": [
          "kerala-faq"
        ],
        "text": "Scheduled Castes / Scheduled Tribes (SC/ST)"
      }
    ],
    "categories": [
      "documents"
    ],
    "citizenIntents": [
      "get community certificate (sc/st) in Kerala",
      "Kerala community certificate (sc/st)"
    ],
    "conditionalDocuments": [
      {
        "evidenceIds": [
          "kerala-faq"
        ],
        "text": "The FAQ says to verify the ration-card number with PDS instead of uploading a copy. Other supporting evidence depends on the certificate and current form."
      }
    ],
    "deadline": null,
    "department": "Revenue Department, Government of Kerala",
    "description": "Kerala eDistrict guidance for community certificate (sc/st). Requirements differ from similarly named certificates in other states.",
    "district": null,
    "documents": [
      {
        "evidenceIds": [
          "kerala-faq"
        ],
        "text": "Selected caste evidence, school certificate and affidavit."
      }
    ],
    "eligibility": null,
    "evidence": [
      {
        "checkedOn": "2026-10-16",
        "contentSha256": "a784de47c2a54d154cfd366b543136cc8ba114ea6736109ede940198abfcb33d",
        "id": "kerala-faq",
        "locator": "General questions; supporting documents and time-limit tables",
        "publisher": "Kerala eDistrict / Kerala State IT Mission",
        "summary": "Certificate naming, account procedure, supporting-evidence and service-time tables.",
        "url": "https://edistrict.kerala.gov.in/generalService.htm?event=faq"
      },
      {
        "checkedOn": "2026-10-16",
        "contentSha256": "598b39f0f7c5c2e8b7624f5514d905b861a61b087f07f3cf9cc3cc8c16391774",
        "id": "kerala-list",
        "locator": "Revenue Department certificate services",
        "publisher": "Kerala eDistrict / Kerala State IT Mission",
        "summary": "Revenue and other department service list.",
        "url": "https://edistrict.kerala.gov.in/servicesNew.html"
      }
    ],
    "fees": null,
    "id": "kl-community-sc-st-certificate",
    "informationUrl": "https://edistrict.kerala.gov.in/generalService.htm?event=faq",
    "level": "State",
    "linkEvidenceIds": [
      "kerala-faq",
      "kerala-faq"
    ],
    "organizationId": "org-af215f9440cbcc78",
    "procedure": [
      {
        "evidenceIds": [
          "kerala-faq"
        ],
        "text": "Create/sign in to the official eDistrict account, complete One Time Registration, then choose Apply for a Certificate and the appropriate applicant registration."
      }
    ],
    "processing": {
      "evidenceIds": [
        "kerala-faq"
      ],
      "text": "The FAQ lists 5 working days for this certificate; this is a published service standard, not a personal delivery guarantee."
    },
    "publicationStatus": "published",
    "reviewNotes": "Source reviewed by Codex on 2026-10-16. Selected facts only; unknown fields remain null. Document lists are partial and case-dependent. The website operator and responsible department are recorded separately. No transaction was submitted. ",
    "sourceUrl": "https://edistrict.kerala.gov.in/generalService.htm?event=faq",
    "state": "Kerala",
    "synonyms": [
      "community certificate (sc/st)",
      "Kerala certificate"
    ],
    "title": "Community Certificate (SC/ST) (Kerala)",
    "verificationStatus": "verified",
    "verifiedOn": "2026-10-16",
    "websiteId": "igod-50b73c3937b8d29b"
  },
  {
    "applicationMode": "online",
    "applicationRouteEvidence": {
      "checkedOn": "2026-10-16",
      "entryReviewed": true,
      "evidenceIds": [
        "kerala-faq",
        "kerala-faq"
      ],
      "finalUrl": "https://edistrict.kerala.gov.in/edportalsignin.jsp?lang=en",
      "httpStatus": 200,
      "linkedFromSource": true,
      "notes": "Normal TLS public login entry reviewed. This opens the shared official sign-in, not a prefilled service form. Authentication, service selection, acceptance and submission were not tested; availability can change.",
      "redirectReviewed": false,
      "status": "confirmed",
      "tlsVerified": true,
      "url": "https://edistrict.kerala.gov.in/edportalsignin.jsp?lang=en"
    },
    "applicationUrl": "https://edistrict.kerala.gov.in/edportalsignin.jsp?lang=en",
    "beneficiaryGroups": null,
    "categories": [
      "documents"
    ],
    "citizenIntents": [
      "get domicile certificate in Kerala",
      "Kerala domicile certificate"
    ],
    "conditionalDocuments": [
      {
        "evidenceIds": [
          "kerala-faq"
        ],
        "text": "The FAQ says to verify the ration-card number with PDS instead of uploading a copy. Other supporting evidence depends on the certificate and current form."
      }
    ],
    "deadline": null,
    "department": "Revenue Department, Government of Kerala",
    "description": "Kerala eDistrict guidance for domicile certificate. Requirements differ from similarly named certificates in other states.",
    "district": null,
    "documents": [
      {
        "evidenceIds": [
          "kerala-faq"
        ],
        "text": "Selected identity/birth evidence and affidavit; parent school evidence may apply."
      }
    ],
    "eligibility": null,
    "evidence": [
      {
        "checkedOn": "2026-10-16",
        "contentSha256": "a784de47c2a54d154cfd366b543136cc8ba114ea6736109ede940198abfcb33d",
        "id": "kerala-faq",
        "locator": "General questions; supporting documents and time-limit tables",
        "publisher": "Kerala eDistrict / Kerala State IT Mission",
        "summary": "Certificate naming, account procedure, supporting-evidence and service-time tables.",
        "url": "https://edistrict.kerala.gov.in/generalService.htm?event=faq"
      },
      {
        "checkedOn": "2026-10-16",
        "contentSha256": "598b39f0f7c5c2e8b7624f5514d905b861a61b087f07f3cf9cc3cc8c16391774",
        "id": "kerala-list",
        "locator": "Revenue Department certificate services",
        "publisher": "Kerala eDistrict / Kerala State IT Mission",
        "summary": "Revenue and other department service list.",
        "url": "https://edistrict.kerala.gov.in/servicesNew.html"
      }
    ],
    "fees": null,
    "id": "kl-domicile-certificate",
    "informationUrl": "https://edistrict.kerala.gov.in/generalService.htm?event=faq",
    "level": "State",
    "linkEvidenceIds": [
      "kerala-faq",
      "kerala-faq"
    ],
    "organizationId": "org-af215f9440cbcc78",
    "procedure": [
      {
        "evidenceIds": [
          "kerala-faq"
        ],
        "text": "Create/sign in to the official eDistrict account, complete One Time Registration, then choose Apply for a Certificate and the appropriate applicant registration."
      }
    ],
    "processing": {
      "evidenceIds": [
        "kerala-faq"
      ],
      "text": "The FAQ lists 3 working days for this certificate; this is a published service standard, not a personal delivery guarantee."
    },
    "publicationStatus": "published",
    "reviewNotes": "Source reviewed by Codex on 2026-10-16. Selected facts only; unknown fields remain null. Document lists are partial and case-dependent. The website operator and responsible department are recorded separately. No transaction was submitted. ",
    "sourceUrl": "https://edistrict.kerala.gov.in/generalService.htm?event=faq",
    "state": "Kerala",
    "synonyms": [
      "domicile certificate",
      "Kerala certificate"
    ],
    "title": "Domicile Certificate (Kerala)",
    "verificationStatus": "verified",
    "verifiedOn": "2026-10-16",
    "websiteId": "igod-50b73c3937b8d29b"
  },
  {
    "applicationMode": "online",
    "applicationRouteEvidence": {
      "checkedOn": "2026-10-16",
      "entryReviewed": true,
      "evidenceIds": [
        "kerala-faq",
        "kerala-faq"
      ],
      "finalUrl": "https://edistrict.kerala.gov.in/edportalsignin.jsp?lang=en",
      "httpStatus": 200,
      "linkedFromSource": true,
      "notes": "Normal TLS public login entry reviewed. This opens the shared official sign-in, not a prefilled service form. Authentication, service selection, acceptance and submission were not tested; availability can change.",
      "redirectReviewed": false,
      "status": "confirmed",
      "tlsVerified": true,
      "url": "https://edistrict.kerala.gov.in/edportalsignin.jsp?lang=en"
    },
    "applicationUrl": "https://edistrict.kerala.gov.in/edportalsignin.jsp?lang=en",
    "beneficiaryGroups": null,
    "categories": [
      "documents"
    ],
    "citizenIntents": [
      "get family membership certificate in Kerala",
      "Kerala family membership certificate"
    ],
    "conditionalDocuments": [
      {
        "evidenceIds": [
          "kerala-faq"
        ],
        "text": "The FAQ says to verify the ration-card number with PDS instead of uploading a copy. Other supporting evidence depends on the certificate and current form."
      }
    ],
    "deadline": null,
    "department": "Revenue Department, Government of Kerala",
    "description": "Kerala eDistrict guidance for family membership certificate. Requirements differ from similarly named certificates in other states.",
    "district": null,
    "documents": [
      {
        "evidenceIds": [
          "kerala-faq"
        ],
        "text": "Selected identity/school evidence, affidavit and death evidence where applicable."
      }
    ],
    "eligibility": null,
    "evidence": [
      {
        "checkedOn": "2026-10-16",
        "contentSha256": "a784de47c2a54d154cfd366b543136cc8ba114ea6736109ede940198abfcb33d",
        "id": "kerala-faq",
        "locator": "General questions; supporting documents and time-limit tables",
        "publisher": "Kerala eDistrict / Kerala State IT Mission",
        "summary": "Certificate naming, account procedure, supporting-evidence and service-time tables.",
        "url": "https://edistrict.kerala.gov.in/generalService.htm?event=faq"
      },
      {
        "checkedOn": "2026-10-16",
        "contentSha256": "598b39f0f7c5c2e8b7624f5514d905b861a61b087f07f3cf9cc3cc8c16391774",
        "id": "kerala-list",
        "locator": "Revenue Department certificate services",
        "publisher": "Kerala eDistrict / Kerala State IT Mission",
        "summary": "Revenue and other department service list.",
        "url": "https://edistrict.kerala.gov.in/servicesNew.html"
      }
    ],
    "fees": null,
    "id": "kl-family-membership-certificate",
    "informationUrl": "https://edistrict.kerala.gov.in/generalService.htm?event=faq",
    "level": "State",
    "linkEvidenceIds": [
      "kerala-faq",
      "kerala-faq"
    ],
    "organizationId": "org-af215f9440cbcc78",
    "procedure": [
      {
        "evidenceIds": [
          "kerala-faq"
        ],
        "text": "Create/sign in to the official eDistrict account, complete One Time Registration, then choose Apply for a Certificate and the appropriate applicant registration."
      }
    ],
    "processing": {
      "evidenceIds": [
        "kerala-faq"
      ],
      "text": "The FAQ lists 6 working days for this certificate; this is a published service standard, not a personal delivery guarantee."
    },
    "publicationStatus": "published",
    "reviewNotes": "Source reviewed by Codex on 2026-10-16. Selected facts only; unknown fields remain null. Document lists are partial and case-dependent. The website operator and responsible department are recorded separately. No transaction was submitted. ",
    "sourceUrl": "https://edistrict.kerala.gov.in/generalService.htm?event=faq",
    "state": "Kerala",
    "synonyms": [
      "family membership certificate",
      "Kerala certificate"
    ],
    "title": "Family Membership Certificate (Kerala)",
    "verificationStatus": "verified",
    "verifiedOn": "2026-10-16",
    "websiteId": "igod-50b73c3937b8d29b"
  },
  {
    "applicationMode": "online",
    "applicationRouteEvidence": {
      "checkedOn": "2026-10-16",
      "entryReviewed": true,
      "evidenceIds": [
        "kerala-faq",
        "kerala-faq"
      ],
      "finalUrl": "https://edistrict.kerala.gov.in/edportalsignin.jsp?lang=en",
      "httpStatus": 200,
      "linkedFromSource": true,
      "notes": "Normal TLS public login entry reviewed. This opens the shared official sign-in, not a prefilled service form. Authentication, service selection, acceptance and submission were not tested; availability can change.",
      "redirectReviewed": false,
      "status": "confirmed",
      "tlsVerified": true,
      "url": "https://edistrict.kerala.gov.in/edportalsignin.jsp?lang=en"
    },
    "applicationUrl": "https://edistrict.kerala.gov.in/edportalsignin.jsp?lang=en",
    "beneficiaryGroups": null,
    "categories": [
      "documents"
    ],
    "citizenIntents": [
      "get relationship certificate in Kerala",
      "Kerala relationship certificate"
    ],
    "conditionalDocuments": [
      {
        "evidenceIds": [
          "kerala-faq"
        ],
        "text": "The FAQ says to verify the ration-card number with PDS instead of uploading a copy. Other supporting evidence depends on the certificate and current form."
      }
    ],
    "deadline": null,
    "department": "Revenue Department, Government of Kerala",
    "description": "Kerala eDistrict guidance for relationship certificate. Requirements differ from similarly named certificates in other states.",
    "district": null,
    "documents": [
      {
        "evidenceIds": [
          "kerala-faq"
        ],
        "text": "Selected identity/school evidence and affidavit."
      }
    ],
    "eligibility": null,
    "evidence": [
      {
        "checkedOn": "2026-10-16",
        "contentSha256": "a784de47c2a54d154cfd366b543136cc8ba114ea6736109ede940198abfcb33d",
        "id": "kerala-faq",
        "locator": "General questions; supporting documents and time-limit tables",
        "publisher": "Kerala eDistrict / Kerala State IT Mission",
        "summary": "Certificate naming, account procedure, supporting-evidence and service-time tables.",
        "url": "https://edistrict.kerala.gov.in/generalService.htm?event=faq"
      },
      {
        "checkedOn": "2026-10-16",
        "contentSha256": "598b39f0f7c5c2e8b7624f5514d905b861a61b087f07f3cf9cc3cc8c16391774",
        "id": "kerala-list",
        "locator": "Revenue Department certificate services",
        "publisher": "Kerala eDistrict / Kerala State IT Mission",
        "summary": "Revenue and other department service list.",
        "url": "https://edistrict.kerala.gov.in/servicesNew.html"
      }
    ],
    "fees": null,
    "id": "kl-relationship-certificate",
    "informationUrl": "https://edistrict.kerala.gov.in/generalService.htm?event=faq",
    "level": "State",
    "linkEvidenceIds": [
      "kerala-faq",
      "kerala-faq"
    ],
    "organizationId": "org-af215f9440cbcc78",
    "procedure": [
      {
        "evidenceIds": [
          "kerala-faq"
        ],
        "text": "Create/sign in to the official eDistrict account, complete One Time Registration, then choose Apply for a Certificate and the appropriate applicant registration."
      }
    ],
    "processing": {
      "evidenceIds": [
        "kerala-faq"
      ],
      "text": "The FAQ lists 7 working days for this certificate; this is a published service standard, not a personal delivery guarantee."
    },
    "publicationStatus": "published",
    "reviewNotes": "Source reviewed by Codex on 2026-10-16. Selected facts only; unknown fields remain null. Document lists are partial and case-dependent. The website operator and responsible department are recorded separately. No transaction was submitted. ",
    "sourceUrl": "https://edistrict.kerala.gov.in/generalService.htm?event=faq",
    "state": "Kerala",
    "synonyms": [
      "relationship certificate",
      "Kerala certificate"
    ],
    "title": "Relationship Certificate (Kerala)",
    "verificationStatus": "verified",
    "verifiedOn": "2026-10-16",
    "websiteId": "igod-50b73c3937b8d29b"
  },
  {
    "applicationMode": "online",
    "applicationRouteEvidence": {
      "checkedOn": "2026-10-16",
      "entryReviewed": true,
      "evidenceIds": [
        "kerala-faq",
        "kerala-faq"
      ],
      "finalUrl": "https://edistrict.kerala.gov.in/edportalsignin.jsp?lang=en",
      "httpStatus": 200,
      "linkedFromSource": true,
      "notes": "Normal TLS public login entry reviewed. This opens the shared official sign-in, not a prefilled service form. Authentication, service selection, acceptance and submission were not tested; availability can change.",
      "redirectReviewed": false,
      "status": "confirmed",
      "tlsVerified": true,
      "url": "https://edistrict.kerala.gov.in/edportalsignin.jsp?lang=en"
    },
    "applicationUrl": "https://edistrict.kerala.gov.in/edportalsignin.jsp?lang=en",
    "beneficiaryGroups": null,
    "categories": [
      "documents"
    ],
    "citizenIntents": [
      "get legal heir certificate in Kerala",
      "Kerala legal heir certificate"
    ],
    "conditionalDocuments": [
      {
        "evidenceIds": [
          "kerala-faq"
        ],
        "text": "The FAQ says to verify the ration-card number with PDS instead of uploading a copy. Other supporting evidence depends on the certificate and current form."
      }
    ],
    "deadline": null,
    "department": "Revenue Department, Government of Kerala",
    "description": "Kerala eDistrict guidance for legal heir certificate. Requirements differ from similarly named certificates in other states.",
    "district": null,
    "documents": [
      {
        "evidenceIds": [
          "kerala-faq"
        ],
        "text": "Selected death and identity evidence and affidavit."
      }
    ],
    "eligibility": null,
    "evidence": [
      {
        "checkedOn": "2026-10-16",
        "contentSha256": "a784de47c2a54d154cfd366b543136cc8ba114ea6736109ede940198abfcb33d",
        "id": "kerala-faq",
        "locator": "General questions; supporting documents and time-limit tables",
        "publisher": "Kerala eDistrict / Kerala State IT Mission",
        "summary": "Certificate naming, account procedure, supporting-evidence and service-time tables.",
        "url": "https://edistrict.kerala.gov.in/generalService.htm?event=faq"
      },
      {
        "checkedOn": "2026-10-16",
        "contentSha256": "598b39f0f7c5c2e8b7624f5514d905b861a61b087f07f3cf9cc3cc8c16391774",
        "id": "kerala-list",
        "locator": "Revenue Department certificate services",
        "publisher": "Kerala eDistrict / Kerala State IT Mission",
        "summary": "Revenue and other department service list.",
        "url": "https://edistrict.kerala.gov.in/servicesNew.html"
      }
    ],
    "fees": null,
    "id": "kl-legal-heir-certificate",
    "informationUrl": "https://edistrict.kerala.gov.in/generalService.htm?event=faq",
    "level": "State",
    "linkEvidenceIds": [
      "kerala-faq",
      "kerala-faq"
    ],
    "organizationId": "org-af215f9440cbcc78",
    "procedure": [
      {
        "evidenceIds": [
          "kerala-faq"
        ],
        "text": "Create/sign in to the official eDistrict account, complete One Time Registration, then choose Apply for a Certificate and the appropriate applicant registration."
      }
    ],
    "processing": {
      "evidenceIds": [
        "kerala-faq"
      ],
      "text": "The FAQ describes a 30-working-day objection period after gazette notification, then issue within 15 working days after that period if there is no objection; confirm the applicable process."
    },
    "publicationStatus": "published",
    "reviewNotes": "Source reviewed by Codex on 2026-10-16. Selected facts only; unknown fields remain null. Document lists are partial and case-dependent. The website operator and responsible department are recorded separately. No transaction was submitted. ",
    "sourceUrl": "https://edistrict.kerala.gov.in/generalService.htm?event=faq",
    "state": "Kerala",
    "synonyms": [
      "legal heir certificate",
      "Kerala certificate"
    ],
    "title": "Legal Heir Certificate (Kerala)",
    "verificationStatus": "verified",
    "verifiedOn": "2026-10-16",
    "websiteId": "igod-50b73c3937b8d29b"
  },
  {
    "applicationMode": "information",
    "applicationRouteEvidence": {
      "checkedOn": "2026-10-16",
      "evidenceIds": [
        "odisha-home"
      ],
      "notes": "Official service facts reviewed. Source-linked directApply endpoints returned normal-TLS HTTP 200 but no readable transaction entry; no Apply action. Some modal wording appears old/incomplete, so full eligibility is not inferred.",
      "status": "unconfirmed"
    },
    "applicationUrl": null,
    "beneficiaryGroups": null,
    "categories": [
      "documents"
    ],
    "citizenIntents": [
      "get resident certificate in Odisha",
      "Odisha resident certificate"
    ],
    "conditionalDocuments": null,
    "deadline": null,
    "department": "Revenue and Disaster Management Department, Government of Odisha",
    "description": "Official Odisha guidance for resident certificate. Full eligibility and a functioning transaction entry still need confirmation.",
    "district": null,
    "documents": [
      {
        "evidenceIds": [
          "odisha-home"
        ],
        "text": "Selected residence-evidence alternatives include a utility bill, land record, rental agreement or employer certificate; provide the applicable evidence, not every listed item."
      }
    ],
    "eligibility": null,
    "evidence": [
      {
        "checkedOn": "2026-10-16",
        "contentSha256": "f2341a48ec83accdbd767b99846c2a3bfe62085b9b852b8fd85743fe97ed2703",
        "id": "odisha-home",
        "locator": "e-Certificates: corresponding named service modal (not the aggregate application statistics)",
        "publisher": "eDistrict Odisha / Revenue and Disaster Management Department",
        "summary": "Public revenue-certificate modal descriptions, document alternatives, fees and time standards.",
        "url": "https://edistrict.odisha.gov.in/"
      }
    ],
    "fees": {
      "evidenceIds": [
        "odisha-home"
      ],
      "text": "The service modal lists no government service charge; other channel charges have not been verified."
    },
    "id": "od-resident-certificate",
    "informationUrl": "https://edistrict.odisha.gov.in/",
    "level": "State",
    "linkEvidenceIds": [
      "odisha-home"
    ],
    "organizationId": "org-7f432ad3e33f5840",
    "procedure": null,
    "processing": {
      "evidenceIds": [
        "odisha-home"
      ],
      "text": "The modal lists 10 working days as the service standard; observed processing and individual outcomes can differ."
    },
    "publicationStatus": "published",
    "reviewNotes": "Source reviewed by Codex on 2026-10-16. Selected facts only; unknown fields remain null. Document lists are partial and case-dependent. The website operator and responsible department are recorded separately. No transaction was submitted. Official service facts reviewed. Source-linked directApply endpoints returned normal-TLS HTTP 200 but no readable transaction entry; no Apply action. Some modal wording appears old/incomplete, so full eligibility is not inferred.",
    "sourceUrl": "https://edistrict.odisha.gov.in/",
    "state": "Odisha",
    "synonyms": [
      "resident certificate",
      "Odisha certificate"
    ],
    "title": "Resident Certificate (Odisha)",
    "verificationStatus": "verified",
    "verifiedOn": "2026-10-16",
    "websiteId": "igod-5e885f0e333d5206"
  },
  {
    "applicationMode": "information",
    "applicationRouteEvidence": {
      "checkedOn": "2026-10-16",
      "evidenceIds": [
        "odisha-home"
      ],
      "notes": "Official service facts reviewed. Source-linked directApply endpoints returned normal-TLS HTTP 200 but no readable transaction entry; no Apply action. Some modal wording appears old/incomplete, so full eligibility is not inferred.",
      "status": "unconfirmed"
    },
    "applicationUrl": null,
    "beneficiaryGroups": null,
    "categories": [
      "documents"
    ],
    "citizenIntents": [
      "get income certificate in Odisha",
      "Odisha income certificate"
    ],
    "conditionalDocuments": null,
    "deadline": null,
    "department": "Revenue and Disaster Management Department, Government of Odisha",
    "description": "Official Odisha guidance for income certificate. Full eligibility and a functioning transaction entry still need confirmation.",
    "district": null,
    "documents": [
      {
        "evidenceIds": [
          "odisha-home"
        ],
        "text": "Selected income evidence includes salary records, tax returns or other income evidence as applicable."
      }
    ],
    "eligibility": null,
    "evidence": [
      {
        "checkedOn": "2026-10-16",
        "contentSha256": "f2341a48ec83accdbd767b99846c2a3bfe62085b9b852b8fd85743fe97ed2703",
        "id": "odisha-home",
        "locator": "e-Certificates: corresponding named service modal (not the aggregate application statistics)",
        "publisher": "eDistrict Odisha / Revenue and Disaster Management Department",
        "summary": "Public revenue-certificate modal descriptions, document alternatives, fees and time standards.",
        "url": "https://edistrict.odisha.gov.in/"
      }
    ],
    "fees": {
      "evidenceIds": [
        "odisha-home"
      ],
      "text": "The service modal lists no government service charge; other channel charges have not been verified."
    },
    "id": "od-income-certificate",
    "informationUrl": "https://edistrict.odisha.gov.in/",
    "level": "State",
    "linkEvidenceIds": [
      "odisha-home"
    ],
    "organizationId": "org-7f432ad3e33f5840",
    "procedure": null,
    "processing": {
      "evidenceIds": [
        "odisha-home"
      ],
      "text": "The modal lists 10 working days as the service standard; observed processing and individual outcomes can differ."
    },
    "publicationStatus": "published",
    "reviewNotes": "Source reviewed by Codex on 2026-10-16. Selected facts only; unknown fields remain null. Document lists are partial and case-dependent. The website operator and responsible department are recorded separately. No transaction was submitted. Official service facts reviewed. Source-linked directApply endpoints returned normal-TLS HTTP 200 but no readable transaction entry; no Apply action. Some modal wording appears old/incomplete, so full eligibility is not inferred.",
    "sourceUrl": "https://edistrict.odisha.gov.in/",
    "state": "Odisha",
    "synonyms": [
      "income certificate",
      "Odisha certificate"
    ],
    "title": "Income Certificate (Odisha)",
    "verificationStatus": "verified",
    "verifiedOn": "2026-10-16",
    "websiteId": "igod-5e885f0e333d5206"
  },
  {
    "applicationMode": "information",
    "applicationRouteEvidence": {
      "checkedOn": "2026-10-16",
      "evidenceIds": [
        "odisha-home"
      ],
      "notes": "Official service facts reviewed. Source-linked directApply endpoints returned normal-TLS HTTP 200 but no readable transaction entry; no Apply action. Some modal wording appears old/incomplete, so full eligibility is not inferred.",
      "status": "unconfirmed"
    },
    "applicationUrl": null,
    "beneficiaryGroups": [
      {
        "evidenceIds": [
          "odisha-home"
        ],
        "text": "Scheduled Castes (SC)"
      }
    ],
    "categories": [
      "documents"
    ],
    "citizenIntents": [
      "get scheduled caste certificate in Odisha",
      "Odisha scheduled caste certificate"
    ],
    "conditionalDocuments": null,
    "deadline": null,
    "department": "Revenue and Disaster Management Department, Government of Odisha",
    "description": "Official Odisha guidance for scheduled caste certificate. Full eligibility and a functioning transaction entry still need confirmation.",
    "district": null,
    "documents": [
      {
        "evidenceIds": [
          "odisha-home"
        ],
        "text": "Selected identity, land or family caste evidence as applicable; the source presents alternatives rather than a universal mandatory list."
      }
    ],
    "eligibility": null,
    "evidence": [
      {
        "checkedOn": "2026-10-16",
        "contentSha256": "f2341a48ec83accdbd767b99846c2a3bfe62085b9b852b8fd85743fe97ed2703",
        "id": "odisha-home",
        "locator": "e-Certificates: corresponding named service modal (not the aggregate application statistics)",
        "publisher": "eDistrict Odisha / Revenue and Disaster Management Department",
        "summary": "Public revenue-certificate modal descriptions, document alternatives, fees and time standards.",
        "url": "https://edistrict.odisha.gov.in/"
      }
    ],
    "fees": {
      "evidenceIds": [
        "odisha-home"
      ],
      "text": "The service modal lists no government service charge; other channel charges have not been verified."
    },
    "id": "od-sc-certificate",
    "informationUrl": "https://edistrict.odisha.gov.in/",
    "level": "State",
    "linkEvidenceIds": [
      "odisha-home"
    ],
    "organizationId": "org-7f432ad3e33f5840",
    "procedure": null,
    "processing": {
      "evidenceIds": [
        "odisha-home"
      ],
      "text": "The modal lists 15 working days as the service standard; observed processing and individual outcomes can differ."
    },
    "publicationStatus": "published",
    "reviewNotes": "Source reviewed by Codex on 2026-10-16. Selected facts only; unknown fields remain null. Document lists are partial and case-dependent. The website operator and responsible department are recorded separately. No transaction was submitted. Official service facts reviewed. Source-linked directApply endpoints returned normal-TLS HTTP 200 but no readable transaction entry; no Apply action. Some modal wording appears old/incomplete, so full eligibility is not inferred.",
    "sourceUrl": "https://edistrict.odisha.gov.in/",
    "state": "Odisha",
    "synonyms": [
      "scheduled caste certificate",
      "Odisha certificate"
    ],
    "title": "Scheduled Caste Certificate (Odisha)",
    "verificationStatus": "verified",
    "verifiedOn": "2026-10-16",
    "websiteId": "igod-5e885f0e333d5206"
  },
  {
    "applicationMode": "information",
    "applicationRouteEvidence": {
      "checkedOn": "2026-10-16",
      "evidenceIds": [
        "odisha-home"
      ],
      "notes": "Official service facts reviewed. Source-linked directApply endpoints returned normal-TLS HTTP 200 but no readable transaction entry; no Apply action. Some modal wording appears old/incomplete, so full eligibility is not inferred.",
      "status": "unconfirmed"
    },
    "applicationUrl": null,
    "beneficiaryGroups": [
      {
        "evidenceIds": [
          "odisha-home"
        ],
        "text": "Scheduled Tribes (ST)"
      }
    ],
    "categories": [
      "documents"
    ],
    "citizenIntents": [
      "get scheduled tribe certificate in Odisha",
      "Odisha scheduled tribe certificate"
    ],
    "conditionalDocuments": null,
    "deadline": null,
    "department": "Revenue and Disaster Management Department, Government of Odisha",
    "description": "Official Odisha guidance for scheduled tribe certificate. Full eligibility and a functioning transaction entry still need confirmation.",
    "district": null,
    "documents": [
      {
        "evidenceIds": [
          "odisha-home"
        ],
        "text": "Selected identity, land or family caste evidence as applicable; the source presents alternatives rather than a universal mandatory list."
      }
    ],
    "eligibility": null,
    "evidence": [
      {
        "checkedOn": "2026-10-16",
        "contentSha256": "f2341a48ec83accdbd767b99846c2a3bfe62085b9b852b8fd85743fe97ed2703",
        "id": "odisha-home",
        "locator": "e-Certificates: corresponding named service modal (not the aggregate application statistics)",
        "publisher": "eDistrict Odisha / Revenue and Disaster Management Department",
        "summary": "Public revenue-certificate modal descriptions, document alternatives, fees and time standards.",
        "url": "https://edistrict.odisha.gov.in/"
      }
    ],
    "fees": {
      "evidenceIds": [
        "odisha-home"
      ],
      "text": "The service modal lists no government service charge; other channel charges have not been verified."
    },
    "id": "od-st-certificate",
    "informationUrl": "https://edistrict.odisha.gov.in/",
    "level": "State",
    "linkEvidenceIds": [
      "odisha-home"
    ],
    "organizationId": "org-7f432ad3e33f5840",
    "procedure": null,
    "processing": {
      "evidenceIds": [
        "odisha-home"
      ],
      "text": "The modal lists 15 working days as the service standard; observed processing and individual outcomes can differ."
    },
    "publicationStatus": "published",
    "reviewNotes": "Source reviewed by Codex on 2026-10-16. Selected facts only; unknown fields remain null. Document lists are partial and case-dependent. The website operator and responsible department are recorded separately. No transaction was submitted. Official service facts reviewed. Source-linked directApply endpoints returned normal-TLS HTTP 200 but no readable transaction entry; no Apply action. Some modal wording appears old/incomplete, so full eligibility is not inferred.",
    "sourceUrl": "https://edistrict.odisha.gov.in/",
    "state": "Odisha",
    "synonyms": [
      "scheduled tribe certificate",
      "Odisha certificate"
    ],
    "title": "Scheduled Tribe Certificate (Odisha)",
    "verificationStatus": "verified",
    "verifiedOn": "2026-10-16",
    "websiteId": "igod-5e885f0e333d5206"
  },
  {
    "applicationMode": "information",
    "applicationRouteEvidence": {
      "checkedOn": "2026-10-16",
      "evidenceIds": [
        "odisha-home"
      ],
      "notes": "Official service facts reviewed. Source-linked directApply endpoints returned normal-TLS HTTP 200 but no readable transaction entry; no Apply action. Some modal wording appears old/incomplete, so full eligibility is not inferred.",
      "status": "unconfirmed"
    },
    "applicationUrl": null,
    "beneficiaryGroups": [
      {
        "evidenceIds": [
          "odisha-home"
        ],
        "text": "Economically Weaker Sections (EWS)"
      }
    ],
    "categories": [
      "documents"
    ],
    "citizenIntents": [
      "get income and asset certificate (ews) in Odisha",
      "Odisha income and asset certificate (ews)"
    ],
    "conditionalDocuments": null,
    "deadline": null,
    "department": "Revenue and Disaster Management Department, Government of Odisha",
    "description": "Official Odisha guidance for income and asset certificate (ews). Full eligibility and a functioning transaction entry still need confirmation.",
    "district": null,
    "documents": [
      {
        "evidenceIds": [
          "odisha-home"
        ],
        "text": "Identity, income and land evidence as applicable. Salary and agricultural-income proof are required when those income values are declared."
      }
    ],
    "eligibility": null,
    "evidence": [
      {
        "checkedOn": "2026-10-16",
        "contentSha256": "f2341a48ec83accdbd767b99846c2a3bfe62085b9b852b8fd85743fe97ed2703",
        "id": "odisha-home",
        "locator": "e-Certificates: corresponding named service modal (not the aggregate application statistics)",
        "publisher": "eDistrict Odisha / Revenue and Disaster Management Department",
        "summary": "Public revenue-certificate modal descriptions, document alternatives, fees and time standards.",
        "url": "https://edistrict.odisha.gov.in/"
      }
    ],
    "fees": {
      "evidenceIds": [
        "odisha-home"
      ],
      "text": "The service modal lists no government service charge; other channel charges have not been verified."
    },
    "id": "od-ews-certificate",
    "informationUrl": "https://edistrict.odisha.gov.in/",
    "level": "State",
    "linkEvidenceIds": [
      "odisha-home"
    ],
    "organizationId": "org-7f432ad3e33f5840",
    "procedure": null,
    "processing": {
      "evidenceIds": [
        "odisha-home"
      ],
      "text": "The modal lists 10 working days as the service standard; observed processing and individual outcomes can differ."
    },
    "publicationStatus": "published",
    "reviewNotes": "Source reviewed by Codex on 2026-10-16. Selected facts only; unknown fields remain null. Document lists are partial and case-dependent. The website operator and responsible department are recorded separately. No transaction was submitted. Official service facts reviewed. Source-linked directApply endpoints returned normal-TLS HTTP 200 but no readable transaction entry; no Apply action. Some modal wording appears old/incomplete, so full eligibility is not inferred.",
    "sourceUrl": "https://edistrict.odisha.gov.in/",
    "state": "Odisha",
    "synonyms": [
      "income and asset certificate (ews)",
      "Odisha certificate"
    ],
    "title": "Income and Asset Certificate (EWS) (Odisha)",
    "verificationStatus": "verified",
    "verifiedOn": "2026-10-16",
    "websiteId": "igod-5e885f0e333d5206"
  },
  {
    "applicationMode": "information",
    "applicationRouteEvidence": {
      "checkedOn": "2026-10-16",
      "evidenceIds": [
        "odisha-home"
      ],
      "notes": "Official service facts reviewed. Source-linked directApply endpoints returned normal-TLS HTTP 200 but no readable transaction entry; no Apply action. Some modal wording appears old/incomplete, so full eligibility is not inferred.",
      "status": "unconfirmed"
    },
    "applicationUrl": null,
    "beneficiaryGroups": null,
    "categories": [
      "documents"
    ],
    "citizenIntents": [
      "get legal heir certificate in Odisha",
      "Odisha legal heir certificate"
    ],
    "conditionalDocuments": null,
    "deadline": null,
    "department": "Revenue and Disaster Management Department, Government of Odisha",
    "description": "Official Odisha guidance for legal heir certificate. Full eligibility and a functioning transaction entry still need confirmation.",
    "district": null,
    "documents": [
      {
        "evidenceIds": [
          "odisha-home"
        ],
        "text": "Death certificate is explicitly mandatory in this service modal; identity and other evidence depend on the case."
      }
    ],
    "eligibility": null,
    "evidence": [
      {
        "checkedOn": "2026-10-16",
        "contentSha256": "f2341a48ec83accdbd767b99846c2a3bfe62085b9b852b8fd85743fe97ed2703",
        "id": "odisha-home",
        "locator": "e-Certificates: corresponding named service modal (not the aggregate application statistics)",
        "publisher": "eDistrict Odisha / Revenue and Disaster Management Department",
        "summary": "Public revenue-certificate modal descriptions, document alternatives, fees and time standards.",
        "url": "https://edistrict.odisha.gov.in/"
      }
    ],
    "fees": {
      "evidenceIds": [
        "odisha-home"
      ],
      "text": "The service modal lists no government service charge; other channel charges have not been verified."
    },
    "id": "od-legal-heir-certificate",
    "informationUrl": "https://edistrict.odisha.gov.in/",
    "level": "State",
    "linkEvidenceIds": [
      "odisha-home"
    ],
    "organizationId": "org-7f432ad3e33f5840",
    "procedure": null,
    "processing": {
      "evidenceIds": [
        "odisha-home"
      ],
      "text": "The modal lists 30 working days as the service standard; observed processing and individual outcomes can differ."
    },
    "publicationStatus": "published",
    "reviewNotes": "Source reviewed by Codex on 2026-10-16. Selected facts only; unknown fields remain null. Document lists are partial and case-dependent. The website operator and responsible department are recorded separately. No transaction was submitted. Official service facts reviewed. Source-linked directApply endpoints returned normal-TLS HTTP 200 but no readable transaction entry; no Apply action. Some modal wording appears old/incomplete, so full eligibility is not inferred.",
    "sourceUrl": "https://edistrict.odisha.gov.in/",
    "state": "Odisha",
    "synonyms": [
      "legal heir certificate",
      "Odisha certificate"
    ],
    "title": "Legal Heir Certificate (Odisha)",
    "verificationStatus": "verified",
    "verifiedOn": "2026-10-16",
    "websiteId": "igod-5e885f0e333d5206"
  },
  {
    "applicationMode": "information",
    "applicationRouteEvidence": {
      "checkedOn": "2026-10-16",
      "evidenceIds": [
        "odisha-home"
      ],
      "notes": "Official service facts reviewed. Source-linked directApply endpoints returned normal-TLS HTTP 200 but no readable transaction entry; no Apply action. Some modal wording appears old/incomplete, so full eligibility is not inferred.",
      "status": "unconfirmed"
    },
    "applicationUrl": null,
    "beneficiaryGroups": [
      {
        "evidenceIds": [
          "odisha-home"
        ],
        "text": "Other Backward Classes (OBC)"
      }
    ],
    "categories": [
      "documents"
    ],
    "citizenIntents": [
      "get obc certificate in Odisha",
      "Odisha obc certificate"
    ],
    "conditionalDocuments": null,
    "deadline": null,
    "department": "Revenue and Disaster Management Department, Government of Odisha",
    "description": "Official Odisha guidance for obc certificate. Full eligibility and a functioning transaction entry still need confirmation.",
    "district": null,
    "documents": [
      {
        "evidenceIds": [
          "odisha-home"
        ],
        "text": "Selected identity evidence, self-declaration, land record or land passbook as applicable."
      }
    ],
    "eligibility": null,
    "evidence": [
      {
        "checkedOn": "2026-10-16",
        "contentSha256": "f2341a48ec83accdbd767b99846c2a3bfe62085b9b852b8fd85743fe97ed2703",
        "id": "odisha-home",
        "locator": "e-Certificates: corresponding named service modal (not the aggregate application statistics)",
        "publisher": "eDistrict Odisha / Revenue and Disaster Management Department",
        "summary": "Public revenue-certificate modal descriptions, document alternatives, fees and time standards.",
        "url": "https://edistrict.odisha.gov.in/"
      }
    ],
    "fees": {
      "evidenceIds": [
        "odisha-home"
      ],
      "text": "The service modal lists no government service charge; other channel charges have not been verified."
    },
    "id": "od-obc-certificate",
    "informationUrl": "https://edistrict.odisha.gov.in/",
    "level": "State",
    "linkEvidenceIds": [
      "odisha-home"
    ],
    "organizationId": "org-7f432ad3e33f5840",
    "procedure": null,
    "processing": {
      "evidenceIds": [
        "odisha-home"
      ],
      "text": "The modal lists 15 working days as the service standard; observed processing and individual outcomes can differ."
    },
    "publicationStatus": "published",
    "reviewNotes": "Source reviewed by Codex on 2026-10-16. Selected facts only; unknown fields remain null. Document lists are partial and case-dependent. The website operator and responsible department are recorded separately. No transaction was submitted. Official service facts reviewed. Source-linked directApply endpoints returned normal-TLS HTTP 200 but no readable transaction entry; no Apply action. Some modal wording appears old/incomplete, so full eligibility is not inferred.",
    "sourceUrl": "https://edistrict.odisha.gov.in/",
    "state": "Odisha",
    "synonyms": [
      "obc certificate",
      "Odisha certificate"
    ],
    "title": "OBC Certificate (Odisha)",
    "verificationStatus": "verified",
    "verifiedOn": "2026-10-16",
    "websiteId": "igod-5e885f0e333d5206"
  },
  {
    "applicationMode": "information",
    "applicationRouteEvidence": {
      "checkedOn": "2026-10-16",
      "evidenceIds": [
        "odisha-home"
      ],
      "notes": "Official service facts reviewed. Source-linked directApply endpoints returned normal-TLS HTTP 200 but no readable transaction entry; no Apply action. Some modal wording appears old/incomplete, so full eligibility is not inferred.",
      "status": "unconfirmed"
    },
    "applicationUrl": null,
    "beneficiaryGroups": [
      {
        "evidenceIds": [
          "odisha-home"
        ],
        "text": "Socially and Educationally Backward Classes (SEBC)"
      }
    ],
    "categories": [
      "documents"
    ],
    "citizenIntents": [
      "get sebc certificate in Odisha",
      "Odisha sebc certificate"
    ],
    "conditionalDocuments": null,
    "deadline": null,
    "department": "Revenue and Disaster Management Department, Government of Odisha",
    "description": "Official Odisha guidance for sebc certificate. Full eligibility and a functioning transaction entry still need confirmation.",
    "district": null,
    "documents": [
      {
        "evidenceIds": [
          "odisha-home"
        ],
        "text": "Selected identity evidence, self-declaration, land record or land passbook as applicable."
      }
    ],
    "eligibility": null,
    "evidence": [
      {
        "checkedOn": "2026-10-16",
        "contentSha256": "f2341a48ec83accdbd767b99846c2a3bfe62085b9b852b8fd85743fe97ed2703",
        "id": "odisha-home",
        "locator": "e-Certificates: corresponding named service modal (not the aggregate application statistics)",
        "publisher": "eDistrict Odisha / Revenue and Disaster Management Department",
        "summary": "Public revenue-certificate modal descriptions, document alternatives, fees and time standards.",
        "url": "https://edistrict.odisha.gov.in/"
      }
    ],
    "fees": {
      "evidenceIds": [
        "odisha-home"
      ],
      "text": "The service modal lists no government service charge; other channel charges have not been verified."
    },
    "id": "od-sebc-certificate",
    "informationUrl": "https://edistrict.odisha.gov.in/",
    "level": "State",
    "linkEvidenceIds": [
      "odisha-home"
    ],
    "organizationId": "org-7f432ad3e33f5840",
    "procedure": null,
    "processing": {
      "evidenceIds": [
        "odisha-home"
      ],
      "text": "The modal lists 15 working days as the service standard; observed processing and individual outcomes can differ."
    },
    "publicationStatus": "published",
    "reviewNotes": "Source reviewed by Codex on 2026-10-16. Selected facts only; unknown fields remain null. Document lists are partial and case-dependent. The website operator and responsible department are recorded separately. No transaction was submitted. Official service facts reviewed. Source-linked directApply endpoints returned normal-TLS HTTP 200 but no readable transaction entry; no Apply action. Some modal wording appears old/incomplete, so full eligibility is not inferred.",
    "sourceUrl": "https://edistrict.odisha.gov.in/",
    "state": "Odisha",
    "synonyms": [
      "sebc certificate",
      "Odisha certificate"
    ],
    "title": "SEBC Certificate (Odisha)",
    "verificationStatus": "verified",
    "verifiedOn": "2026-10-16",
    "websiteId": "igod-5e885f0e333d5206"
  },
  {
    "applicationMode": "information",
    "applicationRouteEvidence": {
      "checkedOn": "2026-10-16",
      "evidenceIds": [
        "py-income"
      ],
      "notes": "The PDF names edistrict.py.gov.in, but normal TLS retrieval failed (curl 35). Information only. The document appendix appears mismatched; eligibility and document requirements remain unknown rather than using a guessed checklist.",
      "status": "unconfirmed"
    },
    "applicationUrl": null,
    "beneficiaryGroups": null,
    "categories": [
      "documents"
    ],
    "citizenIntents": [
      "get income certificate (puducherry)"
    ],
    "conditionalDocuments": null,
    "deadline": null,
    "department": "Revenue and Disaster Management Department, Government of Puducherry",
    "description": "Official information about income certificate (puducherry).",
    "district": null,
    "documents": null,
    "eligibility": null,
    "evidence": [
      {
        "checkedOn": "2026-10-16",
        "contentSha256": "edd020293e08af154056d9acb80c9d9f2107d3c1dea56d1960b5c1e330bd963d",
        "id": "py-income",
        "locator": "Steps I-IV; document appendix not adopted because it appears generic/mismatched",
        "publisher": "Revenue and Disaster Management Department, Puducherry",
        "summary": "Departmental certificate procedure and fee statement.",
        "url": "https://revenue.py.gov.in/downloads/EODB/EODB-Income%20Certificate.pdf"
      }
    ],
    "fees": {
      "evidenceIds": [
        "py-income"
      ],
      "text": "The procedure PDF states no certificate fee; other channel charges have not been verified."
    },
    "id": "py-income-certificate",
    "informationUrl": "https://revenue.py.gov.in/downloads/EODB/EODB-Income%20Certificate.pdf",
    "level": "State",
    "linkEvidenceIds": [
      "py-income"
    ],
    "organizationId": "org-fdd5069647df76c9",
    "procedure": [
      {
        "evidenceIds": [
          "py-income"
        ],
        "text": "The departmental PDF describes eDistrict registration, selecting the Revenue service, submitting the current form and retaining the acknowledgement; approval follows official verification."
      }
    ],
    "processing": null,
    "publicationStatus": "published",
    "reviewNotes": "Source reviewed by Codex on 2026-10-16. Selected facts only; unknown fields remain null. Document lists are partial and case-dependent. The website operator and responsible department are recorded separately. No transaction was submitted. The PDF names edistrict.py.gov.in, but normal TLS retrieval failed (curl 35). Information only. The document appendix appears mismatched; eligibility and document requirements remain unknown rather than using a guessed checklist.",
    "sourceUrl": "https://revenue.py.gov.in/downloads/EODB/EODB-Income%20Certificate.pdf",
    "state": "Puducherry",
    "synonyms": [
      "income certificate (puducherry)"
    ],
    "title": "Income Certificate (Puducherry)",
    "verificationStatus": "verified",
    "verifiedOn": "2026-10-16",
    "websiteId": "igod-7c7865e8e6982bc1"
  },
  {
    "applicationMode": "information",
    "applicationRouteEvidence": {
      "checkedOn": "2026-10-16",
      "evidenceIds": [
        "py-residence"
      ],
      "notes": "The PDF names edistrict.py.gov.in, but normal TLS retrieval failed (curl 35). Information only. The document appendix appears mismatched; eligibility and document requirements remain unknown rather than using a guessed checklist.",
      "status": "unconfirmed"
    },
    "applicationUrl": null,
    "beneficiaryGroups": null,
    "categories": [
      "documents"
    ],
    "citizenIntents": [
      "get residence certificate (puducherry)"
    ],
    "conditionalDocuments": null,
    "deadline": null,
    "department": "Revenue and Disaster Management Department, Government of Puducherry",
    "description": "Official information about residence certificate (puducherry).",
    "district": null,
    "documents": null,
    "eligibility": null,
    "evidence": [
      {
        "checkedOn": "2026-10-16",
        "contentSha256": "15c2837e3faee5545b382cd89b96b1eef5885ec56025f4a26800e2d754540005",
        "id": "py-residence",
        "locator": "Steps I-IV; document appendix not adopted because it appears generic/mismatched",
        "publisher": "Revenue and Disaster Management Department, Puducherry",
        "summary": "Departmental certificate procedure and fee statement.",
        "url": "https://revenue.py.gov.in/downloads/EODB/EODB-Residence%20Certificate.pdf"
      }
    ],
    "fees": {
      "evidenceIds": [
        "py-residence"
      ],
      "text": "The procedure PDF states no certificate fee; other channel charges have not been verified."
    },
    "id": "py-residence-certificate",
    "informationUrl": "https://revenue.py.gov.in/downloads/EODB/EODB-Residence%20Certificate.pdf",
    "level": "State",
    "linkEvidenceIds": [
      "py-residence"
    ],
    "organizationId": "org-fdd5069647df76c9",
    "procedure": [
      {
        "evidenceIds": [
          "py-residence"
        ],
        "text": "The departmental PDF describes eDistrict registration, selecting the Revenue service, submitting the current form and retaining the acknowledgement; approval follows official verification."
      }
    ],
    "processing": null,
    "publicationStatus": "published",
    "reviewNotes": "Source reviewed by Codex on 2026-10-16. Selected facts only; unknown fields remain null. Document lists are partial and case-dependent. The website operator and responsible department are recorded separately. No transaction was submitted. The PDF names edistrict.py.gov.in, but normal TLS retrieval failed (curl 35). Information only. The document appendix appears mismatched; eligibility and document requirements remain unknown rather than using a guessed checklist.",
    "sourceUrl": "https://revenue.py.gov.in/downloads/EODB/EODB-Residence%20Certificate.pdf",
    "state": "Puducherry",
    "synonyms": [
      "residence certificate (puducherry)"
    ],
    "title": "Residence Certificate (Puducherry)",
    "verificationStatus": "verified",
    "verifiedOn": "2026-10-16",
    "websiteId": "igod-7c7865e8e6982bc1"
  },
  {
    "applicationMode": "information",
    "applicationRouteEvidence": {
      "checkedOn": "2026-10-16",
      "evidenceIds": [
        "epan-manual"
      ],
      "notes": "Manual is readable and reviewed; it provides navigation instructions without a directly observed transaction URL. Information only; no credentials or OTPs were entered.",
      "status": "unconfirmed"
    },
    "applicationUrl": null,
    "beneficiaryGroups": [
      {
        "evidenceIds": [
          "epan-manual"
        ],
        "text": "Individual taxpayers"
      }
    ],
    "categories": [
      "taxation"
    ],
    "citizenIntents": [
      "get a new instant e-PAN",
      "apply for instant PAN"
    ],
    "conditionalDocuments": null,
    "deadline": null,
    "department": "Income Tax Department",
    "description": "Official information about instant e-pan: new allotment.",
    "district": null,
    "documents": [
      {
        "evidenceIds": [
          "epan-manual"
        ],
        "text": "The current manual requires consent-based DigiLocker proof of date of birth from its prescribed alternatives; it is not a no-documents service."
      }
    ],
    "eligibility": {
      "evidenceIds": [
        "epan-manual"
      ],
      "text": "Selected prerequisites: an individual without an allotted PAN, not a minor or representative assessee; valid Aadhaar, linked mobile and DigiLocker access. Confirm all declarations in the current manual."
    },
    "evidence": [
      {
        "checkedOn": "2026-10-16",
        "contentSha256": "342b1f4313f2587bfbd5f4398716c9cc2dd104bdb1c9c531be2db8ef796d71a0",
        "id": "epan-manual",
        "locator": "Overview, prerequisites and sections 3.1-3.3",
        "publisher": "Income Tax Department",
        "summary": "Current prerequisites and separate new-PAN and download/status operations.",
        "url": "https://www.incometax.gov.in/iec/foportal/help/all-topics/e-filing-services/instant-e-pan/instant-UM?mobile-app=1"
      }
    ],
    "fees": {
      "evidenceIds": [
        "epan-manual"
      ],
      "text": "The manual describes new digitally signed e-PAN allotment as free of cost."
    },
    "id": "instant-epan-new",
    "informationUrl": "https://www.incometax.gov.in/iec/foportal/help/all-topics/e-filing-services/instant-e-pan/instant-UM?mobile-app=1",
    "level": "Central",
    "linkEvidenceIds": [
      "epan-manual"
    ],
    "organizationId": "org-13df6f9a70161c3e",
    "procedure": [
      {
        "evidenceIds": [
          "epan-manual"
        ],
        "text": "From official e-Filing Quick Links choose Instant e-PAN and Get New e-PAN, then follow the current declarations and consent workflow on the government portals."
      }
    ],
    "processing": null,
    "publicationStatus": "published",
    "reviewNotes": "Source reviewed by Codex on 2026-10-16. Selected facts only; unknown fields remain null. Document lists are partial and case-dependent. The website operator and responsible department are recorded separately. No transaction was submitted. Manual is readable and reviewed; it provides navigation instructions without a directly observed transaction URL. Information only; no credentials or OTPs were entered.",
    "sourceUrl": "https://www.incometax.gov.in/iec/foportal/help/all-topics/e-filing-services/instant-e-pan/instant-UM?mobile-app=1",
    "state": null,
    "synonyms": [
      "instant epan",
      "new PAN card"
    ],
    "title": "Instant e-PAN: new allotment",
    "verificationStatus": "verified",
    "verifiedOn": "2026-10-16",
    "websiteId": "income-tax-efiling"
  },
  {
    "applicationMode": "information",
    "applicationRouteEvidence": {
      "checkedOn": "2026-10-16",
      "evidenceIds": [
        "epan-manual"
      ],
      "notes": "A distinct operation for an existing Instant e-PAN request. Public transaction URL not confirmed; information only. JanSetu does not retrieve status or collect Aadhaar/OTP.",
      "status": "unconfirmed"
    },
    "applicationUrl": null,
    "beneficiaryGroups": null,
    "categories": [
      "taxation"
    ],
    "citizenIntents": [
      "download my instant e-PAN",
      "check instant PAN request"
    ],
    "conditionalDocuments": null,
    "deadline": null,
    "department": "Income Tax Department",
    "description": "Official information about instant e-pan: check request or download.",
    "district": null,
    "documents": null,
    "eligibility": null,
    "evidence": [
      {
        "checkedOn": "2026-10-16",
        "contentSha256": "342b1f4313f2587bfbd5f4398716c9cc2dd104bdb1c9c531be2db8ef796d71a0",
        "id": "epan-manual",
        "locator": "Overview, prerequisites and sections 3.1-3.3",
        "publisher": "Income Tax Department",
        "summary": "Current prerequisites and separate new-PAN and download/status operations.",
        "url": "https://www.incometax.gov.in/iec/foportal/help/all-topics/e-filing-services/instant-e-pan/instant-UM?mobile-app=1"
      }
    ],
    "fees": null,
    "id": "instant-epan-download",
    "informationUrl": "https://www.incometax.gov.in/iec/foportal/help/all-topics/e-filing-services/instant-e-pan/instant-UM?mobile-app=1",
    "level": "Central",
    "linkEvidenceIds": [
      "epan-manual"
    ],
    "organizationId": "org-13df6f9a70161c3e",
    "procedure": [
      {
        "evidenceIds": [
          "epan-manual"
        ],
        "text": "From official e-Filing Quick Links choose Instant e-PAN, then Check Status / Download e-PAN. The manual describes Aadhaar-linked mobile authentication on the official portal."
      }
    ],
    "processing": null,
    "publicationStatus": "published",
    "reviewNotes": "Source reviewed by Codex on 2026-10-16. Selected facts only; unknown fields remain null. Document lists are partial and case-dependent. The website operator and responsible department are recorded separately. No transaction was submitted. A distinct operation for an existing Instant e-PAN request. Public transaction URL not confirmed; information only. JanSetu does not retrieve status or collect Aadhaar/OTP.",
    "sourceUrl": "https://www.incometax.gov.in/iec/foportal/help/all-topics/e-filing-services/instant-e-pan/instant-UM?mobile-app=1",
    "state": null,
    "synonyms": [
      "epan download",
      "instant PAN status"
    ],
    "title": "Instant e-PAN: check request or download",
    "verificationStatus": "verified",
    "verifiedOn": "2026-10-16",
    "websiteId": "income-tax-efiling"
  },
  {
    "applicationMode": "information",
    "applicationRouteEvidence": {
      "checkedOn": "2026-10-16",
      "evidenceIds": [
        "voter-form6"
      ],
      "notes": "Readable official PDF reviewed; the voters portal response is a JavaScript shell, so no online Apply action or current enrollment-window claim is made.",
      "status": "unconfirmed"
    },
    "applicationUrl": null,
    "beneficiaryGroups": null,
    "categories": [
      "documents"
    ],
    "citizenIntents": [
      "register as a new voter",
      "first voter registration"
    ],
    "conditionalDocuments": null,
    "deadline": null,
    "department": "Election Commission of India / Electoral Registration Officer",
    "description": "Official Form 6 information for first inclusion in an electoral roll; the responsible Electoral Registration Officer decides the application.",
    "district": null,
    "documents": [
      {
        "evidenceIds": [
          "voter-form6"
        ],
        "text": "Selected evidence: photograph, age evidence and ordinary-residence evidence from the form alternatives. The form includes alternatives/field verification where specified evidence is unavailable."
      }
    ],
    "eligibility": null,
    "evidence": [
      {
        "checkedOn": "2026-10-16",
        "contentSha256": "2d89e57e2e530989e628872a19c1334a0d968e081ffb542c55e9336ca714a179",
        "id": "voter-form6",
        "locator": "Form 6: sections 7-8, declaration and accessibility notes",
        "publisher": "Election Commission of India",
        "summary": "New-elector application, evidence alternatives and declarations.",
        "url": "https://voters.eci.gov.in/formspdf/Form_6_English.pdf"
      }
    ],
    "fees": null,
    "id": "eci-new-elector-form6",
    "informationUrl": "https://voters.eci.gov.in/formspdf/Form_6_English.pdf",
    "level": "Central",
    "linkEvidenceIds": [
      "voter-form6"
    ],
    "organizationId": "org-e5d43c1e786f7b17",
    "procedure": null,
    "processing": null,
    "publicationStatus": "published",
    "reviewNotes": "Source reviewed by Codex on 2026-10-16. Selected facts only; unknown fields remain null. Document lists are partial and case-dependent. The website operator and responsible department are recorded separately. No transaction was submitted. Readable official PDF reviewed; the voters portal response is a JavaScript shell, so no online Apply action or current enrollment-window claim is made.",
    "sourceUrl": "https://voters.eci.gov.in/formspdf/Form_6_English.pdf",
    "state": null,
    "synonyms": [
      "Form 6",
      "new voter ID"
    ],
    "title": "New voter registration: Form 6",
    "verificationStatus": "verified",
    "verifiedOn": "2026-10-16",
    "websiteId": "igod-85affa23dc47255b"
  },
  {
    "applicationMode": "information",
    "applicationRouteEvidence": {
      "checkedOn": "2026-10-16",
      "evidenceIds": [
        "voter-form8"
      ],
      "notes": "Readable official PDF reviewed. Individual operation requirements differ; no single mandatory checklist or confirmed online Apply route is inferred.",
      "status": "unconfirmed"
    },
    "applicationUrl": null,
    "beneficiaryGroups": null,
    "categories": [
      "documents"
    ],
    "citizenIntents": [
      "correct my voter details",
      "change voter address"
    ],
    "conditionalDocuments": [
      {
        "evidenceIds": [
          "voter-form8"
        ],
        "text": "Evidence depends on the selected operation: residence proof for shifting, supporting proof for corrections, and the stated replacement/disability evidence where applicable."
      }
    ],
    "deadline": null,
    "department": "Election Commission of India / Electoral Registration Officer",
    "description": "Official Form 8 information for an existing electoral record: choose the appropriate correction, shifting, replacement or disability-marking operation.",
    "district": null,
    "documents": null,
    "eligibility": null,
    "evidence": [
      {
        "checkedOn": "2026-10-16",
        "contentSha256": "41d815f8462f48e130f001882b5a0c26e32540e8b2f6b7e589edd03c92c75c90",
        "id": "voter-form8",
        "locator": "Form 8: purpose selection, sections 1-4 and supporting-evidence instructions",
        "publisher": "Election Commission of India",
        "summary": "Existing-elector correction, shifting, replacement and disability-marking choices.",
        "url": "https://voters.eci.gov.in/formspdf/Form_8_English.pdf"
      }
    ],
    "fees": null,
    "id": "eci-elector-form8",
    "informationUrl": "https://voters.eci.gov.in/formspdf/Form_8_English.pdf",
    "level": "Central",
    "linkEvidenceIds": [
      "voter-form8"
    ],
    "organizationId": "org-e5d43c1e786f7b17",
    "procedure": null,
    "processing": null,
    "publicationStatus": "published",
    "reviewNotes": "Source reviewed by Codex on 2026-10-16. Selected facts only; unknown fields remain null. Document lists are partial and case-dependent. The website operator and responsible department are recorded separately. No transaction was submitted. Readable official PDF reviewed. Individual operation requirements differ; no single mandatory checklist or confirmed online Apply route is inferred.",
    "sourceUrl": "https://voters.eci.gov.in/formspdf/Form_8_English.pdf",
    "state": null,
    "synonyms": [
      "Form 8",
      "voter ID correction",
      "EPIC replacement"
    ],
    "title": "Voter record changes: Form 8",
    "verificationStatus": "verified",
    "verifiedOn": "2026-10-16",
    "websiteId": "igod-85affa23dc47255b"
  }
];

window.JANSETU_WEBSITES = [
  {
    "id": "india-portal",
    "title": "National Portal of India",
    "titleHi": "",
    "organization": "National Informatics Centre, Ministry of Electronics & Information Technology",
    "description": "Explore government information, services, schemes and official directories in one place.",
    "descriptionHi": "भारत के राष्ट्रीय पोर्टल से सरकारी जानकारी और सेवाओं के आधिकारिक स्रोत खोजें।",
    "url": "https://www.india.gov.in/",
    "categories": [
      "information"
    ],
    "level": "Central"
  },
  {
    "id": "digilocker",
    "title": "DigiLocker",
    "titleHi": "",
    "organization": "National eGovernance Division, Ministry of Electronics & Information Technology",
    "description": "Access and share digital documents through the Government of India's document wallet.",
    "descriptionHi": "डिजिलॉकर पर उपलब्ध जारीकर्ता संस्थाओं से डिजिटल दस्तावेज़ खोजें। खाता और दस्तावेज़ आधिकारिक साइट पर संभालें।",
    "url": "https://www.digilocker.gov.in/",
    "categories": [
      "documents"
    ],
    "level": "Central"
  },
  {
    "id": "cpgrams",
    "title": "CPGRAMS",
    "titleHi": "",
    "organization": "Department of Administrative Reforms & Public Grievances",
    "description": "Lodge and track public service grievances with central and state authorities.",
    "descriptionHi": "लोक शिकायत दर्ज करने और उसकी स्थिति देखने के लिए सीपीग्राम्स के आधिकारिक पोर्टल पर जाएँ।",
    "url": "https://pgportal.gov.in/",
    "categories": [
      "grievances"
    ],
    "level": "Central"
  },
  {
    "id": "national-career-service",
    "title": "National Career Service",
    "titleHi": "",
    "organization": "Ministry of Labour & Employment",
    "description": "Explore jobs, career guidance and employment services for job seekers and employers.",
    "descriptionHi": "राष्ट्रीय करियर सेवा पर रोज़गार, करियर और संबंधित जानकारी खोजें।",
    "url": "https://ncs.gov.in/",
    "categories": [
      "jobs"
    ],
    "level": "Central"
  },
  {
    "id": "national-scholarship-portal",
    "title": "National Scholarship Portal",
    "titleHi": "",
    "organization": "National Scholarship Portal",
    "description": "Find scholarship schemes and application resources for students.",
    "descriptionHi": "राष्ट्रीय छात्रवृत्ति पोर्टल पर छात्रवृत्ति संबंधी वर्तमान जानकारी और आधिकारिक आवेदन मार्ग खोजें।",
    "url": "https://scholarships.gov.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "udyam-registration",
    "title": "Udyam Registration",
    "titleHi": "",
    "organization": "Ministry of Micro, Small and Medium Enterprises",
    "description": "Access the official MSME registration portal and its registration information.",
    "descriptionHi": "उद्यम पंजीकरण की आधिकारिक वेबसाइट से छोटे व्यवसायों के पंजीकरण की जानकारी लें।",
    "url": "https://udyamregistration.gov.in/",
    "categories": [
      "business"
    ],
    "level": "Central"
  },
  {
    "id": "income-tax-efiling",
    "title": "Income Tax e-Filing",
    "titleHi": "",
    "organization": "Income Tax Department, Ministry of Finance",
    "description": "Find official income-tax e-filing, forms, help, and return services.",
    "descriptionHi": "आयकर विभाग के आधिकारिक पोर्टल से ई-फाइलिंग और संबंधित जानकारी देखें।",
    "url": "https://www.incometax.gov.in/iec/foportal/",
    "categories": [
      "taxation"
    ],
    "level": "Central"
  },
  {
    "id": "rti-online",
    "title": "RTI Online",
    "titleHi": "",
    "organization": "Department of Personnel and Training",
    "description": "Submit and track Right to Information requests and first appeals for central public authorities.",
    "descriptionHi": "आरटीआई ऑनलाइन पोर्टल पर उपलब्ध प्राधिकरणों के लिए सूचना के अधिकार संबंधी जानकारी और आवेदन मार्ग खोजें।",
    "url": "https://rtionline.gov.in/",
    "categories": [
      "transparency",
      "information"
    ],
    "level": "Central"
  },
  {
    "id": "uidai",
    "title": "UIDAI Aadhaar Services",
    "titleHi": "",
    "organization": "Unique Identification Authority of India",
    "description": "Find official Aadhaar information and routes to enrollment, updates, downloads, and security tools.",
    "descriptionHi": "यूआईडीएआई की आधिकारिक वेबसाइट पर आधार संबंधी जानकारी और सेवाओं के मार्ग देखें।",
    "url": "https://uidai.gov.in/en/my-aadhaar",
    "categories": [
      "documents"
    ],
    "level": "Central"
  },
  {
    "id": "igod-001375ad8214cb53",
    "title": "Kendriya Vidyalaya, Jhajjar, Haryana",
    "titleHi": "",
    "organization": "Kendriya Vidyalaya, Jhajjar, Haryana",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://jhajjar.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-003e99213248f460",
    "title": "Biotechnology Industry Research Assistance Council, New Delhi",
    "titleHi": "",
    "organization": "Biotechnology Industry Research Assistance Council, New Delhi",
    "description": "IGOD lists this website under Science, Technology & Research. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://birac.nic.in/",
    "categories": [
      "industry"
    ],
    "level": "Central"
  },
  {
    "id": "igod-005fbd1c80a9ec10",
    "title": "Kendriya Vidyalaya, Sector 47, SHIFT II, Chandigarh",
    "titleHi": "",
    "organization": "Kendriya Vidyalaya, Sector 47, SHIFT II, Chandigarh",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://chandigarhsec47shift2.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-00d095e24c325afd",
    "title": "PM SHRI Kendriya Vidyalaya Air Force Station Chabua, Assam",
    "titleHi": "",
    "organization": "PM SHRI Kendriya Vidyalaya Air Force Station Chabua, Assam",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://chabua.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-00f2c5de61369584",
    "title": "PM SHRI Kendriya Vidyalaya Khagaul, Patna, Bihar",
    "titleHi": "",
    "organization": "PM SHRI Kendriya Vidyalaya Khagaul, Patna, Bihar",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://khagaul.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-0109de09b8f0dbba",
    "title": "Financial Intelligence Unit (FIU-IND)",
    "titleHi": "",
    "organization": "Financial Intelligence Unit (FIU-IND)",
    "description": "IGOD lists this website under Attached / Subordinated Offices. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://fiuindia.gov.in/",
    "categories": [
      "taxation"
    ],
    "level": "Central"
  },
  {
    "id": "igod-010be207865cae2a",
    "title": "Directorate General of Aeronautical Quality Assurance (DGAQA), New Delhi",
    "titleHi": "",
    "organization": "Directorate General of Aeronautical Quality Assurance (DGAQA), New Delhi",
    "description": "IGOD lists this website under Attached / Subordinated Offices. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://dgaeroqa.gov.in/",
    "categories": [
      "safety"
    ],
    "level": "Central"
  },
  {
    "id": "igod-01a4993d95960e0a",
    "title": "Photo Division of PIB",
    "titleHi": "",
    "organization": "Photo Division of PIB",
    "description": "IGOD lists this website under Attached / Subordinated Offices. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://photodivision.gov.in/",
    "categories": [
      "information"
    ],
    "level": "Central"
  },
  {
    "id": "igod-01cd9c8ab38e81b8",
    "title": "India-USA Trade Facilitation Portal",
    "titleHi": "",
    "organization": "India-USA Trade Facilitation Portal",
    "description": "IGOD lists this website under International Affairs. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://indiausatrade.mea.gov.in/",
    "categories": [
      "industry"
    ],
    "level": "Central"
  },
  {
    "id": "igod-01eb2ba571deb4f9",
    "title": "Kendriya Vidyalaya Khatima, Udham Singh Nagar, Uttrakhand",
    "titleHi": "",
    "organization": "Kendriya Vidyalaya Khatima, Udham Singh Nagar, Uttrakhand",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://khatima.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-01ef94319b87318c",
    "title": "PM SHRI Kendriya Vidyalaya, Pragati Vihar (First Shift), Lodhi Road, Delhi",
    "titleHi": "",
    "organization": "PM SHRI Kendriya Vidyalaya, Pragati Vihar (First Shift), Lodhi Road, Delhi",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://pragativihar.kvs.ac.in/",
    "categories": [
      "transport"
    ],
    "level": "Central"
  },
  {
    "id": "igod-020ea898ff68227c",
    "title": "Kendriya Vidyalaya Gunupur, Odisha",
    "titleHi": "",
    "organization": "Kendriya Vidyalaya Gunupur, Odisha",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://gunupur.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-020f5c7e097e5a58",
    "title": "Kendriya Vidyalaya NHPC Gerukamukh, Assam",
    "titleHi": "",
    "organization": "Kendriya Vidyalaya NHPC Gerukamukh, Assam",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://gerukamukh.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-021ee7ef397434ec",
    "title": "All India Institute of Ayurveda (AIIA), New Delhi",
    "titleHi": "",
    "organization": "All India Institute of Ayurveda (AIIA), New Delhi",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://aiia.gov.in/",
    "categories": [
      "health"
    ],
    "level": "Central"
  },
  {
    "id": "igod-02501afc2a1817f5",
    "title": "National Commission for Women Applications",
    "titleHi": "",
    "organization": "National Commission for Women Applications",
    "description": "IGOD lists this website under Commissions/ Committees. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://ncwapps.nic.in/",
    "categories": [
      "welfare"
    ],
    "level": "Central"
  },
  {
    "id": "igod-026f0525c74ea90f",
    "title": "Kendriya Vidyalaya Namrup, Assam",
    "titleHi": "",
    "organization": "Kendriya Vidyalaya Namrup, Assam",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://namrupbvfcl.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-0290d1596660d787",
    "title": "Kendriya Vidyalaya Haldwani Cantt. Shift-2, Uttarakhand",
    "titleHi": "",
    "organization": "Kendriya Vidyalaya Haldwani Cantt. Shift-2, Uttarakhand",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://haldwanicanttshift2.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-02c9128de715df20",
    "title": "PM SHRI Kendriya Vidyalaya Etawah, Uttar Pradesh",
    "titleHi": "",
    "organization": "PM SHRI Kendriya Vidyalaya Etawah, Uttar Pradesh",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://etawa.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-02cffaa8a34b0148",
    "title": "National Institute of Pharmaceutical Education and Research (NIPER), Rae Bareli",
    "titleHi": "",
    "organization": "National Institute of Pharmaceutical Education and Research (NIPER), Rae Bareli",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://niperraebareli.edu.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-02ef645e3a651491",
    "title": "Kendriya Vidyalaya (NIT), Agartala, Tripura",
    "titleHi": "",
    "organization": "Kendriya Vidyalaya (NIT), Agartala, Tripura",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://nitagartala.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-03155927213482b9",
    "title": "PM SHRI Kendriya Vidyalaya, Air Force Site Arjangarh, Delhi",
    "titleHi": "",
    "organization": "PM SHRI Kendriya Vidyalaya, Air Force Site Arjangarh, Delhi",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://arjangarhafs.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-0315e752643d45a0",
    "title": "Swami Vivekanand National Institute of Rehabilitation Training and Research (SVNIRTAR), Olatpur",
    "titleHi": "",
    "organization": "Swami Vivekanand National Institute of Rehabilitation Training and Research (SVNIRTAR), Olatpur",
    "description": "IGOD lists this website under Autonomous Bodies. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://svnirtar.nic.in/",
    "categories": [
      "welfare"
    ],
    "level": "Central"
  },
  {
    "id": "igod-032ce5c1fe88a0c7",
    "title": "PM SHRI Kendriya Vidyalaya, Waltair, Andhra Pradesh",
    "titleHi": "",
    "organization": "PM SHRI Kendriya Vidyalaya, Waltair, Andhra Pradesh",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://waltair.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-03a6abe9970cd643",
    "title": "PM SHRI Kendriya Vidyalaya (INS), Zamorin Ezhimala, Kerala",
    "titleHi": "",
    "organization": "PM SHRI Kendriya Vidyalaya (INS), Zamorin Ezhimala, Kerala",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://ezhimala.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-03b4a2ed0d0e31f5",
    "title": "Andhra Pradesh State Centre, National Informatics Centre (NIC)",
    "titleHi": "",
    "organization": "Andhra Pradesh State Centre, National Informatics Centre (NIC)",
    "description": "IGOD lists this website under Communications & Information Technology. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://ap.nic.in/",
    "categories": [
      "information"
    ],
    "level": "Central"
  },
  {
    "id": "igod-03d60cb7133ebd7a",
    "title": "PM SHRI Kendriya Vidyalaya, Ongole, Andhra Pradesh",
    "titleHi": "",
    "organization": "PM SHRI Kendriya Vidyalaya, Ongole, Andhra Pradesh",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://ongole.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-04ef2b642494e924",
    "title": "PM SHRI Kendriya Vidyalaya (YOL), Kangra, Himachal Pradesh",
    "titleHi": "",
    "organization": "PM SHRI Kendriya Vidyalaya (YOL), Kangra, Himachal Pradesh",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://yolcantt.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-04f9e57f5859293a",
    "title": "Kendriya Vidyalaya Gopalganj, Bihar",
    "titleHi": "",
    "organization": "Kendriya Vidyalaya Gopalganj, Bihar",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://gopalganj.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-05262e3c21fcd302",
    "title": "PM SHRI Kendriya Vidyalaya Dinjan, Assam",
    "titleHi": "",
    "organization": "PM SHRI Kendriya Vidyalaya Dinjan, Assam",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://dinjan.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-05949065b70fc589",
    "title": "Kendriya Vidyalaya Pulgaon Camp, Maharashtra",
    "titleHi": "",
    "organization": "Kendriya Vidyalaya Pulgaon Camp, Maharashtra",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://pulgaoncamp.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-05abbb7d939867ec",
    "title": "Kendriya Vidyalaya Buxar, Bihar",
    "titleHi": "",
    "organization": "Kendriya Vidyalaya Buxar, Bihar",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://buxar.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-05abd38dd3553376",
    "title": "National Institute of Technology (NIT), Sikkim",
    "titleHi": "",
    "organization": "National Institute of Technology (NIT), Sikkim",
    "description": "IGOD lists this website under Autonomous Bodies. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://www.nitsikkim.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-05c2408474879a09",
    "title": "PM SHRI Kendriya Vidyalaya Jalore, Rajasthan",
    "titleHi": "",
    "organization": "PM SHRI Kendriya Vidyalaya Jalore, Rajasthan",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://jalore.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-05db841f04d33ca2",
    "title": "Controller General of Patents, Designs and Trade Marks",
    "titleHi": "",
    "organization": "Controller General of Patents, Designs and Trade Marks",
    "description": "IGOD lists this website under Attached / Subordinated Offices. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://ipindia.gov.in/",
    "categories": [
      "industry"
    ],
    "level": "Central"
  },
  {
    "id": "igod-062495ed7a820a45",
    "title": "Institute of Hotel Management, Catering and Nutrition, Kufri",
    "titleHi": "",
    "organization": "Institute of Hotel Management, Catering and Nutrition, Kufri",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://ihmshimla.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-06291b18ed067f50",
    "title": "Anubhav Portal",
    "titleHi": "",
    "organization": "Anubhav Portal",
    "description": "IGOD lists this website under Schemes / Programmes / Missions / Applications. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://pensionersportal.gov.in/anubhav",
    "categories": [
      "taxation"
    ],
    "level": "Central"
  },
  {
    "id": "igod-06432a091eed1cfa",
    "title": "National Institute of Electronics and Information Technology",
    "titleHi": "",
    "organization": "National Institute of Electronics and Information Technology",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://www.nielit.gov.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-064d092112607c35",
    "title": "Centre for Materials for Electronics Technology (C-MET)",
    "titleHi": "",
    "organization": "Centre for Materials for Electronics Technology (C-MET)",
    "description": "IGOD lists this website under Information & Broadcasting. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://cmet.gov.in/",
    "categories": [
      "information"
    ],
    "level": "Central"
  },
  {
    "id": "igod-06565c472066c7b4",
    "title": "Central Institute of Plastics Engineering and Technology (CIPET)",
    "titleHi": "",
    "organization": "Central Institute of Plastics Engineering and Technology (CIPET)",
    "description": "IGOD lists this website under Chemicals & Fertilizers. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://www.cipet.gov.in/",
    "categories": [
      "industry"
    ],
    "level": "Central"
  },
  {
    "id": "igod-066c1043826fee80",
    "title": "PM SHRI Kendriya Vidyalaya (NAD), Sunabeda, Odisha",
    "titleHi": "",
    "organization": "PM SHRI Kendriya Vidyalaya (NAD), Sunabeda, Odisha",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://sunabedanad.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-06c21514a088dee9",
    "title": "Upper Yamuna River Board",
    "titleHi": "",
    "organization": "Upper Yamuna River Board",
    "description": "IGOD lists this website under Attached / Subordinated Offices. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://jalshakti-dowr.gov.in/upper-yamuna-river-board",
    "categories": [
      "environment"
    ],
    "level": "Central"
  },
  {
    "id": "igod-06da770ed2748612",
    "title": "PM SHRI Kendriya Vidyalaya Amhat, Sultanpur, Uttar Pradesh",
    "titleHi": "",
    "organization": "PM SHRI Kendriya Vidyalaya Amhat, Sultanpur, Uttar Pradesh",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://amhat.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-073230b2e0377f6d",
    "title": "Central Institute of Buddhist Studies",
    "titleHi": "",
    "organization": "Central Institute of Buddhist Studies",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://cibs.ac.in/directors-desk",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-07369fa933d31e99",
    "title": "PM SHRI Kendriya Vidyalaya Aligarh, Uttar Pradesh",
    "titleHi": "",
    "organization": "PM SHRI Kendriya Vidyalaya Aligarh, Uttar Pradesh",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://aligarh.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-076e2536c8f41cfe",
    "title": "National Institute of Technical Teachers Training and Research (NITTTR), Bhopal",
    "titleHi": "",
    "organization": "National Institute of Technical Teachers Training and Research (NITTTR), Bhopal",
    "description": "IGOD lists this website under Autonomous Bodies. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://nitttrbpl.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-078539373ae0f844",
    "title": "PM SHRI Kendriya Vidyalaya Itarana Alwar, Rajasthan",
    "titleHi": "",
    "organization": "PM SHRI Kendriya Vidyalaya Itarana Alwar, Rajasthan",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://itarana.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-078623c3013bbc37",
    "title": "Kendriya Vidyalaya, Central University of Punjab, Ghudda (Bathinda), Punjab",
    "titleHi": "",
    "organization": "Kendriya Vidyalaya, Central University of Punjab, Ghudda (Bathinda), Punjab",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://cupbathinda.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-07c0a9107ffebcf9",
    "title": "PM SHRI Kendriya Vidyalaya (RRC), Fatehgarh, Uttar Pradesh",
    "titleHi": "",
    "organization": "PM SHRI Kendriya Vidyalaya (RRC), Fatehgarh, Uttar Pradesh",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://fatehgarh.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-07de02e6c79e3d47",
    "title": "Lal Bahadur Shastri National Academy of Administration (LBSNAA)",
    "titleHi": "",
    "organization": "Lal Bahadur Shastri National Academy of Administration (LBSNAA)",
    "description": "IGOD lists this website under Autonomous Bodies. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://www.lbsnaa.gov.in/",
    "categories": [
      "information"
    ],
    "level": "Central"
  },
  {
    "id": "igod-085019d46500884e",
    "title": "Directorate of Economics and Statistics (DES)",
    "titleHi": "",
    "organization": "Directorate of Economics and Statistics (DES)",
    "description": "IGOD lists this website under Attached / Subordinated Offices. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://desagri.gov.in/",
    "categories": [
      "information"
    ],
    "level": "Central"
  },
  {
    "id": "igod-08602a84859f0ee8",
    "title": "PM SHRI Kendriya Vidyalaya (AFS), Barnala, Punjab",
    "titleHi": "",
    "organization": "PM SHRI Kendriya Vidyalaya (AFS), Barnala, Punjab",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://afsbarnala.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-089bb5cd9a502a6d",
    "title": "Indian Pharmacopoeia Commission (IPC)",
    "titleHi": "",
    "organization": "Indian Pharmacopoeia Commission (IPC)",
    "description": "IGOD lists this website under Commissions/ Committees. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://ipc.gov.in/",
    "categories": [
      "health"
    ],
    "level": "Central"
  },
  {
    "id": "igod-08eccd4c8bb23126",
    "title": "PM SHRI Kendriya Vidyalaya, Rajampalli, Andhra Pradesh",
    "titleHi": "",
    "organization": "PM SHRI Kendriya Vidyalaya, Rajampalli, Andhra Pradesh",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://rajampalli.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-08eed065ab9727d2",
    "title": "Directorate General of Employment (DGE)",
    "titleHi": "",
    "organization": "Directorate General of Employment (DGE)",
    "description": "IGOD lists this website under Attached / Subordinated Offices. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://dge.gov.in/",
    "categories": [
      "jobs"
    ],
    "level": "Central"
  },
  {
    "id": "igod-09371c164f0541bd",
    "title": "PM SHRI Kendriya Vidyalaya (AFS), Bamrauli, Prayagraj, Uttar Pradesh",
    "titleHi": "",
    "organization": "PM SHRI Kendriya Vidyalaya (AFS), Bamrauli, Prayagraj, Uttar Pradesh",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://afsbamrauli.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-09643c04ed9a63bd",
    "title": "PM SHRI Kendriya Vidyalaya Dakshin Dinajpur, (Balurghat), West Bengal",
    "titleHi": "",
    "organization": "PM SHRI Kendriya Vidyalaya Dakshin Dinajpur, (Balurghat), West Bengal",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://balurghat.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-096bda02a32f362f",
    "title": "National Curriculum Frameworks (NCFs)",
    "titleHi": "",
    "organization": "National Curriculum Frameworks (NCFs)",
    "description": "IGOD lists this website under Councils. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://ncf.ncert.gov.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-09f2deb3eb8fe2b9",
    "title": "PM SHRI Kendriya Vidyalaya, Vikaspuri, Shift-I, Delhi",
    "titleHi": "",
    "organization": "PM SHRI Kendriya Vidyalaya, Vikaspuri, Shift-I, Delhi",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://vikaspuri.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-0a0c0c091b3159dc",
    "title": "Directorate General of Health Services",
    "titleHi": "",
    "organization": "Directorate General of Health Services",
    "description": "IGOD lists this website under Attached / Subordinated Offices. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://dghs.mohfw.gov.in/",
    "categories": [
      "health"
    ],
    "level": "Central"
  },
  {
    "id": "igod-0a24dc81a48d0108",
    "title": "Paradip Port Authority",
    "titleHi": "",
    "organization": "Paradip Port Authority",
    "description": "IGOD lists this website under Autonomous Bodies. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://paradipport.gov.in/",
    "categories": [
      "transport"
    ],
    "level": "Central"
  },
  {
    "id": "igod-0a35881188011dac",
    "title": "Kendriya Vidyalaya Sector 47, Ist Shift, Chandigarh",
    "titleHi": "",
    "organization": "Kendriya Vidyalaya Sector 47, Ist Shift, Chandigarh",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://chandigarhsec47.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-0a4f81c60008ed6c",
    "title": "Kendriya Vidyalaya Namsai, Arunachal Pradesh",
    "titleHi": "",
    "organization": "Kendriya Vidyalaya Namsai, Arunachal Pradesh",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://namsai.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-0aa79f0e4b7d7123",
    "title": "Central Vigilance Commission (CVC)",
    "titleHi": "",
    "organization": "Central Vigilance Commission (CVC)",
    "description": "IGOD lists this website under Commissions/ Committees. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://cvc.gov.in/",
    "categories": [
      "safety"
    ],
    "level": "Central"
  },
  {
    "id": "igod-0af51df4e54fc43d",
    "title": "Indian Institute of Information Technology (IIIT), Una",
    "titleHi": "",
    "organization": "Indian Institute of Information Technology (IIIT), Una",
    "description": "IGOD lists this website under Autonomous Bodies. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://iiitu.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-0b31e15b8353eaff",
    "title": "Composite Regional Centre for Skill Development, Rehabilitation and Empowerment of Persons with Disabilities (CRC), Andaman and Nicobar Islands",
    "titleHi": "",
    "organization": "Composite Regional Centre for Skill Development, Rehabilitation and Empowerment of Persons with Disabilities (CRC), Andaman and Nicobar Islands",
    "description": "IGOD lists this website under Statutory / Autonomous Bodies. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://crcan.nic.in/",
    "categories": [
      "welfare"
    ],
    "level": "Central"
  },
  {
    "id": "igod-0b9577aea437b234",
    "title": "Composite Regional Centre for Skill Development, Rehabilitation and Empowerment of Persons with Disabilities (CRC), Sikkim",
    "titleHi": "",
    "organization": "Composite Regional Centre for Skill Development, Rehabilitation and Empowerment of Persons with Disabilities (CRC), Sikkim",
    "description": "IGOD lists this website under Statutory / Autonomous Bodies. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://crcgangtok.nic.in/",
    "categories": [
      "welfare"
    ],
    "level": "Central"
  },
  {
    "id": "igod-0bc2d895a68f9955",
    "title": "PM SHRI Kendriya Vidyalaya Happy Valley, Shillong, Meghalaya",
    "titleHi": "",
    "organization": "PM SHRI Kendriya Vidyalaya Happy Valley, Shillong, Meghalaya",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://happyvalley.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-0bd896b96d883029",
    "title": "PM SHRI Kendriya Vidyalaya Khanpur, Punjab",
    "titleHi": "",
    "organization": "PM SHRI Kendriya Vidyalaya Khanpur, Punjab",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://khanpur.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-0be55b1d106e1197",
    "title": "PM SHRI Kendriya Vidyalaya Dungarpur, Rajasthan",
    "titleHi": "",
    "organization": "PM SHRI Kendriya Vidyalaya Dungarpur, Rajasthan",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://dungarpur.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-0bf4d792506be669",
    "title": "PM SHRI Kendriya Vidyalaya Hiranagar, Jammu and Kashmir",
    "titleHi": "",
    "organization": "PM SHRI Kendriya Vidyalaya Hiranagar, Jammu and Kashmir",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://hiranagar.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-0c0a79caec2b56f0",
    "title": "Kendriya Vidyalaya (DRDO), Bengaluru, Karnataka",
    "titleHi": "",
    "organization": "Kendriya Vidyalaya (DRDO), Bengaluru, Karnataka",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://drdobangalore.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-0c178c891be20e03",
    "title": "PM SHRI Kendriya Vidyalaya Island Grounds, Chennai-2, Tamil Nadu",
    "titleHi": "",
    "organization": "PM SHRI Kendriya Vidyalaya Island Grounds, Chennai-2, Tamil Nadu",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://islandgrounds.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-0c1e9116a93ee5ac",
    "title": "Central Institute of Higher Tibetan Studies, Sarnath",
    "titleHi": "",
    "organization": "Central Institute of Higher Tibetan Studies, Sarnath",
    "description": "IGOD lists this website under Art & Culture. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://www.cihts.ac.in/",
    "categories": [
      "information"
    ],
    "level": "Central"
  },
  {
    "id": "igod-0c24397ac212c756",
    "title": "PM SHRI Kendriya Vidyalaya, Ambikapur, Chhattisgarh",
    "titleHi": "",
    "organization": "PM SHRI Kendriya Vidyalaya, Ambikapur, Chhattisgarh",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://ambikapur.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-0c3551083bc546e8",
    "title": "PM SHRI Kendriya Vidyalaya K Area Zirakpur, Punjab",
    "titleHi": "",
    "organization": "PM SHRI Kendriya Vidyalaya K Area Zirakpur, Punjab",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://zirakpur.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-0c3b380607f1604b",
    "title": "Kendriya Vidyalaya Banaras Hindu University - Shift-1, Uttar Pradesh",
    "titleHi": "",
    "organization": "Kendriya Vidyalaya Banaras Hindu University - Shift-1, Uttar Pradesh",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://bhuvaranasi.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-0c3b7f78960d4471",
    "title": "National Board for Higher Mathematics (NBHM), Mumbai",
    "titleHi": "",
    "organization": "National Board for Higher Mathematics (NBHM), Mumbai",
    "description": "IGOD lists this website under PSUs / JVs / Companies / Societies. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://nbhm.dae.gov.in/",
    "categories": [
      "information"
    ],
    "level": "Central"
  },
  {
    "id": "igod-0c4848d5ec306fc4",
    "title": "National Horticulture Board (NHB)",
    "titleHi": "",
    "organization": "National Horticulture Board (NHB)",
    "description": "IGOD lists this website under PSUs / JVs / Companies / Societies. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://nhb.gov.in/",
    "categories": [
      "agriculture"
    ],
    "level": "Central"
  },
  {
    "id": "igod-0c4b56982d10a820",
    "title": "Kendriya Vidyalaya Bundi, Rajasthan",
    "titleHi": "",
    "organization": "Kendriya Vidyalaya Bundi, Rajasthan",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://bundi.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-0c874ccd5eab44f1",
    "title": "Integrated Pharmaceutical Database Management System 2.0 (IPDMS)",
    "titleHi": "",
    "organization": "Integrated Pharmaceutical Database Management System 2.0 (IPDMS)",
    "description": "IGOD lists this website under Chemicals & Fertilizers. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://nppaipdms.gov.in/",
    "categories": [
      "industry"
    ],
    "level": "Central"
  },
  {
    "id": "igod-0c977aa24c4f0ac7",
    "title": "Mission for Integrated Development of Horticulture (MIDH)",
    "titleHi": "",
    "organization": "Mission for Integrated Development of Horticulture (MIDH)",
    "description": "IGOD lists this website under Commissions/ Committees. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://midh.gov.in/",
    "categories": [
      "agriculture"
    ],
    "level": "Central"
  },
  {
    "id": "igod-0cd993c6a8bf92b7",
    "title": "National Institute of Electronics and Information Technology (NIELIT), Pali",
    "titleHi": "",
    "organization": "National Institute of Electronics and Information Technology (NIELIT), Pali",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://www.nielit.gov.in/pali",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-0ce274f304f22bf2",
    "title": "PM SHRI Kendriya Chh. Sambhajinagar Cantt. Shift-I, Maharashtra",
    "titleHi": "",
    "organization": "PM SHRI Kendriya Chh. Sambhajinagar Cantt. Shift-I, Maharashtra",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://aurangabadcantt.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-0cf778065eade414",
    "title": "PM SHRI Kendriya Vidyalaya (NEPA), Barapani, Meghalaya",
    "titleHi": "",
    "organization": "PM SHRI Kendriya Vidyalaya (NEPA), Barapani, Meghalaya",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://nepabarapani.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-0d1f3005e8b063da",
    "title": "Kendriya Vidyalaya No.4 (AFS), Jalandhar Cantt, Punjab",
    "titleHi": "",
    "organization": "Kendriya Vidyalaya No.4 (AFS), Jalandhar Cantt, Punjab",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://no4jalandhar.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-0d6a39dcad4295b1",
    "title": "NCERT Journals",
    "titleHi": "",
    "organization": "NCERT Journals",
    "description": "IGOD lists this website under Councils. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://ejournals.ncert.gov.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-0d7de0f5662553a0",
    "title": "PM SHRI Kendriya Vidyalaya Kamla Nehru Nagar, Ghaziabad Shift-1, Uttar Pradesh",
    "titleHi": "",
    "organization": "PM SHRI Kendriya Vidyalaya Kamla Nehru Nagar, Ghaziabad Shift-1, Uttar Pradesh",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://ghaziabad.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-0d9b0093a1e7661c",
    "title": "University Grants Commission (UGC)",
    "titleHi": "",
    "organization": "University Grants Commission (UGC)",
    "description": "IGOD lists this website under Autonomous Bodies. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://www.ugc.gov.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-0db21e9b953cfd35",
    "title": "PM SHRI Kendriya Vidyalaya, Pitampura (Shift II), Delhi",
    "titleHi": "",
    "organization": "PM SHRI Kendriya Vidyalaya, Pitampura (Shift II), Delhi",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://pitampurashift2.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-0ddd3bfc2a378c2b",
    "title": "PM SHRI Kendriya Vidyalaya Ambarnath Shift-2, Thane, Maharashtra",
    "titleHi": "",
    "organization": "PM SHRI Kendriya Vidyalaya Ambarnath Shift-2, Thane, Maharashtra",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://ofambarnathshift2.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-0e1901ad7d91d645",
    "title": "National Institute of Homoeopathy",
    "titleHi": "",
    "organization": "National Institute of Homoeopathy",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://nih.ayush.gov.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-0e38adbe52a9131e",
    "title": "Kendriya Vidyalaya Udupi, Karnataka",
    "titleHi": "",
    "organization": "Kendriya Vidyalaya Udupi, Karnataka",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://udupi.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-0e835902ceb8a539",
    "title": "National Legal Services Authority (NALSA)",
    "titleHi": "",
    "organization": "National Legal Services Authority (NALSA)",
    "description": "IGOD lists this website under PSUs / JVs / Companies / Societies. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://nalsa.gov.in/",
    "categories": [
      "courts"
    ],
    "level": "Central"
  },
  {
    "id": "igod-0f087baa18def20d",
    "title": "Satyajit Ray Film and Television Institute",
    "titleHi": "",
    "organization": "Satyajit Ray Film and Television Institute",
    "description": "IGOD lists this website under Autonomous Bodies. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://srfti.ac.in/",
    "categories": [
      "information"
    ],
    "level": "Central"
  },
  {
    "id": "igod-0f263952ce7358f9",
    "title": "U R Rao Satellite Centre (URSC), Bengaluru",
    "titleHi": "",
    "organization": "U R Rao Satellite Centre (URSC), Bengaluru",
    "description": "IGOD lists this website under Attached / Subordinated Offices. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://www.ursc.gov.in/",
    "categories": [
      "information"
    ],
    "level": "Central"
  },
  {
    "id": "igod-0f81066d9183a3ce",
    "title": "PM SHRI Kendriya Vidyalaya Babina Cantt., Uttar Pradesh",
    "titleHi": "",
    "organization": "PM SHRI Kendriya Vidyalaya Babina Cantt., Uttar Pradesh",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://babinacantt.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-0fc3def67d290d44",
    "title": "PM SHRI Kendriya Vidyalaya (GC), CRPF Bantalab, Jammu and Kashmir",
    "titleHi": "",
    "organization": "PM SHRI Kendriya Vidyalaya (GC), CRPF Bantalab, Jammu and Kashmir",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://crpfbantalab.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-0fdfa7a8a3308383",
    "title": "Kendriya Vidyalaya Bhanjanagar, Odisha",
    "titleHi": "",
    "organization": "Kendriya Vidyalaya Bhanjanagar, Odisha",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://bhanjnagar.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-10537db54fb4e6eb",
    "title": "PM SHRI Kendriya Vidyalaya Gomtinagar Shift-2, Lucknow, Uttar Pradesh",
    "titleHi": "",
    "organization": "PM SHRI Kendriya Vidyalaya Gomtinagar Shift-2, Lucknow, Uttar Pradesh",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://gomtinagarshift2.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-1060ed72fbc7fc05",
    "title": "Kendriya Vidyalaya (CURAJ), Bandarsindri, Rajasthan",
    "titleHi": "",
    "organization": "Kendriya Vidyalaya (CURAJ), Bandarsindri, Rajasthan",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://bandarsindari.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-10a9b90778508101",
    "title": "Indian Institute of Management (IIM), Tiruchirappalli",
    "titleHi": "",
    "organization": "Indian Institute of Management (IIM), Tiruchirappalli",
    "description": "IGOD lists this website under Autonomous Bodies. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://www.iimtrichy.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-10fd4a861728dfe2",
    "title": "Kendriya Vidyalaya Ukhrul, Manipur",
    "titleHi": "",
    "organization": "Kendriya Vidyalaya Ukhrul, Manipur",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://ukhrul.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-1152b340a5342c10",
    "title": "Kendriya Vidyalaya (BSF), Aradhpur, West Bengal",
    "titleHi": "",
    "organization": "Kendriya Vidyalaya (BSF), Aradhpur, West Bengal",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://bsfaradhpur.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-1187d6cc90d2af91",
    "title": "Know Your Standards, Bureau of Indian Standards",
    "titleHi": "",
    "organization": "Know Your Standards, Bureau of Indian Standards",
    "description": "IGOD lists this website under Attached / Subordinated Offices. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://standards.bis.gov.in/",
    "categories": [
      "welfare"
    ],
    "level": "Central"
  },
  {
    "id": "igod-1190ccc4a942d62e",
    "title": "Kendriya Vidyalaya (NTPC), Ramagundam, Telangana",
    "titleHi": "",
    "organization": "Kendriya Vidyalaya (NTPC), Ramagundam, Telangana",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://ramagundamntpc.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-11a2ab816b768645",
    "title": "PM SHRI Kendriya Vidyalaya Hebbal, Karnataka",
    "titleHi": "",
    "organization": "PM SHRI Kendriya Vidyalaya Hebbal, Karnataka",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://hebbalbangalore.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-11f09c4c8f6c4d2f",
    "title": "Kendriya Vidyalaya No.4, Sagar (DHSGVV), Madhya Pradesh",
    "titleHi": "",
    "organization": "Kendriya Vidyalaya No.4, Sagar (DHSGVV), Madhya Pradesh",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://sagarno4hsgv.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-11f6e9e0acb935aa",
    "title": "PM SHRI Kendriya Vidyalaya Chhatarpur, Madhya Pradesh",
    "titleHi": "",
    "organization": "PM SHRI Kendriya Vidyalaya Chhatarpur, Madhya Pradesh",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://chhattarpur.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-1242d09b995ab70b",
    "title": "PM SHRI Kendriya Vidyalaya (AFS), Hakimpet, Telangana",
    "titleHi": "",
    "organization": "PM SHRI Kendriya Vidyalaya (AFS), Hakimpet, Telangana",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://hakimpet.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-127eb61468d51945",
    "title": "Kendriya Vidyalaya Alhilal, Himachal Pradesh",
    "titleHi": "",
    "organization": "Kendriya Vidyalaya Alhilal, Himachal Pradesh",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://alhilal.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-12925c91eecffd9c",
    "title": "PM SHRI Kendriya Vidyalaya Bamangachi, West Bengal",
    "titleHi": "",
    "organization": "PM SHRI Kendriya Vidyalaya Bamangachi, West Bengal",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://bamangachi.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-12a9f10291d84f94",
    "title": "PM SHRI Kendriya Vidyalaya, Bhakli, Haryana",
    "titleHi": "",
    "organization": "PM SHRI Kendriya Vidyalaya, Bhakli, Haryana",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://bhakli.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-12b87ae0d6db9aac",
    "title": "Kendriya Vidyalaya (NTPC Anta AGPP), Rajasthan",
    "titleHi": "",
    "organization": "Kendriya Vidyalaya (NTPC Anta AGPP), Rajasthan",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://agppanta.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-12d10b2c34e6f3e3",
    "title": "Agricultural Scientists Recruitment Board",
    "titleHi": "",
    "organization": "Agricultural Scientists Recruitment Board",
    "description": "IGOD lists this website under Commissions/ Committees. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://asrb.gov.in/",
    "categories": [
      "jobs"
    ],
    "level": "Central"
  },
  {
    "id": "igod-12d3226daa6f5b52",
    "title": "PM SHRI Kendriya Vidyalaya Janjgir, Chhattisgarh",
    "titleHi": "",
    "organization": "PM SHRI Kendriya Vidyalaya Janjgir, Chhattisgarh",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://janjgir.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-12e71c9e30d82276",
    "title": "International Institute for Population Sciences (IIPS)",
    "titleHi": "",
    "organization": "International Institute for Population Sciences (IIPS)",
    "description": "IGOD lists this website under Autonomous Bodies. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://www.iipsdata.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-12f942cc1daa217b",
    "title": "National Institute of Technology (NIT), Durgapur",
    "titleHi": "",
    "organization": "National Institute of Technology (NIT), Durgapur",
    "description": "IGOD lists this website under Autonomous Bodies. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://nitdgp.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-1336f89f443eacf4",
    "title": "Composite Regional Centre for Persons with Disabilities (CRC), Davangere",
    "titleHi": "",
    "organization": "Composite Regional Centre for Persons with Disabilities (CRC), Davangere",
    "description": "IGOD lists this website under Statutory / Autonomous Bodies. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://crcdavanagere.nic.in/",
    "categories": [
      "welfare"
    ],
    "level": "Central"
  },
  {
    "id": "igod-13738e969e8459e6",
    "title": "Indian Institute of Technology (IIT), Madras",
    "titleHi": "",
    "organization": "Indian Institute of Technology (IIT), Madras",
    "description": "IGOD lists this website under Autonomous Bodies. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://www.iitm.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-1376522dd901cc39",
    "title": "Aid Accounts and Audit Division, New Delhi",
    "titleHi": "",
    "organization": "Aid Accounts and Audit Division, New Delhi",
    "description": "IGOD lists this website under Divisions / Units / Wings. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://caaa.gov.in/",
    "categories": [
      "taxation"
    ],
    "level": "Central"
  },
  {
    "id": "igod-1379942bce0cbc37",
    "title": "Indian Railways Institute of Mechanical and Electrical Engineering (IRIMEE), Jamalpur",
    "titleHi": "",
    "organization": "Indian Railways Institute of Mechanical and Electrical Engineering (IRIMEE), Jamalpur",
    "description": "IGOD lists this website under Autonomous Bodies. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://irimee.indianrailways.gov.in/",
    "categories": [
      "transport"
    ],
    "level": "Central"
  },
  {
    "id": "igod-1385d7f16f92b5c7",
    "title": "All India Institute of Medical Sciences (AIIMS), Raipur",
    "titleHi": "",
    "organization": "All India Institute of Medical Sciences (AIIMS), Raipur",
    "description": "IGOD lists this website under Autonomous Bodies. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://www.aiimsraipur.edu.in/",
    "categories": [
      "health"
    ],
    "level": "Central"
  },
  {
    "id": "igod-13a192a319af139c",
    "title": "PM SHRI Kendriya Vidyalaya Bagafa, Tripura",
    "titleHi": "",
    "organization": "PM SHRI Kendriya Vidyalaya Bagafa, Tripura",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://bsfbagafa.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-13c4dc687dbefee2",
    "title": "PM SHRI Kendriya Vidyalaya Barrackpore (Army), West Bengal",
    "titleHi": "",
    "organization": "PM SHRI Kendriya Vidyalaya Barrackpore (Army), West Bengal",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://armybarrackpore.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-13df6e36c39c912b",
    "title": "Kendriya Vidyalaya 205 CoBRA Bn. CRPF Barachatti, Bihar",
    "titleHi": "",
    "organization": "Kendriya Vidyalaya 205 CoBRA Bn. CRPF Barachatti, Bihar",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://205cbbarachatti.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-13ecef1aaaa30b35",
    "title": "Indian Institute of Information Technology, Design and Manufacturing (IIITDM), Kurnool",
    "titleHi": "",
    "organization": "Indian Institute of Information Technology, Design and Manufacturing (IIITDM), Kurnool",
    "description": "IGOD lists this website under Autonomous Bodies. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://iiitk.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-13fbb079499411b2",
    "title": "Administrative Training Institute",
    "titleHi": "",
    "organization": "Administrative Training Institute",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://ati.dae.gov.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-13fde33f4acb6eda",
    "title": "PM SHRI Kendriya Vidyalaya Gooty, Ananthapur, Andhra Pradesh",
    "titleHi": "",
    "organization": "PM SHRI Kendriya Vidyalaya Gooty, Ananthapur, Andhra Pradesh",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://gootyanantpur.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-144877ca1bcae512",
    "title": "AI Impact Summit 2026",
    "titleHi": "",
    "organization": "AI Impact Summit 2026",
    "description": "IGOD lists this website under Others. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://www.impactexpo.indiaai.gov.in/",
    "categories": [
      "information"
    ],
    "level": "Central"
  },
  {
    "id": "igod-14bcbf932322a401",
    "title": "Sardar Vallabhbhai National Institute of Technology, Surat",
    "titleHi": "",
    "organization": "Sardar Vallabhbhai National Institute of Technology, Surat",
    "description": "IGOD lists this website under Autonomous Bodies. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://www.svnit.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-14dfb6533a4a99b9",
    "title": "PM SHRI Kendriya Vidyalaya, Tamulpur, Assam",
    "titleHi": "",
    "organization": "PM SHRI Kendriya Vidyalaya, Tamulpur, Assam",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://tamulpur.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-15f307ed251d8698",
    "title": "Malaviya National Institute of Technology (MNIT), Jaipur",
    "titleHi": "",
    "organization": "Malaviya National Institute of Technology (MNIT), Jaipur",
    "description": "IGOD lists this website under Autonomous Bodies. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://www.mnit.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-160036f18453adc6",
    "title": "PM SHRI Kendriya Vidyalaya Kankinara, West Bengal",
    "titleHi": "",
    "organization": "PM SHRI Kendriya Vidyalaya Kankinara, West Bengal",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://kankinara.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-16167a80e28e38be",
    "title": "Kendriya Vidyalaya (BSF), Raninagar, West Bengal",
    "titleHi": "",
    "organization": "Kendriya Vidyalaya (BSF), Raninagar, West Bengal",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://bsfraninagar.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-164fa068916c396c",
    "title": "PM SHRI Kendriya Vidyalaya Kashipur, Uttrakhand",
    "titleHi": "",
    "organization": "PM SHRI Kendriya Vidyalaya Kashipur, Uttrakhand",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://kashipur.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-16bf98bfa81eca51",
    "title": "Kendriya Vidyalaya Aminoo, Kulgam Jammu and Kashmir",
    "titleHi": "",
    "organization": "Kendriya Vidyalaya Aminoo, Kulgam Jammu and Kashmir",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://aminoo.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-16d6c6320a5e8002",
    "title": "PM SHRI Kendriya Vidyalaya (BLW), Varanasi, Uttar Pradesh",
    "titleHi": "",
    "organization": "PM SHRI Kendriya Vidyalaya (BLW), Varanasi, Uttar Pradesh",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://dlwvaranasi.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-16e40f193abed7bf",
    "title": "National Institute for Micro, Small and Medium Enterprises",
    "titleHi": "",
    "organization": "National Institute for Micro, Small and Medium Enterprises",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://www.nimsme.gov.in/",
    "categories": [
      "industry"
    ],
    "level": "Central"
  },
  {
    "id": "igod-16f753056e6a9ae4",
    "title": "Central Drugs Standard Control Organization (CDSCO)",
    "titleHi": "",
    "organization": "Central Drugs Standard Control Organization (CDSCO)",
    "description": "IGOD lists this website under PSUs / JVs / Companies / Societies. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://cdsco.gov.in/",
    "categories": [
      "health"
    ],
    "level": "Central"
  },
  {
    "id": "igod-17224a46aaa84d2e",
    "title": "North Eastern Hill University (NEHU), Shillong",
    "titleHi": "",
    "organization": "North Eastern Hill University (NEHU), Shillong",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://www.nehu.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-17235289165b06fd",
    "title": "PM SHRI Kendriya Vidyalaya, Masimpur, Assam",
    "titleHi": "",
    "organization": "PM SHRI Kendriya Vidyalaya, Masimpur, Assam",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://masimpur.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-1756152a81eb3c94",
    "title": "Principal Chief Commissioner of Central Tax, Bengaluru Zone Karnataka",
    "titleHi": "",
    "organization": "Principal Chief Commissioner of Central Tax, Bengaluru Zone Karnataka",
    "description": "IGOD lists this website under Commissions/ Committees. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://gstkarnataka.gov.in/",
    "categories": [
      "taxation"
    ],
    "level": "Central"
  },
  {
    "id": "igod-176e5f983413ca7d",
    "title": "Kendriya Vidyalaya Alirajpur, Madhya Pradesh",
    "titleHi": "",
    "organization": "Kendriya Vidyalaya Alirajpur, Madhya Pradesh",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://alirajpur.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-1776b634108c587c",
    "title": "National Bee Board (NBB)",
    "titleHi": "",
    "organization": "National Bee Board (NBB)",
    "description": "IGOD lists this website under PSUs / JVs / Companies / Societies. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://nbb.gov.in/",
    "categories": [
      "agriculture"
    ],
    "level": "Central"
  },
  {
    "id": "igod-17d69b9edf2eb6d5",
    "title": "Central Railways",
    "titleHi": "",
    "organization": "Central Railways",
    "description": "IGOD lists this website under Regional / Field Offices. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://cr.indianrailways.gov.in/",
    "categories": [
      "transport"
    ],
    "level": "Central"
  },
  {
    "id": "igod-1855bebf75ec1303",
    "title": "Kendriya Vidyalaya (CRPF), Peringome, Kerala",
    "titleHi": "",
    "organization": "Kendriya Vidyalaya (CRPF), Peringome, Kerala",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://peringomecrpf.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-185796e403e9d07b",
    "title": "Indian Institute of Information Technology (IIIT), Bhagalpur",
    "titleHi": "",
    "organization": "Indian Institute of Information Technology (IIIT), Bhagalpur",
    "description": "IGOD lists this website under Autonomous Bodies. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://iiitbh.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-18649bde195e5a01",
    "title": "CPWD Sewa - Central Public Works Department (CPWD)",
    "titleHi": "",
    "organization": "CPWD Sewa - Central Public Works Department (CPWD)",
    "description": "IGOD lists this website under Housing & Urban Development. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://cpwdsewa.gov.in/",
    "categories": [
      "land"
    ],
    "level": "Central"
  },
  {
    "id": "igod-189d1a1146e76366",
    "title": "Indian Academy of Highway Engineers (IAHE)",
    "titleHi": "",
    "organization": "Indian Academy of Highway Engineers (IAHE)",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://iahe.morth.gov.in/",
    "categories": [
      "transport"
    ],
    "level": "Central"
  },
  {
    "id": "igod-18af5e506c0b2ec6",
    "title": "Central Board of Secondary Education - Academics",
    "titleHi": "",
    "organization": "Central Board of Secondary Education - Academics",
    "description": "IGOD lists this website under Statutory / Autonomous Bodies. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://cbseacademic.nic.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-18bbc75ca133ffd5",
    "title": "PM SHRI Kendriya Vidyalaya Inderpura, Rajasthan",
    "titleHi": "",
    "organization": "PM SHRI Kendriya Vidyalaya Inderpura, Rajasthan",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://inderpura.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-18e25f62fc7ed17f",
    "title": "PM SHRI Kendriya Vidyalaya, No.2, Delhi Cantt., (Shift II), Delhi",
    "titleHi": "",
    "organization": "PM SHRI Kendriya Vidyalaya, No.2, Delhi Cantt., (Shift II), Delhi",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://no2delhicanttshift2.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-18ec3b27dffb8ee2",
    "title": "National Institute of Technology (NIT), Kurukshetra",
    "titleHi": "",
    "organization": "National Institute of Technology (NIT), Kurukshetra",
    "description": "IGOD lists this website under Autonomous Bodies. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://nitkkr.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-18f87dc7a0c3c04a",
    "title": "PM SHRI Kendriya Vidyalaya, No.1, Shahibaug, Ahmedabad, Gujarat",
    "titleHi": "",
    "organization": "PM SHRI Kendriya Vidyalaya, No.1, Shahibaug, Ahmedabad, Gujarat",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://mrcahmedabad.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-199491107c2ba05d",
    "title": "PM SHRI Kendriya Vidyalaya (NAD), Aluva, Kerala",
    "titleHi": "",
    "organization": "PM SHRI Kendriya Vidyalaya (NAD), Aluva, Kerala",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://nadalwaye.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-19afd52a3652620c",
    "title": "Kendriya Vidyalaya, Matanhail, Haryana",
    "titleHi": "",
    "organization": "Kendriya Vidyalaya, Matanhail, Haryana",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://matanhail.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-19baada5a48ead15",
    "title": "Kendriya Vidyalaya Baddowal Cantt, Punjab",
    "titleHi": "",
    "organization": "Kendriya Vidyalaya Baddowal Cantt, Punjab",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://baddowalcantt.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-19db1d8e632c5e89",
    "title": "PM SHRI Kendriya Vidyalaya Bolarum, Telangana",
    "titleHi": "",
    "organization": "PM SHRI Kendriya Vidyalaya Bolarum, Telangana",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://bolarum.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-19dbecb7d1288ccf",
    "title": "Kendriya Vidyalaya No.2, Dhanbad, Jharkhand",
    "titleHi": "",
    "organization": "Kendriya Vidyalaya No.2, Dhanbad, Jharkhand",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://no2dhanbad.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-1a9541f8d50fb35f",
    "title": "Central Institute of Coastal Engineering for Fishery, Bangalore",
    "titleHi": "",
    "organization": "Central Institute of Coastal Engineering for Fishery, Bangalore",
    "description": "IGOD lists this website under Animal Husbandry & Fishing. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://cicef.gov.in/",
    "categories": [
      "agriculture"
    ],
    "level": "Central"
  },
  {
    "id": "igod-1ac55dd7a3f0a107",
    "title": "New Mangalore Port Authority, Panambur",
    "titleHi": "",
    "organization": "New Mangalore Port Authority, Panambur",
    "description": "IGOD lists this website under Autonomous Bodies. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://newmangaloreport.gov.in/",
    "categories": [
      "transport"
    ],
    "level": "Central"
  },
  {
    "id": "igod-1ac563e7af8f372e",
    "title": "PM SHRI Kendriya Vidyalaya Central Railway Solapur, Maharashtra",
    "titleHi": "",
    "organization": "PM SHRI Kendriya Vidyalaya Central Railway Solapur, Maharashtra",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://sholapur.kvs.ac.in/",
    "categories": [
      "transport"
    ],
    "level": "Central"
  },
  {
    "id": "igod-1ae1d928b222f070",
    "title": "PM SHRI Kendriya Vidyalaya 14 GTC, Subathu, Solan, Himachal Pradesh",
    "titleHi": "",
    "organization": "PM SHRI Kendriya Vidyalaya 14 GTC, Subathu, Solan, Himachal Pradesh",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://subathu.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-1aea31f131dc49aa",
    "title": "Kendriya Vidyalaya, Air Force Station, Darjeepura, Gujarat",
    "titleHi": "",
    "organization": "Kendriya Vidyalaya, Air Force Station, Darjeepura, Gujarat",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://afsdarjeepura.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-1b340671fb321fdc",
    "title": "National Disaster Response Force (NDRF)",
    "titleHi": "",
    "organization": "National Disaster Response Force (NDRF)",
    "description": "IGOD lists this website under Attached / Subordinated Offices. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://www.ndrf.gov.in/",
    "categories": [
      "safety"
    ],
    "level": "Central"
  },
  {
    "id": "igod-1b38b9d3f8ac3275",
    "title": "Indian Railways Institute of Electrical Engineering (IRIEEN)",
    "titleHi": "",
    "organization": "Indian Railways Institute of Electrical Engineering (IRIEEN)",
    "description": "IGOD lists this website under Autonomous Bodies. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://irieen.indianrailways.gov.in/",
    "categories": [
      "transport"
    ],
    "level": "Central"
  },
  {
    "id": "igod-1b75867677052629",
    "title": "Kendriya Vidyalaya No.1, Gwalior Shift-II, Madhya Pradesh",
    "titleHi": "",
    "organization": "Kendriya Vidyalaya No.1, Gwalior Shift-II, Madhya Pradesh",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://no1gwaliorshift2.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-1b911b08d27df6e0",
    "title": "PM SHRI Kendriya Vidyalaya (BSF), Panisagar, Tripura",
    "titleHi": "",
    "organization": "PM SHRI Kendriya Vidyalaya (BSF), Panisagar, Tripura",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://bsfpanisagar.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-1c0a3743cd3338d4",
    "title": "District Development Coordination and Monitoring Committees (DISHA)",
    "titleHi": "",
    "organization": "District Development Coordination and Monitoring Committees (DISHA)",
    "description": "IGOD lists this website under Commissions/ Committees. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://disha.gov.in/",
    "categories": [
      "agriculture"
    ],
    "level": "Central"
  },
  {
    "id": "igod-1c3669702fe3f35a",
    "title": "PM SHRI Kendriya Vidyalaya Aizawl, Mizoram",
    "titleHi": "",
    "organization": "PM SHRI Kendriya Vidyalaya Aizawl, Mizoram",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://aizawal.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-1c540891a2ad438b",
    "title": "Kendriya Vidyalaya (ONGC), Cambay, Gujarat",
    "titleHi": "",
    "organization": "Kendriya Vidyalaya (ONGC), Cambay, Gujarat",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://ongccambey.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-1c55a99a4c33843e",
    "title": "PM SHRI Kendriya Vidyalaya, Sector-4, RK Puram, Delhi",
    "titleHi": "",
    "organization": "PM SHRI Kendriya Vidyalaya, Sector-4, RK Puram, Delhi",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://rkpuramsec4.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-1c86caacf9970ec2",
    "title": "National Ethics Committee Registry for Biomedical and Health Research (NECRBHR)",
    "titleHi": "",
    "organization": "National Ethics Committee Registry for Biomedical and Health Research (NECRBHR)",
    "description": "IGOD lists this website under Commissions/ Committees. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://naitik.gov.in/",
    "categories": [
      "health"
    ],
    "level": "Central"
  },
  {
    "id": "igod-1cd10633f0e6abc3",
    "title": "ICFRE-Forest Research Institute (FRI), Dehradun",
    "titleHi": "",
    "organization": "ICFRE-Forest Research Institute (FRI), Dehradun",
    "description": "IGOD lists this website under Forestry & Wildlife. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://fri.icfre.gov.in/",
    "categories": [
      "environment"
    ],
    "level": "Central"
  },
  {
    "id": "igod-1dab46f90e209082",
    "title": "Dera Baba Nanak - Sri Kartarpur Sahib Pilgrimage",
    "titleHi": "",
    "organization": "Dera Baba Nanak - Sri Kartarpur Sahib Pilgrimage",
    "description": "IGOD lists this website under Tourism. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://prakashpurb550.mha.gov.in/",
    "categories": [
      "safety"
    ],
    "level": "Central"
  },
  {
    "id": "igod-1e216627f330bb85",
    "title": "PM SHRI Kendriya Vidyalaya (CRPF), Mudkhed, Nanded, Maharashtra",
    "titleHi": "",
    "organization": "PM SHRI Kendriya Vidyalaya (CRPF), Mudkhed, Nanded, Maharashtra",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://crpfmudkhed.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-1e5a1b06e527fdb9",
    "title": "University of Delhi",
    "titleHi": "",
    "organization": "University of Delhi",
    "description": "IGOD lists this website under Autonomous Bodies. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://www.du.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-1e67843d948605a8",
    "title": "PM SHRI Kendriya Vidyalaya (AMC Shift-2), Lucknow, Uttar Pradesh",
    "titleHi": "",
    "organization": "PM SHRI Kendriya Vidyalaya (AMC Shift-2), Lucknow, Uttar Pradesh",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://amclucknowshift2.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-1e718f0ef527aca3",
    "title": "PM SHRI Kendriya Vidyalaya, Vasant Kunj, Shift II, Delhi",
    "titleHi": "",
    "organization": "PM SHRI Kendriya Vidyalaya, Vasant Kunj, Shift II, Delhi",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://vasantkunjshift2.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-1eb3cbb45de499b9",
    "title": "Jagjivan Ram RPF Academy, Lucknow",
    "titleHi": "",
    "organization": "Jagjivan Ram RPF Academy, Lucknow",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://jrrpfa.indianrailways.gov.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-1eba74e6edae0f32",
    "title": "PM SHRI Kendriya Vidyalaya, Shalimar Bagh Shift II, Delhi",
    "titleHi": "",
    "organization": "PM SHRI Kendriya Vidyalaya, Shalimar Bagh Shift II, Delhi",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://shalimarbaghshift2.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-1ecaf221205d0248",
    "title": "National Council of Science Museums (NCSM)",
    "titleHi": "",
    "organization": "National Council of Science Museums (NCSM)",
    "description": "IGOD lists this website under Autonomous Bodies. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://ncsm.gov.in/",
    "categories": [
      "information"
    ],
    "level": "Central"
  },
  {
    "id": "igod-1ed6641b5cdb5aab",
    "title": "National Institute of Fashion Technology (NIFT), Jodhpur",
    "titleHi": "",
    "organization": "National Institute of Fashion Technology (NIFT), Jodhpur",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://www.nift.ac.in/jodhpur",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-1ee47f67381086cf",
    "title": "National Institute of Technology (NIT), Meghalaya",
    "titleHi": "",
    "organization": "National Institute of Technology (NIT), Meghalaya",
    "description": "IGOD lists this website under Autonomous Bodies. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://www.nitm.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-1eea7db1d0dacb7b",
    "title": "Kendriya Vidyalaya Assam University, Assam",
    "titleHi": "",
    "organization": "Kendriya Vidyalaya Assam University, Assam",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://assamuniversity.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-1f22238ada12d76f",
    "title": "PM SHRI Kendriya Vidyalaya, Andrews Ganj (Shift-1), Delhi",
    "titleHi": "",
    "organization": "PM SHRI Kendriya Vidyalaya, Andrews Ganj (Shift-1), Delhi",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://andrewsganj.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-1f5ce2f3cf55f7d5",
    "title": "Indian Institute of Information Technology (IIIT), Kalyani",
    "titleHi": "",
    "organization": "Indian Institute of Information Technology (IIIT), Kalyani",
    "description": "IGOD lists this website under Autonomous Bodies. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://iiitkalyani.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-1f6af370f5a12887",
    "title": "PM SHRI Kendriya Vidyalaya Damohi, Madhya Pradesh",
    "titleHi": "",
    "organization": "PM SHRI Kendriya Vidyalaya Damohi, Madhya Pradesh",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://damoh.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-1f99ab1209975894",
    "title": "PM SHRI Kendriya Vidyalaya Ordnance Equipment Factory, Hazratpur, Uttar Pradesh",
    "titleHi": "",
    "organization": "PM SHRI Kendriya Vidyalaya Ordnance Equipment Factory, Hazratpur, Uttar Pradesh",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://oefhazaratpur.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-1fa74c4d86873fbb",
    "title": "Indian Institute of Information Technology (IIIT), Manipur",
    "titleHi": "",
    "organization": "Indian Institute of Information Technology (IIIT), Manipur",
    "description": "IGOD lists this website under Autonomous Bodies. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://iiitmanipur.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  },
  {
    "id": "igod-1fb55dc98db551ad",
    "title": "PM SHRI Kendriya Vidyalaya Dhana, Madhya Pradesh",
    "titleHi": "",
    "organization": "PM SHRI Kendriya Vidyalaya Dhana, Madhya Pradesh",
    "description": "IGOD lists this website under Academies / Institutions. Open the official destination for current information.",
    "descriptionHi": "",
    "url": "https://dhana.kvs.ac.in/",
    "categories": [
      "education"
    ],
    "level": "Central"
  }
];
