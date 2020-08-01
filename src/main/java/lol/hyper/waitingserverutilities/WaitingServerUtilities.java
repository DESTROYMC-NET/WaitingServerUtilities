package lol.hyper.waitingserverutilities;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustByBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public final class WaitingServerUtilities extends JavaPlugin implements Listener {

    public static final HashMap<Player, Long> lastChange = new HashMap<>(); // x1D - Offhand Swap fix
    public static final HashMap<Player, Integer> warnings = new HashMap<>(); // x1D - Offhand Swap fix

    @Override
    public void onEnable() {
        Bukkit.getServer().getPluginManager().registerEvents(this, this);

        for (Player player : Bukkit.getOnlinePlayers()) {
            lastChange.put(player, System.currentTimeMillis());
            warnings.put(player, 0);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        for (Player onlinePlayer : getServer().getOnlinePlayers()) {
            event.getPlayer().hidePlayer(this, onlinePlayer);
            onlinePlayer.hidePlayer(this, event.getPlayer());
        }
        event.setJoinMessage(null);
        lastChange.put(event.getPlayer(), System.currentTimeMillis()); // x1D - Offhand Swap fix
        warnings.put(event.getPlayer(), 0); // x1D - Offhand Swap fix
        event.getPlayer().teleport(new Location(Bukkit.getWorld("world_the_end"), 0.5, 69, 0.5, 0, 0));
        if (event.getPlayer().hasPlayedBefore()) {
            event.getPlayer().sendMessage(ChatColor.GOLD + "Welcome back to DESTROYMC.NET");
        } else {
            event.getPlayer().sendMessage(ChatColor.GOLD + "Welcome to DESTROYMC.NET");
        }
        if (event.getPlayer().hasPermission("mapcha.bypass") && !event.getPlayer().isOp()) {
            String withPlaceholder = PlaceholderAPI.setPlaceholders(Bukkit.getOfflinePlayer(event.getPlayer().getUniqueId()), ChatColor.DARK_AQUA + "Captcha will appear in %luckperms_expiry_time_mapcha.bypass%.");
            event.getPlayer().sendMessage(withPlaceholder);
        } else {
            event.getPlayer().sendMessage(ChatColor.DARK_AQUA + "To prove you are not a bot, please complete the captcha. Open the map and type the code into chat.");
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        event.setQuitMessage(null);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void PlayerCommand(PlayerCommandPreprocessEvent event) {
        if (!event.getPlayer().isOp()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) { event.setCancelled(true); }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.setDeathMessage(null);
    }

    @EventHandler
    public void onPlayerHurt(EntityDamageEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onCrystalExplode(EntityCombustByBlockEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onWeather(WeatherChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void noHunger(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                new BukkitRunnable() {
                    public void run() {
                        player.teleport(new Location(Bukkit.getWorld("world_the_end"), 0.5, 69, 0.5, 0, 0));
                        player.setFallDistance(0F);
                        cancel();
                    }
                }.runTaskLater(this, 1L);
            }
        }
    }
    // x1D - Offhand Swap fix
    @EventHandler
    public void onMainHandChange(PlayerSwapHandItemsEvent event) {
        if (lastChange.get(event.getPlayer()) != null && lastChange.get(event.getPlayer()) + 250 > System.currentTimeMillis()) {
            warnings.put(event.getPlayer(), warnings.get(event.getPlayer()) + 1);
            event.getPlayer().sendMessage(ChatColor.GOLD + "Please slow down or you will be kicked. (" + warnings.get(event.getPlayer()) + "/5)");
            if (warnings.get(event.getPlayer()) > 4) {
                event.getPlayer().kickPlayer("§4§lanti riga exploit\n§7by x1D");
                warnings.put(event.getPlayer(), 0);
            }
        }
        lastChange.put(event.getPlayer(), System.currentTimeMillis());
    }
}
