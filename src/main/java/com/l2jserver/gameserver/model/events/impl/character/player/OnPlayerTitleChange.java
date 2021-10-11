package com.l2jserver.gameserver.model.events.impl.character.player;

import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.events.EventType;
import com.l2jserver.gameserver.model.events.impl.IBaseEvent;

public class OnPlayerTitleChange implements IBaseEvent {
    private L2Character player;
    private String oldTitle;
    private String newTitle;

    public OnPlayerTitleChange(L2Character player, String oldTitle, String newTitle) {
        this.player = player;
        this.oldTitle = oldTitle;
        this.newTitle = newTitle;
    }

    public L2Character getPlayer() {
        return player;
    }

    public String getOldTitle() {
        return oldTitle;
    }

    public String getNewTitle() {
        return newTitle;
    }

    @Override
    public EventType getType() {
        return EventType.ON_PLAYER_TITLE_CHANGE;
    }
}
