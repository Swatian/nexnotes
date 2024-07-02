package com.swatian.nexnotes.bottomsheets;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.swatian.nexnotes.R;
import com.swatian.nexnotes.databinding.BottomSheetTopicsBinding;
import com.swatian.nexnotes.datastore.api.BaseApi;
import com.swatian.nexnotes.datastore.api.TopicsApi;
import com.swatian.nexnotes.helpers.Snackbar;
import com.swatian.nexnotes.interfaces.BottomSheetListener;
import java.time.Instant;

/**
 * @author mmarif
 */
public class BottomSheetTopics extends BottomSheetDialogFragment {

	private BottomSheetListener bottomSheetListener;
	private String source;
	private TopicsApi topicsApi;
	private BottomSheetTopicsBinding bottomSheetTopicsBinding;

	@Nullable @Override
	public View onCreateView(
			@NonNull LayoutInflater inflater,
			@Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {

		bottomSheetTopicsBinding = BottomSheetTopicsBinding.inflate(inflater, container, false);

		topicsApi = BaseApi.getInstance(requireContext(), TopicsApi.class);

		Bundle bundle = getArguments();
		assert bundle != null;

		if (bundle.getString("source") != null) {
			source = bundle.getString("source");
		} else {
			source = "";
		}

		bottomSheetTopicsBinding.closeBs.setOnClickListener(close -> dismiss());

		if (source.equalsIgnoreCase("new")) {

			bottomSheetTopicsBinding.save.setOnClickListener(
					save -> {
						if (topicsApi.checkTopic(
										String.valueOf(bottomSheetTopicsBinding.topic.getText()))
								== 0) {
							if (bottomSheetTopicsBinding.topic.getText().length() > 2) {
								topicsApi.insertTopic(
										String.valueOf(bottomSheetTopicsBinding.topic.getText()),
										"850970",
										(int) Instant.now().getEpochSecond());
								dismiss();

								Snackbar.info(
										requireActivity(),
										requireActivity().findViewById(R.id.new_topic),
										getResources().getString(R.string.topic_created));
							} else {
								Snackbar.info(
										requireContext(),
										bottomSheetTopicsBinding.mainFrame,
										getResources().getString(R.string.topic_min_characters));
							}
						} else {
							Snackbar.info(
									requireContext(),
									bottomSheetTopicsBinding.mainFrame,
									getResources().getString(R.string.topic_exists));
						}
					});
		}

		return bottomSheetTopicsBinding.getRoot();
	}

	@NonNull @Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
		dialog.setContentView(R.layout.bottom_sheet_notes);

		dialog.setOnShowListener(
				dialogInterface -> {
					BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
					View bottomSheet =
							bottomSheetDialog.findViewById(
									com.google.android.material.R.id.design_bottom_sheet);

					if (bottomSheet != null) {

						BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
						behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
						behavior.setPeekHeight(bottomSheet.getHeight());
						behavior.setHideable(false);
					}
				});

		if (dialog.getWindow() != null) {

			WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
			params.height = WindowManager.LayoutParams.MATCH_PARENT;
			dialog.getWindow().setAttributes(params);
		}

		return dialog;
	}

	@Override
	public void onAttach(@NonNull Context context) {

		super.onAttach(context);

		try {
			bottomSheetListener = (BottomSheetListener) context;
		} catch (ClassCastException e) {
			throw new ClassCastException(context + " must implement BottomSheetListener");
		}
	}
}
