package com.developer4droid.contactslister.backend.entity;

import java.util.List;

/**
 * FBFriendsListResponse class
 *
 * @author alien_roger
 * @created at: 21.08.12 22:35
 */
public class FBFriendsListResponse extends FBErrorResponse {
/*
	{"data":[
	{
		"name":"Vladislav Karmakov",
		"id":"588319566",
		"picture":{
			"data":{
				"url":"http:\/\/profile.ak.fbcdn.net\/hprofile-ak-ash2\/49866_588319566_6292_q.jpg",
				"is_silhouette":false
			}
		}
	},
	{
		"name":"Aleksey  Khazov",
		"id":"100003910508529",
		"picture":{
			"data":{
				"url":"http:\/\/profile.ak.fbcdn.net\/hprofile-ak-ash2\/276077_100003910508529_947488552_q.jpg",
				"is_silhouette":false
			}
		}
	}
], "paging":{
	"next":"https:\/\/graph.facebook.com\/100001106562606\/friends?fields=name,picture,location&format=json&access_token=BAABf6gW4HWIBAKZBkGohMJ760PgO1hXzo5bIACQlN5EwvaBjeMPZACPS1ZBedFFhfZBsLTmPdOECdPTPpsu0wPcBrhorwlZCLTatK2BCLmuzsAwKv0uMSdf9rf966HVHy3vMxYibqVAZDZD&limit=5000&offset=5000&__after_id=100003910508529"
}}
	 */

	private List<FBContactItem> data;
	private FBPaging paging;

	public List<FBContactItem> getData() {
		return data;
	}

	public FBPaging getPaging() {
		return paging;
	}
}
