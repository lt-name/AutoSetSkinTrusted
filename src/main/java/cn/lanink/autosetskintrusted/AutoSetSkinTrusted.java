package cn.lanink.autosetskintrusted;

import cn.lanink.autosetskintrusted.task.CheckEntityTask;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntitySpawnEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.network.protocol.PlayerSkinPacket;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.scheduler.Task;

/**
 * @author lt_name
 */
public class AutoSetSkinTrusted extends PluginBase implements Listener {

    private static AutoSetSkinTrusted autoSetSkinTrusted;

    public static AutoSetSkinTrusted getInstance() {
        return autoSetSkinTrusted;
    }

    @Override
    public void onEnable() {
        autoSetSkinTrusted = this;
        getServer().getScheduler().scheduleDelayedRepeatingTask(this,
                new CheckEntityTask(this), 200, 1200);
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Server.getInstance().getScheduler().scheduleDelayedTask(this, new Task() {
            @Override
            public void onRun(int i) {
                if (player != null) {
                    Skin skin = player.getSkin();
                    if (skin == null || skin.isTrusted()) return;
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
                    setPlayerSkin(player, skin);
                }
            }
        }, 10);
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (!event.isCancelled() && event.isHuman()) {
            ((EntityHuman) event.getEntity()).getSkin().setTrusted(true);
        }
    }

    public static void setPlayerSkin(EntityHuman human, Skin skin) {
        PlayerSkinPacket packet = new PlayerSkinPacket();
        packet.skin = skin;
        packet.newSkinName = skin.getSkinId();
        packet.oldSkinName = human.getSkin().getSkinId();
        packet.uuid = human.getUniqueId();
        human.setSkin(skin);
        human.getLevel().getPlayers().values().forEach(player -> player.dataPacket(packet));
    }

}
