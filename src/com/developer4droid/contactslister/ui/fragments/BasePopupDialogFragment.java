package com.developer4droid.contactslister.ui.fragments;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Toast;
import com.developer4droid.contactslister.R;
import com.developer4droid.contactslister.ui.interfaces.PopupDialogFace;
import com.developer4droid.contactslister.ui.views.PopupItem;

/**
 * @author Alexey Schekin (schekin@azoft.com)
 * @created 10.07.12
 * @modified 10.07.12
 */
public abstract class BasePopupDialogFragment extends DialogFragment implements View.OnClickListener  {

    protected static final String POPUP_ITEM = "popup item";

    protected PopupDialogFace listener;
    protected PopupItem popupItem;

    protected int buttonsNumber;
    protected boolean isShowed;
	protected boolean isPaused;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, 0);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(getArguments() != null){
            popupItem = (PopupItem) getArguments().getSerializable(POPUP_ITEM);
        }else{
            popupItem = (PopupItem) savedInstanceState.getSerializable(POPUP_ITEM);
        }
    }

    public void updatePopupItem(PopupItem popupItem) {
        this.popupItem = popupItem;
		if(!isVisible()){
			if(getArguments().containsKey(POPUP_ITEM)){
				setArguments(null);
			}
			Bundle arguments = new Bundle();
			arguments.putSerializable(POPUP_ITEM, popupItem);
			setArguments(arguments);
		}
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(POPUP_ITEM, popupItem);
    }

	@Override
	public void onResume() {
		super.onResume();
		isPaused = false;
	}

	@Override
	public void onPause() {
		super.onPause();
		isPaused = true;
	}

	@Override
    public void onClick(View view) {
        if(view.getId() == R.id.positiveBtn){
            listener.onPositiveBtnClick(this);
        }else if(view.getId() == R.id.neutralBtn){
			listener.onNeutralBtnCLick(this);
        }else if(view.getId() == R.id.negativeBtn){
            listener.onNegativeBtnClick(this);
        }
    }

    public void setButtons(int buttonsNumber){
        this.buttonsNumber = buttonsNumber;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        isShowed = true;
		FragmentTransaction ft = manager.beginTransaction();
		ft.add(this, tag);
		ft.commitAllowingStateLoss();
    }

    @Override
    public int show(FragmentTransaction transaction, String tag) {
        isShowed = true;
        return super.show(transaction, tag);
    }

    @Override
    public void dismiss() {
        if(isShowed || isVisible())
            super.dismiss();
        isShowed = false;
    }

	protected void showToast(String msg) {
		Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
	}

	protected void showToast(int msgId) {
		Toast.makeText(getActivity(), msgId, Toast.LENGTH_SHORT).show();
	}
}
