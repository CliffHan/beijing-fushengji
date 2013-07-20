package com.cliff.beijing.game;

public class StealEvent {
	protected int frequency;  //the frequency of the event
	protected boolean isCash;
	protected int messageId; //the message to display when event happen
	protected int ratoi;  // how many ratio decreased. money=money*(1-ratoi)

	public StealEvent(int freq, int mId, int rate, boolean cash) {
		frequency = freq;
		messageId = mId;
		ratoi = rate;
		isCash = cash;
	}
	
	public int getFrequency() {
		return frequency;
	}
	public int getMessageId() {
		return messageId;
	}
	public int getRatoi() {
		return ratoi;
	}
	
	public boolean isCash() {
		return isCash;
	}
}
