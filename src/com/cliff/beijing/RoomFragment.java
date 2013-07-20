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
import com.cliff.beijing.game.Room;

import de.greenrobot.event.EventBus;

public class RoomFragment extends SherlockListFragment implements OnItemClickListener {
	
	protected LayoutInflater mInflater = null;
	
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
		this.setListAdapter(new RoomListAdapter(this.getActivity(), R.layout.listitem_owned, new ArrayList<RoomDisplay>()));
		EventBus.getDefault().register(this);
		updateRoomDisplay();
	}
	
	@Override
	public void onDestroy() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	public void onEventMainThread(Integer event) {
		switch(event) {
		case Constants.UPDATE_ROOM:
			updateRoomDisplay();
			break;
		case Constants.UPDATE_MARKET:
			RoomListAdapter adapter = (RoomListAdapter) this.getListAdapter();
			adapter.notifyDataSetChanged();
			break;
		}
	}

	protected void updateRoomDisplay() {
		RoomListAdapter adapter = (RoomListAdapter) this.getListAdapter();
		adapter.clear();
		
		Room room = GameEngine.getInstance().getRoom();
		int[] count = room.getGoodsCount();
		int[] cost = room.getGoodsCost();
		
		if ((null == count)||(null == cost)) {
			adapter.notifyDataSetChanged();
			return;
		}
		
		for (int i = 0; i < count.length; i++) {
			if (0 != count[i]) {
				RoomDisplay newLine = new RoomDisplay();
				newLine.goodId = i;
				newLine.iconId = Constants.GOOD_ICONS[i];
				newLine.goodNameId = Constants.GOOD_NAMES[i];
				newLine.goodCount = count[i];
				newLine.goodPrice = cost[i] / count[i];
				adapter.add(newLine);
			}
		}
		adapter.notifyDataSetChanged();
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// check if there's buyer, GameEngine will show dialog if cannot sell
		final RoomDisplay disp = ((RoomListAdapter)getListAdapter()).getItem(position);
		if (!GameEngine.getInstance().checkSellPossibility(disp.goodId))
			return;

		//min is 1
		int max = disp.goodCount;
		String message = String.format(getString(R.string.dialog_selling_detail),
				getString(Constants.GOOD_NAMES[disp.goodId]),
				disp.goodPrice, GameEngine.getInstance().getPrice()[disp.goodId],
				1, max);
		((MainActivity)getActivity()).showNumberInputDialog(
				R.drawable.dialog_wallet, 
				R.string.dialog_selling, 
				message, R.string.dialog_ok, R.string.dialog_cancel, max,				
				new MainActivity.NumberInputCallback() {					
					@Override
					public void onNumberInputed(int number) {
						// TODO Auto-generated method stub
						GameEngine.getInstance().dealWithRoom(disp.goodId, number);
						((MainActivity)getActivity()).playSound("money.wav", 0);
					}
				});
		
	}
	
	protected class RoomDisplay {
		public int iconId;
		public int goodId;
		public int goodNameId;
		public int goodPrice;
		public int goodCount;
	}


	protected class RoomListAdapter extends ArrayAdapter<RoomDisplay> {

		public RoomListAdapter(Context context, int textViewResourceId,
				List<RoomDisplay> objects) {
			super(context, textViewResourceId, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (null == convertView)
				convertView = mInflater.inflate(R.layout.listitem_owned, null);
			RoomDisplay display = getItem(position);
			AQuery aq = new AQuery(convertView);
			aq.id(R.id.textViewRoomGoodName).text(getString(display.goodNameId));
			aq.id(R.id.textViewRoomGoodCount).text(""+display.goodCount);
			aq.id(R.id.imageViewRoomGoodIcon).image(display.iconId);
			aq.id(R.id.textViewRoomGoodPrice).text(
					String.format(getString(R.string.good_buying_price), display.goodPrice));
			
			int marketPrice = GameEngine.getInstance().getPrice()[display.goodId];
			if (0 != marketPrice)
				aq.id(R.id.textViewRoomGoodMarketPrice).text(
					String.format(getString(R.string.good_market_price), marketPrice));
			else
				aq.id(R.id.textViewRoomGoodMarketPrice).text("");
			return convertView;
		}
		
	}

}
