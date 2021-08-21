package com.github.sigureruri.rhino.util

import com.github.sigureruri.rhino.Rhino
import com.github.sigureruri.rhino.item.RhinoItem
import com.github.sigureruri.rhino.item.RhinoItemId
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

object ItemHelper {

    fun getItemFromItemStack(itemStack: ItemStack): RhinoItem? {
        val rawId = itemStack.itemMeta?.persistentDataContainer?.get(
            NamespacedKey(Rhino.instance, "id"),
            PersistentDataType.STRING
        ) ?: return null
        val itemId = try {
            RhinoItemId.of(rawId)
        } catch (e: IllegalArgumentException) {
            return null
        }
        return Rhino.itemRegistry.get(itemId)
    }

    fun isRhinoItem(itemStack: ItemStack) =
        getItemFromItemStack(itemStack) != null

    fun getItemIdFromItemStack(itemStack: ItemStack) =
        getItemFromItemStack(itemStack)?.id

    fun getItemsFromItemStacks(itemStacks: Iterable<ItemStack?>): Map<RhinoItem, List<ItemStack>> {
        return mutableMapOf<RhinoItemId, MutableList<ItemStack>>().apply {
            itemStacks
                .filterNotNull()
                .filter { isRhinoItem(it) }
                .forEach {
                    getOrPut(getItemIdFromItemStack(it)!!) { mutableListOf() }.add(it)
                }
        }.mapKeys { Rhino.itemRegistry.get(it.key)!! }
    }

}