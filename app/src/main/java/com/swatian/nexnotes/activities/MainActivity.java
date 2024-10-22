package com.swatian.nexnotes.activities;

import android.os.Bundle;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.swatian.nexnotes.R;
import com.swatian.nexnotes.databinding.ActivityMainBinding;
import com.swatian.nexnotes.interfaces.BottomSheetListener;

/**
 * @author mmarif
 */
public class MainActivity extends BaseActivity implements BottomSheetListener {

	private ActivityMainBinding binding;
	public static boolean closeActivity = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		binding = ActivityMainBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());

		NavHostFragment navHostFragment =
				(NavHostFragment)
						getSupportFragmentManager()
								.findFragmentById(R.id.nav_host_fragment_activity_main);
		assert navHostFragment != null;
		NavController navController = navHostFragment.getNavController();
		NavigationUI.setupWithNavController(binding.navView, navController);
	}

	@Override
	public void onClickedAction(String text) {}

	@Override
	public void onResume() {
		super.onResume();

		if (closeActivity) {
			finishAndRemoveTask();
			closeActivity = false;
		}
	}
}
