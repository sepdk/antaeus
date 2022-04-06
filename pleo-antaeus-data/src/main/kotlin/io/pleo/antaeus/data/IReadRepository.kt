package io.pleo.antaeus.data

interface IReadRepository<TObjectType, TIdType> {
    fun fetchAll() : List<TObjectType>
    fun fetchById(id: TIdType) : TObjectType?
}