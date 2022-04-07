package io.pleo.antaeus.core.commands

interface ICommandWithInput<TInputType> {
    fun execute(input : TInputType)
}