package com.github.sigureruri.rhino.util

import org.bukkit.inventory.EquipmentSlot

enum class Hand {
    MAIN_HAND,
    OFF_HAND;

    companion object {
        fun fromEquipmentSlot(slot: EquipmentSlot) = when (slot) {
            EquipmentSlot.HAND -> MAIN_HAND
            EquipmentSlot.OFF_HAND -> OFF_HAND
            else -> throw IllegalStateException("This EquipmentSlot is not a kind of hand: $slot")
        }
    }
}