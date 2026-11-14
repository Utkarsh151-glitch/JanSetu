package `in`.jansetu.app.vault

import `in`.jansetu.app.catalog.Service
import `in`.jansetu.app.catalog.ServiceEvidence
import kotlinx.serialization.Serializable
import java.security.MessageDigest
import java.time.Instant
import java.util.UUID

/** How the service record lists a document. JanSetu never upgrades a conditional or unknown item to required. */
enum class RequirementKind(val label: String) {
    REQUIRED("Listed as required"),
    CONDITIONAL("Needed only in some cases")
}

/** One document requirement exactly as recorded, with its sources. [key] changes whenever the wording changes. */
data class RequirementItem(val key: String, val kind: RequirementKind, val text: String, val sources: List<ServiceEvidence>)

/** Requirements recorded for a service. A null list means the record does not confirm that kind of document. */
data class ServiceRequirements(val serviceId: String, val required: List<RequirementItem>?, val conditional: List<RequirementItem>?) {
    val items: List<RequirementItem> get() = required.orEmpty() + conditional.orEmpty()
    val hasAny: Boolean get() = items.isNotEmpty()
}

/** A file the person chose for one requirement. It records their choice only; nobody has checked the file. */
@Serializable
data class PreparationChoice(
    val id: String, val serviceId: String, val requirementKey: String, val requirementText: String,
    val documentId: String, val chosenAt: String,
) {
    override fun toString() = "PreparationChoice(redacted)"
}

data class PreparedItem(val requirement: RequirementItem, val chosen: List<Pair<PreparationChoice, VaultDocument?>>)

/** The preparation view: current requirements with choices, plus earlier choices whose requirement wording changed. */
data class PreparationView(val requirements: ServiceRequirements, val items: List<PreparedItem>,
    val outdatedChoices: List<Pair<PreparationChoice, VaultDocument?>>) {
    val itemsWithChoice: Int get() = items.count { it.chosen.isNotEmpty() }
    /** Counts items with a chosen file. It never says a requirement is met. */
    fun summary(): String = if (items.isEmpty()) "No document requirements are recorded for this service."
        else "You chose files for $itemsWithChoice of ${items.size} listed item${if (items.size == 1) "" else "s"}. JanSetu has not checked whether any file meets a requirement."
}

object Preparation {
    fun requirements(service: Service): ServiceRequirements {
        val evidence = service.evidence.associateBy { it.id }
        fun items(kind: RequirementKind, facts: List<`in`.jansetu.app.catalog.ServiceFact>?) = facts?.map { fact ->
            RequirementItem(key(service.id, kind, fact.text), kind, fact.text, fact.evidenceIds.mapNotNull(evidence::get))
        }
        return ServiceRequirements(service.id, items(RequirementKind.REQUIRED, service.documents),
            items(RequirementKind.CONDITIONAL, service.conditionalDocuments))
    }

    /** Stable for identical wording; a correction to the requirement creates a new key, so old choices are not carried over silently. */
    fun key(serviceId: String, kind: RequirementKind, text: String): String {
        val normalised = text.trim().replace(Regex("\\s+"), " ")
        return MessageDigest.getInstance("SHA-256").digest("$serviceId|${kind.name}|$normalised".toByteArray())
            .joinToString("") { "%02x".format(it) }.take(24)
    }

    fun view(requirements: ServiceRequirements, choices: List<PreparationChoice>, documents: List<VaultDocument>): PreparationView {
        val byId = documents.associateBy { it.id }
        val forService = choices.filter { it.serviceId == requirements.serviceId }.sortedBy { it.chosenAt }
        val keys = requirements.items.map { it.key }.toSet()
        return PreparationView(requirements,
            requirements.items.map { item -> PreparedItem(item, forService.filter { it.requirementKey == item.key }.map { it to byId[it.documentId] }) },
            forService.filter { it.requirementKey !in keys }.map { it to byId[it.documentId] })
    }

    fun newChoice(serviceId: String, item: RequirementItem, documentId: String, now: Instant): PreparationChoice =
        PreparationChoice(UUID.randomUUID().toString(), serviceId, item.key, item.text, documentId, now.toString())

    fun validateStored(choice: PreparationChoice) {
        UUID.fromString(choice.id); UUID.fromString(choice.documentId); Instant.parse(choice.chosenAt)
        require(choice.serviceId.matches(Regex("[a-z0-9][a-z0-9-]{2,100}")) && choice.requirementKey.matches(Regex("[0-9a-f]{24}")))
        require(choice.requirementText.isNotBlank() && choice.requirementText.length <= 2000)
    }
}
