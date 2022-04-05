/*
    Defines the main() entry point of the app.
    Configures the database and sets up the REST web service.
 */

@file:JvmName("AntaeusApp")

package io.pleo.antaeus.app

import getPaymentProvider
import io.pleo.antaeus.core.queries.customer.FetchAllCustomersQuery
import io.pleo.antaeus.core.queries.customer.FetchCustomerByIdQuery
import io.pleo.antaeus.core.queries.invoice.FetchAllInvoicesQuery
import io.pleo.antaeus.core.queries.invoice.FetchInvoiceByIdQuery
import io.pleo.antaeus.core.services.BillingService
import io.pleo.antaeus.data.sql.implementation.CustomerRepository
import io.pleo.antaeus.data.sql.implementation.CustomerTable
import io.pleo.antaeus.data.sql.implementation.InvoiceRepository
import io.pleo.antaeus.data.sql.implementation.InvoiceTable
import io.pleo.antaeus.rest.AntaeusRest
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import setupInitialData
import java.io.File
import java.sql.Connection

fun main() {
    // The tables to create in the database.
    val tables = arrayOf(InvoiceTable, CustomerTable)

    val dbFile: File = File.createTempFile("antaeus-db", ".sqlite")
    // Connect to the database and create the needed tables. Drop any existing data.
    val db = Database
        .connect(url = "jdbc:sqlite:${dbFile.absolutePath}",
            driver = "org.sqlite.JDBC",
            user = "root",
            password = "")
        .also {
            TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
            transaction(it) {
                addLogger(StdOutSqlLogger)
                // Drop all existing tables to ensure a clean slate on each run
                SchemaUtils.drop(*tables)
                // Create all tables
                SchemaUtils.create(*tables)
            }
        }

    // Set up data access layer.
    val customerRepository = CustomerRepository(db = db)
    val invoiceRepository = InvoiceRepository(db = db)

    // Insert example data in the database.
    setupInitialData(customerRepository = customerRepository, invoiceRepository =  invoiceRepository)

    // Get third parties
    val paymentProvider = getPaymentProvider()


    // Create core services
    val getCustomersQuery= FetchAllCustomersQuery(repository = customerRepository)
    val getcustomerByIdQuery = FetchCustomerByIdQuery(repository = customerRepository)
    val getInvoicesQuery = FetchAllInvoicesQuery(repository = invoiceRepository)
    val getInvoiceByIdQuery = FetchInvoiceByIdQuery(repository = invoiceRepository)

    // This is _your_ billing service to be included where you see fit
    val billingService = BillingService(paymentProvider = paymentProvider)

    // Create REST web service
    AntaeusRest(
        getInvoiceByIdQuery = getInvoiceByIdQuery,
        getInvoicesQuery = getInvoicesQuery,
        getCustomersQuery = getCustomersQuery,
        getcustomerByIdQuery = getcustomerByIdQuery
    ).run()
}
