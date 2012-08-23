package com.developer4droid.contactslister.backend.entity;

/**
 * FBError class
 *
 * @author alien_roger
 * @created at: 23.08.12 6:08
 */
public class FBError {
/*
    "message": "Unknown path components: \/friendslist",
    "type": "OAuthException",
    "code": 2500
 */
	private String message;
	private String type;
	private int code;

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	public String getType() {
		return type;
	}
}
