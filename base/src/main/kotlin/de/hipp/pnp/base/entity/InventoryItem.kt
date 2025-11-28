package de.hipp.pnp.base.entity

import java.io.Serializable

data class InventoryItem(
    var name: String = "",
    var amount: Int = 0,
    var customLabel: String = "",
) : Serializable
