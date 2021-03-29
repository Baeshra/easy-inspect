package com.yahoo.baeshra.EasyInspect;

import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.BaseComponent;

public class SpigotPlayer extends Player.Spigot {
    
    LocationalCommandSender sender;

    public SpigotPlayer(LocationalCommandSender sender) {
        this.sender = sender;
    }

    @Override
    public void sendMessage(net.md_5.bungee.api.chat.BaseComponent component) {
        sender.sendMessage(BaseComponent.toPlainText(component));
    }
}
