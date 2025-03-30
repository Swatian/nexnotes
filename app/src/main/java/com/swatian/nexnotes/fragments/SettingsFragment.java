package com.swatian.nexnotes.fragments;

import static android.content.Context.MODE_PRIVATE;
import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.biometric.BiometricManager;
import androidx.fragment.app.Fragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.swatian.nexnotes.R;
import com.swatian.nexnotes.databinding.FragmentSettingsBinding;
import com.swatian.nexnotes.datastore.api.BaseApi;
import com.swatian.nexnotes.datastore.api.NoteTopicsApi;
import com.swatian.nexnotes.datastore.api.NotesApi;
import com.swatian.nexnotes.helpers.AppSettingsInit;
import com.swatian.nexnotes.helpers.Snackbar;
import com.swatian.nexnotes.helpers.Utils;
import java.util.LinkedHashMap;
import java.util.Locale;

/**
 * @author mmarif
 */
public class SettingsFragment extends Fragment {

	private FragmentSettingsBinding binding;
	private NotesApi notesApi;
	private NoteTopicsApi noteTopicsApi;
	private static int langSelectedChoice;

	public View onCreateView(
			@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		binding = FragmentSettingsBinding.inflate(inflater, container, false);

		// about section
		binding.appVersion.setText(Utils.getAppVersion(requireContext()));

		binding.supportPatreonFrame.setOnClickListener(
				v11 ->
						Utils.openUrlInBrowser(
								requireContext(),
								requireActivity(),
								requireActivity().findViewById(R.id.nav_view),
								getResources().getString(R.string.link_patreon)));
		binding.crowdinFrame.setOnClickListener(
				v13 ->
						Utils.openUrlInBrowser(
								requireContext(),
								requireActivity(),
								requireActivity().findViewById(R.id.nav_view),
								getResources().getString(R.string.crowd_in_link)));
		binding.websiteFrame.setOnClickListener(
				v14 ->
						Utils.openUrlInBrowser(
								requireContext(),
								requireActivity(),
								requireActivity().findViewById(R.id.nav_view),
								getResources().getString(R.string.app_website_link)));
		binding.sourceCodeFrame.setOnClickListener(
				v15 ->
						Utils.openUrlInBrowser(
								requireContext(),
								requireActivity(),
								requireActivity().findViewById(R.id.nav_view),
								getResources().getString(R.string.source_code_link)));
		// about section

		// language selection dialog
		LinkedHashMap<String, String> lang = new LinkedHashMap<>();
		lang.put("sys", getString(R.string.system));
		for (String langCode : getResources().getStringArray(R.array.languages)) {
			lang.put(langCode, getLanguageDisplayName(langCode));
		}

		String[] locale =
				AppSettingsInit.getSettingsValue(requireContext(), AppSettingsInit.APP_LOCALE_KEY)
						.split("\\|");
		langSelectedChoice = Integer.parseInt(locale[0]);
		binding.languageSelected.setText(
				lang.get(lang.keySet().toArray(new String[0])[langSelectedChoice]));

		binding.languageSelectionFrame.setOnClickListener(
				view -> {
					MaterialAlertDialogBuilder materialAlertDialogBuilder =
							new MaterialAlertDialogBuilder(requireContext())
									.setTitle(R.string.settings_language_selector_dialog_title)
									.setCancelable(langSelectedChoice != -1)
									.setSingleChoiceItems(
											lang.values().toArray(new String[0]),
											langSelectedChoice,
											(dialogInterface, i) -> {
												String selectedLanguage =
														lang.keySet().toArray(new String[0])[i];
												AppSettingsInit.updateSettingsValue(
														requireContext(),
														i + "|" + selectedLanguage,
														AppSettingsInit.APP_LOCALE_KEY);

												/*if (selectedLanguage.equalsIgnoreCase("sys")) {
													selectedLanguage =
															requireContext()
																	.getResources()
																	.getConfiguration()
																	.getLocales()
																	.get(0)
																	.getLanguage();
												}*/

												String[] multiCodeLang =
														selectedLanguage.split("-");
												if (selectedLanguage.contains("-")) {
													selectedLanguage = multiCodeLang[0];
												}

												SharedPreferences prefs =
														requireContext()
																.getSharedPreferences(
																		"nexnotes_preferences",
																		MODE_PRIVATE);
												prefs.edit()
														.putString("app_locale", selectedLanguage)
														.apply();

												Utils.setLocale(requireContext(), selectedLanguage);
												dialogInterface.dismiss();
												requireActivity().recreate();
											});

					materialAlertDialogBuilder.create().show();
				});
		// language selection dialog

		// Markdown mode switcher
		binding.switchMdMode.setChecked(
				Boolean.parseBoolean(
						AppSettingsInit.getSettingsValue(
								requireContext(), AppSettingsInit.APP_MD_MODE_KEY)));

		binding.switchMdMode.setOnCheckedChangeListener(
				(buttonView, isChecked) -> {
					if (isChecked) {

						AppSettingsInit.updateSettingsValue(
								requireContext(), "true", AppSettingsInit.APP_MD_MODE_KEY);
					} else {

						AppSettingsInit.updateSettingsValue(
								requireContext(), "false", AppSettingsInit.APP_MD_MODE_KEY);
						Snackbar.info(
								requireActivity(),
								requireActivity().findViewById(R.id.nav_view),
								getString(R.string.settings_saved));
					}
				});

		binding.mdModeFrame.setOnClickListener(
				v -> binding.switchMdMode.setChecked(!binding.switchMdMode.isChecked()));
		// Markdown mode switcher

		// biometric switcher
		binding.switchBiometric.setChecked(
				Boolean.parseBoolean(
						AppSettingsInit.getSettingsValue(
								requireContext(), AppSettingsInit.APP_BIOMETRIC_KEY)));

		binding.switchBiometric.setOnCheckedChangeListener(
				(buttonView, isChecked) -> {
					if (isChecked) {

						BiometricManager biometricManager = BiometricManager.from(requireContext());
						KeyguardManager keyguardManager =
								(KeyguardManager)
										requireContext().getSystemService(Context.KEYGUARD_SERVICE);

						if (!keyguardManager.isDeviceSecure()) {

							switch (biometricManager.canAuthenticate(
									BIOMETRIC_STRONG | DEVICE_CREDENTIAL)) {
								case BiometricManager.BIOMETRIC_SUCCESS:
									AppSettingsInit.updateSettingsValue(
											requireContext(),
											"true",
											AppSettingsInit.APP_BIOMETRIC_KEY);
									Snackbar.info(
											requireActivity(),
											requireActivity().findViewById(R.id.nav_view),
											getString(R.string.settings_saved));
									break;
								case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
								case BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED:
								case BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED:
								case BiometricManager.BIOMETRIC_STATUS_UNKNOWN:
									AppSettingsInit.updateSettingsValue(
											requireContext(),
											"false",
											AppSettingsInit.APP_BIOMETRIC_KEY);
									binding.switchBiometric.setChecked(false);
									Snackbar.info(
											requireActivity(),
											requireActivity().findViewById(R.id.nav_view),
											getString(R.string.biometric_not_supported));
									break;
								case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
									AppSettingsInit.updateSettingsValue(
											requireContext(),
											"false",
											AppSettingsInit.APP_BIOMETRIC_KEY);
									binding.switchBiometric.setChecked(false);
									Snackbar.info(
											requireActivity(),
											requireActivity().findViewById(R.id.nav_view),
											getString(R.string.biometric_not_available));
									break;
								case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
									AppSettingsInit.updateSettingsValue(
											requireContext(),
											"false",
											AppSettingsInit.APP_BIOMETRIC_KEY);
									binding.switchBiometric.setChecked(false);
									Snackbar.info(
											requireActivity(),
											requireActivity().findViewById(R.id.nav_view),
											getString(R.string.enroll_biometric));
									break;
							}
						} else {

							AppSettingsInit.updateSettingsValue(
									requireContext(), "true", AppSettingsInit.APP_BIOMETRIC_KEY);
							Snackbar.info(
									requireActivity(),
									requireActivity().findViewById(R.id.nav_view),
									getString(R.string.settings_saved));
						}
					} else {

						AppSettingsInit.updateSettingsValue(
								requireContext(), "false", AppSettingsInit.APP_BIOMETRIC_KEY);
						Snackbar.info(
								requireActivity(),
								requireActivity().findViewById(R.id.nav_view),
								getString(R.string.settings_saved));
					}
				});

		binding.biometricFrame.setOnClickListener(
				v -> binding.switchBiometric.setChecked(!binding.switchBiometric.isChecked()));
		// biometric switcher

		// database - notes
		notesApi = BaseApi.getInstance(requireContext(), NotesApi.class);
		noteTopicsApi = BaseApi.getInstance(requireContext(), NoteTopicsApi.class);

		assert notesApi != null;
		binding.databaseNotesCount.setText(String.valueOf(notesApi.getCount()));

		binding.deleteNotes.setOnClickListener(
				deleteAllNotes -> {
					if (notesApi.getCount() > 0) {
						new MaterialAlertDialogBuilder(requireContext())
								.setMessage(R.string.delete_all_notes_dialog_message)
								.setPositiveButton(
										R.string.delete,
										(dialog, which) -> {
											deleteAllNotes();
											binding.databaseNotesCount.setText(
													String.valueOf(notesApi.getCount()));
											dialog.dismiss();
										})
								.setNeutralButton(R.string.cancel, null)
								.show();
					} else {
						Snackbar.info(
								requireActivity(),
								requireActivity().findViewById(R.id.nav_view),
								requireContext().getResources().getString(R.string.all_good));
					}
				});
		// database - notes

		return binding.getRoot();
	}

	private static String getLanguageDisplayName(String langCode) {
		Locale english = new Locale("en");

		String[] multiCodeLang = langCode.split("-");
		String countryCode;
		if (langCode.contains("-")) {
			langCode = multiCodeLang[0];
			countryCode = multiCodeLang[1];
		} else {
			countryCode = "";
		}

		Locale translated = new Locale(langCode, countryCode);
		return String.format(
				"%s (%s)",
				translated.getDisplayName(translated), translated.getDisplayName(english));
	}

	public void deleteAllNotes() {
		notesApi.deleteAllNotes();
		noteTopicsApi.deleteAllNoteTopics();
		Snackbar.info(
				requireActivity(),
				requireActivity().findViewById(R.id.nav_view),
				requireContext()
						.getResources()
						.getQuantityString(R.plurals.note_delete_message, 2));
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		binding = null;
	}
}
