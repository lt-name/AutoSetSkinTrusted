package cn.lanink.autosetskintrusted.task;

import cn.lanink.autosetskintrusted.AutoSetSkinTrusted;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.level.Level;
import cn.nukkit.scheduler.PluginTask;

/**
 * @author lt_name
 */
public class CheckEntityTask extends PluginTask<AutoSetSkinTrusted> {

    public CheckEntityTask(AutoSetSkinTrusted owner) {
        super(owner);
    }

    @Override
    public void onRun(int i) {
        for (Level level : Server.getInstance().getLevels().values()) {
            if (level == null) continue;
            for (Entity entity : level.getEntities()) {
                if (entity instanceof EntityHuman) {
                    EntityHuman entityHuman = (EntityHuman) entity;
                    Skin skin = entityHuman.getSkin();
                    if (skin == null || skin.isTrusted()) continue;
                    skin.setTrusted(true);
                    AutoSetSkinTrusted.setPlayerSkin(entityHuman, skin);
                }
            }
        }
    }

}
