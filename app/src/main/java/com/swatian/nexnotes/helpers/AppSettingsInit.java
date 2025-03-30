package com.swatian.nexnotes.helpers;

import android.content.Context;
import com.swatian.nexnotes.datastore.api.AppSettingsApi;
import com.swatian.nexnotes.datastore.api.BaseApi;
import com.swatian.nexnotes.datastore.models.AppSettings;

/**
 * @author mmarif
 */
public class AppSettingsInit {

	public static String APP_LOCALE_KEY = "app_locale";
	public static String APP_LOCALE_KEY_DEFAULT = "1|en";
	public static String APP_BIOMETRIC_KEY = "app_biometric";
	public static String APP_BIOMETRIC_DEFAULT = "false";
	public static String APP_BIOMETRIC_LIFE_CYCLE_KEY = "app_biometric_life_cycle";
	public static String APP_BIOMETRIC_LIFE_CYCLE_DEFAULT = "false";
	public static String APP_MD_MODE_KEY = "app_default_md_mode";
	public static String APP_MD_MODE_DEFAULT = "true";

	public static void initDefaultSettings(Context ctx) {

		AppSettingsApi appSettingsApi = BaseApi.getInstance(ctx, AppSettingsApi.class);
		assert appSettingsApi != null;

		if (appSettingsApi.fetchSettingCountByKey(APP_LOCALE_KEY) == 0) {
			appSettingsApi.insertNewSetting(
					APP_LOCALE_KEY, APP_LOCALE_KEY_DEFAULT, APP_LOCALE_KEY_DEFAULT);
		}

		if (appSettingsApi.fetchSettingCountByKey(APP_BIOMETRIC_KEY) == 0) {
			appSettingsApi.insertNewSetting(
					APP_BIOMETRIC_KEY, APP_BIOMETRIC_DEFAULT, APP_BIOMETRIC_DEFAULT);
		}
		if (appSettingsApi.fetchSettingCountByKey(APP_BIOMETRIC_LIFE_CYCLE_KEY) == 0) {
			appSettingsApi.insertNewSetting(
					APP_BIOMETRIC_LIFE_CYCLE_KEY,
					APP_BIOMETRIC_LIFE_CYCLE_DEFAULT,
					APP_BIOMETRIC_LIFE_CYCLE_DEFAULT);
		}

		if (appSettingsApi.fetchSettingCountByKey(APP_MD_MODE_KEY) == 0) {
			appSettingsApi.insertNewSetting(
					APP_MD_MODE_KEY, APP_MD_MODE_DEFAULT, APP_MD_MODE_DEFAULT);
		}
	}

	public static String getSettingsValue(Context ctx, String key) {

		AppSettingsApi appSettingsApi = BaseApi.getInstance(ctx, AppSettingsApi.class);
		assert appSettingsApi != null;
		AppSettings appSettings = appSettingsApi.fetchSettingByKey(key);
		return appSettings.getSettingValue();
	}

	public static void updateSettingsValue(Context ctx, String val, String key) {

		AppSettingsApi appSettingsApi = BaseApi.getInstance(ctx, AppSettingsApi.class);
		assert appSettingsApi != null;
		appSettingsApi.updateSettingValueByKey(val, key);
	}
}
