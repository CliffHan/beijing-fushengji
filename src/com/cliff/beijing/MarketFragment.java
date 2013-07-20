package com.cliff.beijing;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;

import com.actionbarsherlock.app.SherlockListFragment;
import com.androidquery.AQuery;
import com.cliff.beijing.game.Constants;
import com.cliff.beijing.game.GameEngine;

import de.greenrobot.event.EventBus;

public class MarketFragment extends SherlockListFragment implements OnItemClickListener {
	protected LayoutInflater mInflater = null;
//	private final String TAG = "MarketFragment";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		this.getListView().setOnItemClickListener(this);
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mInflater = getLayoutInflater(savedInstanceState);
		this.setListAdapter(new MarketListAdapter(this.getActivity(), R.layout.listitem_market, new ArrayList<MarketDisplay>()));
		EventBus.getDefault().register(this);
		updateMarketDisplay();
	}
	
	@Override
	public void onDestroy() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	public void onEventMainThread(Integer event) {
		switch(event) {
		case Constants.UPDATE_MARKET:
			updateMarketDisplay();
			break;
		}
	}

	protected void updateMarketDisplay() {
//		Log.d(TAG, "updateMarketDisplay");
		MarketListAdapter adapter = (MarketListAdapter) this.getListAdapter();
		adapter.clear();
		
		int[] price = GameEngine.getInstance().getPrice();
		if (null == price) {
			adapter.notifyDataSetChanged();
			return;
		}
		
		for (int i = 0; i < price.length; i++) {
			if (0 != price[i]) {
				MarketDisplay newLine = new MarketDisplay();
				newLine.goodId = i;
				newLine.iconId = Constants.GOOD_ICONS[i];
				newLine.goodNameId = Constants.GOOD_NAMES[i];
				newLine.goodPrice = price[i];
				adapter.add(newLine);
			}
		}
		adapter.notifyDataSetChanged();
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// check money, if not enough, just quit, GameEngine will show dialog
		final MarketDisplay disp = ((MarketListAdapter)getListAdapter()).getItem(position);
		if (!GameEngine.getInstance().checkBuyPossibility(disp.goodId))
			return;
//		showMarketDialog();
		
		//min is 1, buying possbility is confirmed by GameEngine already
		int max = GameEngine.getInstance().getMaxBuyCount(disp.goodId);
		String message = String.format(getString(R.string.dialog_buying_detail),
				getString(Constants.GOOD_NAMES[disp.goodId]),
				1, max);
		((MainActivity)getActivity()).showNumberInputDialog(
				R.drawable.dialog_wallet, 
				R.string.dialog_buying, 
				message, R.string.dialog_ok, R.string.dialog_cancel, max,				
				new MainActivity.NumberInputCallback() {					
					@Override
					public void onNumberInputed(int number) {
						GameEngine.getInstance().dealWithMarket(disp.goodId, number);
						((MainActivity)getActivity()).playSound("buy.wav", 0);
					}
				});
	}
	
	protected class MarketDisplay {
		public int iconId;
		public int goodId;
		public int goodNameId;
		public int goodPrice;
	}

	protected class MarketListAdapter extends ArrayAdapter<MarketDisplay> {
		
		public MarketListAdapter(Context context, int textViewResourceId,
				List<MarketDisplay> objects) {
			super(context, textViewResourceId, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (null == convertView)
				convertView = mInflater.inflate(R.layout.listitem_market, null);
			MarketDisplay display = getItem(position);
			AQuery aq = new AQuery(convertView);
			aq.id(R.id.textViewGoodName).text(getString(display.goodNameId));
			aq.id(R.id.textViewGoodPrice).text("¥" + display.goodPrice);
			aq.id(R.id.imageViewGoodIcon).image(display.iconId);
			aq.id(R.id.textViewRoomGoodMarketPrice).text(GameEngine.getInstance().getPrice()[display.goodId]);
			return convertView;
		}
		
	}
}
