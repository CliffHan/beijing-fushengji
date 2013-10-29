package com.cliff.beijing;

import java.util.ArrayList;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferenceManager {
	
	public static final String KEY_PLAYED_BEFORE = "IsPlayedBefore";
	public static final String KEY_LAST_PLAYER_NAME = "LastPlayerName";
	public static final String KEY_RANKING_ORDER = "RankingOrder"; 
	public static final String KEY_AUTO_CHANGE_TAB = "IsAutoChangeTab";
	public static final String KEY_HACKER_ENABLED = "IsHackerEnabled";	
	public static final String KEY_PLAY_SOUND = "IsPlaySound";	
	
	protected final String KEY_RANKINGS[] = new String[]{
			"Ranking.0", "Ranking.1", "Ranking.2", "Ranking.3", "Ranking.4", 
			"Ranking.5", "Ranking.6", "Ranking.7", "Ranking.8", "Ranking.9"}; 

	public final static String DEFAULT_LAST_PLAYER_NAME = "无名氏";	
	
	protected SharedPreferences mPref = null;

	public PreferenceManager(SharedPreferences pref) {
		mPref = pref;
	}
	
	public SharedPreferences getSharedPreference() {
		return mPref;
	}
	
	public boolean isPlayedBefore() {
		return mPref.getBoolean(KEY_PLAYED_BEFORE, false);
	}
	
	public void setPlayedBefore(boolean isPlayedBefore) {
		SharedPreferences.Editor editor = mPref.edit();
		editor.putBoolean(KEY_PLAYED_BEFORE, isPlayedBefore);
		editor.commit();
	}

	public String getLastPlayerName() {
		return mPref.getString(KEY_LAST_PLAYER_NAME, DEFAULT_LAST_PLAYER_NAME);
	}
	
	public void setLastPlayerName(String name) {
		SharedPreferences.Editor editor = mPref.edit();
		editor.putString(KEY_LAST_PLAYER_NAME, name);
		editor.commit();
	}
	
	public boolean isPlaySound() {
		return mPref.getBoolean(KEY_PLAY_SOUND, false);
	}

	public boolean isAutoChangeTab() {
		return mPref.getBoolean(KEY_AUTO_CHANGE_TAB, false);
	}

	public boolean isHackerEnabled() {
		return mPref.getBoolean(KEY_HACKER_ENABLED, false);
	}

	public ArrayList<Ranking> getRanking() {
		String rankingOrder = mPref.getString(KEY_RANKING_ORDER, "");
		char[] rankingOrderArray = rankingOrder.toCharArray();
		ArrayList<Ranking> ret = new ArrayList<Ranking>();
		for (int i = 0; i < rankingOrderArray.length; i++) {
			ret.add(getRankingFromString(mPref.getString(KEY_RANKINGS[(rankingOrderArray[i]-'0')], null)));
		}		
		return ret;
	}
	
	public int getPosition(int money) {
		String rankingOrder = mPref.getString(KEY_RANKING_ORDER, "");
		char[] rankingOrderArray = rankingOrder.toCharArray();
		int ret = rankingOrderArray.length;
		Ranking r = null;
		for (int i = 0; i < rankingOrderArray.length; i++) {			
			r = getRankingFromString(mPref.getString(KEY_RANKINGS[(rankingOrderArray[i]-'0')], null));
			if (money > r.money) {
				ret = i;
				break;
			}
		}
		if (ret == rankingOrderArray.length) {
			if (rankingOrderArray.length < 10)
				return rankingOrderArray.length;	//last rank
			else
				return -1;	//no rank
		}
		return ret;		
	}
	
	public void addRanking(String name, int health, int fame, int money) {
		//get insert position
		int position = getPosition(money);
		if (position < 0)
			return;

		Ranking r = new Ranking(name, health, fame, money);
		String rankingOrder = mPref.getString(KEY_RANKING_ORDER, "");
		char[] rankingOrderArray = rankingOrder.toCharArray();
		char[] targetOrderArray = null;
		if (rankingOrderArray.length < 10)
			targetOrderArray = new char[rankingOrderArray.length+1];
		else
			targetOrderArray = rankingOrder.toCharArray();
		
		Editor editor = mPref.edit();
		//target have only 1 record, from 0->1
		if (0 == rankingOrderArray.length) {
			targetOrderArray[0] = '0';
			editor.putString(KEY_RANKINGS[0], getStringFromRanking(r));
			editor.putString(KEY_RANKING_ORDER, new String(targetOrderArray));
			editor.commit();
			return;
		}
		
		//original order string is less than 10, just add new record to position, and copy all behind chars
		if (rankingOrderArray.length < 10) {
			System.arraycopy(rankingOrderArray, 0, targetOrderArray, 0, position);
			targetOrderArray[position] = (char)('0' + rankingOrderArray.length);
			System.arraycopy(rankingOrderArray, position, targetOrderArray, position+1, rankingOrderArray.length-position);			
			
			editor.putString(KEY_RANKINGS[targetOrderArray[position]-'0'], getStringFromRanking(r));
			editor.putString(KEY_RANKING_ORDER, new String(targetOrderArray));
			editor.commit();
			return;			
		}

		//the last one order char will be removed, and value replaced by new string 
		System.arraycopy(rankingOrderArray, 0, targetOrderArray, 0, position);
		targetOrderArray[position] = rankingOrderArray[9];
		System.arraycopy(rankingOrderArray, position, targetOrderArray, position+1, rankingOrderArray.length-position-1);			
		
		editor.putString(KEY_RANKINGS[targetOrderArray[position]-'0'], getStringFromRanking(r));
		editor.putString(KEY_RANKING_ORDER, new String(targetOrderArray));
		editor.commit();
		return;			
	}
	
	protected Ranking getRankingFromString(String rankingStr) {
		String r[] = rankingStr.split("\\|");
		return new Ranking(r[0], Integer.parseInt(r[1]), Integer.parseInt(r[2]), Integer.parseInt(r[3]));
	}

	protected String getStringFromRanking(Ranking ranking) {
		//replace invalide char
		char split = '|';
		char replace = '-';
		ranking.name = ranking.name.replace(split, replace);
		return ranking.name + split + ranking.health + split + ranking.fame + split +ranking.money;
	}

	public class Ranking {
		public Ranking(String name, int health, int fame, int money) {
			super();
			this.name = name;
			this.health = health;
			this.fame = fame;
			this.money = money;
		}

		public String name;
		public int health;
		public int fame;
		public int money;
	}
}
