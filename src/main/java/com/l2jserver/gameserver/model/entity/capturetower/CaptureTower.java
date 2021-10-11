package com.l2jserver.gameserver.model.entity.capturetower;

import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.enums.TowerMode;
import com.l2jserver.gameserver.model.L2Spawn;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2NpcInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.actor.templates.L2NpcTemplate;
import com.l2jserver.gameserver.model.events.Containers;
import com.l2jserver.gameserver.model.events.EventDispatcher;
import com.l2jserver.gameserver.model.events.impl.character.player.OnPlayerTowerCapture;

import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CaptureTower extends L2NpcInstance {
    //	private final int TOWER_ID = 40009;
    private int towerRange = 400;
    private int refreshRate = 200;
    private L2PcInstance capturer;
    private int progress;
    private boolean hasBeenReset = true;
    private boolean hasReached100 = false;
    private final List<Function<List<L2PcInstance>, L2PcInstance>> funcBehavior;
    private final Consumer<CaptureTower> onProgressUpdate;
    private final BiConsumer<CaptureTower, L2PcInstance> onAction;
    private boolean active = true;
    private final ScheduledFuture<?> future;
    private boolean conquerorIsClan = false;

    public CaptureTower(L2NpcTemplate template, List<Function<List<L2PcInstance>, L2PcInstance>> behaviors, Consumer<CaptureTower> onProgressUpdate, BiConsumer<CaptureTower, L2PcInstance> onAction) {
        super(template);
        this.progress = 0;
        this.funcBehavior = behaviors;
        this.onProgressUpdate = onProgressUpdate;
        this.onAction = onAction;
        future = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(this::update, 0, refreshRate, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onAction(L2PcInstance player, boolean interact) {
        onAction.accept(this, player);
    }

    private void update() {
        if (!active) {
            future.cancel(true);
        }
        L2PcInstance currentCapturer = anyCapturer();
        if (currentCapturer == null) {
            return;
        }
        if (capturer == null) {
            capturer = currentCapturer;
        }
        if (currentCapturer != capturer) {
            progress = 0;
            hasBeenReset = true;
            hasReached100 = false;
            capturer = currentCapturer;

        }
        progress++;

        if (progress > 0 && !hasReached100) {
            if (progress >= 100) {
                progress = 100;
                hasReached100 = true;
            }
            onProgressUpdate.accept(this);
            broadcastInfo();
            broadcastStatusUpdate();
        }
        if (hasBeenReset && progress == 100) {
            EventDispatcher.getInstance().notifyEventAsync(new OnPlayerTowerCapture(capturer, this));
            hasBeenReset = false;
        }

    }

    public void stopTower() {
        active = false;
    }

    public void resetMe() {
        progress = 0;
        capturer = null;
        setTitle("");
    }

    private L2PcInstance anyCapturer() {
        List<L2PcInstance> playersNearTower = getKnownList().getKnownCharacters().stream().filter(c -> c instanceof L2PcInstance && !c.isAlikeDead() && c.isInsideRadius(getLocation(), towerRange, false, true)).map(c -> (L2PcInstance) c).collect(Collectors.toList());
        if (playersNearTower.size() == 0) {
            return null;
        }
        return funcBehavior.stream().map(f -> {
            L2PcInstance pc = f.apply(playersNearTower);
            if (f instanceof CheckForClan || f instanceof CheckForAlliance) {
                conquerorIsClan = true;
            }
            return pc;
        }).filter(Objects::nonNull).findAny().orElse(null);
    }


    public int getTowerRange() {
        return towerRange;
    }

    public int getRefreshRate() {
        return refreshRate;
    }

    public L2PcInstance getCapturer() {
        return capturer;
    }

    public int getProgress() {
        return progress;
    }

    public boolean isHasBeenReset() {
        return hasBeenReset;
    }


    public void setTowerRange(int towerRange) {
        this.towerRange = towerRange;
    }

    public void setRefreshRate(int refreshRate) {
        this.refreshRate = refreshRate;
    }

    public void setCapturer(L2PcInstance capturer) {
        this.capturer = capturer;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public void setHasBeenReset(boolean hasBeenReset) {
        this.hasBeenReset = hasBeenReset;
    }

    public boolean isConquerorIsClan() {
        return conquerorIsClan;
    }
}
