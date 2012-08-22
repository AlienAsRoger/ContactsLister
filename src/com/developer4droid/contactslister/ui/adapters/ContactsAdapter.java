package com.developer4droid.contactslister.ui.adapters;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.developer4droid.contactslister.R;
import com.developer4droid.contactslister.backend.entity.ContactItem;
import com.developer4droid.contactslister.backend.image_load.ProgressImageView;

import java.util.List;

/**
 * ContactsAdapter class
 *
 * @author alien_roger
 * @created at: 22.08.12 5:33
 */
public class ContactsAdapter extends ItemsAdapter<ContactItem> {

	public ContactsAdapter(Context context, List<ContactItem> itemList) {
		super(context, itemList);
	}

	@Override
	protected View createView(ViewGroup parent) {

		View view = inflater.inflate(R.layout.contact_list_item, parent, false);
		ViewHolder holder = new ViewHolder();
		holder.name = (TextView) view.findViewById(R.id.contactName);
		holder.progressImage = new ProgressImageView();
		holder.progressImage.placeholder = ((BitmapDrawable) context.getResources().getDrawable(R.drawable.ic_launcher)).getBitmap();
		holder.progressImage.noImage = ((BitmapDrawable) context.getResources().getDrawable(R.drawable.ic_launcher)).getBitmap();
		holder.progressImage.progress = (ProgressBar) view.findViewById(R.id.imageProgressBar);
		holder.progressImage.imageView = (ImageView) view.findViewById(R.id.icon);

		view.setTag(holder);
		return view;
	}

	@Override
	protected void bindView(ContactItem item, int pos, View convertView) {
		ViewHolder holder = (ViewHolder) convertView.getTag();
		holder.name.setText(item.getName());
		imageLoader.download(item.getIconUrl(), holder.progressImage);
	}

	private static class ViewHolder{
		public ProgressImageView progressImage;
		public TextView name;
	}
}
