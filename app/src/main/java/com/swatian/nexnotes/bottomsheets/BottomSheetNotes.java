package com.swatian.nexnotes.bottomsheets;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.swatian.nexnotes.R;
import com.swatian.nexnotes.databinding.BottomSheetNotesBinding;
import com.swatian.nexnotes.datastore.api.BaseApi;
import com.swatian.nexnotes.datastore.api.NoteTopicsApi;
import com.swatian.nexnotes.datastore.api.NotesApi;
import com.swatian.nexnotes.datastore.api.TopicsApi;
import com.swatian.nexnotes.datastore.models.Notes;
import com.swatian.nexnotes.datastore.models.Topics;
import com.swatian.nexnotes.helpers.Markdown;
import com.swatian.nexnotes.interfaces.BottomSheetListener;
import com.vdurmont.emoji.EmojiParser;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * @author mmarif
 */
public class BottomSheetNotes extends BottomSheetDialogFragment {

	// private final List<Topics> topicsList = new ArrayList<>();
	private BottomSheetListener bottomSheetListener;
	private String source;
	private NotesApi notesApi;
	private int noteId;
	private Notes notes;
	private BottomSheetNotesBinding bottomSheetNotesBinding;

	@Nullable @Override
	public View onCreateView(
			@NonNull LayoutInflater inflater,
			@Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {

		bottomSheetNotesBinding = BottomSheetNotesBinding.inflate(inflater, container, false);

		notesApi = BaseApi.getInstance(requireContext(), NotesApi.class);
		TopicsApi topicsApi = BaseApi.getInstance(requireContext(), TopicsApi.class);
		NoteTopicsApi noteTopicsApi = BaseApi.getInstance(requireContext(), NoteTopicsApi.class);

		assert topicsApi != null;
		List<Topics> topicsList = topicsApi.fetchTopics();
		List<String> topicList_ = new ArrayList<>();

		for (Topics data : topicsList) {
			topicList_.add(data.getTopic());
		}

		ArrayAdapter<String> UnitAdapter =
				new ArrayAdapter<>(
						requireContext(),
						android.R.layout.simple_spinner_dropdown_item,
						topicList_);
		bottomSheetNotesBinding.topicsDropdown.setAdapter(UnitAdapter);

		Bundle bundle = getArguments();
		assert bundle != null;

		if (bundle.getString("source") != null) {
			source = bundle.getString("source");
			noteId = bundle.getInt("noteId");
		} else {
			noteId = bundle.getInt("noteId");
			source = "";
		}

		bottomSheetNotesBinding.closeBs.setOnClickListener(close -> dismiss());

		bottomSheetNotesBinding.view.setOnClickListener(
				close -> {
					bottomSheetNotesBinding.contents.setVisibility(View.GONE);
					bottomSheetNotesBinding.renderContents.setVisibility(View.VISIBLE);
					Markdown.render(
							requireContext(),
							EmojiParser.parseToUnicode(
									bottomSheetNotesBinding.contents.getText().toString()),
							bottomSheetNotesBinding.renderContents);
				});

		bottomSheetNotesBinding.edit.setOnClickListener(
				close -> {
					bottomSheetNotesBinding.contents.setVisibility(View.VISIBLE);
					bottomSheetNotesBinding.renderContents.setVisibility(View.GONE);
				});

		assert noteTopicsApi != null;
		if (noteTopicsApi.checkByNoteId(noteId) > 0) {
			int topicId = noteTopicsApi.getTopicId(noteId);
			String topic = topicsApi.getTopicById(topicId);
			for (int i = 0; i < topicList_.size(); i++) {
				if (topicList_.get(i).equalsIgnoreCase(topic)) {
					bottomSheetNotesBinding.topicsDropdown.setText(topicList_.get(i), false);
				}
			}
		}

		bottomSheetNotesBinding.topicsDropdown.setOnItemClickListener(
				(parent, view, position, id) -> {
					int topicId =
							topicsApi.getTopicId(
									bottomSheetNotesBinding.topicsDropdown.getText().toString());

					if (noteTopicsApi.checkByNoteId(noteId) > 0) {
						// update
						noteTopicsApi.updateTopicId(noteId, topicId);
					} else {
						// new
						noteTopicsApi.insertNoteTopic(noteId, topicId);
					}
				});

		if (source.equalsIgnoreCase("edit")) {

			notes = notesApi.fetchNoteById(noteId);

			bottomSheetNotesBinding.contents.setText(notes.getContent());
			bottomSheetNotesBinding.title.setText(notes.getTitle());

			assert notes.getContent() != null;
			bottomSheetNotesBinding.contents.setSelection(notes.getContent().length());

			bottomSheetNotesBinding.title.addTextChangedListener(textWatcher);
			bottomSheetNotesBinding.contents.addTextChangedListener(textWatcher);
		} else if (source.equalsIgnoreCase("new")) {

			bottomSheetNotesBinding.topicsDropdownDivider.setVisibility(View.GONE);
			bottomSheetNotesBinding.topicsDropdownLayout.setVisibility(View.GONE);

			bottomSheetNotesBinding.title.addTextChangedListener(textWatcher);
			bottomSheetNotesBinding.contents.addTextChangedListener(textWatcher);
		}

		DisplayMetrics displayMetrics = new DisplayMetrics();
		((Activity) requireContext())
				.getWindowManager()
				.getDefaultDisplay()
				.getMetrics(displayMetrics);
		int height = displayMetrics.heightPixels;
		bottomSheetNotesBinding.contents.setMinHeight(height);
		bottomSheetNotesBinding.renderContents.setMinHeight(height);

		return bottomSheetNotesBinding.getRoot();
	}

	private void updateNote(String content, String title, int noteId) {
		notesApi.updateNote(content, title, Instant.now().getEpochSecond(), noteId);
	}

	private final TextWatcher textWatcher =
			new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

				@Override
				public void afterTextChanged(Editable s) {

					String text = bottomSheetNotesBinding.contents.getText().toString();
					String title = bottomSheetNotesBinding.title.getText().toString();

					if (bottomSheetNotesBinding.title.getText().hashCode() == s.hashCode()) {
						if (noteId > 0) {
							updateNote(text, title, noteId);
						} else {
							if (title.length() > 2) {

								bottomSheetNotesBinding.topicsDropdownDivider.setVisibility(
										View.VISIBLE);
								bottomSheetNotesBinding.topicsDropdownLayout.setVisibility(
										View.VISIBLE);

								noteId =
										(int)
												notesApi.insertNote(
														text,
														title,
														(int) Instant.now().getEpochSecond());
							}
						}
					} else if (bottomSheetNotesBinding.contents.getText().hashCode()
							== s.hashCode()) {
						if (noteId > 0) {
							updateNote(text, title, noteId);
						} else {
							if (title.length() > 2) {
								noteId =
										(int)
												notesApi.insertNote(
														text,
														title,
														(int) Instant.now().getEpochSecond());
							}
						}
					}
				}
			};

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
