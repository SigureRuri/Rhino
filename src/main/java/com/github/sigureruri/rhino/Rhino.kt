package com.github.sigureruri.rhino

import com.github.sigureruri.rhino.event.TickEvent
import com.github.sigureruri.rhino.item.RhinoItemRegistry
import com.github.sigureruri.rhino.listener.ItemListener
import org.bukkit.plugin.java.JavaPlugin

class Rhino : JavaPlugin() {

    override fun onEnable() {
        instance = this

        itemRegistry = RhinoItemRegistry()

        server.pluginManager.registerEvents(ItemListener(), this)

        server.scheduler.runTaskTimer(this, Runnable {
            server.pluginManager.callEvent(TickEvent())
        }, 1, 1)
    }

    companion object {

        lateinit var instance: Rhino
            private set

        lateinit var itemRegistry: RhinoItemRegistry
            private set

    }

}