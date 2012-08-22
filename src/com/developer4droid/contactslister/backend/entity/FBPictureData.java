package com.developer4droid.contactslister.backend.entity;

/**
 * FBPictureData class
 *
 * @author alien_roger
 * @created at: 21.08.12 22:44
 */
public class FBPictureData {
	/*
		"url":"http:\/\/profile.ak.fbcdn.net\/hprofile-ak-ash2\/49866_588319566_6292_q.jpg",
		"is_silhouette":false
	 */
	private String url;
	private boolean is_silhouette; // is !real photo

	public boolean isIs_silhouette() {
		return is_silhouette;
	}

	public String getUrl() {
		return url;
	}
}
