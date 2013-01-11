package com.bethzur.gcm4j.auth;

public class ApiKeyAuthProvider {
	private String api_key;

	public ApiKeyAuthProvider(String api_key) {
		this.api_key = api_key;
	}

	public String getKey() {
		return api_key;
	}
}
