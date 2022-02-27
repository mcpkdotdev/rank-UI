package com.mcpkdot.rankui;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.logging.Logger;

public class RankUI extends JavaPlugin implements Listener {

    private static final ItemStack[] icons = new ItemStack[54];
    Boolean essentials;
    Double premiumPrice, ultraPrice, bananePrice, championPrice, ultimatePrice, masterPrice;
    String premiumName, ultraName, bananeName, championName, ultimateName, masterName;
    Boolean english;
    String premiumColor, ultraColor, bananeColor, championColor, ultimateColor, masterColor;
    private static final Logger log = Logger.getLogger("Minecraft");
    private static Economy econ = null;

    @Override
    public void onEnable() {

        if (!setupEconomy() ) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        FileConfiguration config = this.getConfig();

        config.addDefault("premium", (double)10);
        config.addDefault("ultra", (double)10);
        config.addDefault("banane", (double)10);
        config.addDefault("champion", (double)10);
        config.addDefault("ultimate", (double)10);
        config.addDefault("master", (double)10);

        config.addDefault("r1", "premium");
        config.addDefault("r2", "ultra");
        config.addDefault("r3", "banane");
        config.addDefault("r4", "champion");
        config.addDefault("r5", "ultimate");
        config.addDefault("r6", "master");

        config.addDefault("english", true);

        config.addDefault("premiumColor", "§6");
        config.addDefault("ultraColor", "§9");
        config.addDefault("bananeColor", "§e");
        config.addDefault("championColor", "§d");
        config.addDefault("ultimateColor", "§b");
        config.addDefault("masterColor", "§4");

        config.options().copyDefaults(true);
        saveConfig();




        premiumPrice = config.getDouble("premium");
        ultraPrice = config.getDouble("ultra");
        bananePrice = config.getDouble("banane");
        championPrice = config.getDouble("champion");
        ultimatePrice = config.getDouble("ultimate");
        masterPrice = config.getDouble("master");

        premiumName = config.getString("r1");
        ultraName = config.getString("r2");
        bananeName = config.getString("r3");
        championName = config.getString("r4");
        ultimateName = config.getString("r5");
        masterName = config.getString("r6");

        english = config.getBoolean("english");

        premiumColor = config.getString("premiumColor");
        ultraColor = config.getString("ultraColor");
        bananeColor = config.getString("bananeColor");
        championColor = config.getString("championColor");
        ultimateColor = config.getString("ultimateColor");
        masterColor = config.getString("masterColor");

        ItemStack nothingDark = createItem("", Material.STAINED_GLASS_PANE, 8, "");
        ItemStack nothingLight = createItem("", Material.STAINED_GLASS_PANE, 7, "");
        ItemStack premium = createItem(premiumColor + premiumName + " rank", Material.IRON_BLOCK, 5, "Price: " + premiumPrice);
        ItemStack ultra = createItem(ultraColor + ultraName + " rank", Material.GOLD_BLOCK, 5, "Price: " + ultraPrice);
        ItemStack banane = createItem(bananeColor + bananeName + " rank", Material.REDSTONE_BLOCK, 5, "Price: " + bananePrice);
        ItemStack champion = createItem(championColor + championName + " rank", Material.LAPIS_BLOCK, 5, "Price: " + championPrice);
        ItemStack ultimate = createItem(ultimateColor + ultimateName + " rank", Material.DIAMOND_BLOCK, 5, "Price: " + ultimatePrice);
        ItemStack master = createItem(masterColor + masterName + " rank", Material.EMERALD_BLOCK, 5, "Price: " + masterPrice);

        {
        for (int i = 0; i < icons.length; i++) {
            if (i % 2 == 1) {
                icons[i] = nothingLight;
            } else {
                icons[i] = nothingDark;
            }
        }
        icons[4] = premium;
        icons[13] = ultra;
        icons[22] = banane;
        icons[31] = champion;
        icons[40] = ultimate;
        icons[49] = master;
        } // INIT OF INVENTORY

        Bukkit.getPluginManager().registerEvents(this, this);


        essentials = getServer().getPluginManager().getPlugin("Essentials") != null;

        {
            getCommand("rankshop").setExecutor(new RankShop());
            getCommand("rank").setExecutor(new Rank());
        } //Commands


    }

    @EventHandler
    public void onInventoryModify(InventoryClickEvent e){
        if(e.getClickedInventory().getTitle().equals("Rank shop")){
            (e.getWhoClicked()).closeInventory();
            e.setCancelled(true);
            onClick((Player) e.getWhoClicked(), e.getSlot());
            e.getInventory().remove(createItem("", Material.STAINED_GLASS_PANE, 8, ""));
            e.getInventory().remove(createItem("", Material.STAINED_GLASS_PANE, 7, ""));
        }
    }


    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public static Economy getEconomy() {
        return econ;
    }



