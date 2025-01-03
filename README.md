# Holograms

Holograms is a powerful plugin that allows you to create and manage holographic displays in your Minecraft server.With
the HologramAPI, you can easily add user-specific and static holograms to enhance the gameplay experience.

## Table of Contents

- [Repository](#repository)
- [Getting started](#getting-started)
- [Static Holograms](#static-holograms)
- [User-Specific Holograms](#user-specific-holograms)

## Versions

> [!IMPORTANT]
> Holograms only supports the latest version of Paper (1.21.4)<br>
> The latest version of Holograms requires Java 21

[Latest version supporting 1.21-1.21.1 (Java 21)](https://github.com/TheNextLvl-net/holograms/releases/tag/v2.1.3)<br>
[Latest version supporting 1.19-1.20.4 (Java 19)](https://github.com/TheNextLvl-net/holograms/releases/tag/v2.0.2)<br>
[Latest version supporting 1.19-1.20.2 (Java 17)](https://github.com/TheNextLvl-net/holograms/releases/tag/v2.0.0)<br>

## Repository

To seamlessly integrate our library into your project, please visit
our [repository](https://repo.thenextlvl.net/#/releases/net/thenextlvl/holograms/api).
Select your preferred build tool and incorporate the version you intend to use.

### Gradle Example

For Gradle users, add the repository and dependency to your `build.gradle.kts`:

```kt
repositories {
    maven("https://repo.thenextlvl.net/releases")
}

dependencies {
    compileOnly("net.thenextlvl.holograms:api:version")
}
```

**Note:** Be sure to replace **version** with the actual version number.
Also note that you shouldn't shade the API as it is already provided by the plugin.

## Getting Started

To get started with Holograms, you'll need to access various components of the API. Here's how you can do that:

### Get the HologramProvider

```java
HologramProvider provider = Bukkit.getServicesManager().getRegistration(HologramProvider.class).getProvider();
```

The Hologram Provider serves as the foundation for all the capabilities offered by the Hologram API.

### Get the HologramLoader

```java
HologramLoader loader = provider.getHologramLoader();
```

The Hologram Loader is responsible for the dynamic management of holograms, including loading, unloading, updating, and
teleporting them for specific players.

### Get the HologramRegistry

```java
HologramRegistry registry = provider.getHologramRegistry();
```

The Hologram Registry plays a crucial role in managing static holograms.

### Get the HologramFactory

```java
HologramFactory factory = provider.getHologramFactory();
```

The factory is your go-to tool for creating new holograms and hologram lines.

### Creating Holograms

To create a hologram, use the HologramFactory:

```java
Hologram hologram = factory.createHologram(location, factory.createTextLine(display -> {
    display.text("Your Text Here");
}));
```

**Note:** hologram objects are not yet rendered, first you have to either register or load them.

### Static Holograms

Static holograms are holograms that are visible to all players, providing a consistent viewing experience for everyone.
They are commonly used to display static content without user-specific data.
Importantly, the loading and unloading of static holograms are fully automated, requiring no manual intervention.

#### Registering

To add a static hologram, simply call the register method from the HologramRegistry:

```java
registry.register(hologram);
```

This will add the static hologram for everyone.

#### Unregistering

To remove a static hologram, call the unregister method from the HologramRegistry:

```java
registry.unregister(hologram);
```

This will remove the static hologram for everyone.

### User-Specific Holograms

User-Specific Holograms are unique holograms that are exclusively visible to the players for whom they are loaded.
They are typically used to display personalized text or data.
However, it's important to note that the loading and unloading of these holograms must be managed manually.

#### Loading

To load a user-specific hologram, simply call the load method from the HologramLoader:

```java
loader.load(hologram, player);
```

This will load the hologram for the specified player.

#### Unloading

To remove a user-specific hologram, call the unload method from the HologramLoader:

```java
loader.unload(hologram, player);
```

This will remove the hologram displayed for the specified player.

**Note:** In future updates, we are planning to merge the concepts of user-specific and static holograms to create an
even simpler and more streamlined method for loading and managing holograms.
This enhancement will provide a more straightforward way to handle holographic displays in your server.
