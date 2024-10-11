package com.swatian.nexnotes.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.swatian.nexnotes.helpers.AppSettingsInit;
import com.swatian.nexnotes.helpers.Utils;

/**
 * @author mmarif
 */
public abstract class BaseActivity extends AppCompatActivity {

	protected Context ctx = this;
	protected Context appCtx;

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		this.appCtx = getApplicationContext();

		AppSettingsInit.initDefaultSettings(appCtx);

		AppSettingsInit.updateSettingsValue(
				getApplicationContext(), "false", AppSettingsInit.APP_BIOMETRIC_LIFE_CYCLE_KEY);

		applyLocale();
	}

	private void applyLocale() {

		String savedLocale =
				getSharedPreferences("nexnotes_preferences", MODE_PRIVATE)
						.getString("app_locale", "en");
		Utils.setLocale(this, savedLocale);
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(Utils.setLocale(base, getCurrentLocaleFromPreferences(base)));
	}

	private String getCurrentLocaleFromPreferences(Context context) {
		return context.getSharedPreferences("nexnotes_preferences", MODE_PRIVATE)
				.getString("app_locale", "en");
	}

	public void onResume() {
		super.onResume();

		if (Boolean.parseBoolean(
						AppSettingsInit.getSettingsValue(ctx, AppSettingsInit.APP_BIOMETRIC_KEY))
				&& !Boolean.parseBoolean(
						AppSettingsInit.getSettingsValue(
								ctx, AppSettingsInit.APP_BIOMETRIC_LIFE_CYCLE_KEY))) {

			Intent unlockIntent = new Intent(ctx, BiometricLockActivity.class);
			ctx.startActivity(unlockIntent);
		}
	}
}
