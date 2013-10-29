package com.cliff.beijing.game;

import com.cliff.beijing.R;

import android.content.Context;
import de.greenrobot.event.EventBus;

//GameEngine is where all game logics running, it use 2 ways to communicate with user interface
//1st is EventBus.getDefault().post(Integer), to send commands, like interface update and game over
//2nd is EventBus.getDefault().post(String), to send notifications, shown as a dialog
//upper interface need to use queue to process commands and notifications, 
//because all commands and notifications were send with out waiting here
public class GameEngine {

	protected static GameEngine instance = new GameEngine();
	protected Man man = new Man();
	protected Room room = new Room();
	protected Market market = new Market();
	protected int place = -1;
	protected int day = 0;
	protected int[] price = null;
	protected int internetCount = 0;
	protected boolean is1stBadWine = true;
	protected boolean is1stBadBook = true;
	protected boolean isHackerEnabled = false;
	protected Context mContext = null;
	
	protected GameEngine() {		
	}
	
	public static GameEngine getInstance() {
		return instance;
	}
	
	public void init(Context context, boolean isHackerEnabled) {
		man.init();
		room.init();
		place = -1;
		day = 0;
		price = market.getGoodPrices(3);
		internetCount = 0;
		is1stBadWine = true;
		is1stBadBook = true;
		this.isHackerEnabled = isHackerEnabled; 
//		EventBus.getDefault().post(new Integer(Constants.UPDATE_ALL));
		if (null == mContext)
			mContext = context;
	}

	public void quit(QuitCallback callback) {
		EventBus.getDefault().post(mContext.getString(R.string.notify_quit_nomoney));
		if (null != callback)
			callback.afterQuitMessage();
	}
	
