package com.cliff.beijing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.androidquery.AQuery;
import com.cliff.beijing.PreferenceManager.Ranking;
import com.cliff.beijing.game.Constants;
import com.cliff.beijing.game.GameEngine;
import com.cliff.beijing.game.Man;
import com.cliff.beijing.game.Room;
import com.viewpagerindicator.TabPageIndicator;

import de.greenrobot.event.EventBus;

public class MainActivity extends SherlockFragmentActivity implements OnClickListener {
	
	protected int tabtitles[] = new int[]{R.string.tabtitle_market, R.string.tabtitle_location, R.string.tabtitle_room};
	protected final int TAB_MARKET = 0;
	protected final int TAB_LOCATION = 1;
	protected final int TAB_ROOM = 2;
	protected final int ACTION_NEWGAME = 1;
	protected final int ACTION_ABOUT = 2;
	protected final int ACTION_RANKING = 3;
	protected final int ACTION_HELP = 4;
	protected final int ACTION_QUIT = 5;	
	protected final int ACTION_SETTINGS = 6;	
	protected final int ACTION_TIPS = 7;	
	protected final String SPECIAL_EVENT_QUIT = "==quit==";
	protected final String SPECIAL_EVENT_GAMEOVER = "==gameover==";
//	protected final String SPECIAL_EVENT_CHANGE_TAB = "==changetab==";
	
	protected final int EVENT_QUEUE_SIZE = 24;
	protected final String URL_HELP = "file:///android_asset/help.html";
	protected final String URL_ABOUT = "file:///android_asset/about.html";
	protected final String URL_TIPS = "file:///android_asset/tips.html";
	
	protected EventQueue notifications = new EventQueue();
	protected boolean isNotifying = false;
	protected boolean isPlaySound = false;
	protected boolean isAutoChangeTab = false;
	
	protected AQuery aq = null;
	protected TabPageIndicator indicator = null;
	protected ViewPager pager = null;
	protected PreferenceManager prefman = null;
	protected SoundPool soundPool = null;
	HashMap<String, Integer> soundPoolMap = null;
	
	private final String TAG = "MainActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initSound();
		android.preference.PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		prefman = new PreferenceManager(android.preference.PreferenceManager.getDefaultSharedPreferences(this));		
//		prefman = new PreferenceManager(getPreferences(MODE_PRIVATE));
		updateSettings();
		
		FragmentPagerAdapter adapter = new MainActivityAdapter(getSupportFragmentManager());
        pager = (ViewPager)findViewById(R.id.pager);
        pager.setAdapter(adapter);

        indicator = (TabPageIndicator)findViewById(R.id.indicator);
        indicator.setViewPager(pager);

		aq = new AQuery(this);		
		aq.id(R.id.buttonBank).clicked(this);
        aq.id(R.id.buttonHospital).clicked(this);
        aq.id(R.id.buttonPost).clicked(this);
        aq.id(R.id.buttonRent).clicked(this);
        aq.id(R.id.buttonNetwork).clicked(this);

		EventBus.getDefault().register(this);
		
		//init game at 1st time
		startNewGame();

