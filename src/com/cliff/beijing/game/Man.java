package com.cliff.beijing.game;

public class Man {

	protected int cash = Constants.INIT_CASH;
	protected int debt = Constants.INIT_DEBT;
	protected int deposit = Constants.INIT_DEPOSIT;
	protected int fame = Constants.MAX_FAME;
	protected int health = Constants.MAX_HEALTH;

	public int getCash() {
		return cash;
	}

	public int getDebt() {
		return debt;
	}

	public int getDeposit() {
		return deposit;
	}

	public int getFame() {
		return fame;
	}

	public int getHealth() {
		return health;
	}

	public void init() {
		cash = Constants.INIT_CASH;
		deposit = Constants.INIT_DEPOSIT;
		debt = Constants.INIT_DEBT;
		health = Constants.MAX_HEALTH;
		fame = Constants.MAX_FAME;
	}
	public void setCash(int cash) {
		this.cash = cash;
	}
	public void setDebt(int debt) {
		this.debt = debt;
	}
	public void setDeposit(int deposit) {
		this.deposit = deposit;
	}
	public void setFame(int fame) {
		this.fame = fame;
	}
	
	public void setHealth(int health) {
		this.health = health;
	}
}