	//game core, 1 day flowed, many things happened
	public void flow(int place) {
		int daysLeft = 0;
		//update place
		this.place = place;
		EventBus.getDefault().post(Constants.UPDATE_PLACE);
		
		//update day display
		day++;
		EventBus.getDefault().post(Constants.UPDATE_DAY);
		
		//handle deposit and debt problems
		calculateFinance();
		EventBus.getDefault().post(Constants.UPDATE_MONEY);			
		//check debt problem, if too much, be beaten, if dead, end
		if (man.getDebt() > Constants.FIXED_DEBT_LIMIT) {
			man.setHealth(man.getHealth() - Constants.EVENT_BEATEN);
			if (man.getHealth() < 0)
				man.setHealth(0);
			EventBus.getDefault().post(Constants.UPDATE_HEALTH);
			String result = String.format(mContext.getString(R.string.notify_bad), Constants.EVENT_BEATEN);
			EventBus.getDefault().post("[kill.wav]"+mContext.getString(R.string.notify_beaten)+result);
			if (man.getHealth() <= 0) {
				gameoverByHealth();
				return;
			}
		}
		
		//random BadEvent and update health display
		for (int i = 0; i < Constants.EVENTS_BAD.length; i++) {
			if (0 ==(Constants.getRandom(Constants.RANDOM_DIVIDEND) % Constants.EVENTS_BAD[i].getFrequency())) {
				int health = man.getHealth() - Constants.EVENTS_BAD[i].getHurt();
				if (health < 0)
					health = 0;
				man.setHealth(health);					
				String reason = mContext.getString(Constants.EVENTS_BAD[i].getMessageId());
				reason += String.format(mContext.getString(R.string.notify_bad), Constants.EVENTS_BAD[i].getHurt());
				reason = "["+Constants.EVENTS_BAD[i].getSoundwav()+"]"+reason;
				EventBus.getDefault().post(Constants.UPDATE_HEALTH);
				EventBus.getDefault().post(reason);				
			}
		}
	    // user's health is little than 80? and it is more than 3 days to end the game.
	    // He needs medical care without permission. The money for medical care would be added to his debt. 
		if ((man.getHealth() < Constants.HEALTH_WARNING)
				&&((Constants.MAX_DAY-day)>Constants.HEALTH_EMERGENCY_MAX_DAY)) {
			String location = mContext.getString(Constants.PLACES_LOCATION[place]);
			String location_detail = mContext.getString(Constants.PLACES_FAINT[Constants.getRandom(Constants.PLACES_FAINT.length)]);
			int daysDelayed = 1 + Constants.getRandom(Constants.HEALTH_EMERGENCY_MAX_DAY-1);
			int pay = daysDelayed * (Constants.HEALTH_EMERGENCY_BASE+Constants.getRandom(Constants.HEALTH_EMERGENCY_RANDOM));
			
			day += daysDelayed;
			EventBus.getDefault().post(Constants.UPDATE_DAY);

			man.setDebt(man.getDebt()+pay);
			EventBus.getDefault().post(Constants.UPDATE_MONEY);

			int health = man.getHealth()+Constants.HEALTH_EMERGENCY_CURE;
			if (health > Constants.MAX_HEALTH)
				health = Constants.MAX_HEALTH;
			man.setHealth(health);
			EventBus.getDefault().post(Constants.UPDATE_HEALTH);

			String reason = String.format(mContext.getString(R.string.notify_place_faint), location, location_detail, daysDelayed, pay);			
			EventBus.getDefault().post(reason);				
		}
		// health is less than 20(usually cannot happen, only kept beaten in last 3 days)
		// if less than 0, dead
		if ((man.getHealth() < Constants.HEALTH_CRITICAL)
			&&(man.getHealth() > 0))	{
			EventBus.getDefault().post(mContext.getString(R.string.notify_danger));							
		} 
		else if (man.getHealth() <= 0) {
			gameoverByHealth();
			return;			
		}

		//determine if the game need to be ended according time, and hint when 1 day left
		daysLeft = Constants.MAX_DAY - day;
		if (0 >= daysLeft) { //over here
			gameoverByTime();
			return;
		}				
		if (1 == daysLeft)
			EventBus.getDefault().post(mContext.getString(R.string.notify_time_lastday));			
		
		//random StealEvent and update money display
		for (int i = 0; i < Constants.EVENTS_STEAL.length; i++) {
			if (0 ==(Constants.getRandom(Constants.RANDOM_DIVIDEND) % Constants.EVENTS_STEAL[i].getFrequency())) {
				String reason = mContext.getString(Constants.EVENTS_STEAL[i].getMessageId());
				if (Constants.EVENTS_STEAL[i].isCash()) {
					man.setCash(man.getCash() - (man.getCash()*Constants.EVENTS_STEAL[i].getRatoi()/100));					
					reason += String.format(mContext.getString(R.string.notify_steal_cash), Constants.EVENTS_STEAL[i].getRatoi());
				} 
				else {
					man.setDeposit(man.getDeposit() - (man.getDeposit()*Constants.EVENTS_STEAL[i].getRatoi()/100));					
					reason += String.format(mContext.getString(R.string.notify_steal_deposit), Constants.EVENTS_STEAL[i].getRatoi());					
				}
				EventBus.getDefault().post(Constants.UPDATE_MONEY);
				EventBus.getDefault().post(reason);				
			}
		}
		
		//random HackerEvent
		if (isHackerEnabled) {
			if (0 ==(Constants.getRandom(Constants.RANDOM_DIVIDEND) % Constants.EVENTS_HACKER_FREQ)) {
				
				String reason = null;
				
				if (man.getDeposit() < Constants.FIXED_LEVEL_POOR) {
					   // deposit is too little, no need to crack
				}
				else if(man.getDeposit() > Constants.FIXED_LEVEL_MIDDLE )
				{
					//really rich than a middle-layer
					
					//usually have 2/3 chance of decreasing
					//and 1/3 chance of increasing
					//the number should be 1/21 to 1/2 of your deposit
					int num = (int)(man.getDeposit() / (2+Constants.getRandom(20)) );
					if(Constants.getRandom(20)%3!=0){
						reason = String.format(mContext.getString(R.string.notify_hacker_decrease), 
								num);
						man.setDeposit(man.getDeposit() - num);
		            }
					else{
						reason = String.format(mContext.getString(R.string.notify_hacker_increase), 
								num);
						man.setDeposit(man.getDeposit() + num);			             
					}
				}
		        else		
				{
		        	//middle-layer, only got chance of increasing
					int num=(int)(man.getDeposit()/(1+Constants.getRandom(15)));
					reason = String.format(mContext.getString(R.string.notify_hacker_increase), 
							num);
					man.setDeposit(man.getDeposit() + num);
				}
				EventBus.getDefault().post(Constants.UPDATE_MONEY);
				EventBus.getDefault().post(reason);
			}			
		}
		
		//random Market Event and update goods display
		//usually, leave should be 3, that means 5-7 goods will be in the market 
		//if left days < 2, all goods must be in the market(leave = 0)
		int leave = 3;
		if ((Constants.MAX_DAY - day) < 2)
			leave = 0;		
		price = market.getGoodPrices(leave);		
		for (int i = 0; i < Constants.EVENTS_MARKET.length; i++) {
			if (0 ==(Constants.getRandom(Constants.RANDOM_DIVIDEND) % Constants.EVENTS_MARKET[i].getFrequency())) {
				MarketEvent m = Constants.EVENTS_MARKET[i];
				boolean isNoRoom = false;
				if (0 == price[m.getGoodId()])	//today no this good, go on
					continue;
				
				if (m.getMultiplier() > 0)
					price[m.getGoodId()] *= m.getMultiplier();
				else if (m.getDivisor() > 0)
					price[m.getGoodId()] /= m.getDivisor();
				else if (m.getAddCount() > 0) {
					int spaceLeft = room.getSpace() - room.getAllGoodsCount();
					if (spaceLeft >= m.getAddCount())
						room.storeGoods(m.getGoodId(), m.getAddCount(), 0);
					else if (spaceLeft <= 0)
						isNoRoom = true;
					else {
						room.storeGoods(m.getGoodId(), spaceLeft, 0);
						isNoRoom = true;
					}
				}
				
				if (m.getAddDebt() > 0) {
					man.setDebt(man.getDebt() + m.addDebt);
					EventBus.getDefault().post(Constants.UPDATE_MONEY);
				}
					
				EventBus.getDefault().post(mContext.getString(m.getMessageId()));
				if (isNoRoom)
					EventBus.getDefault().post(String.format(mContext.getString(R.string.notify_no_room), room.getSpace()));
			}
		}
		
		EventBus.getDefault().post(Constants.UPDATE_ROOM);
		EventBus.getDefault().post(Constants.UPDATE_MARKET);
	}
	