    public void onClick(Player p, int slot) {
        switch (slot) {
            case 4:
                if (getEconomy().getBalance(p) >= premiumPrice) {
                    if(p.hasPermission("group." + premiumName) || p.hasPermission("group." + ultraName) || p.hasPermission("group." + bananeName) || p.hasPermission("group." + championName) || p.hasPermission("group." + ultimateName) || p.hasPermission("group." + championName)){
                        getEconomy().withdrawPlayer(p, premiumPrice);
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + p.getName() + " parent set " + premiumName);
                        p.closeInventory();
                        p.sendMessage("§7You bought " + premiumColor + premiumName + " §7rank!");
                    } else{
                        p.sendMessage("§7You already have higher rank!");
                    }
                } else {
                    p.sendMessage("§7You do not have enough money.");
                }
                break;
            case 13:
                if (getEconomy().getBalance(p) >= ultraPrice) {
                    if(p.hasPermission("group." + ultraName) || p.hasPermission("group." + bananeName) || p.hasPermission("group." + championName) || p.hasPermission("group." + ultimateName) || p.hasPermission("group." + championName)) {
                        getEconomy().withdrawPlayer(p, ultraPrice);
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + p.getName() + " parent set " + ultraName);
                        p.closeInventory();
                        p.sendMessage("§7You bought " + ultraColor + ultraName + " §7rank!");
                    } else{
                        p.sendMessage("§7You already have higher rank!");
                    }
                } else {
                    p.sendMessage("§7You do not have enough money.");
                }
                break;
            case 22:
                if (getEconomy().getBalance(p) >= bananePrice) {
                    if(p.hasPermission("group." + bananeName) || p.hasPermission("group." + championName) || p.hasPermission("group." + ultimateName) || p.hasPermission("group." + championName)) {
                        getEconomy().withdrawPlayer(p, bananePrice);
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + p.getName() + " parent set " + bananeName);
                        p.closeInventory();
                        p.sendMessage("§7You bought " + bananeColor + bananeName + " §7rank!");
                    } else{
                        p.sendMessage("§7You already have higher rank!");
                    }
                } else{
                    p.sendMessage("§7You do not have enough money.");
                }
                break;
            case 31:
                if (getEconomy().getBalance(p) >= championPrice) {
                    if(p.hasPermission("group." + championName) || p.hasPermission("group." + ultimateName) || p.hasPermission("group." + championName)) {
                        getEconomy().withdrawPlayer(p, championPrice);
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + p.getName() + " parent set " + championName);
                        p.closeInventory();
                        p.sendMessage("§7You bought " + championColor + championName + " §7rank!");
                    } else{
                        p.sendMessage("§7You already have higher rank!");
                    }
                } else{
                    p.sendMessage("§7You do not have enough money.");
                }
                break;
            case 40:
                if (getEconomy().getBalance(p) >= ultimatePrice) {
                    if(p.hasPermission("group." + ultimateName) || p.hasPermission("group." + championName)) {
                        getEconomy().withdrawPlayer(p, ultimatePrice);
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + p.getName() + " parent set " + ultimateName);
                        p.closeInventory();
                        p.sendMessage("§7You bought " + ultimateColor + ultimateName + " §7rank!");
                    } else{
                        p.sendMessage("§7You already have higher rank!");
                    }
                } else{
                    p.sendMessage("§7You do not have enough money.");
                }
                break;
            case 49:
                if (getEconomy().getBalance(p) >= masterPrice) {
                    if(p.hasPermission("group." + championName)) {
                        getEconomy().withdrawPlayer(p, masterPrice);
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + p.getName() + " parent set " + masterName);
                        p.closeInventory();
                        p.sendMessage("§7You bought " + masterColor + masterName + " §7rank!");
                    } else{
                        p.sendMessage("§7You already have higher rank!");
                    }
                } else {
                    p.sendMessage("§7You do not have enough money.");
                }
        }
    }

    public static void openInv(Player player) {
        Inventory inventory = Bukkit.createInventory(player, 54, "Rank shop");
        for (int i = 0; i < 54; i++) {
            if (icons[i] != null) {
                inventory.setItem(i, icons[i]);
            }
        }
        player.openInventory(inventory);
    }

    private ItemStack createItem(String name, Material mat, int dyeColor, String lore){
        ItemStack stack = new ItemStack(mat, 1, (short) dyeColor);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Collections.singletonList(lore));
        stack.setItemMeta(meta);
        return stack;
    }
}
