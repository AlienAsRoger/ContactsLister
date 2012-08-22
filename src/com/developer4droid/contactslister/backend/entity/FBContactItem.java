package com.developer4droid.contactslister.backend.entity;

/**
 * FBContactItem class
 *
 * @author alien_roger
 * @created at: 21.08.12 22:41
 */
public class FBContactItem extends ContactItem {
	/*
		"name":"Vladislav Karmakov",
		"id":"588319566",
		"picture":{
			"data":{
				"url":"http:\/\/profile.ak.fbcdn.net\/hprofile-ak-ash2\/49866_588319566_6292_q.jpg",
				"is_silhouette":false
			}
		}
	 */

	private String id;
	private FBPicture picture;

	public String getId() {
		return id;
	}

	public FBPicture getPicture() {
		return picture;
	}

	@Override
	public String getIconUrl() {
		return picture.getData().getUrl();
	}
}
