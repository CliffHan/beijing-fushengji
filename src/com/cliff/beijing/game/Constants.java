package com.cliff.beijing.game;

import java.util.Random;

import com.cliff.beijing.R;

public class Constants {
	
	//game logic parameters
	public static final int FIXED_INTEREST_PERCENTAGE = 1;
	public static final int FIXED_DEBT_PERCENTAGE = 10;
	public static final int FIXED_DEBT_LIMIT = 100000;
	public static final int FIXED_LEVEL_POOR = 1000;
	public static final int FIXED_LEVEL_MIDDLE = 100000;
	public static final int FIXED_LEVEL_RICH = 10000000;
	public static final int FIXED_LEVEL_ZILLIONAIRE = 100000000;

	public static final int INIT_CASH = 2000;
	public static final int INIT_DEPOSIT = 0;
	public static final int INIT_DEBT = 5500;
	
	public static final int EVENT_BEATEN = 30;
	
	//man status parameters
	public static final int MAX_DAY = 40;
	public static final int MAX_HEALTH = 100;
	public static final int MAX_FAME = 100;
	
	//health parameters
	public static final int HEALTH_WARNING = 80;
	public static final int HEALTH_CRITICAL = 20;
	public static final int HEALTH_EMERGENCY_BASE = 1000;
	public static final int HEALTH_EMERGENCY_RANDOM = 8500;
	public static final int HEALTH_EMERGENCY_MAX_DAY = 3;
	public static final int HEALTH_EMERGENCY_CURE = 10;
	public static final int HEALTHY_CURE_PRICE = 3500;
	
	//rent parameters
	//when rent deals, actual price will be PRICE+CHEAT
	//so make sure actual price do not exceed cash limit (RENT_LIMIT)
	public static final int RENT_LIMIT = 30000;
	public static final int RENT_PRICE_ONLIMIT = 20000;
	public static final int RENT_CHEAT = 2000;
	public static final int RENT_CHEAT_ONLIMIT = 5000;    

	//internet bar parameters
	public static final int INTERNET_VISIT_LIMIT = 3;
	public static final int INTERNET_RANDOM = 10;
	public static final int INTERNET_CASH_LIMIT = 15;

	//room parameters
	public static final int ROOM_MAX = 140;
	public static final int ROOM_INIT = 100;
	public static final int ROOM_STEP = 10;

	//interface update events
	public static final int UPDATE_GAME_OVER = 0;
	public static final int UPDATE_DAY = 1;
	public static final int UPDATE_HEALTH = 2;
	public static final int UPDATE_FAME = 3;
	public static final int UPDATE_MONEY = 4;
	public static final int UPDATE_MARKET = 5;
	public static final int UPDATE_ROOM = 6;
	public static final int UPDATE_PLACE = 7;
	public static final int UPDATE_DEAL = 8;
	public static final int UPDATE_SETTINGS = 9;
	
	// steal events
	public static final StealEvent EVENTS_STEAL[] = new StealEvent[] {
			new StealEvent(60, R.string.notify_steal_0, 10, true),
			new StealEvent(125, R.string.notify_steal_1, 10, true),
			new StealEvent(100, R.string.notify_steal_2, 40, true),
			new StealEvent(65, R.string.notify_steal_3, 20, true),
			new StealEvent(35, R.string.notify_steal_4, 15, false),
			new StealEvent(27, R.string.notify_steal_5, 10, false),
			new StealEvent(40, R.string.notify_steal_6, 5, true) 
	};

	// bad events
	public static final BadEvent EVENTS_BAD[] = new BadEvent[] {
		new BadEvent(117, R.string.notify_bad_0, 3, "kill.wav"),
		new BadEvent(157, R.string.notify_bad_1, 20, "death.wav"),
		new BadEvent(21, R.string.notify_bad_2, 1, "dog.wav"),
		new BadEvent(100, R.string.notify_bad_3, 1, "harley.wav"),
		new BadEvent(35, R.string.notify_bad_4, 1, "hit.wav"),
		new BadEvent(313, R.string.notify_bad_5, 10, "flee.wav"),
		new BadEvent(120, R.string.notify_bad_6, 5, "death.wav"),
		new BadEvent(29, R.string.notify_bad_7, 3, "el.wav"),
		new BadEvent(43, R.string.notify_bad_8, 1, "vomit.wav"),
		new BadEvent(45, R.string.notify_bad_9, 1, "level.wav"),
		new BadEvent(48, R.string.notify_bad_10, 1, "lan.wav"),
		new BadEvent(33, R.string.notify_bad_11, 1, "breath.wav")
	};
	
