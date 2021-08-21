package com.github.sigureruri.rhino.item

import com.github.sigureruri.rhino.Rhino
import com.github.sigureruri.rhino.util.Hand
import org.bukkit.NamespacedKey
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.Entity
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

open class RhinoItem(
    val id: RhinoItemId,
    val icon: ItemStack
) {

    open fun use(event: UseEvent) {}

    open fun rightClickEntity(event: RightClickEntityEvent) {}

    open fun drop(event: DropEvent) {}

    open fun pickup(event: PickupEvent) {}

    open fun swap(event: SwapEvent) {}

    open fun clickedInInventory(event: ClickInInventoryEvent) {}

    open fun hurtEntity(event: HurtEntityEvent) {}

    open fun spawn(event: SpawnEvent) {}

    open fun despawn(event: DespawnEvent) {}

    open fun inventoryTick(event: InventoryTickEvent) {}

    open fun entityTick(event: EntityTickEvent) {}

    fun toItemStack(amount: Int = 1): ItemStack {
        return icon.clone().apply {
            this.amount = amount
            itemMeta = itemMeta!!.apply {
                persistentDataContainer.set(
                    NamespacedKey(Rhino.instance, "id"),
                    PersistentDataType.STRING,
                    id.toString()
                )
            }
        }
    }


    class UseEvent(
        val player: Player,
        val itemStack: ItemStack,
        val hand: Hand,
        val block: Block?,
        val blockFace: BlockFace,
        var isCancelled: Boolean
    )

    class RightClickEntityEvent(
        val player: Player,
        val itemStack: ItemStack,
        val entity: Entity,
        val hand: Hand,
        var isCancelled: Boolean
    )

    class DropEvent(
        val player: Player,
        val itemStack: ItemStack,
        val item: Item,
        var isCancelled: Boolean
    )

    class PickupEvent(
        val player: Player,
        val itemStack: ItemStack,
        val item: Item,
        var isCancelled: Boolean
    )

    class SwapEvent(
        val player: Player,
        val itemStack: ItemStack,
        val from: Hand,
        var isCancelled: Boolean
    )

    class ClickInInventoryEvent(
        val player: Player,
        val itemStack: ItemStack,
        val inventory: Inventory,
        val clickedItemIn: ClickedItemIn,
        val slot: Int,
        val rawSlot: Int,
        val hotbarButton: Int,
        val slotType: InventoryType.SlotType,
        val action: InventoryAction,
        val click: ClickType,
        var isCancelled: Boolean
    ) {
        enum class ClickedItemIn {
            CURSOR,
            CURRENT
        }
    }

    class HurtEntityEvent(
        val player: Player,
        val itemStack: ItemStack,
        val victim: Entity,
        val cause: EntityDamageEvent.DamageCause,
        var damage: Double,
        var isCancelled: Boolean
    )

    class SpawnEvent(
        val itemStack: ItemStack,
        val item: Item,
        var isCancelled: Boolean
    )

    class DespawnEvent(
        val itemStack: ItemStack,
        val item: Item,
        var isCancelled: Boolean
    )

    class InventoryTickEvent(
        val entity: Entity,
        val itemStacks: List<ItemStack>
    )

    class EntityTickEvent(
        val item: Item,
        val itemStack: ItemStack
    )

}