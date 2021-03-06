package me.onlyfire.firefreeze.command;

import me.onlyfire.firefreeze.Firefreeze;
import me.onlyfire.firefreeze.objects.FreezeProfile;
import me.onlyfire.firefreeze.utils.ColorUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MainCommand implements CommandExecutor {

    private String[] help = new String[]{
            "",
            ColorUtil.colorize("&4Fire&cFreeze &7(v2.3)"),
            "",
            ColorUtil.colorize("&c/firefreeze arguments:"),
            ColorUtil.colorize(" &c|- &freload &4- &cReloads the plugin"),
            ColorUtil.colorize(" &c|- &fupdate &4- &cChecks for updates"),
            ColorUtil.colorize(" &c|- &frestoredata &4- &cDeletes ALL the entries"),
            ColorUtil.colorize(" &c|- &fremovedata <player> &4- &cDeletes all the entries for a specified player"),
            ColorUtil.colorize(" &c|- &funfreezeall &4- &cUnfreezes all the players"),
            ColorUtil.colorize(" &c|- &fsetpos <frozen|staff|final> &4- &cSets the positions"),
            ColorUtil.colorize(" &c|- &fclearpos <frozen|staff|final|all> &4- &cClears a position"),
            "",
            ColorUtil.colorize("&cOther commands:"),
            ColorUtil.colorize("&f/freezehistory &4- &cSee the freeze history for a player"),
            ""
    };
    private String[] setPosHelp = new String[]{
            "",
            ColorUtil.colorize("&c/firefreeze setpos arguments:"),
            ColorUtil.colorize(" &c|- &ffrozen &4- &cSets the frozen spawn"),
            ColorUtil.colorize(" &c|- &fstaff &4- &cSets the staff spawn"),
            ColorUtil.colorize(" &c|- &ffinal &4- &cSets the final spawn"),
            ""
    };
    private String[] clearPosHelp = new String[]{
            "",
            ColorUtil.colorize("&c/firefreeze clearpos arguments:"),
            ColorUtil.colorize(" &c|- &ffrozen &4- &cClears the frozen spawn"),
            ColorUtil.colorize(" &c|- &fstaff &4- &cClears the staff spawn"),
            ColorUtil.colorize(" &c|- &ffinal &4- &cClears the final spawn"),
            ColorUtil.colorize(" &c|- &fall &4- &cClears all positions"),
            ""
    };
    private Firefreeze plugin;

    public MainCommand(Firefreeze plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("firefreeze.admin")) {
            sender.sendMessage(ColorUtil.colorize(plugin.getPrefix() + plugin.getMessagesFile().getString("errors.not_permission")));
            return true;
        }


        if (args.length == 1) {

            switch (args[0]) {

                case "reload": {
                    plugin.getServer().getPluginManager().disablePlugin(plugin);
                    plugin.getServer().getPluginManager().enablePlugin(plugin);

                    sender.sendMessage("§aSuccessfully reloaded FireFreeze!");
                    break;
                }

                case "update": {
                    sender.sendMessage("§aChecking for updates...");
                    plugin.getUpdater().update();
                    if (plugin.getUpdater().updateAvailable()) {
                        sender.sendMessage(ColorUtil.colorize("&4[FireFreeze] &cFound an update on SpigotMC! " +
                                        "Please download it at &4https://www.spigotmc.org/resources/77105/"));
                    } else {
                        sender.sendMessage("§aNo update found.");
                    }
                    break;
                }

                case "help": {
                    sender.sendMessage(help);
                    break;
                }

                case "setpos": {
                    sender.sendMessage(setPosHelp);
                    break;
                }

                case "clearpos": {
                    sender.sendMessage(clearPosHelp);
                    break;
                }

                case "restoredata": {
                    this.plugin.getConnection().restoreEntries();
                    sender.sendMessage("§aSuccessfully restored the database!");
                    break;
                }

                case "unfreezeall": {
                    for (FreezeProfile profiles : plugin.getAllPlayers()) {
                        if (profiles.isFrozen() && profiles.getWhoFroze() != null)
                            profiles.forceUnfreeze(profiles.getWhoFroze());
                    }
                    sender.sendMessage("§aSuccesfully unfroze all the online players!");
                    break;
                }

                default: {
                    sender.sendMessage("§cUnrecognized command! Use /firefreeze help for a list of commands");
                    break;
                }

            }
            return true;
        }


        if (args.length == 2) {

            if (args[0].equalsIgnoreCase("removedata")) {
                final Player target = this.plugin.getServer().getPlayerExact(args[1]);
                if (target == null) {
                    sender.sendMessage(ColorUtil.colorize(this.plugin.getPrefix() + this.plugin.getMessagesFile().getString("errors.player_not_found")));
                    return true;
                }
                this.plugin.getConnection().removeEntriesFor(target.getName());
                sender.sendMessage("§aSuccessfully removed the entries for " + target.getName());
                return true;
            }

            if (args[0].equalsIgnoreCase("setpos")) {

                if (!(sender instanceof Player)) {
                    sender.sendMessage(ColorUtil.colorize(plugin.getMessagesFile().getString("errors.only_players")));
                    return true;
                }

                Player player = (Player) sender;

                switch (args[1]) {

                    case "frozen": {
                        plugin.getLocationFile().set("frozen.use", true);
                        plugin.getLocationFile().set("frozen.world", player.getLocation().getWorld().getName());
                        plugin.getLocationFile().set("frozen.x", player.getLocation().getX());
                        plugin.getLocationFile().set("frozen.y", player.getLocation().getY());
                        plugin.getLocationFile().set("frozen.z", player.getLocation().getZ());
                        plugin.getLocationFile().set("frozen.yaw", player.getLocation().getYaw());
                        plugin.getLocationFile().set("frozen.pitch", player.getLocation().getPitch());
                        plugin.getLocationFile().saveConfig();
                        player.sendMessage("§aSuccessfully set the 'frozen' spawn.");
                        player.sendMessage("§cNOTE: You need to restart the server to apply the changes!");
                        break;
                    }

                    case "staff": {
                        plugin.getLocationFile().set("staff.use", true);
                        plugin.getLocationFile().set("staff.world", player.getLocation().getWorld().getName());
                        plugin.getLocationFile().set("staff.x", player.getLocation().getX());
                        plugin.getLocationFile().set("staff.y", player.getLocation().getY());
                        plugin.getLocationFile().set("staff.z", player.getLocation().getZ());
                        plugin.getLocationFile().set("staff.yaw", player.getLocation().getYaw());
                        plugin.getLocationFile().set("staff.pitch", player.getLocation().getPitch());
                        plugin.getLocationFile().saveConfig();
                        player.sendMessage("§aSuccessfully set the 'staff' spawn.");
                        player.sendMessage("§cNOTE: You need to restart the server to apply the changes!");
                        break;
                    }

                    case "final": {
                        plugin.getLocationFile().set("final.use", true);
                        plugin.getLocationFile().set("final.world", player.getLocation().getWorld().getName());
                        plugin.getLocationFile().set("final.x", player.getLocation().getX());
                        plugin.getLocationFile().set("final.y", player.getLocation().getY());
                        plugin.getLocationFile().set("final.z", player.getLocation().getZ());
                        plugin.getLocationFile().set("final.yaw", player.getLocation().getYaw());
                        plugin.getLocationFile().set("final.pitch", player.getLocation().getPitch());
                        plugin.getLocationFile().saveConfig();
                        player.sendMessage("§aSuccessfully set the 'final' spawn.");
                        player.sendMessage("§cNOTE: You need to restart the server to apply the changes!");
                        break;
                    }

                    default: {
                        sender.sendMessage("§cIncorrect postion! Use §7frozen/staff/final");
                        break;
                    }

                }

            } else if (args[0].equalsIgnoreCase("clearpos")) {

                switch (args[1]) {

                    case "frozen": {
                        plugin.getLocationFile().set("frozen.use", false);
                        plugin.getLocationFile().set("frozen.world", "world");
                        plugin.getLocationFile().set("frozen.x", 0);
                        plugin.getLocationFile().set("frozen.y", 0);
                        plugin.getLocationFile().set("frozen.z", 0);
                        plugin.getLocationFile().set("frozen.yaw", 0);
                        plugin.getLocationFile().set("frozen.pitch", 0);
                        plugin.getLocationFile().saveConfig();
                        sender.sendMessage("§aSuccessfully cleared the 'frozen' spawn.");
                        sender.sendMessage("§cNOTE: You need to restart the server to apply the changes!");
                        break;
                    }

                    case "staff": {
                        plugin.getLocationFile().set("staff.use", false);
                        plugin.getLocationFile().set("staff.world", "world");
                        plugin.getLocationFile().set("staff.x", 0);
                        plugin.getLocationFile().set("staff.y", 0);
                        plugin.getLocationFile().set("staff.z", 0);
                        plugin.getLocationFile().set("staff.yaw", 0);
                        plugin.getLocationFile().set("staff.pitch", 0);
                        plugin.getLocationFile().saveConfig();
                        sender.sendMessage("§aSuccessfully cleared the 'staff' spawn.");
                        sender.sendMessage("§cNOTE: You need to restart the server to apply the changes!");
                        break;
                    }

                    case "final": {
                        plugin.getLocationFile().set("final.use", false);
                        plugin.getLocationFile().set("final.world", "world");
                        plugin.getLocationFile().set("final.x", 0);
                        plugin.getLocationFile().set("final.y", 0);
                        plugin.getLocationFile().set("final.z", 0);
                        plugin.getLocationFile().set("final.yaw", 0);
                        plugin.getLocationFile().set("final.pitch", 0);
                        plugin.getLocationFile().saveConfig();
                        sender.sendMessage("§aSuccessfully cleared the 'final' spawn.");
                        sender.sendMessage("§cNOTE: You need to restart the server to apply the changes!");
                        break;
                    }

                    case "all": {
                        plugin.getLocationFile().set("frozen.use", false);
                        plugin.getLocationFile().set("frozen.world", "world");
                        plugin.getLocationFile().set("frozen.x", 0);
                        plugin.getLocationFile().set("frozen.y", 0);
                        plugin.getLocationFile().set("frozen.z", 0);
                        plugin.getLocationFile().set("frozen.yaw", 0);
                        plugin.getLocationFile().set("frozen.pitch", 0);

                        plugin.getLocationFile().set("staff.use", false);
                        plugin.getLocationFile().set("staff.world", "world");
                        plugin.getLocationFile().set("staff.x", 0);
                        plugin.getLocationFile().set("staff.y", 0);
                        plugin.getLocationFile().set("staff.z", 0);
                        plugin.getLocationFile().set("staff.yaw", 0);
                        plugin.getLocationFile().set("staff.pitch", 0);

                        plugin.getLocationFile().set("final.use", false);
                        plugin.getLocationFile().set("final.world", "world");
                        plugin.getLocationFile().set("final.x", 0);
                        plugin.getLocationFile().set("final.y", 0);
                        plugin.getLocationFile().set("final.z", 0);
                        plugin.getLocationFile().set("final.yaw", 0);
                        plugin.getLocationFile().set("final.pitch", 0);
                        plugin.getLocationFile().saveConfig();
                        sender.sendMessage("§aSuccessfully cleared all positions.");
                        sender.sendMessage("§cNOTE: You need to restart the server to apply the changes!");
                        break;
                    }
                    default: {
                        sender.sendMessage("§cIncorrect postion! Use §7frozen/staff/final/all");
                        break;
                    }
                }

            } else {
                sender.sendMessage("§cUnrecognized command! Use /firefreeze help for a list of commands");
            }
            return true;
        }

        sender.sendMessage("§4Fire§cFreeze §7(v2.3) | §cType /firefreeze help for a list of commands");
        return true;

    }
}
