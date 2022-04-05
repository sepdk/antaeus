package io.pleo.antaeus.core.queries

interface IQuery <T> {
    fun execute() : T
}

