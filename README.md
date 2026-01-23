<div align="center">
    <img src="./beacon_core_scaled.png" alt="Beacon Core Logo" width="100">
    <h1 style="margin: 0;">Beacon Core</h1>
    <h3 style="margin: 0;">Progressive Minecraft modding framework focused on Developer Experience.</h3>
    <p>
        <img src="https://img.shields.io/badge/Minecraft-1.21.1-blue?style=for-the-badge" alt="Minecraft Version">
        <img src="https://img.shields.io/badge/NeoForge-Latest-orange?style=for-the-badge" alt="NeoForge">
        <img src="https://img.shields.io/badge/License-MIT-green?style=for-the-badge" alt="License">
    </p>
</div>

---

> [!TIP]
> ¿Hablas español? [Haz click aquí para ver la versión de este archivo en español.](./README_ES.md)

## What is Beacon?

**Beacon Core** is a progressive framework for [NeoForge](https://github.com/neoforged/NeoForge) that makes modding more efficient by reducing NeoForge boilerplate considerably.

## Why Beacon?

**For newer developers:** Beacon Core aims to be extremely easy to learn. As a declarative framework, it allows you to add blocks and items in minutes simply by using `@annotations`.

**For experienced developers:** Beacon Core is built for flexibility and designed for seamless interoperability with native NeoForge code. You don't need to migrate your entire project to see the benefits; you can simply bootstrap your mod's entry point and start using it alongside your existing logic.

## The Beacon Way

### Native NeoForge

```java
public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
public static final DeferredItem<Item> MY_ITEM = ITEMS.register("my_item", () -> new Item(new Item.Properties()));
// ... and don't forget to register the BUS in the constructor and the item in the creative tab 🪦🥀🥀🥀
```

### Beacon

```java
@RegisterItem("my_item")
@InTab("my_tab")
public static Item MY_ITEM; // JUST DO THIS
```

#### Need properties? Do this:

```java
@RegisterItemProps
public static Item.Properties MY_ITEM_PROPS = new Item.Properties();
```

```java
@RegisterItem("my_item")
@InTab("my_tab")
@WithItemProps // Beacon Core automatically looks for MY_ITEM_PROPS
public static Item MY_ITEM;
```

> [!CAUTION]
> ## Debugging made EASY
> What if you accidentally make a typo while writing your property name?
>
> ```java
> @RegisterItemProps
> public static Item.Properties MY_ITEN_PROPERTIES = new Item.Properties();
> ```
>
> Don't worry! If you try to run your mod, you will see a very **helpful** error message instead of a cryptic crash.
>
> Beacon Core will tell you exactly what went wrong:
>
> ```yaml
> [Beacon Core Error]
> Property with ID 'MY_ITEM_PROPS' was not found!
> 
> 💡 Did you mean MY_ITEN_PROPERTIES?
> 
> Try annotating a field with:
> @WithItemProps("MY_ITEN_PROPERTIES")
> ```

## The future of Beacon

Right now, **Beacon** is in alpha stage, but the vision is clear: we are building a complete ecosystem to make modding as professional as modern web development.

Our next big milestone is the **Beacon Test Engine**, a BDD-style testing framework (think "Jest for Minecraft"). It will allow you to validate your registries and logic without even launching the game, making CI/CD easier and more robust than ever.

In the near future, we plan to release a system for "Bulk Registration". Imagine adding a full set of blocks — slabs, stairs, and walls — with a single line of code, without ever worrying about manual data-gen.

---

## Installation

Beacon Core is currently in **Alpha** and distributed via JitPack.

```gradle
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    // implementation 'com.github.SirCesarium:Beacon-Core:%version%'
    implementation 'com.github.SirCesarium:Beacon-Core:v2.1.0-alpha'
}
```

Don't forget to call the bootstrapper in your mod's entrypoint.

```java
@BeaconMod
@Mod(MyMod.MODID)
public class MyMod {
    // ...
}
```

> [!WARNING]
> ### ⚠️ Alpha Stage Notes
> As we are in early development:
> - Package Names: Be aware that package names and Group IDs may change as we transition from Alpha to Beta.
> - Distribution: We plan to migrate from JitPack to Maven Central once the framework reaches a stable Beta stage.
> - Tooling: A dedicated Beacon CLI is currently under development to scaffold new projects instantly.

## Contributing

Beacon is an ambitious project, and we’re just getting started. Whether you found a bug, have a killer idea for the CLI, or want to help us build the Test Engine, your help is welcome!

1. **Check the Issues:** See what we’re currently working on.
2. **Open a PR:** We love clean, well-documented code.
3. **Join the Vision:** Help us kill the boilerplate, one annotation at a time.

Made with ❤️ for the Modding Community.
