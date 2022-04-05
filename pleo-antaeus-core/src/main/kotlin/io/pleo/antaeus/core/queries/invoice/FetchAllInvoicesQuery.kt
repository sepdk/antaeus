package io.pleo.antaeus.core.queries.invoice
import io.pleo.antaeus.core.queries.IQuery
import io.pleo.antaeus.models.Customer
import io.pleo.antaeus.data.IRepository
import io.pleo.antaeus.models.Invoice

class FetchAllInvoicesQuery(private val repository: IRepository<Invoice, Int>) : IQuery<List<Invoice>> {
    override fun execute(): List<Invoice> {
        return repository.fetchAll()
    }
}