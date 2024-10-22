package com.swatian.nexnotes.activities;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import com.swatian.nexnotes.R;
import com.swatian.nexnotes.databinding.ActivityBiometricLockBinding;
import com.swatian.nexnotes.helpers.AppSettingsInit;
import java.util.concurrent.Executor;

/**
 * @author mmarif
 */
public class BiometricLockActivity extends AppCompatActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		ActivityBiometricLockBinding activityUnlockBinding =
				ActivityBiometricLockBinding.inflate(getLayoutInflater());
		setContentView(activityUnlockBinding.getRoot());
	}

	public void onResume() {
		super.onResume();

		Executor executor = ContextCompat.getMainExecutor(this);

		BiometricPrompt biometricPrompt =
				new BiometricPrompt(
						this,
						executor,
						new BiometricPrompt.AuthenticationCallback() {

							@Override
							public void onAuthenticationError(
									int errorCode, @NonNull CharSequence errString) {

								super.onAuthenticationError(errorCode, errString);

								MainActivity.closeActivity = true;
								finish();
							}

							// Authentication succeeded, continue to app
							@Override
							public void onAuthenticationSucceeded(
									@NonNull BiometricPrompt.AuthenticationResult result) {
								super.onAuthenticationSucceeded(result);
								AppSettingsInit.updateSettingsValue(
										getApplicationContext(),
										"true",
										AppSettingsInit.APP_BIOMETRIC_LIFE_CYCLE_KEY);
								finish();
							}

							// Authentication failed, close the app
							@Override
							public void onAuthenticationFailed() {
								super.onAuthenticationFailed();
							}
						});

		BiometricPrompt.PromptInfo biometricPromptBuilder =
				new BiometricPrompt.PromptInfo.Builder()
						.setTitle(getString(R.string.biometric_auth_title))
						.setSubtitle(getString(R.string.biometric_auth_sub_title))
						.setNegativeButtonText(getString(R.string.cancel))
						.build();

		biometricPrompt.authenticate(biometricPromptBuilder);
	}
}
