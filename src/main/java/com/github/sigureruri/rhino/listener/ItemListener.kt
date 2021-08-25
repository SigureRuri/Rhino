package com.github.sigureruri.rhino.listener

import com.github.sigureruri.rhino.event.TickEvent
import com.github.sigureruri.rhino.item.RhinoItem
import com.github.sigureruri.rhino.util.Hand
import com.github.sigureruri.rhino.util.ItemHelper
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Item
import org.bukkit.entity.ItemFrame
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Villager
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.entity.ItemDespawnEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.InventoryHolder

class ItemListener : Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    fun onUse(event: PlayerInteractEvent) {
        val itemStack = event.item ?: return
        val item = ItemHelper.getItemFromItemStack(itemStack) ?: return

        val useEvent = RhinoItem.UseEvent(
            event.player,
            itemStack,
            Hand.fromEquipmentSlot(event.hand!!),
            event.clickedBlock,
            event.blockFace,
            event.useItemInHand() == Event.Result.DENY
        )

        item.use(useEvent)

        event.isCancelled = useEvent.isCancelled
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onRightClickEntity(event: PlayerInteractEntityEvent) {
        val player = event.player
        val itemStack = player.inventory.itemInMainHand.also { if (it.type == Material.AIR) return }
        val item = ItemHelper.getItemFromItemStack(itemStack) ?: return

        val rightClickEntityEvent = RhinoItem.RightClickEntityEvent(
            player,
            itemStack,
            event.rightClicked,
            Hand.fromEquipmentSlot(event.hand),
            event.isCancelled
        )

        item.rightClickEntity(rightClickEntityEvent)

        event.isCancelled = rightClickEntityEvent.isCancelled
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onDrop(event: PlayerDropItemEvent) {
        val itemEntity = event.itemDrop
        val itemStack = itemEntity.itemStack
        val item = ItemHelper.getItemFromItemStack(itemStack) ?: return

        val dropEvent = RhinoItem.DropEvent(
            event.player,
            itemStack,
            itemEntity,
            event.isCancelled
        )

        item.drop(dropEvent)

        event.isCancelled = dropEvent.isCancelled
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPickup(event: EntityPickupItemEvent) {
        val player = event.entity as? Player ?: return
        val itemEntity = event.item
        val itemStack = itemEntity.itemStack
        val item = ItemHelper.getItemFromItemStack(itemStack) ?: return

        val pickupEvent = RhinoItem.PickupEvent(
            player,
            itemStack,
            itemEntity,
            event.isCancelled
        )

        item.pickup(pickupEvent)

        event.isCancelled = pickupEvent.isCancelled
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onSwap(event: PlayerSwapHandItemsEvent) {
        event.mainHandItem?.let { mainHandItemStack ->
            ItemHelper.getItemFromItemStack(mainHandItemStack)?.let { mainHandItem ->
                val swapEvent = RhinoItem.SwapEvent(
                    event.player,
                    mainHandItemStack,
                    // from
                    Hand.OFF_HAND,
                    event.isCancelled
                )

                mainHandItem.swap(swapEvent)

                event.isCancelled = swapEvent.isCancelled
            }
        }

        event.offHandItem?.let { mainHandItemStack ->
            ItemHelper.getItemFromItemStack(mainHandItemStack)?.let { mainHandItem ->
                val swapEvent = RhinoItem.SwapEvent(
                    event.player,
                    mainHandItemStack,
                    // from
                    Hand.MAIN_HAND,
                    event.isCancelled
                )

                mainHandItem.swap(swapEvent)

                event.isCancelled = swapEvent.isCancelled
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onClickInInventory(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        val inventory = event.clickedInventory ?: return

        event.cursor?.let { cursorItemStack ->
            ItemHelper.getItemFromItemStack(cursorItemStack)?.let { cursorItem ->
                val clickEvent = RhinoItem.ClickInInventoryEvent(
                    player,
                    cursorItemStack,
                    inventory,
                    RhinoItem.ClickInInventoryEvent.ClickedItemIn.CURSOR,
                    event.slot,
                    event.rawSlot,
                    event.hotbarButton,
                    event.slotType,
                    event.action,
                    event.click,
                    event.isCancelled
                )

                cursorItem.clickedInInventory(clickEvent)

                event.isCancelled = clickEvent.isCancelled
            }
        }

        event.currentItem?.let { currentItemStack ->
            ItemHelper.getItemFromItemStack(currentItemStack)?.let { currentItem ->
                val clickEvent = RhinoItem.ClickInInventoryEvent(
                    player,
                    currentItemStack,
                    inventory,
                    RhinoItem.ClickInInventoryEvent.ClickedItemIn.CURRENT,
                    event.slot,
                    event.rawSlot,
                    event.hotbarButton,
                    event.slotType,
                    event.action,
                    event.click,
                    event.isCancelled
                )

                currentItem.clickedInInventory(clickEvent)

                event.isCancelled = clickEvent.isCancelled
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onHurtEntity(event: EntityDamageByEntityEvent) {
        val damager = event.damager as? Player ?: return
        val itemStack = damager.inventory.itemInMainHand.also { if (it.type == Material.AIR) return }
        val item = ItemHelper.getItemFromItemStack(itemStack) ?: return

        val hurtEntityEvent = RhinoItem.HurtEntityEvent(
            damager,
            itemStack,
            event.entity,
            event.cause,
            event.finalDamage,
            event.isCancelled
        )

        item.hurtEntity(hurtEntityEvent)

        event.damage = hurtEntityEvent.damage
        event.isCancelled = hurtEntityEvent.isCancelled
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onSpawn(event: EntitySpawnEvent) {
        val itemEntity = event.entity as? Item ?: return
        val itemStack = itemEntity.itemStack
        val item = ItemHelper.getItemFromItemStack(itemStack) ?: return

        val spawnEvent = RhinoItem.SpawnEvent(
            itemStack,
            itemEntity,
            event.isCancelled
        )

        item.spawn(spawnEvent)

        event.isCancelled = spawnEvent.isCancelled
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onDespawn(event: ItemDespawnEvent) {
        val itemEntity = event.entity
        val itemStack = itemEntity.itemStack
        val item = ItemHelper.getItemFromItemStack(itemStack) ?: return

        val despawnEvent = RhinoItem.DespawnEvent(
            itemStack,
            itemEntity,
            event.isCancelled
        )

        item.despawn(despawnEvent)

        event.isCancelled = despawnEvent.isCancelled
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onTick(event: TickEvent) {
        Bukkit.getWorlds().forEach { world ->
            world.entities.forEach { entity ->
                // entity tick
                if (entity is Item) {
                    ItemHelper.getItemFromItemStack(entity.itemStack)?.let { item ->
                        val entityTickEvent = RhinoItem.EntityTickEvent(
                            entity,
                            entity.itemStack
                        )

                        item.entityTick(entityTickEvent)
                    }
                }

                // inventory ticks
                if (entity is InventoryHolder) {
                    if (entity is Player) {
                        val itemStacks = listOf(
                            *entity.inventory.toList().toTypedArray(),
                            *entity.enderChest.toList().toTypedArray(),
                            entity.itemOnCursor
                        )
                        ItemHelper.getItemsFromItemStacks(itemStacks).forEach { (item, itemStacks) ->
                            val inventoryTickEvent = RhinoItem.InventoryTickEvent(
                                entity,
                                itemStacks
                            )

                            item.inventoryTick(inventoryTickEvent)
                        }
                    } else if (entity is Villager) {
                        val itemStacks = listOf(
                            *EquipmentSlot.values().mapNotNull { entity.equipment?.getItem(it) }.toTypedArray(),
                            *entity.inventory.toList().toTypedArray()
                        )
                        ItemHelper.getItemsFromItemStacks(itemStacks).forEach { (item, itemStacks) ->
                            val inventoryTickEvent = RhinoItem.InventoryTickEvent(
                                entity,
                                itemStacks
                            )

                            item.inventoryTick(inventoryTickEvent)
                        }
                    } else {
                        ItemHelper.getItemsFromItemStacks(entity.inventory).forEach { (item, itemStacks) ->
                            val inventoryTickEvent = RhinoItem.InventoryTickEvent(
                                entity,
                                itemStacks
                            )

                            item.inventoryTick(inventoryTickEvent)
                        }
                    }
                } else if (entity is LivingEntity) {
                    val itemStacks = EquipmentSlot.values().mapNotNull { entity.equipment?.getItem(it) }
                    ItemHelper.getItemsFromItemStacks(itemStacks).forEach { (item, itemStacks) ->
                        val inventoryTickEvent = RhinoItem.InventoryTickEvent(
                            entity,
                            itemStacks
                        )

                        item.inventoryTick(inventoryTickEvent)
                    }
                }

                if (entity is ItemFrame) {
                    ItemHelper.getItemFromItemStack(entity.item)?.let { item ->
                        val inventoryTickEvent = RhinoItem.InventoryTickEvent(
                            entity,
                            listOf(entity.item)
                        )

                        item.inventoryTick(inventoryTickEvent)
                    }
                }
            }
        }
    }

}