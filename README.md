# **FlyBuff-Next**
![COMMIT](https://shields.io/endpoint?url=https://flybuff-github.rgbmc.org)
- 下一代宝石镶嵌系统
- FlyBuff 1.x的继任
- 功能模块化

## Coming Soon
- FlyBuff 2.x 即将发布

## 从FlyBuff 2.x开始

FlyBuff 2.x 全面重写了 FlyBuff 1.x的所有框架

并对功能的自主实现提供API

插件本体现不再内置任何实用性功能(如原先的: 药水效果, 粒子效果)

所有功能将设计为模块 载入FlyBuff2.x

## 模块开发

开发FlyBuff2.x模块需要对BukkitAPI熟练操作

所开发的模块 可为 **开源** 或 **闭源**

模块并不管制 **付费模式**

无需遵循此仓库所使用的开源协议 (即不限制开源协议)

所有模块需要打包为jar并放入`plugins\FlyBuff\modules\ `内

## 模块注册

所有模块仅需要实现FlyBuff2.x的接口```Buff**Handler```

在jar被FlyBuff加载时将自动注册

## 额外协议

- 本内容根据GPLv3第7条发布

```
1. 禁止任何人以任何方式对FlyBuff-Next其可执行内容或源码进行付费分发
2. 任何人对FlyBuff-Next源码进行分发时必须附上此仓库链接
3. FlyBuff-Next开发者有权力对违规使用(诈骗行为, 违法行为等)FlyBuff的用户撤销使用授权
```

## 添加至依赖

- Maven

```xml

<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <artifactId>example</artifactId>
    <repositories>
        <repository>
            <id>fastmcmirror-repo</id>
            <url>https://repo.fastmcmirror.org/content/repositories/releases/</url>
        </repository>
    </repositories>

    <dependencies>
        <dependency>
            <groupId>org.fastmcmirror</groupId>
            <artifactId>flybuff-next</artifactId>
            <version>2.0.0-SNAPSHOT-{COMMIT_ID}</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>
</project>
```

- Gradle Groovy

```groovy
repositories {
    maven {
        name = 'fastmcmirror-repo'
        url = "https://repo.fastmcmirror.org/content/repositories/releases/"
    }
}

dependencies {
    compileOnly 'org.fastmcmirror:flybuff-next:2.0.0-SNAPSHOT-{COMMIT_ID}'
}
```

- Gradle Kotlin

```kotlin
repositories {
    maven {
        name = "fastmcmirror-repo"
        url = uri("https://repo.fastmcmirror.org/content/repositories/releases/")
    }
}

dependencies {
    compileOnly("org.fastmcmirror:flybuff-next:2.0.0-SNAPSHOT-{COMMIT_ID}")
}
```
