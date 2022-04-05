package io.pleo.antaeus.core.queries.invoice
import io.pleo.antaeus.core.exceptions.InvoiceNotFoundException
import io.pleo.antaeus.core.queries.IQueryWithInput
import io.pleo.antaeus.data.IRepository
import io.pleo.antaeus.models.Invoice

class FetchInvoiceByIdQuery(private val repository: IRepository<Invoice?, Int>) : IQueryWithInput<Int, Invoice>{
    override fun execute(id: Int): Invoice {
        return repository.fetchById(id) ?: throw InvoiceNotFoundException(id)
    }
}