package com.arc.arc.core.model

import java.util.Date
import java.util.UUID

/**
 * Native Kotlin representation of ARC's Transaction model.
 * Preserves 100% schema compatibility with Flutter Hive Type 3 and Firestore `users/{uid}/transactions`.
 */
data class TransactionModel(
    val id: String = UUID.randomUUID().toString(),
    val ownerUid: String = "",
    val amount: Double = 0.0,
    val type: String = "Expense",
    val category: String = "Other",
    val date: Date = Date(),
    val time: String = "",
    val notes: String = "",
    val createdAt: Date = Date(),
    val source: String = "manual",
    val automationFingerprint: String? = null,
) {
    val isIncome: Boolean
        get() = type.equals("Income", ignoreCase = true)

    fun toMap(): Map<String, Any?> {
        val map = mutableMapOf<String, Any?>()
        map["id"] = id
        map["amount"] = amount
        map["type"] = type
        map["category"] = category
        map["date"] = DateTimeUtils.formatIso(date)
        map["time"] = time
        map["notes"] = notes
        map["createdAt"] = DateTimeUtils.formatIso(createdAt)
        map["source"] = source
        map["automationFingerprint"] = automationFingerprint
        return map
    }

    companion object {
        fun fromMap(map: Map<String, Any?>, ownerUid: String = ""): TransactionModel {
            val now = Date()
            val amountVal = when (val a = map["amount"]) {
                is Number -> a.toDouble()
                else -> 0.0
            }
            val dateParsed = (map["date"] as? String)?.let { DateTimeUtils.parseIso(it) } ?: now
            val createdAtDate = (map["createdAt"] as? String)?.let { DateTimeUtils.parseIso(it) } ?: now

            return TransactionModel(
                id = (map["id"] as? String)?.takeIf { it.isNotBlank() } ?: UUID.randomUUID().toString(),
                ownerUid = ownerUid,
                amount = amountVal,
                type = (map["type"] as? String) ?: "Expense",
                category = (map["category"] as? String) ?: "Other",
                date = dateParsed,
                time = (map["time"] as? String) ?: "",
                notes = (map["notes"] as? String) ?: "",
                createdAt = createdAtDate,
                source = (map["source"] as? String) ?: "manual",
                automationFingerprint = map["automationFingerprint"] as? String,
            )
        }
    }
}
