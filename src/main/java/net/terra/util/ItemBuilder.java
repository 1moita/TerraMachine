package net.terra.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Field;
import java.util.*;

public final class ItemBuilder {

    private final ItemStack stack;
    private final ItemMeta meta;

    public ItemBuilder() {
        this(Material.AIR);
    }

    public ItemBuilder(Material type) {
        this(type, 1);
    }

    public ItemBuilder(Material type, int amount) {
        this(new ItemStack(type, amount), true);
    }

    public ItemBuilder(ItemStack item) {
        this(item, true);
    }

    public ItemBuilder(ItemStack item, boolean direct) {
        this.stack = direct ? item : item.clone();
        this.meta = this.stack.getItemMeta();
    }

    public Material type() {
        return this.stack.getType();
    }

    public ItemBuilder type(Material type) {
        this.stack.setType(type);
        return this;
    }

    public int amount() {
        return this.stack.getAmount();
    }

    public ItemBuilder amount(int amount) {
        if(amount < 0 || amount > 64)
            return this;

        this.stack.setAmount(amount);
        return this;
    }

    public short durability() {
        return this.stack.getDurability();
    }

    public ItemBuilder durability(short durability) {
        this.stack.setDurability(durability);
        return this;
    }

    public String name() {
        if(!this.meta.hasDisplayName()) return null;
        return this.meta.getDisplayName();
    }

    public ItemBuilder name(String content) {
        this.meta.setDisplayName(ChatColor.colorize(content));
        return make();
    }

    public List<String> lore() {
        if(!this.meta.hasLore()) return Collections.emptyList();
        return this.meta.getLore();
    }

    public ItemBuilder lore(List<String> content) {
        this.meta.setLore(ChatColor.colorize(content));
        return make();
    }

    public ItemBuilder lore(String... content) {
        this.meta.setLore(ChatColor.colorize(Arrays.asList(content)));
        return make();
    }

    public Color color() {
        switch(this.stack.getType()) {
            case LEATHER_BOOTS:
            case LEATHER_LEGGINGS:
            case LEATHER_CHESTPLATE:
            case LEATHER_HELMET:
                return ((LeatherArmorMeta) this.meta).getColor();

            default:
                return null;
        }
    }

    public ItemBuilder color(Color color) {
        switch(this.stack.getType()) {
            case LEATHER_BOOTS:
            case LEATHER_LEGGINGS:
            case LEATHER_CHESTPLATE:
            case LEATHER_HELMET:
                ((LeatherArmorMeta) this.meta).setColor(color);
        }

        return make();
    }

    public boolean effect(PotionEffectType type) {
        if(this.stack.getType() != Material.POTION)
            return false;

        return ((PotionMeta) this.meta).hasCustomEffect(type);
    }

    public ItemBuilder effect(PotionEffect effect) {
        if(this.stack.getType() != Material.POTION)
            return this;

        ((PotionMeta) this.meta).addCustomEffect(effect, true);
        return make();
    }

    public ItemBuilder effect(PotionEffectType type, int duration, int amplifier, boolean particles) {
        return effect(new PotionEffect(type, duration, amplifier, true, particles));
    }

    public ItemBuilder texture(String base64) {
        if(this.stack.getType() != Material.SKULL_ITEM)
            return this;

        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", base64));
        try {
            Field field = ((SkullMeta) this.meta).getClass().getDeclaredField("profile");
            field.setAccessible(true);
            field.set(this.meta, profile);
        } catch(Exception ignored) {}

        return make();
    }

    public ItemBuilder owner(String name) {
        if(this.stack.getType() != Material.SKULL_ITEM)
            return this;

        ((SkullMeta) this.meta).setOwner(name);
        return make();
    }

    public boolean flag(ItemFlag flag) {
        return this.meta.hasItemFlag(flag);
    }

    public ItemBuilder flag(ItemFlag... flags) {
        this.meta.addItemFlags(flags);
        return make();
    }

    public boolean unbreakable() {
        return this.meta.spigot().isUnbreakable();
    }

    public ItemBuilder unbreakable(boolean unbreakable) {
        this.meta.spigot().setUnbreakable(unbreakable);
        return make();
    }

    public ItemBuilder make() {
        this.stack.setItemMeta(this.meta);
        return this;
    }

    public ItemStack build() {
        return this.stack;
    }

    public static ItemBuilder of() {
        return new ItemBuilder();
    }

    public static ItemBuilder of(Material type) {
        return new ItemBuilder(type);
    }

    public static ItemBuilder of(Material type, int amount) {
        return new ItemBuilder(type, amount);
    }

    public static ItemBuilder of(ItemStack item) {
        return new ItemBuilder(item);
    }

    public static ItemBuilder of(ItemStack item, boolean direct) {
        return new ItemBuilder(item, direct);
    }

}
