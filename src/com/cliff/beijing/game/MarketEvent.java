package com.cliff.beijing.game;

public class MarketEvent {
	protected int frequency;   // the frequency of the events 
	protected int messageId;  // what message to display when event happen
	protected int goodId;  // goods ID to be influenced
	protected int multiplier;  // price increased ( *)
	protected int divisor; //price decrease   ( /)
	protected int addCount;   // how many goods to give user (+)
	protected int addDebt;	//only for the mobile phone

	public MarketEvent(int frequency, int messageId, int goodId, int plus,
			int minus, int addCount, int addDebt) {
		super();
		this.frequency = frequency;
		this.messageId = messageId;
		this.goodId = goodId;
		this.multiplier = plus;
		this.divisor = minus;
		this.addCount = addCount;
		this.addDebt = addDebt;
	}

	public MarketEvent(int frequency, int messageId, int goodId, int plus,
			int minus, int addCount) {
		super();
		this.frequency = frequency;
		this.messageId = messageId;
		this.goodId = goodId;
		this.multiplier = plus;
		this.divisor = minus;
		this.addCount = addCount;
		this.addDebt = 0;
	}

	public int getFrequency() {
		return frequency;
	}

	public int getMessageId() {
		return messageId;
	}

	public int getGoodId() {
		return goodId;
	}

	public int getMultiplier() {
		return multiplier;
	}

	public int getDivisor() {
		return divisor;
	}

	public int getAddCount() {
		return addCount;
	}

	public int getAddDebt() {
		return addDebt;
	}
			
}
