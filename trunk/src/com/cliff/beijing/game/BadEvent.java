package com.cliff.beijing.game;

public class BadEvent {
	public BadEvent(int frequency, int messageId, int hurt, String soundwav) {
		super();
		this.frequency = frequency;
		this.messageId = messageId;
		this.hurt = hurt;
		this.soundwav = soundwav;
	}

	protected int frequency; // the frequency of this event
	protected int messageId; // the message to dispplay while event happen
	protected int hurt; // how many points user get hurted when event happen
	protected String soundwav;

	// protected int soundId; // the sound file to play for the event

	public int getFrequency() {
		return frequency;
	}

	public int getMessageId() {
		return messageId;
	}

	public int getHurt() {
		return hurt;
	}

	public String getSoundwav() {
		return soundwav;
	}
}
