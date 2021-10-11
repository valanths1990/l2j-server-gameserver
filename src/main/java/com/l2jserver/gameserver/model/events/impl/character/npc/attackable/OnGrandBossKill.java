package com.l2jserver.gameserver.model.events.impl.character.npc.attackable;

import com.l2jserver.gameserver.model.actor.L2Playable;
import com.l2jserver.gameserver.model.actor.instance.L2GrandBossInstance;
import com.l2jserver.gameserver.model.events.EventType;
import com.l2jserver.gameserver.model.events.impl.IBaseEvent;

public class OnGrandBossKill implements IBaseEvent {
    private final L2Playable killer;
    private final L2GrandBossInstance boss;

    public OnGrandBossKill(L2Playable killer, L2GrandBossInstance boss) {
        this.killer = killer;
        this.boss = boss;
    }

    public L2Playable getActiveChar() {
        return killer;
    }

    public L2GrandBossInstance getBoss() {
        return boss;
    }

    @Override
    public EventType getType() {
        return EventType.ON_GRANDBOSS_KILL;
    }
}
