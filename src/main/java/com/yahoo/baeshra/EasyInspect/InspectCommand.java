package com.yahoo.baeshra.EasyInspect;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;

import net.md_5.bungee.api.ChatColor;

public class InspectCommand implements CommandExecutor {

        // Entry regex: 
        // Page regex: 

    private static final Pattern ENTRY_REGEX = Pattern.compile("(\\d+.\\d+)\\/([h|m|d]) ago - (\\S*) (added|removed) x(\\d+) (\\S*).");
    private static final Pattern PAGE_REGEX = Pattern.compile(".*Page (\\d+)\\/(\\d+).*");

    public InspectCommand() {

    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            return inspectPageOne(sender, cmd, label, args);
        }
        return false;
    }

    public void parseResults(Map<String,Map<String,Integer>> existingMap, List<String> input, Location l, Player p) {
        Map<String,Map<String,Integer>> resultMap = new HashMap<>();
        if(existingMap != null) {
            resultMap = existingMap;
        }
        boolean hasNextPage = false;
        //first and second line are always headers
        //last line is always page indicator (except if there's no page 2)
        for(String s : input) {
            s = ChatColor.stripColor(s);
            Matcher entry = ENTRY_REGEX.matcher(s);
            Matcher page = PAGE_REGEX.matcher(s);
            //Utils.sendMessage(p, s);
            if(entry.matches()) {
                String timeAgo = entry.group(1) + "/" + entry.group(2);
                String player = entry.group(3);
                String action = entry.group(4);
                int amount = Integer.parseInt(entry.group(5));
                String item = entry.group(6);
                if(!resultMap.containsKey(player))
                    resultMap.put(player, new HashMap<String,Integer>());
                if(!resultMap.get(player).containsKey(item))
                    resultMap.get(player).put(item, 0);
                resultMap.get(player).put(item, resultMap.get(player).get(item) + amount * (action.equals("added") ? 1 : -1));
            } else if(page.matches()) {
                int currentPage = Integer.parseInt(page.group(1));
                int maxPages = Integer.parseInt(page.group(2));
                if(currentPage < maxPages) {
                    hasNextPage = true;
                    inspectNextPage(resultMap, l, p, currentPage + 1);
                }
            }
        }
        if(!hasNextPage) {
            for(String player : resultMap.keySet()) {
                for(String item : resultMap.get(player).keySet()) {
                    int sum = resultMap.get(player).get(item);
                    Utils.sendMessage(p, String.format("%s %s %s %s", player, (sum < 0 ? "removed" : "added"), Math.abs(sum), item));
                }
            }
        }
    }

    // TODO instead of iterating through each page, make the initial get call,
    //      discard the results, then use co l 1:1000000 to get one page with all the results.
    //      this removes delays from making multiple calls
    public void inspectNextPage(final Map<String,Map<String,Integer>> resultMap, final Location l, final Player player, int page) {
        LocationalCommandSender.runCommandAt(player, l, 
        String.format("co l %s", page), PAGE_REGEX.toString(), 100L, new CallbackInterface() {
            @Override
            public void callback(List<String> input) {
                parseResults(resultMap, input, l, player);
            }
        });
    }

    public boolean inspectPageOne(CommandSender sender, Command cmd, String label, String[] args) {
        final Player player = (Player) sender;
        Block b = player.getTargetBlock(null, 200);
        TimeArg timeArg = new TimeArg(args.length > 0 ? args[0] : null, TimeArg.TimeUnit.DAY, 3);

        if(b.getState() instanceof InventoryHolder) {
            final Location l = b.getLocation();
            LocationalCommandSender.runCommandAt(player, l, 
                String.format("co lookup a:container r:1 t:%ss", timeArg.getSeconds()), PAGE_REGEX.toString(), 200L, new CallbackInterface() {
                    @Override
                    public void callback(List<String> input) {
                        parseResults(null, input, l, player);
                    }
                });
            return true;
        } else {
            Utils.sendMessage(player, "Target must be an inventory");
        }
        return false;
    }
    
}