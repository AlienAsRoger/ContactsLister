package com.developer4droid.contactslister.ui.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import com.developer4droid.contactslister.R;
import com.developer4droid.contactslister.backend.entity.ContactItem;
import com.developer4droid.contactslister.backend.entity.FBFriendGroupsResponse;
import com.developer4droid.contactslister.backend.entity.FBFriendsListResponse;
import com.developer4droid.contactslister.backend.entity.FBGroupData;
import com.developer4droid.contactslister.backend.interfaces.AbstractUpdateListener;
import com.developer4droid.contactslister.backend.interfaces.ContactItemGetFace;
import com.developer4droid.contactslister.backend.tasks.GetEmailsFromContactsTask;
import com.developer4droid.contactslister.backend.tasks.JsonFromStringTask;
import com.developer4droid.contactslister.db.QueryParams;
import com.developer4droid.contactslister.statics.AppData;
import com.developer4droid.contactslister.statics.StaticData;
import com.developer4droid.contactslister.ui.adapters.ContactsAdapter;
import com.developer4droid.contactslister.ui.adapters.GroupsAdapter;
import com.facebook.android.*;

import java.util.ArrayList;
import java.util.List;

public class ContactsListActivity extends BaseFragmentActivity implements View.OnClickListener {

	private static final String TAG = "ContactsListActivity";
	private static final long FACEBOOK_DELAY = 200;
	private Facebook facebook;
	private Handler handler;
	private ListView listView;
	private ContactsAdapter contactsAdapter;
	private GroupsAdapter groupsAdapter;



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contacts_list_screen);

		handler = new Handler();
		listView = (ListView) findViewById(R.id.listView);

		findViewById(R.id.getContactsBtn).setOnClickListener(this);
		findViewById(R.id.listFriendsBtn).setOnClickListener(this);
		findViewById(R.id.listGroupsBtn).setOnClickListener(this);

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
		int id = view.getId();
		if (id == R.id.getContactsBtn) {

//			getContactEmails(this);
			QueryParams params = new QueryParams();
			params.setUri(ContactsContract.Contacts.CONTENT_URI);

			new GetEmailsFromContactsTask(new DbUpdateListener(), params, new ArrayList<ContactItem>()).executeTask();
		} else if (id == R.id.listFriendsBtn) {
			Bundle params = new Bundle();
			params.putString("fields", "name, picture, location, groups, list_type");
			Utility.mAsyncRunner.request("me/friends", params,
			new FriendsRequestListener());
		} else if (id == R.id.listGroupsBtn) {
			Bundle params1 = new Bundle();
			params1.putString("fields", "name, list_type");
			Utility.mAsyncRunner.request("me/friendlists", params1,
			new FriendsListRequestListener());
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
					new JsonFromStringTask<FBFriendsListResponse>(new FriendsParseUpdateListener(), FBFriendsListResponse.class)
							.executeTask(response);
				}
			});

		}

		public void onFacebookError(FacebookError error) {
			Toast.makeText(getApplicationContext(), "Facebook Error: " + error.getMessage(),
					Toast.LENGTH_SHORT).show();
		}
	}

	private class FriendsParseUpdateListener extends AbstractUpdateListener<FBFriendsListResponse> {

		public FriendsParseUpdateListener() {
			super(ContactsListActivity.this, null);
		}

		@Override
		public void updateData(FBFriendsListResponse returnedItem) {
			super.updateData(returnedItem);
			if(returnedItem.getError() == null){
				List<ContactItem> itemList = new ArrayList<ContactItem>();
				itemList.addAll(returnedItem.getData());
				updateList(itemList);
			} else {
				Log.d(TAG, "FriendsParseUpdateListener error: code = " + returnedItem.getError().getCode()
						+ " message = "  +returnedItem.getError().getMessage());
				showToast(returnedItem.getError().getMessage());
			}

		}
	}


	private void updateList(List<ContactItem> itemsList){
		if (contactsAdapter == null) {
			contactsAdapter = new ContactsAdapter(ContactsListActivity.this, itemsList);
		} else {
			contactsAdapter.setItemsList(itemsList);
		}
		listView.setAdapter(contactsAdapter);
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


	/*
		 * callback after friends are fetched via me/friends or fql query.
		 */
	public class FriendsListRequestListener extends BaseRequestListener {

		@Override
		public void onComplete(final String response, final Object state) {
			Log.d("TEST", "response = " + response);
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					new JsonFromStringTask<FBFriendGroupsResponse>(new GroupsParseUpdateListener(), FBFriendGroupsResponse.class)
							.executeTask(response);
				}
			});
		}

		public void onFacebookError(FacebookError error) {
			Toast.makeText(getApplicationContext(), "Facebook Error: " + error.getMessage(),
					Toast.LENGTH_SHORT).show();
		}
	}


	private class GroupsParseUpdateListener extends AbstractUpdateListener<FBFriendGroupsResponse> {

		public GroupsParseUpdateListener() {
			super(ContactsListActivity.this, null);
		}

		@Override
		public void updateData(FBFriendGroupsResponse returnedItem) {
			super.updateData(returnedItem);
			if(returnedItem.getError() == null){
				List<FBGroupData> itemsList = new ArrayList<FBGroupData>();
				itemsList.addAll(returnedItem.getData());

				// starts count
				if (groupsAdapter == null) {
					groupsAdapter = new GroupsAdapter(ContactsListActivity.this, itemsList);
				} else {
					groupsAdapter.setItemsList(itemsList);
				}
				listView.setAdapter(groupsAdapter);

			} else {
				Log.d(TAG, "GroupsParseUpdateListener error: code = " + returnedItem.getError().getCode()
						+ " message = "  +returnedItem.getError().getMessage());
				showToast(returnedItem.getError().getMessage());
			}

		}
	}
}
