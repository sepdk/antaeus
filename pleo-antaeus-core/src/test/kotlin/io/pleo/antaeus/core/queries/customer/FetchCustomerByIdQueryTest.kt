
package io.pleo.antaeus.core.queries

import io.mockk.every
import io.mockk.mockk
import io.pleo.antaeus.core.exceptions.CustomerNotFoundException
import io.pleo.antaeus.core.queries.customer.FetchCustomerByIdQuery
import io.pleo.antaeus.data.IReadRepository
import io.pleo.antaeus.models.Customer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class FetchCustomerByIdQueryTest {
    private val repository = mockk<IReadRepository<Customer, Int>> {
        every { fetchById(404) } returns null
    }

    private val fetchCustomerByIdQuery = FetchCustomerByIdQuery(repository = repository)

    @Test
    fun `will throw if customer is not found`() {
        assertThrows<CustomerNotFoundException> {
            fetchCustomerByIdQuery.execute(404)
        }
    }
}

