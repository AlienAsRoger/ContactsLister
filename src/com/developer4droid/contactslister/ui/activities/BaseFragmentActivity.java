package com.developer4droid.contactslister.ui.activities;

import android.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import com.developer4droid.contactslister.statics.StaticData;
import com.developer4droid.contactslister.ui.fragments.PopupDialogFragment;
import com.developer4droid.contactslister.ui.fragments.PopupProgressFragment;
import com.developer4droid.contactslister.ui.interfaces.PopupDialogFace;
import com.developer4droid.contactslister.ui.views.PopupItem;

import java.util.ArrayList;
import java.util.List;

/**
 * BaseFragmentActivity class
 *
 * @author alien_roger
 * @created at: 07.07.12 6:42
 */
public abstract class BaseFragmentActivity extends FragmentActivity implements PopupDialogFace {

	private static final String INFO_POPUP_TAG = "information popup";
	private static final String PROGRESS_TAG = "progress dialog popup";
	protected static final String NETWORK_CHECK_TAG = "network check popup";
	protected static final int NETWORK_REQUEST = 3456;
	protected static final String RE_LOGIN_TAG = "re-login popup";
	protected static final String CHESS_NO_ACCOUNT_TAG = "chess no account popup";
	protected static final String CHECK_UPDATE_TAG = "check update";


	protected DisplayMetrics metrics;


	private Context context;
	protected SharedPreferences preferences;
	protected SharedPreferences.Editor preferencesEditor;

	protected PopupDialogFragment popupDialogFragment;
	protected PopupItem popupItem;
	protected PopupItem popupProgressItem;
	protected PopupProgressFragment popupProgressDialogFragment;
	protected List<PopupDialogFragment> popupManager;

	protected boolean isPaused;
	protected Animation fadeInAnimation;
	protected Animation fadeOutAnimation;

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DITHER);
		getWindow().setFormat(PixelFormat.RGBA_8888);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		context = this;


		popupItem = new PopupItem();
		popupDialogFragment = PopupDialogFragment.newInstance(popupItem);
		popupProgressItem = new PopupItem();
		popupProgressDialogFragment = PopupProgressFragment.newInstance(popupProgressItem);

		popupManager = new ArrayList<PopupDialogFragment>();

		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		preferencesEditor = preferences.edit();

		metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);


		fadeInAnimation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
		fadeOutAnimation = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
	}

	@Override
	protected void onResume() {
		super.onResume();
		isPaused = false;

	}

	@Override
	protected void onPause() {
		super.onPause();
		isPaused = true;
	}

	@Override
	public void onPositiveBtnClick(DialogFragment fragment) {
		dismissFragmentDialog(fragment);
	}

	@Override
	public void onNeutralBtnCLick(DialogFragment fragment) {
		dismissFragmentDialog(fragment);
	}

	@Override
	public void onNegativeBtnClick(DialogFragment fragment) {
		dismissFragmentDialog(fragment);
	}

	private void dismissFragmentDialog(DialogFragment fragment){
		popupDialogFragment.setButtons(2);

		popupItem.setPositiveBtnId(R.string.ok);
		popupItem.setNegativeBtnId(R.string.cancel);
		fragment.setCancelable(true);
		fragment.dismiss();
	}

	public void showKeyBoard(EditText editText){
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
	}

	public void hideKeyBoard(View editText){
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}

	protected void showToast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	protected void showToast(int msgId) {
		Toast.makeText(this, msgId, Toast.LENGTH_SHORT).show();
	}

	// Single button no callback dialogs
	protected void showSinglePopupDialog(int titleId, int messageId) {
		showPopupDialog(titleId, messageId, INFO_POPUP_TAG);
		popupDialogFragment.setButtons(1);
	}

	protected void showSinglePopupDialog(String title, String message) {
		showPopupDialog(title, message, INFO_POPUP_TAG);
		popupDialogFragment.setButtons(1);
	}

	protected void showSinglePopupDialog(int titleId, String message) {
		showPopupDialog(titleId, message, INFO_POPUP_TAG);
		popupDialogFragment.setButtons(1);
	}

	protected void showSinglePopupDialog(String message) {
		showPopupDialog(message, INFO_POPUP_TAG);
		popupDialogFragment.setButtons(1);
	}

	protected void showSinglePopupDialog(int messageId) {
		showPopupDialog(messageId, INFO_POPUP_TAG);
		popupDialogFragment.setButtons(1);
	}

	// Default Dialogs
	protected void showPopupDialog(int titleId, int messageId, String tag) {
		popupItem.setTitle(titleId);
		popupItem.setMessage(messageId);
		updatePopupAndShow(tag);
	}

	protected void showPopupDialog(int titleId, String messageId, String tag) {
		popupItem.setTitle(titleId);
		popupItem.setMessage(messageId);
		updatePopupAndShow(tag);
	}

	protected void showPopupDialog(String title, String message, String tag) {
		popupItem.setTitle(title);
		popupItem.setMessage(message);
		updatePopupAndShow(tag);
	}

	protected void showPopupDialog(int titleId, String tag) {
		popupItem.setTitle(titleId);
		popupItem.setMessage(StaticData.SYMBOL_EMPTY);
		updatePopupAndShow(tag);
	}

	protected void showPopupDialog(String title, String tag) {
		popupItem.setTitle(title);
		popupItem.setMessage(StaticData.SYMBOL_EMPTY);
		updatePopupAndShow(tag);
	}

	private void updatePopupAndShow(String tag){
		popupDialogFragment.updatePopupItem(popupItem);
		popupDialogFragment.show(getSupportFragmentManager(), tag);
	}

	// Progress Dialogs
	protected void showPopupProgressDialog(String title) {
		popupProgressItem.setTitle(title);
		popupProgressItem.setMessage(StaticData.SYMBOL_EMPTY);
		updateProgressAndShow();
	}

	protected void showPopupProgressDialog(String title, String message) {
		popupProgressItem.setTitle(title);
		popupProgressItem.setMessage(message);
		updateProgressAndShow();
	}

	protected void showPopupProgressDialog(int titleId) {
		popupProgressItem.setTitle(titleId);
		popupProgressItem.setMessage(StaticData.SYMBOL_EMPTY);
		updateProgressAndShow();
	}

	protected void showPopupHardProgressDialog(int titleId) {
		popupProgressItem.setTitle(titleId);
		popupProgressItem.setMessage(StaticData.SYMBOL_EMPTY);
		popupProgressDialogFragment.setNotCancelable();
		updateProgressAndShow();
	}

	protected void showPopupProgressDialog(int titleId, int messageId) {
		popupProgressItem.setTitle(titleId);
		popupProgressItem.setMessage(messageId);
		updateProgressAndShow();
	}

	private void updateProgressAndShow(){
		popupProgressDialogFragment.updatePopupItem(popupProgressItem);
		popupProgressDialogFragment.show(getSupportFragmentManager(), PROGRESS_TAG);
	}

	protected void dismissFragmentDialog() {
		popupDialogFragment.dismiss();
	}

	protected void dismissProgressDialog() {
		popupProgressDialogFragment.dismiss();
	}

	public void dismissAllPopups() {
		for (PopupDialogFragment fragment : popupManager) {
			fragment.dismiss();
		}
	}

	protected String getTextFromField(EditText editText) {
		return editText.getText().toString().trim();
	}

	protected Context getContext() {
		return context;
	}
}
