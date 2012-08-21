package com.developer4droid.contactslister.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import com.developer4droid.contactslister.R;
import com.developer4droid.contactslister.statics.AppData;
import com.developer4droid.contactslister.statics.StaticData;
import com.facebook.android.Facebook;
import com.facebook.android.LoginButton;
import com.facebook.android.SessionEvents;
import com.facebook.android.SessionStore;

public class ContactsListActivity extends BaseFragmentActivity {

	private static final long FACEBOOK_DELAY = 200;
	private Facebook facebook;
	private Handler handler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contacts_list_screen);

		handler = new Handler();

		LoginButton facebookLoginButton = (LoginButton) findViewById(R.id.fb_connect);

		facebook = new Facebook(AppData.FACEBOOK_APP_ID);
		SessionStore.restore(facebook, this);

		SessionEvents.dropAuthListeners();
		SessionEvents.addAuthListener(new SampleAuthListener());
		SessionEvents.dropLogoutListeners();
		SessionEvents.addLogoutListener(new SampleLogoutListener());
		facebookLoginButton.init(this, facebook);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK ){
			if(requestCode == Facebook.DEFAULT_AUTH_ACTIVITY_CODE){
				handler.postDelayed(new DelayedCallback(data, requestCode, resultCode), FACEBOOK_DELAY);
			}
		}
	}

	/**
	 * Prevent earlier launch of task, as it finish right after onPause callback
	 */
	private class DelayedCallback implements Runnable {

		private Intent data;
		private int resultCode;
		private int requestCode;

		private DelayedCallback(Intent data, int requestCode, int resultCode) {
			this.data = data;
			this.requestCode = requestCode;
			this.resultCode = resultCode;
		}

		@Override
		public void run() {
			handler.removeCallbacks(this);
			facebook.authorizeCallback(requestCode, resultCode, data);
		}
	}

	public class SampleAuthListener implements SessionEvents.AuthListener {
		@Override
		public void onAuthSucceed() {


		}

		@Override
		public void onAuthFail(String error) {
			showToast(getString(R.string.login_failed)+ StaticData.SYMBOL_SPACE + error);
		}
	}

	public class SampleLogoutListener implements SessionEvents.LogoutListener {
		@Override
		public void onLogoutBegin() {
			showToast(R.string.loggin_out);
		}

		@Override
		public void onLogoutFinish() {
			showToast(R.string.you_logged_out);
		}
	}
}