		if (!prefman.isPlayedBefore())
		{
			//ask if the 1st time to game
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setIcon(R.drawable.dialog_info)
				   .setTitle(R.string.dialog_first_time)
			       .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
//			        	   prefman.setPlayedBefore(true);
			        	   showWebpageDialog(URL_HELP);
			           }
			       })
			       .setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			        	   prefman.setPlayedBefore(true);
			        	   dialog.dismiss();
			           }
			       });
			AlertDialog alert = builder.create();
			alert.show();
		}
	}

	@Override
	protected void onDestroy() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		
		menu.add(0, ACTION_NEWGAME, Menu.NONE, R.string.menu_newgame)
			.setIcon(android.R.drawable.ic_menu_rotate)
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		menu.add(0, ACTION_ABOUT, Menu.NONE, R.string.menu_about)
		.setIcon(android.R.drawable.ic_menu_info_details)
		.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		menu.add(0, ACTION_RANKING, Menu.NONE, R.string.menu_ranking)
		.setIcon(android.R.drawable.ic_menu_sort_by_size)
		.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		menu.add(0, ACTION_HELP, Menu.NONE, R.string.menu_help)
		.setIcon(android.R.drawable.ic_menu_help)
		.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		
		menu.add(0, ACTION_QUIT, Menu.NONE, R.string.menu_quit)
		.setIcon(android.R.drawable.ic_menu_revert)
		.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		menu.add(0, ACTION_TIPS, Menu.NONE, R.string.menu_tips)
		.setIcon(android.R.drawable.ic_menu_directions)
		.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		menu.add(0, ACTION_SETTINGS, Menu.NONE, R.string.menu_settings)
		.setIcon(android.R.drawable.ic_menu_preferences)
		.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		// return super.onCreateOptionsMenu(menu);
		return true;
    }

	//GameEngine use string to send notifications
	//MainActivity need to process it with EventQueue, so simply add string into it
	public void onEventMainThread(String event) {
		notifications.add(event);
	}
	
	//GameEngine use integer to send commands, like update interface and game over
	//MainActivity need to process it if possible
	//while some commands need to be processed after notification dialog(like change tab or restart) 
	//so need to be converted with special_event string, and insert into notification queue with EventBus	
	public void onEventMainThread(Integer event) {
		switch(event) {
		case Constants.UPDATE_DAY:
			updateDay();
			autoChangeTab();
			break;
		case Constants.UPDATE_DEAL:
			autoChangeTab();
			break;
		case Constants.UPDATE_MARKET:
			break;
		case Constants.UPDATE_ROOM:
			updateStatus();
			break;
		case Constants.UPDATE_MONEY:
		case Constants.UPDATE_FAME:
		case Constants.UPDATE_HEALTH:
			updateStatus();
			break;
		case Constants.UPDATE_GAME_OVER:
			//game over may be triggered by health event or time
			//that means maybe some event dialog is in the front
			//so need to use SPECIAL_EVENT_GAMEOVER to trigger some action after event dialog
			EventBus.getDefault().post(SPECIAL_EVENT_GAMEOVER);
		case Constants.UPDATE_SETTINGS:
			updateSettings();
			break;
		}
	}
	
	protected void updateSettings() {
		isAutoChangeTab = prefman.isAutoChangeTab();
		isPlaySound = prefman.isPlaySound();
	}

	protected void initSound() {
		soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
		soundPoolMap = new HashMap<String, Integer>();
		soundPoolMap.put("kill.wav", soundPool.load(this, R.raw.kill, 1));
		soundPoolMap.put("death.wav", soundPool.load(this, R.raw.death, 1));
		soundPoolMap.put("dog.wav", soundPool.load(this, R.raw.dog, 1));
		soundPoolMap.put("harley.wav", soundPool.load(this, R.raw.harley, 1));
		soundPoolMap.put("hit.wav", soundPool.load(this, R.raw.hit, 1));
		soundPoolMap.put("flee.wav", soundPool.load(this, R.raw.flee, 1));
		soundPoolMap.put("death.wav", soundPool.load(this, R.raw.death, 1));
		soundPoolMap.put("el.wav", soundPool.load(this, R.raw.el, 1));
		soundPoolMap.put("vomit.wav", soundPool.load(this, R.raw.vomit, 1));
		soundPoolMap.put("level.wav", soundPool.load(this, R.raw.level, 1));
		soundPoolMap.put("lan.wav", soundPool.load(this, R.raw.lan, 1));
		soundPoolMap.put("breath.wav", soundPool.load(this, R.raw.breath, 1));
		soundPoolMap.put("shutdoor.wav", soundPool.load(this, R.raw.shutdoor, 1));
		soundPoolMap.put("opendoor.wav", soundPool.load(this, R.raw.opendoor, 1));
		soundPoolMap.put("buy.wav", soundPool.load(this, R.raw.buy, 1));
		soundPoolMap.put("money.wav", soundPool.load(this, R.raw.money, 1));
		soundPoolMap.put("airport.wav", soundPool.load(this, R.raw.airport, 1));
	}
	
	protected void playSound(String sound, int loop) {
		if (!isPlaySound)
			return;
		
//		soundPoolMap.put(1, soundPool.load(this, R.raw.hos, 1));
//		soundPoolMap.put(2, soundPool.load(this, R.raw.airplane, 2));
		AudioManager mgr = (AudioManager) this
				.getSystemService(Context.AUDIO_SERVICE);
		float streamVolumeCurrent = mgr
				.getStreamVolume(AudioManager.STREAM_MUSIC);
		float streamVolumeMax = mgr
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		float volume = streamVolumeCurrent / streamVolumeMax;
		soundPool.play(soundPoolMap.get(sound), volume, volume, 1, loop, 1f);
		// 参数：1、Map中取值 2、当前音量 3、最大音量 4、优先级 5、重播次数 6、播放速度

	}

	protected void autoChangeTab() {
		if (!isAutoChangeTab)
			return;
		
		//have space, and money enough(or no goods), go to market
		//else goto room
		int goodsCount = GameEngine.getInstance().getRoom().getAllGoodsCount();
		int spaceLeft = GameEngine.getInstance().getRoom().getSpace() - goodsCount;
		if (0 == goodsCount) {
			indicator.setCurrentItem(TAB_MARKET);
			return;			
		} 
		else if (0 == spaceLeft) {
			indicator.setCurrentItem(TAB_ROOM);
			return;
		}
		
		//room have space here, see money enough to buy goods?
		boolean moneyEnough = false;
		int[] price = GameEngine.getInstance().getPrice();
		int money = GameEngine.getInstance().getMan().getCash() + GameEngine.getInstance().getMan().getDeposit();
		for (int i = 0; i < price.length; i++) {
			if ((price[i]!=0)&&(money>=price[i])) {
				moneyEnough = true;
				break;
			}
		}			
		if (!moneyEnough) {
			indicator.setCurrentItem(TAB_ROOM);
			return;
		}

		//a day passed, change to room
		if (TAB_LOCATION == pager.getCurrentItem()) {
			indicator.setCurrentItem(TAB_ROOM);
			return;
		}
	}
	
	@Override
    public void onBackPressed() {
		confirmQuit();
	}	

	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		
		switch(item.getItemId()) {
		case ACTION_NEWGAME:
			restartNewGame();
			break;
		case ACTION_QUIT:
			confirmQuit();
			break;
		case ACTION_HELP:
			showWebpageDialog(URL_HELP);
			break;
		case ACTION_ABOUT:
			showWebpageDialog(URL_ABOUT);
			break;
		case ACTION_RANKING:
			showRankingDialog(-1, null);
			break;
		case ACTION_TIPS:
			showWebpageDialog(URL_TIPS);
			break;
		case ACTION_SETTINGS:
			//TODO not finished, just debug
			startActivity(new Intent(this, SettingsActivity.class));
			break;
		default:
		}
            
        return super.onOptionsItemSelected(item);
    }
	
	protected void restartNewGame() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.dialog_warning)
			   .setTitle(R.string.dialog_abandon)
		       .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   startNewGame();
		           }
		       })
		       .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
	}

	protected void startNewGame() {
		notifications.clear();
		EventBus.getDefault().unregister(this);
		GameEngine.getInstance().init(getApplicationContext(), prefman.isHackerEnabled());
		
		//update all fragments
		EventBus.getDefault().post(Constants.UPDATE_PLACE);
		EventBus.getDefault().post(Constants.UPDATE_MARKET);
		EventBus.getDefault().post(Constants.UPDATE_ROOM);
		
		//update MainActivity
		indicator.setCurrentItem(TAB_LOCATION);
		updateDay();
		updateStatus();

		EventBus.getDefault().register(this);	
	}
	
	protected void confirmQuit() {
		//quit in the middle of game
		//after confirmed, GameEngine need to show a message before quit
		//so need to process finish() in EventQueue
		//SPECIAL_EVENT_QUIT will told EventQueue to quit immediately after dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.dialog_warning)
			   .setTitle(R.string.dialog_quit)
		       .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                GameEngine.getInstance().quit(new GameEngine.QuitCallback() {							
							@Override
							public void afterQuitMessage() {
								EventBus.getDefault().post(SPECIAL_EVENT_QUIT);
							}
						});
		           }
		       })
		       .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();		
	}
		
	protected void updateDay() {
		setTitle(String.format(getString(R.string.app_title), GameEngine.getInstance().getDay()));		
	}
	
	protected void updateStatus() {
		Man man = GameEngine.getInstance().getMan();
		Room room = GameEngine.getInstance().getRoom();
		
		aq.id(R.id.textViewCash).text(""+man.getCash());
		aq.id(R.id.textViewDeposit).text(""+man.getDeposit());
		aq.id(R.id.textViewDebt).text(""+man.getDebt());
		aq.id(R.id.textViewHealth).text(man.getHealth()+"/"+Constants.MAX_HEALTH);
		aq.id(R.id.textViewFame).text(man.getFame()+"/"+Constants.MAX_FAME);
		aq.id(R.id.textViewRoom).text(room.getAllGoodsCount()+"/"+room.getSpace());
	}
	
	protected String getFameStr(int fame) {
		if (fame >= 100) 
			return getString(R.string.fame_100);
		else if (fame > 90)
			return getString(R.string.fame_90);
		else if (fame > 80)
			return getString(R.string.fame_80);
		else if (fame > 60)
			return getString(R.string.fame_60);
		else if (fame > 40)
			return getString(R.string.fame_40);
		else if (fame > 20)
			return getString(R.string.fame_20);
		else if (fame > 10)
			return getString(R.string.fame_10);
		else
			return getString(R.string.fame_0);
	}
	
	protected String getHealthStr(int health) {
		if (health >= 100)
			return getString(R.string.health_100);
		else if (health > 90)
			return getString(R.string.health_90);
		else if (health > 80)
			return getString(R.string.health_80);
		else if (health > 60)
			return getString(R.string.health_60);
		else
			return getString(R.string.health_0);
	}
	
	protected void showWebpageDialog(String webUrl) {
		final WebView web = new WebView(this);
		web.loadUrl(webUrl);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.dialog_info)
			   .setTitle(R.string.app_name)
			   .setView(web)
		       .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   dialog.dismiss();
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();		
	}

	protected void showRankingDialog(final int pos, DialogInterface.OnDismissListener dismiss) {
		ArrayList<Ranking> ra = prefman.getRanking();
		if (ra.size() > 0) {
			final ListView rankingList = new ListView(this);
			rankingList.setAdapter(new ArrayAdapter<PreferenceManager.Ranking>(this, R.layout.listitem_ranking, ra) {
	
				@Override
				public View getView(int position, View convertView, ViewGroup parent) {
					if (null == convertView)
						convertView = getLayoutInflater().inflate(R.layout.listitem_ranking, null);
					PreferenceManager.Ranking display = getItem(position);
					AQuery aq = new AQuery(convertView);
					aq.id(R.id.textViewRanking).text(""+(position+1));
					aq.id(R.id.textViewName).text(""+display.name);
					aq.id(R.id.textViewFame).text(getFameStr(display.fame));
					aq.id(R.id.textViewHealth).text(getHealthStr(display.health));
					aq.id(R.id.textViewMoney).text("¥ "+display.money);
					if (position == pos) {
						aq.id(R.id.textViewRanking).getTextView().setTextColor(Color.RED);
						aq.id(R.id.textViewFame).getTextView().setTextColor(Color.RED);
						aq.id(R.id.textViewName).getTextView().setTextColor(Color.RED);
						aq.id(R.id.textViewHealth).getTextView().setTextColor(Color.RED);
						aq.id(R.id.textViewMoney).getTextView().setTextColor(Color.RED);
					}
					else {
						aq.id(R.id.textViewRanking).getTextView().setTextColor(Color.BLACK);
						aq.id(R.id.textViewFame).getTextView().setTextColor(Color.BLACK);
						aq.id(R.id.textViewName).getTextView().setTextColor(Color.BLACK);
						aq.id(R.id.textViewHealth).getTextView().setTextColor(Color.BLACK);
						aq.id(R.id.textViewMoney).getTextView().setTextColor(Color.BLACK);					
					}
					return convertView;	
				}
				
			});
			
			CommonDialogFragment.showRankingDialog(getString(R.string.dialog_ranking), rankingList, dismiss, this);
		}
		else {
			CommonDialogFragment.showRankingDialog(getString(R.string.dialog_ranking), null, dismiss, this);
		}
		
	}

	protected void showNumberInputDialog(int iconId, int titleId, String message, int okId, int cancelId, int initNumber, final NumberInputCallback callback) {
		final EditText input = new EditText(this);
		input.setInputType(InputType.TYPE_CLASS_NUMBER);
		input.setText(""+initNumber);
		input.setSelectAllOnFocus(true);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(iconId)
				.setTitle(titleId)
				.setMessage(message)
				.setView(input)
				.setPositiveButton(okId,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								if (null != callback) {
									try {
										int number = Integer.parseInt(input.getText().toString());
										callback.onNumberInputed(number);
									}
									catch(NumberFormatException ex) {
										EventBus.getDefault().post("error input, not processed here");
									}
								}
							}
						})
				.setNegativeButton(cancelId,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.dismiss();
							}
						});
		final AlertDialog alert = builder.create();
		input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
		    @Override
		    public void onFocusChange(View v, boolean hasFocus) {
		        if (hasFocus) {
		        	alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		        }
		    }
		});
		
		alert.show();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.buttonPost:
			playSound("opendoor.wav", 0);
			onButtonPost();
			break;
		case R.id.buttonHospital:
			playSound("opendoor.wav", 0);
			onButtonHospital();
			break;
		case R.id.buttonRent:
			playSound("opendoor.wav", 0);
			onButtonRent();
			break;
		case R.id.buttonNetwork:
			playSound("airport.wav", 0);
			GameEngine.getInstance().dealWithInternetBar();
			break;
		case R.id.buttonBank:
			playSound("opendoor.wav", 0);
			onButtonBank();
			break;
		default:
			EventBus.getDefault().post("not processed");
			break;			
		}		
	}
	
	protected void onButtonPost() {
		if (!GameEngine.getInstance().checkPostPossibility())
			return;
		
		//min is 1
		Man man = GameEngine.getInstance().getMan();
		int max = Math.min(man.getCash(), man.getDebt());
		String message = String.format(getString(R.string.dialog_post_detail), man.getDebt());
		showNumberInputDialog(
				R.drawable.dialog_post, 
				R.string.dialog_post, 
				message, R.string.dialog_ok, R.string.dialog_post_cancel, max,				
				new MainActivity.NumberInputCallback() {					
					@Override
					public void onNumberInputed(int number) {
						GameEngine.getInstance().dealWithDebt(number);
					}
				});		
	}

	protected void onButtonHospital() {
		if (!GameEngine.getInstance().checkMedicalPossibility())
			return;
		
		//min is 1
		Man man = GameEngine.getInstance().getMan();
		int max = Constants.MAX_HEALTH-man.getHealth();
		String message = String.format(getString(R.string.dialog_hospital_detail), man.getHealth(), max, Constants.HEALTHY_CURE_PRICE);
		showNumberInputDialog(
				R.drawable.dialog_hospital, 
				R.string.dialog_hospital, 
				message, R.string.dialog_ok, R.string.dialog_hospital_cancel, max,				
				new MainActivity.NumberInputCallback() {					
					@Override
					public void onNumberInputed(int number) {
						GameEngine.getInstance().dealWithHospital(number);
					}
				});		
	}

	protected void onButtonRent() {
		if (!GameEngine.getInstance().checkRentPossibility())
			return;
		
		Room room = GameEngine.getInstance().getRoom();
		String message = String.format(getString(R.string.dialog_rent_detail), 
				room.getSpace(), 
				GameEngine.getInstance().getRentPrice(), 
				room.getSpace() + Constants.ROOM_STEP);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.dialog_rent)
			   .setTitle(R.string.dialog_rent)
			   .setMessage(message)
		       .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   GameEngine.getInstance().dealWithRent();
		           }
		       })
		       .setNegativeButton(R.string.dialog_rent_cancel, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   dialog.dismiss();
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	protected void onButtonBank() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.dialog_bank)
			   .setTitle(R.string.dialog_bank)
