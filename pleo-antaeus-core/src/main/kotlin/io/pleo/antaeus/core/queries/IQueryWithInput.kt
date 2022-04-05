package io.pleo.antaeus.core.queries

interface IQueryWithInput<TInput, TOuput> {
    fun execute(input : TInput) : TOuput
}
