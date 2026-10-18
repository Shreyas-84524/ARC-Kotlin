package com.arc.arc.contracts

import com.arc.arc.core.model.DateTimeUtils
import com.arc.arc.core.model.TransactionModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Date

/**
 * Data contract tests verifying Transaction model compatibility with Flutter Hive Type 3
 * and Firestore `users/{uid}/transactions` schema.
 */
class TransactionContractTest {

    @Test
    fun testCompleteTransactionSerializationRoundTrip() {
        val now = Date(1773000000000L)
        val txDate = Date(1772900000000L)

        val tx = TransactionModel(
            id = "tx-uuid-555",
            ownerUid = "uid-fin-001",
            amount = 1250.75,
            type = "Expense",
            category = "Food",
            date = txDate,
            time = "12:30",
            notes = "Lunch with team",
            createdAt = now,
            source = "manual",
            automationFingerprint = null,
        )

        val map = tx.toMap()
        assertEquals("tx-uuid-555", map["id"])
        assertEquals(1250.75, map["amount"] as Double, 0.001)
        assertEquals("Expense", map["type"])
        assertEquals("Food", map["category"])
        assertEquals(DateTimeUtils.formatIso(txDate), map["date"])
        assertEquals("12:30", map["time"])
        assertEquals("Lunch with team", map["notes"])
        assertEquals(DateTimeUtils.formatIso(now), map["createdAt"])
        assertEquals("manual", map["source"])
        assertNull(map["automationFingerprint"])

        val restored = TransactionModel.fromMap(map, ownerUid = "uid-fin-001")
        assertEquals(tx.id, restored.id)
        assertEquals(tx.ownerUid, restored.ownerUid)
        assertEquals(tx.amount, restored.amount, 0.001)
        assertEquals(tx.type, restored.type)
        assertEquals(tx.category, restored.category)
        assertEquals(tx.time, restored.time)
        assertEquals(tx.notes, restored.notes)
        assertEquals(tx.source, restored.source)
        assertFalse(restored.isIncome)
    }

    @Test
    fun testAutoDetectedSmsTransactionContract() {
        val now = Date(1773000000000L)
        val tx = TransactionModel(
            id = "tx-auto-999",
            ownerUid = "uid-fin-002",
            amount = 45000.0,
            type = "Income",
            category = "Salary",
            date = now,
            time = "09:00",
            notes = "Salary credited",
            createdAt = now,
            source = "sms_auto",
            automationFingerprint = "sms_hash_abc123xyz",
        )

        val map = tx.toMap()
        assertEquals("sms_auto", map["source"])
        assertEquals("sms_hash_abc123xyz", map["automationFingerprint"])

        val restored = TransactionModel.fromMap(map, ownerUid = "uid-fin-002")
        assertEquals("sms_auto", restored.source)
        assertEquals("sms_hash_abc123xyz", restored.automationFingerprint)
        assertTrue(restored.isIncome)
    }

    @Test
    fun testTransactionSparseDocumentDefaults() {
        val sparse = mapOf<String, Any?>(
            "id" to "tx-sparse",
            "amount" to 50, // int in map
        )

        val restored = TransactionModel.fromMap(sparse, ownerUid = "uid-0")
        assertEquals("tx-sparse", restored.id)
        assertEquals(50.0, restored.amount, 0.001)
        assertEquals("Expense", restored.type)
        assertEquals("Other", restored.category)
        assertEquals("manual", restored.source)
        assertNotNull(restored.date)
        assertNotNull(restored.createdAt)
    }
}
