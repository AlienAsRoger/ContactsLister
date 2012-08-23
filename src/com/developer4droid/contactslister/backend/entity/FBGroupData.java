package com.developer4droid.contactslister.backend.entity;

/**
 * FBGroupData class
 *
 * @author alien_roger
 * @created at: 23.08.12 6:41
 */
public class FBGroupData extends FBCommonData {
/*
{
	"name":"\u0422\u041f\u0423",
	"list_type":"education",
	"id":"282213608492189"
}
 */
	private String list_type;
	private int count;

	public String getList_type() {
		return list_type;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
}