	//faint places
	public static final int PLACES_LOCATION[] = new int[] {
		R.string.location_pos0,
		R.string.location_pos1,
		R.string.location_pos2,
		R.string.location_pos3,
		R.string.location_pos4,
		R.string.location_pos5,
		R.string.location_pos6,
		R.string.location_pos7,
		R.string.location_pos8	};
	
	public static final int[] PLACES_FAINT = new int[] {
		R.string.place_faint_0,
		R.string.place_faint_1,
		R.string.place_faint_2,
		R.string.place_faint_3,
		R.string.place_faint_4,
		R.string.place_faint_5,
		R.string.place_faint_6,
		R.string.place_faint_7,
		R.string.place_faint_8,
		R.string.place_faint_9,
		R.string.place_faint_10,
		R.string.place_faint_11,
		R.string.place_faint_12,
		R.string.place_faint_13,
		R.string.place_faint_14,
		R.string.place_faint_15,
		R.string.place_faint_16,
		R.string.place_faint_17,
		R.string.place_faint_18,
		R.string.place_faint_19,
		R.string.place_faint_20,
		R.string.place_faint_21,
		R.string.place_faint_22,
		R.string.place_faint_23,
		R.string.place_faint_24,
		R.string.place_faint_25,
		R.string.place_faint_26,
		R.string.place_faint_27,
		R.string.place_faint_28	};

	public static final int[] TITLES = new int[] {
		R.string.title_0,
		R.string.title_1,
		R.string.title_2,
		R.string.title_3,
		R.string.title_4
	};
	
	public static final MarketEvent EVENTS_MARKET[] = new MarketEvent[] {
			new MarketEvent(170, R.string.notify_market_0, 5, 2, 0, 0),
			new MarketEvent(139, R.string.notify_market_1, 3, 3, 0, 0),
			new MarketEvent(100, R.string.notify_market_2, 4, 5, 0, 0),
			new MarketEvent(41, R.string.notify_market_3, 2, 4, 0, 0),
			new MarketEvent(37, R.string.notify_market_4, 1, 3, 0, 0),
			new MarketEvent(23, R.string.notify_market_5, 7, 4, 0, 0),
			new MarketEvent(37, R.string.notify_market_6, 4, 8, 0, 0),
			new MarketEvent(15, R.string.notify_market_7, 7, 7, 0, 0),
			new MarketEvent(40, R.string.notify_market_8, 3, 7, 0, 0),
			new MarketEvent(29, R.string.notify_market_9, 6, 7, 0, 0),
			new MarketEvent(35, R.string.notify_market_10, 1, 8, 0, 0),
			new MarketEvent(17, R.string.notify_market_11, 0, 0, 8, 0),
			new MarketEvent(24, R.string.notify_market_12, 5, 0, 5, 0),
			new MarketEvent(18, R.string.notify_market_13, 2, 0, 8, 0),
			new MarketEvent(160, R.string.notify_market_14, 1, 0, 0, 2),
			new MarketEvent(45, R.string.notify_market_15, 0, 0, 0, 6),
			new MarketEvent(35, R.string.notify_market_16, 3, 0, 0, 4),
			new MarketEvent(140, R.string.notify_market_17, 6, 0, 0, 1, 2500) };
	
	//goods parmeters
	public static final int GOOD_TYPE_COUNT = 8;	
	public static final int GOOD_TYPE_LEAVE = 3;	
	public static final int GOOD_NAMES[] = new int[]{
		R.string.good_0,
		R.string.good_1,
		R.string.good_2,
		R.string.good_3,
		R.string.good_4,
		R.string.good_5,
		R.string.good_6,
		R.string.good_7,
		};
	public static final int GOOD_ICONS[] = new int[]{
		R.drawable.good_0,
		R.drawable.good_1,
		R.drawable.good_2,
		R.drawable.good_3,
		R.drawable.good_4,
		R.drawable.good_5,
		R.drawable.good_6,
		R.drawable.good_7,
		};
	public static final int GOOD_FORBIDDEN_BOOK = 4;
	public static final int GOOD_POISON_WINE = 3;
	public static final int GOOD_FAME_FORBIDDEN_BOOK = 7;
	public static final int GOOD_FAME_POISON_WINE = 10;
	
	//random function
	public static final int RANDOM_DIVIDEND = 1000;	//all event generated use (0==(random([0,RANDOM_DIVIDEND))%frequency))
	protected static Random random = new Random();	
	public static int getRandom(int max) {
		return random.nextInt(max);
	}
	
}
