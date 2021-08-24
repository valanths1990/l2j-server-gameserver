package com.l2jserver.gameserver.model.holders;

import org.jetbrains.annotations.NotNull;

public class Participant {
	private String name;
	private Integer points;

	@Override public String toString() {
		return "Participant{" + "name='" + name + '\'' + ", points=" + points + '}';
	}

	public Participant(String name, Integer points) {
		this.name = name;
		this.points = points;
	}

	public String getPlayerName() {
		return name;
	}

	public Integer getPoints() {
		return points;
	}

}
