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
    private fun fetchInvoice(id: Int): Invoice? {
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
        val result =  transaction(db) {
            InvoiceTable.update(
                    where = { InvoiceTable.id.eq(entity.id) }
            ) {
                it[this.value] = entity.amount.value
                it[this.currency] = entity.amount.currency.toString()
                it[this.status] = entity.status.toString()
            }
        }
        // assuming that the integer value will be 1 for true and 0 for false otherwise it would be the id of the entity(then -1 would probably mean false), i found it really hard to find a answer when googling exposed update integer return value
        return result == 1
    }

    private fun fetchInvoices(): List<Invoice> {
        return  transaction(db) {
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
