package org.fastmcmirror.flybuff.module;

import org.bukkit.Bukkit;
import org.fastmcmirror.flybuff.FlyBuff;
import org.fastmcmirror.flybuff.api.BuffInstallHandler;
import org.fastmcmirror.flybuff.api.BuffListenerHandler;
import org.fastmcmirror.flybuff.api.BuffRemoveHandler;
import org.fastmcmirror.flybuff.api.BuffTickHandler;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class ModuleLoader {
    public static Map<String, BuffInstallHandler> buffInstallHandlers;
    public static Map<String, BuffRemoveHandler> buffRemoveHandlers;
    public static Map<String, BuffTickHandler> buffTickHandlers;
    public static Map<String, BuffListenerHandler> buffListenerHandlers;
    private static URLClassLoader loader;

    public static void load() {
        File dir = new File(FlyBuff.instance.getDataFolder() + "/modules/");
        if (!dir.exists()) {
            FlyBuff.instance.getLogger().warning("模块目录不存在 正在新建文件夹...");
            dir.mkdirs();
        }
        List<URL> modules = new ArrayList<URL>();
        List<String> className = new ArrayList<>();
        for (File subFile : dir.listFiles()) {
            if (!subFile.isDirectory() && subFile.getName().endsWith(".jar")) {
                try {
                    URL url = subFile.toURI().toURL();
                    modules.add(url);
                    try (final JarInputStream stream = new JarInputStream(url.openStream())) {
                        JarEntry entry;
                        while ((entry = stream.getNextJarEntry()) != null) {
                            String name = entry.getName();
                            if (name.endsWith(".class")) {
                                className.add(name.substring(0, name.lastIndexOf('.')).replace('/', '.'));
                            }
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        loader = new URLClassLoader(modules.toArray(new URL[0]), FlyBuff.class.getClassLoader());
        buffInstallHandlers = new HashMap<>();
        buffRemoveHandlers = new HashMap<>();
        buffTickHandlers = new HashMap<>();
        buffListenerHandlers = new HashMap<>();
        main:
        for (String name : className) {
            Class<?> clz = null;
            try {
                clz = loader.loadClass(name);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            if (clz.isInterface()) continue;
            if (clz.isEnum()) continue;
            if (Modifier.isPrivate(clz.getModifiers())) continue;
            if (Modifier.isProtected(clz.getModifiers())) continue;
            if (Modifier.isAbstract(clz.getModifiers())) continue;
            if (clz.getInterfaces().length == 0) continue;
            if (BuffInstallHandler.class.isAssignableFrom(clz)) {
                BuffInstallHandler instance = null;
                try {
                    instance = (BuffInstallHandler) clz.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                for (String depend : instance.getDependencies()) {
                    if (Bukkit.getPluginManager().getPlugin(depend) == null) {
                        FlyBuff.instance.getLogger().severe("[FlyBuff模块] 无法加载宝石安装模块: " + instance.getName() + " 作者: " + instance.getAuthor() + " 版本: " + instance.getVersion() + " 原因: 缺失模块依赖 " + depend);
                        continue main;
                    }
                }
                FlyBuff.instance.getLogger().info("[FlyBuff模块] 正在注册宝石安装模块: " + instance.getName() + " 作者: " + instance.getAuthor() + " 标识符: " + instance.getIdentifier() + " 版本: " + instance.getVersion());
                buffInstallHandlers.put(instance.getIdentifier(), instance);
            } else if (BuffRemoveHandler.class.isAssignableFrom(clz)) {
                BuffRemoveHandler instance = null;
                try {
                    instance = (BuffRemoveHandler) clz.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                for (String depend : instance.getDependencies()) {
                    if (Bukkit.getPluginManager().getPlugin(depend) == null) {
                        FlyBuff.instance.getLogger().severe("[FlyBuff模块] 无法加载宝石移除模块: " + instance.getName() + " 作者: " + instance.getAuthor() + " 版本: " + instance.getVersion() + " 原因: 缺失模块依赖 " + depend);
                        continue main;
                    }
                }
                FlyBuff.instance.getLogger().info("[FlyBuff模块] 正在注册宝石移除模块: " + instance.getName() + " 作者: " + instance.getAuthor() + " 标识符: " + instance.getIdentifier() + " 版本: " + instance.getVersion());
                buffRemoveHandlers.put(instance.getIdentifier(), instance);
            } else if (BuffTickHandler.class.isAssignableFrom(clz)) {
                BuffTickHandler instance = null;
                try {
                    instance = (BuffTickHandler) clz.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                for (String depend : instance.getDependencies()) {
                    if (Bukkit.getPluginManager().getPlugin(depend) == null) {
                        FlyBuff.instance.getLogger().severe("[FlyBuff模块] 无法加载宝石定时模块: " + instance.getName() + " 作者: " + instance.getAuthor() + " 版本: " + instance.getVersion() + " 原因: 缺失模块依赖 " + depend);
                        continue main;
                    }
                }
                FlyBuff.instance.getLogger().info("[FlyBuff模块] 正在注册宝石定时模块: " + instance.getName() + " 作者: " + instance.getAuthor() + " 标识符: " + instance.getIdentifier() + " 版本: " + instance.getVersion());
                buffTickHandlers.put(instance.getIdentifier(), instance);
            } else if (BuffListenerHandler.class.isAssignableFrom(clz)) {
                BuffListenerHandler instance = null;
                try {
                    instance = (BuffListenerHandler) clz.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                for (String depend : instance.getDependencies()) {
                    if (Bukkit.getPluginManager().getPlugin(depend) == null) {
                        FlyBuff.instance.getLogger().severe("[FlyBuff模块] 无法加载监听模块: " + instance.getName() + " 作者: " + instance.getAuthor() + " 版本: " + instance.getVersion() + " 原因: 缺失模块依赖 " + depend);
                        continue main;
                    }
                }
                FlyBuff.instance.getLogger().info("[FlyBuff模块] 正在注册宝石监听模块: " + instance.getName() + " 作者: " + instance.getAuthor() + " 标识符: " + instance.getIdentifier() + " 版本: " + instance.getVersion());
                buffListenerHandlers.put(instance.getIdentifier(), instance);
            }
        }
    }

    public static void unload() {
        try {
            loader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        loader = null;
    }
}
