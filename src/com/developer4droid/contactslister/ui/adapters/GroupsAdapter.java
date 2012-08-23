package com.developer4droid.contactslister.ui.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.developer4droid.contactslister.R;
import com.developer4droid.contactslister.backend.entity.FBGroupData;

import java.util.List;

/**
 * ContactsAdapter class
 *
 * @author alien_roger
 * @created at: 22.08.12 5:33
 */
public class GroupsAdapter extends ItemsAdapter<FBGroupData> {

	public GroupsAdapter(Context context, List<FBGroupData> itemList) {
		super(context, itemList);
	}

	@Override
	protected View createView(ViewGroup parent) {

		View view = inflater.inflate(R.layout.group_list_item, parent, false);
		ViewHolder holder = new ViewHolder();
		holder.name = (TextView) view.findViewById(R.id.groupNameTxt);
		holder.count = (TextView) view.findViewById(R.id.groupCntTxt);

		view.setTag(holder);
		return view;
	}

	@Override
	protected void bindView(FBGroupData item, int pos, View convertView) {
		ViewHolder holder = (ViewHolder) convertView.getTag();
		holder.name.setText(item.getName());


	}

	private static class ViewHolder{
		public TextView name;
		public TextView count;
	}
}
