package io.pleo.antaeus.core.commands.payment

import io.pleo.antaeus.core.commands.ICommand
import io.pleo.antaeus.core.exceptions.CurrencyMismatchException
import io.pleo.antaeus.core.exceptions.CustomerNotFoundException
import io.pleo.antaeus.core.exceptions.NetworkException
import io.pleo.antaeus.data.IRepository
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceStatus
import io.pleo.antaeus.service.PaymentService
import mu.KLogger
import java.time.LocalDateTime

class SchedulePaymentsCommand (private val paymentService: PaymentService, private val invoiceRepository: IRepository<Invoice, Int>, private val logger : KLogger) : ICommand
{
    override fun execute() {
        try {
            val currentDate = LocalDateTime.now()
            if(currentDate.dayOfMonth != 1){
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
                        logger.error("Customer not found with id ${it.customerId} while processing invoice with id ${it.id}. message: ${e.message} stacktrace ${e.stackTrace}")
                    } catch (e: CurrencyMismatchException) {
                        logger.error("The currency doesnt match the customer account while processing invoice with id ${it.id}. message: ${e.message} stacktrace ${e.stackTrace}")
                    } catch (e: NetworkException) {
                        logger.error("A network exception occured while processing invoice with id ${it.id}. message: ${e.message} stacktrace ${e.stackTrace}")
                    } catch (e: Exception) {
                        logger.error("Unexpected exception occured while processing invoice with id ${it.id}. message: ${e.message} stacktrace ${e.stackTrace}")
                    }
                }
            }
        }catch (e: Exception) {
            logger.error("Unexpected exception occured in SchedulePaymentsCommand message: ${e.message} stacktrace ${e.stackTrace}")
        }
    }
}