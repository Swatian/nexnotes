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

		String[] locale =
				AppSettingsInit.getSettingsValue(ctx, AppSettingsInit.APP_LOCALE_KEY).split("\\|");

		if (locale[0].equals("0")) {
			Utils.setAppLocale(
					ctx.getResources().getConfiguration().getLocales().get(0).getLanguage());
		} else {
			Utils.setAppLocale(locale[1]);
		}
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