//		   .setMessage(R.string.dialog_bank_detail)
		       .setPositiveButton(R.string.dialog_bank_save, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   //save dialog
		        	   MainActivity.this.showNumberInputDialog(
		    				R.drawable.dialog_bank, 
		    				R.string.dialog_bank_save, 
		    				getString(R.string.dialog_bank_save_detail), 
		    				R.string.dialog_ok, R.string.dialog_cancel, 
		    				GameEngine.getInstance().getMan().getCash(),				
		    				new MainActivity.NumberInputCallback() {					
		    					@Override
		    					public void onNumberInputed(int number) {
		    						GameEngine.getInstance().dealWithBank(number, true);
		    					}
		    				});		
		           }
		       })
		       .setNeutralButton(R.string.dialog_bank_withdraw, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   //withdraw dialog
		        	   MainActivity.this.showNumberInputDialog(
		    				R.drawable.dialog_bank, 
		    				R.string.dialog_bank_withdraw, 
		    				getString(R.string.dialog_bank_withdraw_detail), 
		    				R.string.dialog_ok, R.string.dialog_cancel, 
		    				GameEngine.getInstance().getMan().getDeposit(),				
		    				new MainActivity.NumberInputCallback() {					
		    					@Override
		    					public void onNumberInputed(int number) {
		    						GameEngine.getInstance().dealWithBank(number, false);
		    					}
		    				});		
		           }
		       })
		       .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   dialog.dismiss();
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
	}		

    class MainActivityAdapter extends FragmentPagerAdapter {
    	
        public MainActivityAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

        	switch(position) {
        	case TAB_LOCATION:
            	return new LocationFragment();
        	case TAB_MARKET:
            	return new MarketFragment();
        	case TAB_ROOM:
            	return new RoomFragment();
//              return TestFragment.newInstance(getString(TABNAMES[position % TABNAMES.length]));
        	}
			return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return MainActivity.this.getText(tabtitles[position]);
        }

        @Override
        public int getCount() {
          return tabtitles.length;
        }
    }
    
    protected class EventQueue extends ArrayBlockingQueue<String> implements DialogInterface.OnDismissListener {

		private static final long serialVersionUID = 1L;
		protected ReentrantLock mutex = new ReentrantLock();
    	
		public EventQueue() {
			super(EVENT_QUEUE_SIZE);
		}		

		@Override
		public boolean add(String e) {
			Log.d(TAG, "===add:"+e);
			boolean ret;
			mutex.lock();
			if (null == peek()) {
				ret = super.add(e);
				processEvent();
			}
			else
				ret = super.add(e);
			mutex.unlock();
			return ret;
		}

		protected void processEvent() {
			String notify = notifications.peek();
			Log.d(TAG, "notify="+notify);
			
			if (null == notify) {	//no events
				return;
			}

			if (SPECIAL_EVENT_QUIT.equals(notify)) {	//quit
				poll();
				MainActivity.this.finish();
				return;
			}
			if (SPECIAL_EVENT_GAMEOVER.equals(notify)) {	//game over restart
				poll();
				alertRanking();
				return;
			}
			
			String soundwav = null;
			if (notify.startsWith("["))	{//have sound
				int headpos = notify.indexOf(']');
				soundwav = notify.substring(1, headpos);
				notify = notify.substring(headpos+1);
			}
			CommonDialogFragment.showEventDialog(getString(R.string.notify_title), notify, this, MainActivity.this);				
			if (null != soundwav)
				MainActivity.this.playSound(soundwav, 0);
		}
		
		protected void alertRanking() {
			final Man man = GameEngine.getInstance().getMan();
			final int money = man.getCash()+man.getDeposit()-man.getDebt();
			final int pos = prefman.getPosition(money); 
			if (pos >= 0) {
				CommonDialogFragment.showTextInputDialog(getString(R.string.dialog_congratulations), 
						String.format(getString(R.string.title_alert), money, (pos+1), getString(Constants.TITLES[Constants.getRandom(5)])), 
						prefman.getLastPlayerName(),
						new CommonDialogFragment.TextInputCallback() {
							@Override
							public void onTextInputed(String text) {
								if (null == text)
									text = PreferenceManager.DEFAULT_LAST_PLAYER_NAME;
								else {
									if ("".equals(text.trim()))
										text = PreferenceManager.DEFAULT_LAST_PLAYER_NAME;
									prefman.setLastPlayerName(text);
								}
								prefman.addRanking(text, man.getHealth(), man.getFame(), money);
							}					
						},
						new DialogInterface.OnDismissListener() {							
							@Override
							public void onDismiss(DialogInterface dialog) {
								MainActivity.this.showRankingDialog(pos, new DialogInterface.OnDismissListener() {
									
									@Override
									public void onDismiss(DialogInterface dialog) {
										confirmGameOverRestart();
									}
								});
							}
						},
						MainActivity.this);
			}
			else {
				CommonDialogFragment.showEventDialog(getString(R.string.dialog_sorry), 
						String.format(getString(R.string.title_alert_failed), money), 
						new DialogInterface.OnDismissListener() {					
					@Override
					public void onDismiss(DialogInterface dialog) {
						MainActivity.this.showRankingDialog(pos, new DialogInterface.OnDismissListener() {							
							@Override
							public void onDismiss(DialogInterface dialog) {
								confirmGameOverRestart();
							}
						});
					}
				}, MainActivity.this);								
			}
			
		}

		protected void confirmGameOverRestart() {
			AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
			builder.setIcon(R.drawable.dialog_warning)
				   .setTitle(R.string.dialog_restart)
				   .setCancelable(false)
			       .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			        	   startNewGame();
			           }
			       })
			       .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			        	   //quit after the game, should be immediately
			        	   MainActivity.this.finish();
			           }
			       });
			AlertDialog alert = builder.create();
			alert.show();		
		}

		@Override
		public void onDismiss(DialogInterface dialog) {
			mutex.lock();
			Log.d(TAG, "===poll");
			poll();
			processEvent();
			mutex.unlock();						
		}	
    	
    }
    
    public interface NumberInputCallback {
    	public void onNumberInputed(int number);
    }

