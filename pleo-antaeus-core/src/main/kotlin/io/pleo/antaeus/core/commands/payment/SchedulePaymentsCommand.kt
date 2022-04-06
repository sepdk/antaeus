package io.pleo.antaeus.core.commands.payment

import io.pleo.antaeus.core.commands.ICommand
import io.pleo.antaeus.data.IRepository
import io.pleo.antaeus.models.Customer
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.service.PaymentService

class SchedulePaymentsCommand (private val paymentService: PaymentService, private val customerRepository: IRepository<Customer, Int>, private val invoiceRepository: IRepository<Invoice, Int>) : ICommand
{
    override fun execute() {
        // TODO write schedule payment/billing logic here
    }
}