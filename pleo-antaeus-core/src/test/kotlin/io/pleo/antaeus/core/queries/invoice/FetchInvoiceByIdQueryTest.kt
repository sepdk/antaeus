
package io.pleo.antaeus.core.services

import io.mockk.every
import io.mockk.mockk
import io.pleo.antaeus.core.exceptions.InvoiceNotFoundException
import io.pleo.antaeus.core.queries.invoice.FetchInvoiceByIdQuery
import io.pleo.antaeus.data.IReadRepository
import io.pleo.antaeus.models.Invoice
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class FetchInvoiceByIdQueryTest {
    private val repository = mockk<IReadRepository<Invoice, Int>> {
        every { fetchById(404) } returns null
    }

    private val invoiceService = FetchInvoiceByIdQuery(repository = repository)

    @Test
    fun `will throw if invoice is not found`() {
        assertThrows<InvoiceNotFoundException> {
            invoiceService.execute(404)
        }
    }
}

