package com.swatian.nexnotes.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.swatian.nexnotes.R;
import com.swatian.nexnotes.bottomsheets.BottomSheetNotes;
import com.swatian.nexnotes.datastore.api.BaseApi;
import com.swatian.nexnotes.datastore.api.NoteTopicsApi;
import com.swatian.nexnotes.datastore.api.NotesApi;
import com.swatian.nexnotes.datastore.models.Notes;
import com.swatian.nexnotes.helpers.Snackbar;
import com.swatian.nexnotes.helpers.TimeUtils;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author mmarif
 */
public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesViewHolder> {

	private final List<Notes> notesList;
	private final NoteTopicsApi noteTopicsApi;
	private final Context ctx;
	private final String source;
	private final FloatingActionButton button;
	private final Activity activity;

	public NotesAdapter(
			Context ctx,
			String source,
			List<Notes> notesListMain,
			FloatingActionButton button,
			Activity activity) {
		this.ctx = ctx;
		this.source = source;
		this.notesList = notesListMain;
		this.button = button;
		this.activity = activity;
		noteTopicsApi = BaseApi.getInstance(ctx, NoteTopicsApi.class);
	}

	public class NotesViewHolder extends RecyclerView.ViewHolder {

		private Notes notes;
		private final TextView title;
		private final TextView content;
		private final TextView noteTopic;
		private final TextView datetime;
		private final MaterialCardView topicCard;

		private NotesViewHolder(View itemView) {

			super(itemView);

			title = itemView.findViewById(R.id.title);
			content = itemView.findViewById(R.id.content);
			noteTopic = itemView.findViewById(R.id.notes_topic_badge);
			datetime = itemView.findViewById(R.id.datetime);
			ImageView deleteNote = itemView.findViewById(R.id.delete_note);
			topicCard = itemView.findViewById(R.id.note_topic_card);

			itemView.setOnClickListener(
					cardView -> {
						Bundle bundle = new Bundle();
						bundle.putString("source", "edit");
						bundle.putInt("noteId", notes.getNoteId());

						BottomSheetNotes bottomSheet = new BottomSheetNotes();
						bottomSheet.setArguments(bundle);
						bottomSheet.show(
								((FragmentActivity) ctx).getSupportFragmentManager(),
								"notesBottomSheet");
					});

			deleteNote.setOnClickListener(
					itemDelete -> {
						MaterialAlertDialogBuilder materialAlertDialogBuilder =
								new MaterialAlertDialogBuilder(
										itemView.getContext(),
										com.google.android.material.R.style
												.ThemeOverlay_Material3_Dialog_Alert);

						materialAlertDialogBuilder
								.setMessage(
										itemView.getContext()
												.getString(R.string.delete_note_dialog_message))
								.setPositiveButton(
										R.string.delete,
										(dialog, whichButton) ->
												deleteNote(getAdapterPosition(), notes.getNoteId()))
								.setNeutralButton(R.string.cancel, null)
								.show();
					});
		}
	}

	private void deleteNote(int position, int noteId) {

		NotesApi notesApi = BaseApi.getInstance(ctx, NotesApi.class);
		assert notesApi != null;
		notesApi.deleteNote(noteId);
		noteTopicsApi.deleteNoteTopicByNoteId(noteId);
		notesList.remove(position);
		notifyItemRemoved(position);
		notifyItemRangeChanged(position, notesList.size());
		Snackbar.info(
				activity,
				button,
				ctx.getResources().getQuantityString(R.plurals.note_delete_message, 1));
	}

	@NonNull @Override
	public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view =
				LayoutInflater.from(parent.getContext())
						.inflate(R.layout.list_notes, parent, false);
		return new NotesViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull NotesViewHolder holder, int position) {

		Locale locale = ctx.getResources().getConfiguration().getLocales().get(0);
		Notes currentItem = notesList.get(position);
		holder.notes = currentItem;

		assert currentItem.getContent() != null;
		String contentTrimmed =
				currentItem
						.getContent()
						.substring(0, Math.min(currentItem.getContent().length(), 54));
		// Markdown.render(ctx, EmojiParser.parseToUnicode(contentTrimmed), holder.content);

		if (noteTopicsApi.checkByNoteId(currentItem.getNoteId()) > 0) {
			holder.topicCard.setVisibility(View.VISIBLE);

			holder.noteTopic.setText(noteTopicsApi.getTopicByJoin(currentItem.getNoteId()));
			holder.noteTopic.setBackgroundColor(
					Integer.parseInt(
							String.valueOf(
									Color.parseColor(
											"#"
													+ noteTopicsApi.getTopicColorByJoin(
															currentItem.getNoteId())))));
		}

		holder.content.setText(contentTrimmed);
		holder.title.setText(currentItem.getTitle());

		if (currentItem.getModified() != null) {
			String modifiedTime =
					TimeUtils.formatTime(
							Date.from(Instant.ofEpochSecond(currentItem.getModified())), locale);
			holder.datetime.setText(
					ctx.getResources().getString(R.string.updated_with, modifiedTime));
		} else {
			String createdTime =
					TimeUtils.formatTime(
							Date.from(Instant.ofEpochSecond(currentItem.getDatetime())), locale);
			holder.datetime.setText(
					ctx.getResources().getString(R.string.created_with, createdTime));
		}
	}

	@Override
	public int getItemCount() {
		return notesList.size();
	}

	@SuppressLint("NotifyDataSetChanged")
	public void notifyDataChanged() {
		notifyDataSetChanged();
	}
}
