package com.cliff.beijing.game;

public class Room {
	protected int count[] = null;
	protected int cost[] = null;
	
	protected int space = Constants.ROOM_INIT;
		
	public void init() {
		count = new int[Constants.GOOD_TYPE_COUNT];
		cost = new int[Constants.GOOD_TYPE_COUNT];
		space = Constants.ROOM_INIT;
	}
	
	public int getAllGoodsCount() {
		int ret = 0;
		for (int i = 0; i < count.length; i++)
			ret += count[i];		
		return ret;
	}
	
	public void storeGoods(int id, int goodCount, int price) {
		count[id] += goodCount;
		cost[id] += goodCount*price;
	}

	public int sellGoods(int id, int number) {
		if (number > count[id])
			return 0;
		int original_count = count[id];
		count[id] -= number;
		if (0 == count[id])
			cost[id] = 0;
		else
			cost[id] = cost[id]*count[id]/original_count;
		return number;
	}

	public void clearAllGoods() {
		count = new int[Constants.GOOD_TYPE_COUNT];
		cost = new int[Constants.GOOD_TYPE_COUNT];
	}
	
	public void addSpace() {
		space += Constants.ROOM_STEP;
	}
	
	public int getSpace() {
		return space;
	}
	
	public int[] getGoodsCount() {
		return count;
	}
	
	public int[] getGoodsCost() {
		return cost;
	}
}
