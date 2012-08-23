package com.developer4droid.contactslister.backend.entity;

import java.util.List;

/**
 * FBFriendGroupsResponse class
 *
 * @author alien_roger
 * @created at: 23.08.12 6:40
 */
public class FBFriendGroupsResponse extends FBErrorResponse{
/*
{"data":[
	{
		"name":"\u0422\u041f\u0423",
		"list_type":"education",
		"id":"282213608492189"
	},
	{
		"name":"Coworers",
		"list_type":"user_created",
		"id":"185135934866624"
	}
], "paging":{
	"next":"https:\/\/graph.facebook.com\/100001106562606\/friendlists?fields=name,list_type&format=json&access_token=BAABf6gW4HWIBAIK8yVHIHIYu6aVF6nxmFOVu3JWfWGSGep7lCFdUjg1d74v6qeEHOa7tPDu67TzXtGk4s9PaZCsORwrxjZADWTbXpsV6EIj9SDgewGZBafXcADA2rNaLK6UNU55oAZDZD&limit=5000&offset=5000&__after_id=185135934866624"
}}
	 */
	private List<FBGroupData> data;
	private FBPaging paging;

	public List<FBGroupData> getData() {
		return data;
	}

	public FBPaging getPaging() {
		return paging;
	}

}
