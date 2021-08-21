package com.github.sigureruri.rhino.item

class RhinoItemRegistry {

    private val map: MutableMap<RhinoItemId, RhinoItem> = mutableMapOf()

    fun register(item: RhinoItem) {
        if (contains(item.id)) throw IllegalArgumentException("The item has already registered: ${item.id}")
        map[item.id] = item
    }

    fun get(id: RhinoItemId) =
        map[id]

    fun getAll() =
        map.values.toList()

    fun contains(itemId: RhinoItemId) =
        map.contains(itemId)

    fun isEmpty(): Boolean =
        map.isEmpty()

}