package org.fastmcmirror.flybuff;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.fastmcmirror.flybuff.command.BuffCommand;
import org.fastmcmirror.flybuff.gui.RemoveGUIListener;
import org.fastmcmirror.flybuff.listeners.GeneralListener;
import org.fastmcmirror.flybuff.listeners.WorkbenchListener;
import org.fastmcmirror.flybuff.module.ModuleLoader;
import org.fastmcmirror.flybuff.task.BuffTickTask;
import org.fastmcmirror.yaml.file.YamlConfiguration;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class FlyBuff extends JavaPlugin {

    public static FlyBuff instance;
    private YamlConfiguration configuration = null;

    @Override
    public void onEnable() {
        instance = this;
        printLogo("\n" +
                "                                       \n" +
                "───────────────────────────────────────\n" +
                "                                       \n" +
                "╔═╗┬ ┬ ┬┌┐ ┬ ┬┌─┐┌─┐  ╔╗╔┌─┐─┐ ┬┌┬┐    \n" +
                "╠╣ │ └┬┘├┴┐│ │├┤ ├┤   ║║║├┤ ┌┴┬┘ │     \n" +
                "╚  ┴─┘┴ └─┘└─┘└  └    ╝╚╝└─┘┴ └─ ┴     \n" +
                "                                       \n" +
                "───────────────────────────────────────\n" +
                "FlyBuff-Next v" + getDescription().getVersion() + "\n" +
                "作者: FlyProject\n" +
                "Github: https://github.com/killerprojecte/FlyBuff-Next\n" +
                "本插件遵循GPLv3协议开源\n" +
                "且带有附加条款 请仔细阅读仓库内README文件\n" +
                "                                       \n");
        saveFile("config.yml");
        ModuleLoader.load();
        new BukkitRunnable() {
            @Override
            public void run() {
                GeneralListener generalListener = new GeneralListener();
                Reflections reflections = new Reflections(new ConfigurationBuilder().forPackages(getConfiguration().getStringList("eventPackages").toArray(new String[0])));
                Set<Class<? extends Event>> eventClasses = reflections.getSubTypesOf(Event.class).stream().
                        filter(clazz -> !Modifier.isAbstract(clazz.getModifiers()) && Arrays.stream(clazz.getDeclaredFields())
                                .anyMatch(field -> field.getType().getName().endsWith("HandlerList")))
                        .collect(Collectors.toSet());
                EventExecutor eventExecutor = (listener, event) -> generalListener.onCallEvent(event);
                eventClasses.forEach(clazz -> Bukkit.getPluginManager()
                        .registerEvent(clazz, generalListener, EventPriority.MONITOR, eventExecutor, FlyBuff.instance));
            }
        }.runTaskAsynchronously(this);
        getLogger().info("[全局监听器] 已找到 " + HandlerList.getHandlerLists().size() + " 个可监听事件");
        getConfiguration().getStringList("eventPackages").toArray(new String[0]);
        Bukkit.getPluginManager().registerEvents(new RemoveGUIListener(), this);
        Bukkit.getPluginManager().registerEvents(new WorkbenchListener(), this);
        new BuffTickTask().runTaskTimerAsynchronously(this, 20L, 20L);
        getCommand("flybuff").setExecutor(new BuffCommand());
        try {
            InputStream inputStream = getResource("config.yml");
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            YamlConfiguration tempConfig = new YamlConfiguration();
            tempConfig.loadFromString(new String(bytes));
            boolean modify = false;
            for (String point : tempConfig.getKeys(true)) {
                if (getConfiguration().get(point) == null) {
                    modify = true;
                    getConfiguration().set(point, tempConfig.get(point));
                    getLogger().info("已补全配置节点: " + point);
                }
            }
            if (modify) {
                saveConfig();
            }

        } catch (Exception e) {
            getLogger().warning("补全配置文件时遇到错误: " + e.getMessage());
        }
        // Plugin startup logic

    }

    public YamlConfiguration getConfiguration() {
        if (configuration == null) {
            configuration = YamlConfiguration.loadConfiguration(new File(getDataFolder() + "/config.yml"));
        }
        return configuration;
    }

    @Override
    public FileConfiguration getConfig() {
        getLogger().warning("有插件正在尝试调用不受支持的**getConfig**方法! 详细信息:");
        List<StackTraceElement> elements = Arrays.asList(Thread.currentThread().getStackTrace());
        elements = elements.subList(1, 11);
        elements.forEach(stackTraceElement -> getLogger().warning("  " + stackTraceElement.getClassName() + "." + stackTraceElement.getMethodName() + "(" + stackTraceElement.getFileName() + ":" + stackTraceElement.getLineNumber() + ")"));
        return null;
    }


    public void reloadConfig() {
        configuration = YamlConfiguration.loadConfiguration(new File(getDataFolder() + "/config.yml"));
    }


    public void saveConfig() {
        try {
            configuration.save(new File(getDataFolder() + "/config.yml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        reloadConfig();
    }

    @Override
    public void onDisable() {
        ModuleLoader.unload();
        // Plugin shutdown logic
    }

    private void printLogo(String str) {
        String[] lines = str.split("\n");
        for (String l : lines) {
            getLogger().info(l);
        }
    }

    public void saveFile(String name) {
        saveFile(name, false, name);
    }

    public void saveFile(String name, boolean replace, String saveName) {
        URL url = getClass().getClassLoader().getResource(name);
        if (url == null) {
            getLogger().severe(name + " Not Found in JarFile");
            return;
        }
        File file = new File(getDataFolder() + "/" + saveName);
        if (!replace) {
            if (file.exists()) return;
        }
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        URLConnection connection = null;
        try {
            connection = url.openConnection();
        } catch (IOException e) {
            getLogger().severe("Failed unpack file " + name + ":" + e.getMessage());
        }
        connection.setUseCaches(false);
        try {
            saveFile(connection.getInputStream(), file);
        } catch (IOException e) {
            getLogger().severe("Failed unpack file " + name + ":" + e.getMessage());
        }
    }

    private void saveFile(InputStream inputStream, File file) throws IOException {
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            int read;
            byte[] bytes = new byte[1024];
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        }
    }
}
