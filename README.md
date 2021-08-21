# Rhino
A library to create original items for SpigotMC servers

## For example:
```kotlin
package com.github.sigureruri.rhino.debug

import com.github.sigureruri.rhino.Rhino
import com.github.sigureruri.rhino.item.RhinoItem
import com.github.sigureruri.rhino.item.RhinoItemId
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Animals
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Monster
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack

object Debug : Listener {

    fun init() {
        registerItems()
    }

    private fun registerItems() {
        listOf(
            SuperSwordItem(),
            MaterialItem(),
            DebugItem()
        ).forEach {
            Rhino.itemRegistry.register(it)
        }
    }

    class SuperSwordItem : RhinoItem(
        RhinoItemId.of("super_sword"),
        ItemStack(Material.DIAMOND_SWORD).apply {
            itemMeta = itemMeta?.apply {
                setDisplayName("${ChatColor.RED}${ChatColor.BOLD}すごい剣")
                lore = listOf(
                    "",
                    "${ChatColor.RED}${ChatColor.BOLD} < 説明 >",
                    "${ChatColor.WHITE}すごい剣。",
                    "${ChatColor.WHITE}敵を攻撃するとすごい力が出る。",
                    "${ChatColor.WHITE}動物を右クリックすると体力を回復させる。",
                    "",
                    "${ChatColor.RED}${ChatColor.BOLD} < レアリティ >",
                    "${ChatColor.RED}${ChatColor.BOLD} A"
                )
            }
        }
    ) {
        override fun rightClickEntity(event: RightClickEntityEvent) {
            val animal = event.entity as? Animals ?: return

            animal.health = animal.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.value

            animal.world.spawnParticle(
                Particle.HEART,
                animal.location,
                10,
                0.2,
                0.2,
                0.2,
                1.0
            )
            animal.world.playSound(
                event.player.location,
                Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
                1f,
                2f,
            )
        }

        override fun hurtEntity(event: HurtEntityEvent) {
            val enemy = event.victim as? Monster ?: return

            enemy.damage(10.0)

            enemy.world.spawnParticle(
                Particle.CRIT,
                enemy.location,
                10,
                0.2,
                0.2,
                0.2,
                1.0
            )
            enemy.world.playSound(
                event.player.location,
                Sound.ENTITY_EVOKER_FANGS_ATTACK,
                1f,
                1f,
            )
        }
    }

    class MaterialItem : RhinoItem(
        RhinoItemId.of("material"),
        ItemStack(Material.DIAMOND).apply {
            itemMeta = itemMeta?.apply {
                setDisplayName("${ChatColor.RED}${ChatColor.BOLD}素材")
                lore = listOf(
                    "",
                    "${ChatColor.RED}${ChatColor.BOLD} < 説明 >",
                    "${ChatColor.WHITE}何かの素材。",
                    "${ChatColor.WHITE}右クリックで何かが起きる？",
                    "",
                    "${ChatColor.RED}${ChatColor.BOLD} < レアリティ >",
                    "${ChatColor.GOLD}${ChatColor.BOLD} B"
                )
            }
        }
    ) {
        override fun use(event: UseEvent) {
            event.itemStack.amount--

            val rhinoItem = Rhino.itemRegistry.get(RhinoItemId.of("super_sword"))!!
            event.player.inventory.addItem(rhinoItem.toItemStack(1))

            event.player.playSound(
                event.player.location,
                Sound.ITEM_TOTEM_USE,
                1f,
                1f
            )
        }
    }

    class DebugItem : RhinoItem(
        RhinoItemId.of("debug_item"),
        ItemStack(Material.CARROT_ON_A_STICK).apply {
            itemMeta = itemMeta?.apply {
                setDisplayName("${ChatColor.RED}${ChatColor.BOLD}デバッグアイテム")
                lore = listOf(
                    "",
                    "${ChatColor.RED}${ChatColor.BOLD} < 説明 >",
                    "${ChatColor.WHITE}デバッグアイテムです。",
                    "",
                    "${ChatColor.RED}${ChatColor.BOLD} < レアリティ >",
                    "${ChatColor.AQUA}${ChatColor.BOLD} S"
                )
            }
        }
    ) {

        override fun use(event: UseEvent) {
            if (event.block != null) {
                event.block.type = Material.AIR
            }
        }

        override fun swap(event: SwapEvent) {
            event.player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent("${ChatColor.AQUA}Debug item was swapped"))
        }

        override fun pickup(event: PickupEvent) {
            event.item.remove()
            event.isCancelled = true
        }

        override fun clickedInInventory(event: ClickInInventoryEvent) {
            event.player.sendTitle("${ChatColor.DARK_AQUA}${ChatColor.BOLD}!! Detected !!", "${ChatColor.AQUA}Clicked Debug Item in your inventory", 10, 70, 20)
        }

        override fun rightClickEntity(event: RightClickEntityEvent) {
            val victim = event.entity as? LivingEntity ?: return
            event.player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent("Target's Health: ${victim.health}"))
        }

        override fun drop(event: DropEvent) {
            if (!event.player.isSneaking) {
                event.player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent("${ChatColor.RED}You can't drop this item!"))
                event.isCancelled = true
            }
        }

        override fun hurtEntity(event: HurtEntityEvent) {
            val victim = event.victim as? LivingEntity ?: return
            victim.damage(100.toDouble())
        }

        override fun entityTick(event: EntityTickEvent) {
            event.item.world.spawnParticle(
                Particle.FLAME,
                event.item.location,
                20,
                1.0,
                1.0,
                1.0,
                1.0
            )
        }

        override fun inventoryTick(event: InventoryTickEvent) {
            event.entity.world.spawnParticle(
                Particle.GLOW,
                event.entity.location,
                20,
                1.0,
                1.0,
                1.0,
                1.0
            )
        }

    }
}
```