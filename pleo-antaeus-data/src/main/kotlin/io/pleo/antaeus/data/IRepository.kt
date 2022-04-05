package io.pleo.antaeus.data

interface IRepository<TObjectType, TIdType> {
    fun fetchAll() : List<TObjectType>;
    fun fetchById(id: TIdType) : TObjectType;
}