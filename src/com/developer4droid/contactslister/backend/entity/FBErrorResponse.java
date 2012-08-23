package com.developer4droid.contactslister.backend.entity;

/**
 * FBErrorResponse class
 *
 * @author alien_roger
 * @created at: 21.08.12 22:35
 */
public class FBErrorResponse {
	/*
	{
  "error": {
    "message": "Unknown path components: \/friendslist",
    "type": "OAuthException",
    "code": 2500
  }
}
	 */
	private FBError error;

	public FBError getError() {
		return error;
	}
}
