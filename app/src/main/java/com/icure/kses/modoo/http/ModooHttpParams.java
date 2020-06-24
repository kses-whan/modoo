package com.icure.kses.modoo.http;

import android.graphics.Bitmap;

import java.io.File;

public interface ModooHttpParams {

	void pushDataKey(String key, String value);
	void pushFileKey(String key, File value);
	void pushImageKey(String key, Bitmap value);
	void addJsonBody(String jsonBody);
	void addAuthorizationHeader(String authHeader);
}