	protected void calculateFinance() {
		int debtInc = man.getDebt() * Constants.FIXED_DEBT_PERCENTAGE / 100;
		int depositInc = man.getDeposit() * Constants.FIXED_INTEREST_PERCENTAGE / 100;
		man.setDebt(man.getDebt() + debtInc);
		man.setDeposit(man.getDeposit() + depositInc);		
	}
	
	protected void gameoverByTime() {
		//notify time over
		EventBus.getDefault().post(mContext.getString(R.string.notify_time_over));

		price = market.getGoodPrices(0);		
		EventBus.getDefault().post(Constants.UPDATE_MARKET);

		//sold all goods in room
		int count[] = room.getGoodsCount();
		int money = 0;
		String goodName = "";
		for (int i = 0; i < count.length; i++) {
			if (count[i] > 0) {
				goodName += mContext.getString(Constants.GOOD_NAMES[i]) + " ";
				money += count[i]*price[i];
			}
		}
		if (0 != money) {
			room.clearAllGoods();
			man.setCash(man.getCash()+money);
			EventBus.getDefault().post(Constants.UPDATE_MONEY);		
			EventBus.getDefault().post(Constants.UPDATE_ROOM);		
			EventBus.getDefault().post(String.format(mContext.getString(R.string.notify_system_sold), goodName));
			EventBus.getDefault().post(Constants.UPDATE_DEAL);
		}
		
		//notify game over
		EventBus.getDefault().post(Constants.UPDATE_GAME_OVER);		
	}
	
	protected void gameoverByHealth() {
		//notify dead
		EventBus.getDefault().post("[death.wav]"+mContext.getString(R.string.notify_dead));
				
		//notify game over
		EventBus.getDefault().post(Constants.UPDATE_GAME_OVER);		
	}	
	
