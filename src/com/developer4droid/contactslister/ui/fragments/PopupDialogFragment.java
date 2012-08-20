package com.developer4droid.contactslister.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.developer4droid.contactslister.R;
import com.developer4droid.contactslister.statics.StaticData;
import com.developer4droid.contactslister.ui.interfaces.PopupDialogFace;
import com.developer4droid.contactslister.ui.views.PopupItem;


/**
 * PopupDialogFragment class
 *
 * @author alien_roger
 * @created at: 07.04.12 7:13
 */
public class PopupDialogFragment extends BasePopupDialogFragment {

    private TextView titleTxt;
    private TextView messageTxt;
    private Button leftBtn;
    private Button neutralBtn;
    private Button rightBtn;

	public static PopupDialogFragment newInstance(PopupItem popupItem) {
        PopupDialogFragment frag = new PopupDialogFragment();
		Bundle arguments = new Bundle();
		arguments.putSerializable(POPUP_ITEM, popupItem);
		frag.setArguments(arguments);
        return frag;
    }

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		listener = (PopupDialogFace) activity;
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.popup_default, container, false);

        messageTxt = (TextView)view.findViewById(R.id.popupMessage);
        titleTxt = (TextView)view.findViewById(R.id.popupTitle);

        leftBtn = (Button)view.findViewById(R.id.positiveBtn);
        neutralBtn = (Button)view.findViewById(R.id.neutralBtn);
        rightBtn = (Button)view.findViewById(R.id.negativeBtn);

        leftBtn.setOnClickListener(this);
        neutralBtn.setOnClickListener(this);
        rightBtn.setOnClickListener(this);

        neutralBtn.setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        switch (buttonsNumber){
            case 1:
                rightBtn.setVisibility(View.GONE);
                break;
            case 3:
				neutralBtn.setVisibility(View.VISIBLE);
                break;
        }
    }

	@Override
	public void onResume() {
		super.onResume();

		String message = popupItem.getMessage(getActivity());
		if(message.contains(StaticData.SYMBOL_TAG)){
			messageTxt.setText(Html.fromHtml(message));
		}else{
			messageTxt.setText(message);
		}
		messageTxt.setVisibility(View.VISIBLE);
		titleTxt.setText(popupItem.getTitle(getActivity()));

		leftBtn.setText(popupItem.getPositiveBtnId());
		if(buttonsNumber == 3)
			neutralBtn.setText(popupItem.getNeutralBtnId());
		rightBtn.setText(popupItem.getNegativeBtnId());
	}

}
