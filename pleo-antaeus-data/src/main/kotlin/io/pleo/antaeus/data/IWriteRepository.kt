package io.pleo.antaeus.data

interface IWriteRepository<TObjectType> {
    fun update(entity : TObjectType) : Boolean
}