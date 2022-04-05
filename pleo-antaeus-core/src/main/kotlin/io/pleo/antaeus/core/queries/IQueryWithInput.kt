package io.pleo.antaeus.core.queries

internal interface IQueryWithInput<TInput, TOuput> {
    fun execute(input : TInput) : TOuput
}
