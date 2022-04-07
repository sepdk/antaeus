package io.pleo.antaeus.core.commands.payment

import io.pleo.antaeus.core.commands.ICommandWithInput
import io.pleo.antaeus.core.exceptions.CurrencyMismatchException
import io.pleo.antaeus.core.exceptions.CustomerNotFoundException
import io.pleo.antaeus.core.exceptions.NetworkException
import io.pleo.antaeus.data.IRepository
import io.pleo.antaeus.logger.ILogger
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceStatus
import io.pleo.antaeus.service.IPaymentService
import java.time.LocalDate

class ProcessPaymentsCommand (private val paymentService: IPaymentService, private val invoiceRepository: IRepository<Invoice, Int>, private val logger : ILogger) : ICommandWithInput<LocalDate>
{
    // I think input param should be called currentDate, but it will cause a warning and im not fan of having warnings or suppressing them. This is a parameter in order to mock it
    override fun execute(input: LocalDate) {
        try {
            if(input.dayOfMonth != 1){
                return
            }
            val invoices = invoiceRepository.fetchAll()
            invoices.forEach {
                if (it.status == InvoiceStatus.PENDING) {
                    try {
                        if (paymentService.charge(it)) {
                            val invoiceUpdateModel = Invoice(it.id, it.customerId, it.amount, InvoiceStatus.PAID)
                            if(!invoiceRepository.update(invoiceUpdateModel)){
                                logger.error("Could not update db status for invoice with id ${it.id}.")
                            }
                        } else {
                            logger.warn("Customer account balance did not allow charge while processing invoice with id ${it.id}.")
                        }
                    } catch (e: CustomerNotFoundException) {
                        logger.error("Customer not found with id ${it.customerId} while processing invoice with id ${it.id}.")
                    } catch (e: CurrencyMismatchException) {
                        logger.error("The currency doesn't match the customer account while processing invoice with id ${it.id}.")
                    } catch (e: NetworkException) {
                        logger.error("A network exception occurred while processing invoice with id ${it.id}.")
                    } catch (e: Exception) {
                        logger.error("Unexpected exception occurred while processing invoice with id ${it.id}.")
                    }
                }
            }
        }catch (e: Exception) {
            logger.error("Unexpected exception occurred in SchedulePaymentsCommand")
        }
    }
}