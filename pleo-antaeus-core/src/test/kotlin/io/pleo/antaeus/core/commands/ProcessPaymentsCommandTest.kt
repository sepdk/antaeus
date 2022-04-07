
package io.pleo.antaeus.core.commands

import io.mockk.*
import io.pleo.antaeus.core.commands.payment.ProcessPaymentsCommand
import io.pleo.antaeus.core.exceptions.CurrencyMismatchException
import io.pleo.antaeus.core.exceptions.CustomerNotFoundException
import io.pleo.antaeus.core.exceptions.NetworkException
import io.pleo.antaeus.data.IRepository
import io.pleo.antaeus.logger.ILogger
import io.pleo.antaeus.models.Currency
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceStatus
import io.pleo.antaeus.models.Money
import io.pleo.antaeus.service.IPaymentService
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ProcessPaymentsCommandTest {
    private val pendingInvoiceMock = Invoice(1, 1, Money(BigDecimal.valueOf(1000), Currency.DKK), InvoiceStatus.PENDING)
    private val updatedPendingInvoiceMock = Invoice(1, 1, Money(BigDecimal.valueOf(1000), Currency.DKK), InvoiceStatus.PAID)
    private val paidInvoiceMock = Invoice(2, 2, Money(BigDecimal.valueOf(1000), Currency.DKK), InvoiceStatus.PAID)
    private val repository = mockk<IRepository<Invoice, Int>>{
        every { fetchAll() } returns listOf(pendingInvoiceMock, paidInvoiceMock)
    }
    private val paymentService = mockk<IPaymentService>()
    private val logger = mockk<ILogger>(){
        every { error(any()) } returns Unit
        every { warn(any()) } returns Unit
    }
    private val firstDayOfMonth = LocalDate.parse("2022-01-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    private val processPaymentsCommand = ProcessPaymentsCommand(paymentService = paymentService, invoiceRepository =  repository, logger = logger)


    @Test
    @Order(1)
    fun `will return before executing due to date not being the first of the month`() {
        clearMocks(paymentService)

        val date = LocalDate.parse("2022-01-05", DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        processPaymentsCommand.execute(date)

        verify(exactly = 0) { repository.fetchAll() }
        verify(exactly = 0) { repository.update(any()) }
        verify(exactly = 0) { paymentService.charge(any()) }
    }

    @Test
    @Order(2)
    fun `will run success scenario one pending`() {
        clearMocks(paymentService)
        every { paymentService.charge(pendingInvoiceMock) } returns true

        processPaymentsCommand.execute(firstDayOfMonth)

        verify(exactly = 1) { repository.fetchAll() }
        verify(exactly = 1) { repository.update(updatedPendingInvoiceMock) }
        verify(exactly = 1) { paymentService.charge(pendingInvoiceMock) }

        confirmVerified(repository)
        confirmVerified(paymentService)
    }

    @Test
    @Order(3)
    fun `will log customer cannot be charged`() {
        clearMocks(paymentService)
        every { paymentService.charge(pendingInvoiceMock) } returns false

        processPaymentsCommand.execute(firstDayOfMonth)

        verify(exactly = 1) { repository.fetchAll() }
        verify(exactly = 0) { repository.update(updatedPendingInvoiceMock) }
        verify(exactly = 1) { paymentService.charge(pendingInvoiceMock) }
        verify(exactly = 1) { logger.warn("Customer account balance did not allow charge while processing invoice with id ${pendingInvoiceMock.id}.") }

        confirmVerified(repository)
        confirmVerified(paymentService)
        confirmVerified(logger)
    }

    @Test
    @Order(4)
    fun `will log if customer is not found`() {
        clearMocks(paymentService)
        every { paymentService.charge(pendingInvoiceMock) }.throws(CustomerNotFoundException(pendingInvoiceMock.customerId))

        processPaymentsCommand.execute(firstDayOfMonth)

        verify(exactly = 1) { repository.fetchAll() }
        verify(exactly = 0) { repository.update(updatedPendingInvoiceMock) }
        verify(exactly = 1) { paymentService.charge(pendingInvoiceMock) }
        verify(exactly = 1) { logger.error("Customer not found with id ${pendingInvoiceMock.customerId} while processing invoice with id ${pendingInvoiceMock.id}.") }

        confirmVerified(repository)
        confirmVerified(paymentService)
        confirmVerified(logger)
    }

    @Test
    @Order(5)
    fun `will log currency mismatch`() {
        clearMocks(paymentService)
        every { paymentService.charge(pendingInvoiceMock) }.throws(CurrencyMismatchException(pendingInvoiceMock.id, pendingInvoiceMock.customerId))

        processPaymentsCommand.execute(firstDayOfMonth)

        verify(exactly = 1) { repository.fetchAll() }
        verify(exactly = 0) { repository.update(updatedPendingInvoiceMock) }
        verify(exactly = 1) { paymentService.charge(pendingInvoiceMock) }
        verify(exactly = 1) { logger.error("The currency doesn't match the customer account while processing invoice with id ${pendingInvoiceMock.id}.") }

        confirmVerified(repository)
        confirmVerified(paymentService)
        confirmVerified(logger)
    }

    @Test
    @Order(6)
    fun `will log network exception`() {
        clearMocks(paymentService)
        every { paymentService.charge(pendingInvoiceMock) }.throws(NetworkException())

        processPaymentsCommand.execute(firstDayOfMonth)

        verify(exactly = 1) { repository.fetchAll() }
        verify(exactly = 0) { repository.update(updatedPendingInvoiceMock) }
        verify(exactly = 1) { paymentService.charge(pendingInvoiceMock) }
        verify(exactly = 1) { logger.error("A network exception occurred while processing invoice with id ${pendingInvoiceMock.id}.") }

        confirmVerified(repository)
        confirmVerified(paymentService)
        confirmVerified(logger)
    }

    @Test
    @Order(7)
    fun `will log exception on charge`() {
        clearMocks(paymentService)
        every { paymentService.charge(pendingInvoiceMock) }.throws(Exception())

        processPaymentsCommand.execute(firstDayOfMonth)

        verify(exactly = 1) { repository.fetchAll() }
        verify(exactly = 0) { repository.update(updatedPendingInvoiceMock) }
        verify(exactly = 1) { paymentService.charge(pendingInvoiceMock) }
        verify(exactly = 1) { logger.error("Unexpected exception occurred while processing invoice with id ${pendingInvoiceMock.id}.") }

        confirmVerified(repository)
        confirmVerified(paymentService)
        confirmVerified(logger)
    }

    @Test
    @Order(8)
    fun `will log exception on fetchAllInvoices`() {
        clearMocks(paymentService, repository)
        every { repository.fetchAll() }.throws(Exception())

        processPaymentsCommand.execute(firstDayOfMonth)

        verify(exactly = 1) { repository.fetchAll() }
        verify(exactly = 0) { repository.update(updatedPendingInvoiceMock) }
        verify(exactly = 0) { paymentService.charge(pendingInvoiceMock) }
        verify(exactly = 1) { logger.error("Unexpected exception occurred in SchedulePaymentsCommand") }

        confirmVerified(repository)
        confirmVerified(paymentService)
        confirmVerified(logger)
    }
}

