package io.pleo.antaeus.core.queries.customer
import io.pleo.antaeus.core.exceptions.CustomerNotFoundException
import io.pleo.antaeus.models.Customer
import io.pleo.antaeus.core.queries.IQueryWithInput
import io.pleo.antaeus.data.IReadRepository

class FetchCustomerByIdQuery(private val repository: IReadRepository<Customer, Int>) : IQueryWithInput<Int, Customer>{
    override fun execute(input: Int): Customer {
        return repository.fetchById(input) ?: throw CustomerNotFoundException(input)
    }
}