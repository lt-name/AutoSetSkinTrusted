package cn.lanink.autosetskintrusted;

import cn.lanink.autosetskintrusted.task.CheckEntityTask;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntitySpawnEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.network.protocol.PlayerSkinPacket;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.scheduler.Task;

import java.util.HashSet;

/**
 * @author lt_name
 */
public class AutoSetSkinTrusted extends PluginBase implements Listener {

    private static AutoSetSkinTrusted autoSetSkinTrusted;

    public static AutoSetSkinTrusted getInstance() {
        return autoSetSkinTrusted;
    }

    @Override
    public void onLoad() {
        autoSetSkinTrusted = this;
    }

    @Override
    public void onEnable() {
        this.getServer().getScheduler().scheduleRepeatingTask(this,
                new CheckEntityTask(this), 20);
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Server.getInstance().getScheduler().scheduleDelayedTask(this, new Task() {
            @Override
            public void onRun(int i) {
                if (player != null && player.isOnline()) {
                    Skin skin = player.getSkin();
                    if (skin == null || skin.isTrusted()) {
                        return;
                    }
                    switch(skin.getSkinData().data.length) {
                        case 8192:
                        case 16384:
                        case 32768:
                        case 65536:
                            break;
                        default:
                            return;
                    }
                    skin.setTrusted(true);
                    setHumanSkin(player, skin);
                }
            }
        }, 10);
    }

    //@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (event.isHuman()) {
            EntityHuman human = (EntityHuman) event.getEntity();
            Skin skin = human.getSkin();
            Server.getInstance().getScheduler().scheduleDelayedTask(this, () -> {
                if (skin != null && !skin.isTrusted()) {
                    skin.setTrusted(true);
                    setHumanSkin(human, skin);
                }
            }, 20);
        }
    }

    public static void setHumanSkin(EntityHuman human, Skin skin) {
        PlayerSkinPacket packet = new PlayerSkinPacket();
        packet.uuid = human.getUniqueId();
        packet.skin = skin;
        packet.newSkinName = skin.getSkinId();
        packet.oldSkinName = human.getSkin().getSkinId();
        HashSet<Player> players = new HashSet<>(human.getViewers().values());
        if (human instanceof Player) {
            players.add((Player) human);
        }
        if (!players.isEmpty()) {
            Server.broadcastPacket(players, packet);
        }
        human.setSkin(skin);
    }

}
