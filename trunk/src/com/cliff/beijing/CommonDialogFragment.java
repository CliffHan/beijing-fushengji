package com.cliff.beijing;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class CommonDialogFragment extends SherlockDialogFragment {
	
	protected int iconId = 0;
	protected String title = null;
	protected String message = null;
	protected boolean cancelable = true;
	protected View view = null;
	protected int positiveId = 0;
	protected int negativeId = 0;
	protected int neutralId = 0;
	protected DialogInterface.OnClickListener onPositive = null;
	protected DialogInterface.OnClickListener onNegative = null;
	protected DialogInterface.OnClickListener onNeutral = null;
	protected DialogInterface.OnDismissListener onDismiss = null;
	protected DialogInterface.OnClickListener defaultClickListener = new DialogInterface.OnClickListener() {				
		@Override
		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();
		}
	};
	
	public static void showRankingDialog(String title, View view, DialogInterface.OnDismissListener dismiss, FragmentActivity fragmentActivity) {
		CommonDialogFragment frag = CommonDialogFragment.newInstance(R.drawable.dialog_info, title, null, true);
		frag.setOnPositive(R.string.dialog_ok, null);
		frag.setView(view);
		frag.setOnDismiss(dismiss);
		frag.show(fragmentActivity.getSupportFragmentManager(), "dialog");		
	}

	public static void showWebDialog(String webUrl, FragmentActivity fragmentActivity) {
		CommonDialogFragment frag = CommonDialogFragment.newInstance(R.drawable.dialog_warning, "test0", "testmessage", true);
		frag.setOnPositive(R.string.dialog_ok, null);
		final WebView web = new WebView(fragmentActivity);
		web.loadUrl(webUrl);
		frag.setView(web);
		frag.show(fragmentActivity.getSupportFragmentManager(), "dialog");		
	}	

	public static void showEventDialog(String title, String message, DialogInterface.OnDismissListener dismiss, FragmentActivity fragmentActivity) {
		CommonDialogFragment frag = CommonDialogFragment.newInstance(R.drawable.dialog_warning, title, message, true);
		frag.setOnPositive(R.string.dialog_ok, null);
		frag.setOnDismiss(dismiss);
		frag.show(fragmentActivity.getSupportFragmentManager(), "dialog");		
	}	

	public static void showTextInputDialog(String title, String message, String defaultStr, final TextInputCallback callback, 
			DialogInterface.OnDismissListener dismiss, FragmentActivity fragmentActivity) {
		final EditText input = new EditText(fragmentActivity);
		input.setInputType(InputType.TYPE_CLASS_TEXT);
		input.setText(defaultStr);
		input.setSelectAllOnFocus(true);

		CommonDialogFragment frag = CommonDialogFragment.newInstance(R.drawable.dialog_warning, title, message, true);
		frag.setOnPositive(R.string.dialog_ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (null != callback)
					callback.onTextInputed(input.getText().toString());
			}
		});
		frag.setOnNegative(R.string.dialog_cancel, new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (null != callback)
					callback.onTextInputed(null);				
			}
		});
		frag.setOnDismiss(dismiss);
		frag.setView(input);
		frag.show(fragmentActivity.getSupportFragmentManager(), "dialog");

	}	

	public static CommonDialogFragment newInstance(int iconId, String title, String message, boolean cancelable) {
		CommonDialogFragment frag = new CommonDialogFragment();
		frag.iconId = iconId;
		frag.title = title;
		frag.message = message;
		frag.cancelable = cancelable;
        return frag;
    }
	
	public void setOnPositive(int positiveId, DialogInterface.OnClickListener listener) {
		this.positiveId = positiveId; 
		if (null == listener)
			onPositive = defaultClickListener; 
		else
			onPositive = listener;
	}

	public void setOnNegative(int negativeId, DialogInterface.OnClickListener listener) {
		this.negativeId = negativeId; 
		if (null == listener)
			onNegative = defaultClickListener; 
		else
			onNegative = listener;
	}

	public void setOnNeutual(int neutralId, DialogInterface.OnClickListener listener) {
		this.neutralId = neutralId; 
		if (null == listener)
			onNeutral = defaultClickListener; 
		else
			onNeutral = listener;
	}
	
	public void setOnDismiss(DialogInterface.OnDismissListener listener) {
		this.onDismiss = listener;
	}

	public void setView(View view) {
		this.view = view;	
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setCancelable(cancelable);
		if (0 != iconId)
			builder.setIcon(iconId);
		
		if (null != title)
			builder.setTitle(title);

		if (null != message)
			builder.setMessage(message);
		
		if (null != onPositive)
			builder.setPositiveButton(positiveId, onPositive);
		if (null != onNegative)
			builder.setNegativeButton(negativeId, onNegative);
		if (null != onNeutral)
			builder.setNeutralButton(neutralId, onNeutral);

		if (null != view)
			builder.setView(view);
						
		final Dialog dialog = builder.create();
		if ((null != view)&&(view instanceof EditText)) {			
			view.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			    @Override
			    public void onFocusChange(View v, boolean hasFocus) {
			        if (hasFocus) {
			        	dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
			        }
			    }
			});
		}
		return dialog;
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		if (null != onDismiss)
			onDismiss.onDismiss(dialog);

		super.onDismiss(dialog);
	}

	public interface TextInputCallback {
    	public void onTextInputed(String text);
    }
}