	public void dealWithBank(int money, boolean isSave) {
		if (isSave) {
			money = Math.min(money, man.getCash());
			man.setCash(man.getCash()-money);
			man.setDeposit(man.getDeposit()+money);
		}
		else {
			money = Math.min(money, man.getDeposit());
			man.setCash(man.getCash()+money);
			man.setDeposit(man.getDeposit()-money);			
		}
		EventBus.getDefault().post(Constants.UPDATE_MONEY);				
	}
	
	public void dealWithHospital(int point) {
		point = Math.min(point, Constants.MAX_HEALTH-man.getHealth());
		int money = point * Constants.HEALTHY_CURE_PRICE;
		if (money > man.getCash()) {
			EventBus.getDefault().post(mContext.getString(R.string.notify_hospital_cannot_pay));
			return;
		}

		man.setCash(man.getCash()-money);
		man.setHealth(man.getHealth()+point);
		EventBus.getDefault().post(Constants.UPDATE_MONEY);				
		EventBus.getDefault().post(Constants.UPDATE_HEALTH);				
	}
	
	public void dealWithDebt(int money) {
		int cash = man.getCash();
		int debt = man.getDebt();
				
		if (money > cash) {
			//input a number more than he can pay
			if (cash < debt) {
				//man cannot pay the debt 
				EventBus.getDefault().post(mContext.getString(R.string.notify_post_cannot_pay));
				return;
			}
		}

		int pay = Math.min(money, debt);
		cash -= pay;
		debt -= pay;
		man.setCash(cash);
		man.setDebt(debt);
		EventBus.getDefault().post(Constants.UPDATE_MONEY);		
	}
	
	public void dealWithRent() {
		int cheat = Constants.RENT_CHEAT_ONLIMIT;
		if (man.getCash() > Constants.RENT_LIMIT)
			cheat = Constants.RENT_CHEAT;
		
		int pay = getRentPrice() + cheat;
		if (man.getCash() < pay)
			return;
		
		man.setCash(man.getCash() - pay);
		room.addSpace();
		EventBus.getDefault().post(Constants.UPDATE_MONEY);		
		EventBus.getDefault().post(Constants.UPDATE_ROOM);
		EventBus.getDefault().post(String.format(mContext.getString(R.string.notify_rent_cheated), room.getSpace()));
	}
	
	public boolean checkWithRentalAgent() {
		return false;
	}

	public void dealWithInternetBar() {
		if (internetCount >= Constants.INTERNET_VISIT_LIMIT) {
			EventBus.getDefault().post(mContext.getString(R.string.notify_internet_no_need));
			return;
		}
		
		if (man.getCash() < Constants.INTERNET_CASH_LIMIT) {
			EventBus.getDefault().post(String.format(mContext.getString(R.string.notify_internet_cannot_pay), Constants.INTERNET_CASH_LIMIT));
			return;
		}
		
		internetCount++;
		int internetMoney = Constants.getRandom(Constants.INTERNET_RANDOM) + 1;
		EventBus.getDefault().post(String.format(mContext.getString(R.string.notify_internet_money), internetMoney));
		man.setCash(man.getCash()+internetMoney);
		EventBus.getDefault().post(Constants.UPDATE_MONEY);
	}
	
	public void dealWithMarket(int goodId, int count) {
		if (count <= 0)
			return;
		
		int roomSpace = room.getSpace()-room.getAllGoodsCount();
		int cash = man.getCash() - (price[goodId] * count);
		if ((cash < 0)||(roomSpace < count))
			return;
		
		man.setCash(cash);
		room.storeGoods(goodId, count, price[goodId]);
		EventBus.getDefault().post(Constants.UPDATE_MONEY);
		EventBus.getDefault().post(Constants.UPDATE_ROOM);		
		EventBus.getDefault().post(Constants.UPDATE_DEAL);
	}

