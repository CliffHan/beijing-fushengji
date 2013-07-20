package com.cliff.beijing;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.actionbarsherlock.app.SherlockFragment;
import com.androidquery.AQuery;
import com.cliff.beijing.game.Constants;
import com.cliff.beijing.game.GameEngine;
import de.greenrobot.event.EventBus;

public class LocationFragment extends SherlockFragment implements OnClickListener, OnItemSelectedListener {
	
	protected AQuery aq;
	protected Spinner spinner = null;
	protected int check = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}
	
	public void onEventMainThread(Integer event) {
		switch(event) {
		case Constants.UPDATE_PLACE:
			updatePlaceDisplay();
			break;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.fragment_map, null);
        aq = new AQuery(view);
                
        spinner = (Spinner)view.findViewById(R.id.spinnerLocation);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
        		R.array.places, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        
        aq.id(R.id.buttonChangePlace).clicked(this);
        aq.id(R.id.buttonStayHere).clicked(this);
     
        updatePlaceDisplay();        
//		return super.onCreateView(inflater, container, savedInstanceState);
		return view;
	}
	
	protected void updatePlaceDisplay() {		
        int place = GameEngine.getInstance().getPlace();
        if (place < 0)
        	place = Constants.PLACES_LOCATION.length - 1;
        if (place == spinner.getSelectedItemPosition())
        	return;
        
        AdapterView.OnItemSelectedListener  tempListener = spinner.getOnItemSelectedListener();
        spinner.setOnItemSelectedListener(null);
    	spinner.setSelection(place);
    	check = 0;
        spinner.setOnItemSelectedListener(tempListener);        	
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		int currentPos = GameEngine.getInstance().getPlace();
		if (currentPos < 0)
			currentPos = Constants.PLACES_LOCATION.length - 1;
		int targetPos = currentPos;
		
		switch(view.getId()) {
		case R.id.buttonChangePlace:
			while (targetPos == currentPos)
				targetPos = Constants.getRandom(Constants.PLACES_LOCATION.length);
		case R.id.buttonStayHere:
			((MainActivity)getActivity()).playSound("shutdoor.wav", 0);
			GameEngine.getInstance().flow(targetPos);
			break;
		default:
			EventBus.getDefault().post("not processed");
			break;
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		check ++;
		if (check > 1)
			GameEngine.getInstance().flow(pos);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}		
}
