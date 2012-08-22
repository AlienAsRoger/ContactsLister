package com.developer4droid.contactslister.ui.activities;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import com.developer4droid.contactslister.R;
import com.developer4droid.contactslister.backend.entity.ContactItem;
import com.developer4droid.contactslister.backend.entity.FBFriendsListResponse;
import com.developer4droid.contactslister.backend.interfaces.AbstractUpdateListener;
import com.developer4droid.contactslister.backend.interfaces.ContactItemGetFace;
import com.developer4droid.contactslister.backend.tasks.GetEmailsFromContactsTask;
import com.developer4droid.contactslister.backend.tasks.JsonFromStringTask;
import com.developer4droid.contactslister.db.QueryParams;
import com.developer4droid.contactslister.statics.AppData;
import com.developer4droid.contactslister.statics.StaticData;
import com.developer4droid.contactslister.ui.adapters.ContactsAdapter;
import com.facebook.android.*;

import java.util.ArrayList;
import java.util.List;

import static android.provider.ContactsContract.CommonDataKinds.Email;
import static android.provider.ContactsContract.CommonDataKinds.Phone;

public class ContactsListActivity extends BaseFragmentActivity implements View.OnClickListener {

	private static final String TAG = "ContactsListActivity";
	private static final long FACEBOOK_DELAY = 200;
	private Facebook facebook;
	private Handler handler;
	private ListView listView;
	private ContactsAdapter contactsAdapter;



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contacts_list_screen);

		handler = new Handler();
		listView = (ListView) findViewById(R.id.listView);

		findViewById(R.id.getContactsBtn).setOnClickListener(this);

		LoginButton facebookLoginButton = (LoginButton) findViewById(R.id.fb_connect);
		facebook = new Facebook(AppData.FACEBOOK_APP_ID);
		SessionStore.restore(facebook, this);

		SessionEvents.dropAuthListeners();
		SessionEvents.addAuthListener(new SampleAuthListener());
		SessionEvents.dropLogoutListeners();
		SessionEvents.addLogoutListener(new SampleLogoutListener());

		Utility.mAsyncRunner = new AsyncFacebookRunner(facebook);

		facebookLoginButton.init(this, facebook);
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == Facebook.DEFAULT_AUTH_ACTIVITY_CODE) {
				handler.postDelayed(new DelayedCallback(data, requestCode, resultCode), FACEBOOK_DELAY);
			}
		}
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.getContactsBtn) {

//			getContactEmails(this);
			QueryParams params = new QueryParams();
			params.setUri(ContactsContract.Contacts.CONTENT_URI);

			new GetEmailsFromContactsTask(new DbUpdateListener(), params, new ArrayList<ContactItem>()).executeTask();
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

			Bundle params = new Bundle();
			params.putString("fields", "name, picture, location");
			Utility.mAsyncRunner.request("me/friends", params,
					new FriendsRequestListener());

		}

		@Override
		public void onAuthFail(String error) {
			showToast(getString(R.string.login_failed) + StaticData.SYMBOL_SPACE + error);
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

	/*
	 * callback after friends are fetched via me/friends or fql query.
	 */
	public class FriendsRequestListener extends BaseRequestListener {

		@Override
		public void onComplete(final String response, final Object state) {
			Log.d("TEST", "response = " + response);
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					new JsonFromStringTask<FBFriendsListResponse>(new JsonParseUpdateListener(), FBFriendsListResponse.class)
							.executeTask(response);
				}
			});

		}

		public void onFacebookError(FacebookError error) {
			Toast.makeText(getApplicationContext(), "Facebook Error: " + error.getMessage(),
					Toast.LENGTH_SHORT).show();
		}
	}

	private class JsonParseUpdateListener extends AbstractUpdateListener<FBFriendsListResponse> {

		public JsonParseUpdateListener() {
			super(ContactsListActivity.this, null);
		}

		@Override
		public void updateData(FBFriendsListResponse returnedItem) {
			super.updateData(returnedItem);
			List<ContactItem> itemList = new ArrayList<ContactItem>();
			itemList.addAll(returnedItem.getData());
			updateList(itemList);

		}
	}

	public static void getContactEmails(Context context) {
		int emailType = Email.TYPE_WORK;
		String contactName = null;


		ContentResolver cr = context.getContentResolver();
		Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
				null, null, null);
		if (cur.getCount() > 0) {
			while (cur.moveToNext()) {
				String id = cur.getString(cur
						.getColumnIndex(BaseColumns._ID));
				contactName = cur
						.getString(cur
								.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				// Log.i(TAG,"....contact name....." +
				// contactName);

				cr.query(
						Phone.CONTENT_URI,
						null,
						Phone.CONTACT_ID
								+ " = ?", new String[] { id }, null);

				Cursor emailsCursor = cr.query(Email.CONTENT_URI, null,
						Email.CONTACT_ID + " = " + id, null, null);
				while (emailsCursor.moveToNext()) {
					String contactEmail = emailsCursor.getString(emailsCursor
							.getColumnIndex(Email.DATA));
					Log.d(TAG, "Contact Name = " + contactName
							+ " contact mail = " + contactEmail
							+ " email type = " + emailType
					);
					emailType = emailsCursor.getInt(emailsCursor
							.getColumnIndex(Phone.TYPE));


				}
				emailsCursor.close();

			}
		}// end of contact name cursor
		cur.close();


	}

	private void updateList(List<ContactItem> itemsList){
		if (contactsAdapter == null) {
			contactsAdapter = new ContactsAdapter(ContactsListActivity.this, itemsList);

			listView.setAdapter(contactsAdapter);
		} else {
			contactsAdapter.setItemsList(itemsList);
		}
	}

	private class DbUpdateListener extends AbstractUpdateListener<Cursor> implements ContactItemGetFace<ContactItem, Cursor> {
		public DbUpdateListener() {
			super(ContactsListActivity.this, null);
		}

		@Override
		public void updateContacts(List<ContactItem> itemsList) {
			updateList(itemsList);
		}
	}


}
