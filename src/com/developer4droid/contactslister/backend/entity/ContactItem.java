package com.developer4droid.contactslister.backend.entity;

/**
 * FBContactItem class
 *
 * @author alien_roger
 * @created at: 21.08.12 22:41
 */
public class ContactItem {

	private String name;
	private String iconUrl = "http://upload.wikimedia.org/wikipedia/commons/thumb/d/d7/Android_robot.svg/100px-Android_robot.svg.png";
	private String email;

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}


}
