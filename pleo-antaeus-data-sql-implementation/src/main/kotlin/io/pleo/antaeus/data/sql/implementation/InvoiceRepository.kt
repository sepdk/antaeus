/*
    Implements the data access layer (DAL).
    The data access layer generates and executes requests to the database.

    See the `mappings` module for the conversions between database rows and Kotlin objects.
 */

package io.pleo.antaeus.data.sql.implementation

import io.pleo.antaeus.data.IRepository
import io.pleo.antaeus.models.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class InvoiceRepository(private val db: Database) : IRepository<Invoice, Int> {
    fun fetchInvoice(id: Int): Invoice? {
        // transaction(db) runs the internal query as a new database transaction.
        return transaction(db) {
            // Returns the first invoice with matching id.
            InvoiceTable
                .select { InvoiceTable.id.eq(id) }
                .firstOrNull()
                ?.toInvoice()
        }
    }

    override fun fetchById(id: Int): Invoice? {
        return fetchInvoice(id)
    }

    override fun fetchAll(): List<Invoice> {
        return fetchInvoices()
    }

    override fun update(entity: Invoice): Boolean {
        //TODO google how to do this correctly and how to return a boolean result
        val id = transaction(db) {
            // Insert the invoice and returns its new id.
            InvoiceTable
                    .update {
                        it[this.value] = entity.amount.value
                        it[this.currency] = entity.amount.currency.toString()
                        it[this.status] = status.toString()
                    }
        }
        return id == 1
    }

    fun fetchInvoices(): List<Invoice> {
        return transaction(db) {
            InvoiceTable
                .selectAll()
                .map { it.toInvoice() }
        }
    }


    fun createInvoice(amount: Money, customer: Customer, status: InvoiceStatus = InvoiceStatus.PENDING): Invoice? {
        val id = transaction(db) {
            // Insert the invoice and returns its new id.
            InvoiceTable
                .insert {
                    it[this.value] = amount.value
                    it[this.currency] = amount.currency.toString()
                    it[this.status] = status.toString()
                    it[this.customerId] = customer.id
                } get InvoiceTable.id
        }

        return fetchInvoice(id)
    }
}
