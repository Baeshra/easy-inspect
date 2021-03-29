package com.yahoo.baeshra.EasyInspect;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class LocationalCommandSender extends CraftPlayer {

    private List<String> messageHistory = new ArrayList<String>();
    private Pattern callbackPattern = null;
    private CallbackInterface callbackInterface;
    private Long maxTimeout;
    private BukkitTask timeoutTask = null;
    private boolean returned = false;
    private Location location;
    private SpigotPlayer spigotPlayer;

    public static void runCommandAt(Player player, Location location, String command, String endRegex, Long timeoutInMs, CallbackInterface callback) {
        EntityPlayer entityPlayer = (EntityPlayer) ((CraftPlayer) player).getHandle();;
        LocationalCommandSender delegate = new LocationalCommandSender((CraftServer) Bukkit.getServer(), entityPlayer, location, endRegex, timeoutInMs, callback);
        delegate.setOp(true);
        Bukkit.getServer().dispatchCommand(delegate.getPlayer(), command);
        delegate.resetTimeout();
    }

    public void resetTimeout() {
        if(timeoutTask != null) {
            timeoutTask.cancel();
        }
        timeoutTask = Bukkit.getScheduler().runTaskLater(App.plugin, new Runnable() {
            @Override
            public void run() {
                if(!returned) {
                    returned = true;
                    callbackInterface.callback(messageHistory);
                }
            }
        }, maxTimeout / 50); //Unit is number of server ticks
    }

    public void resetMessages() {
        messageHistory.clear();
    }

    public List<String> getMessages() {
        return messageHistory;
    }

    public LocationalCommandSender(CraftServer server, EntityPlayer entity, Location location, String callRegex, Long timeoutInMs, CallbackInterface callback) {
        super(server, entity);
        this.maxTimeout = timeoutInMs;
        if(callRegex != null) {
            this.callbackPattern = Pattern.compile(callRegex);
        }
        this.location = location;
        this.callbackInterface = callback;
        this.spigotPlayer = new SpigotPlayer(this);
    }

    private void checkCallback() {
        if(callbackPattern != null) {
            String message = messageHistory.get(messageHistory.size() - 1);
            Matcher m = callbackPattern.matcher(message);
            if(m.find()) {
                if(!returned) {
                    returned = true;
                    timeoutTask.cancel();
                    timeoutTask = Bukkit.getScheduler().runTaskLater(App.plugin, new Runnable() {
                        @Override
                        public void run() {
                            callbackInterface.callback(messageHistory);
                        }
                    }, 4);
                }
            }
        }
    }

    @Override
    public Player.Spigot spigot() {
        return spigotPlayer;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public void sendMessage(String message) {
        messageHistory.add(message);
        checkCallback();
    }

    @Override
    public void sendMessage(String[] messages) {
        String message = String.join(" ", messages);
        messageHistory.add(message);
        checkCallback();
    }

    @Override
    public void sendMessage(UUID arg0, String message) {
        messageHistory.add(message);
        checkCallback();
    }

    @Override
    public void sendMessage(UUID arg0, String[] messages) {
        String message = String.join(" ", messages);
        messageHistory.add(message);
        checkCallback();
    }
    
    @Override
    public void sendRawMessage(String message) {
        messageHistory.add(message);
        checkCallback();
    }
    
    @Override
    public void sendRawMessage(UUID sender, String message) {
        messageHistory.add(message);
        checkCallback();
    }
}
