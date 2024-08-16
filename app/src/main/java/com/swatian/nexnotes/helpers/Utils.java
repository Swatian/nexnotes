package com.swatian.nexnotes.helpers;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.view.View;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.pm.PackageInfoCompat;
import androidx.core.os.LocaleListCompat;
import com.swatian.nexnotes.R;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * @author mmarif
 */
public class Utils {

	public static void setAppLocale(String locCode) {

		String[] multiCodeLang = locCode.split("-");
		if (locCode.contains("-")) {
			locCode = multiCodeLang[0];
		}

		AppCompatDelegate.setApplicationLocales(
				LocaleListCompat.create(Locale.forLanguageTag(locCode)));
	}

	public static int getAppBuildNo(Context context) {

		try {
			PackageInfo packageInfo =
					context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return (int) PackageInfoCompat.getLongVersionCode(packageInfo);
		} catch (PackageManager.NameNotFoundException e) {
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	public static String getAppVersion(Context context) {

		try {
			PackageInfo packageInfo =
					context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionName;
		} catch (PackageManager.NameNotFoundException e) {
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	public static boolean isPremium(Context context) {
		return context.getPackageName().equals("com.swatian.nexnotes.premium");
	}

	public static void openUrlInBrowser(Context ctx, Activity activity, View view, String url) {

		Intent i;
		i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		i.addCategory(Intent.CATEGORY_BROWSABLE);

		try {
			Intent browserIntent = wrapBrowserIntent(ctx, i);
			if (browserIntent == null) {
				Snackbar.info(activity, view, ctx.getString(R.string.generic_error));
			}
			ctx.startActivity(browserIntent);
		} catch (ActivityNotFoundException e) {
			Snackbar.info(activity, view, ctx.getString(R.string.browser_open_failed));
		} catch (Exception e) {
			Snackbar.info(activity, view, ctx.getString(R.string.generic_error));
		}
	}

	private static Intent wrapBrowserIntent(Context context, Intent intent) {

		final PackageManager pm = context.getPackageManager();
		final List<ResolveInfo> activities =
				pm.queryIntentActivities(
						new Intent(intent)
								.setData(
										Objects.requireNonNull(intent.getData())
												.buildUpon()
												.authority("example.com")
												.scheme("https")
												.build()),
						PackageManager.MATCH_ALL);
		final ArrayList<Intent> chooserIntents = new ArrayList<>();
		final String ourPackageName = context.getPackageName();

		activities.sort(new ResolveInfo.DisplayNameComparator(pm));

		for (ResolveInfo resInfo : activities) {
			ActivityInfo info = resInfo.activityInfo;
			if (!info.enabled || !info.exported) {
				continue;
			}
			if (info.packageName.equals(ourPackageName)) {
				continue;
			}

			Intent targetIntent = new Intent(intent);
			targetIntent.setPackage(info.packageName);
			targetIntent.setDataAndType(intent.getData(), intent.getType());
			chooserIntents.add(targetIntent);
		}

		if (chooserIntents.isEmpty()) {
			return null;
		}

		final Intent lastIntent = chooserIntents.remove(chooserIntents.size() - 1);
		if (chooserIntents.isEmpty()) {
			return lastIntent;
		}

		Intent chooserIntent = Intent.createChooser(lastIntent, null);
		String extraName = Intent.EXTRA_ALTERNATE_INTENTS;
		chooserIntent.putExtra(extraName, chooserIntents.toArray(new Intent[0]));
		return chooserIntent;
	}
}
