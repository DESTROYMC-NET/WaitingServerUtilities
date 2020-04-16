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
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class WaitingServerUtilities extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
        Bukkit.getServer().getPluginManager().registerEvents(new eJoinLeaveEvents(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new eOffhandCheck(), this);

        for (Player player : Bukkit.getOnlinePlayers()) {
            eOffhandCheck.lastChange.put(player, System.currentTimeMillis());
            eOffhandCheck.warnings.put(player, 0);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        event.getPlayer().teleport(new Location(event.getPlayer().getWorld(), 0.5, 3.5, 0.5));
        event.getPlayer().sendMessage(ChatColor.GOLD + "Welcome to DESTROYMC.NET");
        if (event.getPlayer().hasPermission("mapcha.bypass")) {
            event.getPlayer().sendMessage(PlaceholderAPI.setPlaceholders(event.getPlayer(), ChatColor.GOLD + "Captcha will appear in %luckperms_expiry_time_mapcha.bypass%."));
        } else {
            event.getPlayer().sendMessage(ChatColor.GREEN + "Open the map and type the code to complete the captcha. This captcha will appear again after 24 hours.");
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
    public void onPlayerDropItem(PlayerDropItemEvent event) {
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
                        player.teleport(new Location(player.getWorld(), 0.5, 3.5, 0.5));
                        player.setFallDistance(0F);
                        cancel();
                    }
                }.runTaskLater(this, 1L);
            }
        }
    }
}
