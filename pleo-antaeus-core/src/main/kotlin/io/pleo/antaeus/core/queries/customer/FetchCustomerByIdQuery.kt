package io.pleo.antaeus.core.queries.customer
import io.pleo.antaeus.core.exceptions.CustomerNotFoundException
import io.pleo.antaeus.models.Customer
import io.pleo.antaeus.core.queries.IQueryWithInput
import io.pleo.antaeus.data.IRepository

class FetchCustomerByIdQuery(private val repository: IRepository<Customer, Int>) : IQueryWithInput<Int, Customer>{
    override fun execute(id: Int): Customer {
        return repository.fetchById(id) ?: throw CustomerNotFoundException(id)
    }
}