	public void dealWithRoom(int goodId, int count) {
		if (count <= 0)
			return;
		
		int sellCount = room.sellGoods(goodId, count);
		if (0 == sellCount)
			return;
		
		int cash = man.getCash() + (price[goodId] * sellCount);
		man.setCash(cash);
		EventBus.getDefault().post(Constants.UPDATE_MONEY);
		EventBus.getDefault().post(Constants.UPDATE_ROOM);
		EventBus.getDefault().post(Constants.UPDATE_DEAL);
		
		if (Constants.GOOD_POISON_WINE == goodId) {
			int fame = man.getFame() - Constants.GOOD_FAME_POISON_WINE;
			if (fame < 0)
				fame = 0;
			man.setFame(fame);
			EventBus.getDefault().post(Constants.UPDATE_FAME);
			if (is1stBadWine) {
				is1stBadWine = false;				
				EventBus.getDefault().post(mContext.getString(R.string.notify_market_badwine));
			}			
		} 
		else if (Constants.GOOD_FORBIDDEN_BOOK == goodId) {
			int fame = man.getFame() - Constants.GOOD_FAME_FORBIDDEN_BOOK;
			if (fame < 0)
				fame = 0;
			man.setFame(fame);
			EventBus.getDefault().post(Constants.UPDATE_FAME);
			if (is1stBadBook) {
				is1stBadBook = false;
				EventBus.getDefault().post(mContext.getString(R.string.notify_market_badbook));
			}
		}		
	}

	public boolean checkMedicalPossibility() {
		
		if (man.getHealth() >= Constants.MAX_HEALTH) {
			EventBus.getDefault().post(mContext.getString(R.string.notify_hospital_no_need));
			return false;
		}		
		return true;
	}

	public boolean checkRentPossibility() {
		if (man.getCash() < Constants.RENT_LIMIT) {
			EventBus.getDefault().post(String.format(mContext.getString(R.string.notify_rent_cannot_pay), Constants.RENT_LIMIT));
			return false;
		} 
		else if (room.getSpace() >= Constants.ROOM_MAX) {
			EventBus.getDefault().post(mContext.getString(R.string.notify_rent_no_need));
			return false;			
		}
		
		return true;
	}

	public boolean checkPostPossibility() {
		if (man.getDebt() <= 0) {
			int money = man.getCash() + man.getDeposit();
			if (money < Constants.FIXED_LEVEL_POOR) 
				EventBus.getDefault().post(mContext.getString(R.string.notify_post_poor));
			else if (money < Constants.FIXED_LEVEL_MIDDLE)
				EventBus.getDefault().post(mContext.getString(R.string.notify_post_middle));
			else if (money < Constants.FIXED_LEVEL_RICH)
				EventBus.getDefault().post(mContext.getString(R.string.notify_post_rich));
			else if (money < Constants.FIXED_LEVEL_ZILLIONAIRE)
				EventBus.getDefault().post(mContext.getString(R.string.notify_post_zillionaire));
			else
				EventBus.getDefault().post(mContext.getString(R.string.notify_post_great));	//impossible
			return false;
		}		
		return true;
	}

	public boolean checkBuyPossibility(int goodId) {
		if (man.getCash() < price[goodId]) {
			if ((man.getCash() + man.getDeposit()) >= price[goodId])
				EventBus.getDefault().post(mContext.getString(R.string.notify_no_cash));
			else
				EventBus.getDefault().post(mContext.getString(R.string.notify_no_money));				
			return false;
		}		
		if (room.getAllGoodsCount() >= room.getSpace()) {
			EventBus.getDefault().post(String.format(mContext.getString(R.string.notify_no_room_tobuy), room.getSpace()));				
			return false;
		}
		return true;
	}

	public boolean checkSellPossibility(int goodId) {
		if (0 == price[goodId]) {
			EventBus.getDefault().post(String.format(mContext.getString(R.string.notify_no_buyer), mContext.getString(Constants.GOOD_NAMES[goodId])));
			return false;
		}
		return true;
	}
	
	public int getRentPrice() {
		if (man.getCash() <= Constants.RENT_LIMIT)
			return Constants.RENT_PRICE_ONLIMIT;
		else
			return man.getCash()/2;
	}
	
	public int getDay() {
		return day;
	}
	
	public Man getMan() {
		return man;
	}

	public Room getRoom() {
		return room;
	}

	public int[] getPrice() {
		return price;
	}
	
	public int getPlace() {
		return place;
	}
	
	public int getMaxBuyCount(int goodId) {
		return Math.min((int)man.getCash()/price[goodId], room.getSpace()-room.getAllGoodsCount());
	}

	public interface QuitCallback {
		public void afterQuitMessage();
	}
}
