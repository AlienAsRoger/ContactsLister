package com.developer4droid.contactslister.ui.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.developer4droid.contactslister.R;
import com.developer4droid.contactslister.backend.entity.FBGroupData;
import com.developer4droid.contactslister.statics.StaticData;

import java.util.List;

/**
 * ContactsAdapter class
 *
 * @author alien_roger
 * @created at: 22.08.12 5:33
 */
public class GroupsAdapter extends ItemsAdapter<FBGroupData> {

	private static final String ZERO_CNT = "0";

	public GroupsAdapter(Context context, List<FBGroupData> itemList) {
		super(context, itemList);
	}

	@Override
	protected View createView(ViewGroup parent) {

		View view = inflater.inflate(R.layout.group_list_item, parent, false);
		ViewHolder holder = new ViewHolder();
		holder.name = (TextView) view.findViewById(R.id.groupNameTxt);
		holder.count = (TextView) view.findViewById(R.id.groupCntTxt);
		holder.countProgress = view.findViewById(R.id.countProgress);

		view.setTag(holder);
		return view;
	}

	@Override
	protected void bindView(FBGroupData item, int pos, View convertView) {
		ViewHolder holder = (ViewHolder) convertView.getTag();
		holder.name.setText(item.getName());
		int count = item.getCount();
		if (count == 0) {
			holder.countProgress.setVisibility(View.VISIBLE);
			holder.count.setText(StaticData.SYMBOL_EMPTY);
		} else if(count == -1) {
			holder.countProgress.setVisibility(View.INVISIBLE);
			holder.count.setText(ZERO_CNT);
		} else {
			holder.countProgress.setVisibility(View.INVISIBLE);
			holder.count.setText(String.valueOf(count));
		}
	}

	public void updateGroupsCnt(String id, int size) {
		for (FBGroupData data : itemsList) {
			if (data.getId().equals(id)) {
				size = size == 0 ? -1 : size;
				data.setCount(size);
				break;
			}
		}
		notifyDataSetChanged();
	}

	public int getTotalCount() {
		int pplCnt = 0;
		for (FBGroupData data : itemsList) {
			pplCnt += data.getCount();
		}

		return pplCnt;
	}

	private static class ViewHolder {
		public TextView name;
		public TextView count;
		public View countProgress;
	}
}
