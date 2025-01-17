package io.pleo.antaeus.core.queries.invoice
import io.pleo.antaeus.core.exceptions.InvoiceNotFoundException
import io.pleo.antaeus.core.queries.IQueryWithInput
import io.pleo.antaeus.data.IReadRepository
import io.pleo.antaeus.models.Invoice

class FetchInvoiceByIdQuery(private val repository: IReadRepository<Invoice, Int>) : IQueryWithInput<Int, Invoice>{
    override fun execute(input: Int): Invoice {
        return repository.fetchById(input) ?: throw InvoiceNotFoundException(input)
    }
}