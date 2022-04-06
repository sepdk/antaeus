package io.pleo.antaeus.data

interface IRepository<TObjectType, TIdType>: IWriteRepository<TObjectType>, IReadRepository<TObjectType, TIdType> {
}