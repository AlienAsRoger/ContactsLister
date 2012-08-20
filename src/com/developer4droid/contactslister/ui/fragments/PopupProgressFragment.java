package com.developer4droid.contactslister.ui.fragments;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.developer4droid.contactslister.R;
import com.developer4droid.contactslister.statics.StaticData;
import com.developer4droid.contactslister.ui.views.PopupItem;


/**
 * @author alien_roger
 * @created at: 07.04.12 7:13
 */
public class PopupProgressFragment extends BasePopupDialogFragment {

    private TextView titleTxt;
    private TextView messageTxt;
    private boolean cancelable;

	public static PopupProgressFragment newInstance(PopupItem popupItem) {
		PopupProgressFragment frag = new PopupProgressFragment();
		Bundle arguments = new Bundle();
		arguments.putSerializable(POPUP_ITEM, popupItem);
		frag.setArguments(arguments);
		return frag;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.popup_progress, container, false);

		titleTxt = (TextView) view.findViewById(R.id.popupTitle);
		messageTxt = (TextView) view.findViewById(R.id.popupMessage);
		return view;
	}

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(!cancelable)
            setCancelable(false);
    }

	@Override
	public void onResume() {
		super.onResume();
		String message = popupItem.getMessage(getActivity());
		if(message.equals(StaticData.SYMBOL_EMPTY)){
			messageTxt.setVisibility(View.GONE);
		}else{
			if(message.contains(StaticData.SYMBOL_TAG)){
				messageTxt.setText(Html.fromHtml(message));
			}else{
				messageTxt.setText(message);
			}
			messageTxt.setVisibility(View.VISIBLE);
		}
		titleTxt.setText(popupItem.getTitle(getActivity()));
	}

	public void setNotCancelable(){
        cancelable = false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cancelable = true;
    }



}
