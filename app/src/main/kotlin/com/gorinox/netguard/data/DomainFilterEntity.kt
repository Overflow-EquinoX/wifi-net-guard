package com.gorinox.netguard.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "domain_filters")
data class DomainFilterEntity(
    @PrimaryKey
    val domain: String,
    val isWhitelist: Boolean // true ise asla engelleme, false ise engelle
)
