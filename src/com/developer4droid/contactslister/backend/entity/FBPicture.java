package com.developer4droid.contactslister.backend.entity;

/**
 * FBPicture class
 *
 * @author alien_roger
 * @created at: 21.08.12 22:44
 */
public class FBPicture {
	/*
	"data":{
				"url":"http:\/\/profile.ak.fbcdn.net\/hprofile-ak-ash2\/49866_588319566_6292_q.jpg",
				"is_silhouette":false
			}
		}
	 */
	private FBPictureData data;

	public FBPictureData getData() {
		return data;
	}
}
