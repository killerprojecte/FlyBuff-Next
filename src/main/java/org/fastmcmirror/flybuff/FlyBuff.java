package org.fastmcmirror.flybuff;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import org.fastmcmirror.flybuff.command.BuffCommand;
import org.fastmcmirror.flybuff.gui.RemoveGUIListener;
import org.fastmcmirror.flybuff.listeners.GeneralListener;
import org.fastmcmirror.flybuff.listeners.WorkbenchListener;
import org.fastmcmirror.flybuff.module.ModuleLoader;
import org.fastmcmirror.flybuff.task.BuffTickTask;
import org.reflections.Reflections;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public final class FlyBuff extends JavaPlugin {

    public static FlyBuff instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        ModuleLoader.load();
        GeneralListener generalListener = new GeneralListener();
        EventExecutor eventExecutor = (listener, event) -> generalListener.onCallEvent(event);
        Reflections reflections = new Reflections("org.bukkit");
        Set<Class<? extends Event>> eventClasses = reflections.getSubTypesOf(Event.class).stream().
                filter(clazz -> Arrays.stream(clazz.getDeclaredFields())
                        .anyMatch(field -> field.getType().getName().endsWith("HandlerList")))
                .collect(Collectors.toSet());
        getLogger().info("[全局监听器] 已找到 " + eventClasses.size() + " 个Bukkit内部事件");
        eventClasses.forEach(clazz -> Bukkit.getPluginManager()
                .registerEvent(clazz, generalListener, EventPriority.MONITOR, eventExecutor, this));
        Bukkit.getPluginManager().registerEvents(new RemoveGUIListener(), this);
        Bukkit.getPluginManager().registerEvents(new WorkbenchListener(), this);
        new BuffTickTask().runTaskTimerAsynchronously(this, 20L, 20L);
        getCommand("flybuff").setExecutor(new BuffCommand());
        // Plugin startup logic

    }

    @Override
    public void onDisable() {
        ModuleLoader.unload();
        // Plugin shutdown logic
    }
}
