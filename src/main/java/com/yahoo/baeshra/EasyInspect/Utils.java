package com.yahoo.baeshra.EasyInspect;

import java.util.List;
import java.util.UUID;

import com.mojang.authlib.GameProfile;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.MinecraftServer;
import net.minecraft.server.v1_16_R3.PlayerInteractManager;
import net.minecraft.server.v1_16_R3.WorldServer;

public class Utils {
    
    public static void sendMessage(Player p, String message) {
        p.sendMessage(String.format("[EasyInspect] %s", message));
    }

}
