package `in`.jansetu.app.ui

/**
 * Editorial shortcuts chosen by the JanSetu team to show the kinds of tasks
 * the catalog covers. They are not ranked by, or derived from, usage data.
 * Each query is checked against the bundled catalog by HomeShortcutsTest.
 */
data class TaskShortcut(val query: String, val title: String, val titleHi: String, val illustration: Illustration)

val HomeTaskShortcuts = listOf(
    TaskShortcut("income certificate", "Income certificate", "आय प्रमाण पत्र", Illustration.CERTIFICATES),
    TaskShortcut("scholarship", "Scholarships", "छात्रवृत्ति", Illustration.EDUCATION),
    TaskShortcut("government jobs", "Find a job", "नौकरी खोजें", Illustration.EMPLOYMENT),
    TaskShortcut("pension", "Pension schemes", "पेंशन योजनाएँ", Illustration.PENSIONS),
    TaskShortcut("udyam msme registration", "Register a business", "व्यवसाय पंजीकरण", Illustration.BUSINESS),
    TaskShortcut("public grievance", "File a complaint", "शिकायत दर्ज करें", Illustration.GRIEVANCES)
)
