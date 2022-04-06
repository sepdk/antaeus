package io.pleo.antaeus.core.queries.customer
import io.pleo.antaeus.core.queries.IQuery
import io.pleo.antaeus.models.Customer
import io.pleo.antaeus.data.IReadRepository

class FetchAllCustomersQuery(private val repository: IReadRepository<Customer, Int>) : IQuery<List<Customer>> {
    override fun execute(): List<Customer> {
        return repository.fetchAll()
    }
}