//    public final static class TestFragment extends Fragment {
//        private static final String KEY_CONTENT = "TestFragment:Content";
//
//        public static TestFragment newInstance(String content) {
//            TestFragment fragment = new TestFragment();
//
//            StringBuilder builder = new StringBuilder();
//            for (int i = 0; i < 20; i++) {
//                builder.append(content).append(" ");
//            }
//            builder.deleteCharAt(builder.length() - 1);
//            fragment.mContent = builder.toString();
//
//            return fragment;
//        }
//
//        private String mContent = "???";
//
//        @Override
//        public void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//
//            if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT)) {
//                mContent = savedInstanceState.getString(KEY_CONTENT);
//            }
//        }
//
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//            TextView text = new TextView(getActivity());
//            text.setGravity(Gravity.CENTER);
//            text.setText(mContent);
//            text.setTextSize(20 * getResources().getDisplayMetrics().density);
//            text.setPadding(20, 20, 20, 20);
//
//            LinearLayout layout = new LinearLayout(getActivity());
//            layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//            layout.setGravity(Gravity.CENTER);
//            layout.addView(text);
//
//            return layout;
//        }
//
//        @Override
//        public void onSaveInstanceState(Bundle outState) {
//            super.onSaveInstanceState(outState);
//            outState.putString(KEY_CONTENT, mContent);
//        }
//    }

	
}
