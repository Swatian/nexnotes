package com.swatian.nexnotes.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.swatian.nexnotes.R;
import com.swatian.nexnotes.datastore.api.BaseApi;
import com.swatian.nexnotes.datastore.api.NoteTopicsApi;
import com.swatian.nexnotes.datastore.api.TopicsApi;
import com.swatian.nexnotes.datastore.models.Topics;
import com.swatian.nexnotes.helpers.Snackbar;
import java.util.List;

/**
 * @author mmarif
 */
public class TopicsAdapter extends RecyclerView.Adapter<TopicsAdapter.TopicsViewHolder> {

	private final NoteTopicsApi noteTopicsApi;
	private final List<Topics> topicsList;
	private final Context ctx;
	private View view;
	private final FloatingActionButton button;

	public TopicsAdapter(
			Context ctx, String source, List<Topics> notesListMain, FloatingActionButton button) {
		this.ctx = ctx;
		this.topicsList = notesListMain;
		this.button = button;

		noteTopicsApi = BaseApi.getInstance(ctx, NoteTopicsApi.class);
	}

	public class TopicsViewHolder extends RecyclerView.ViewHolder {

		private Topics topics;
		private final TextView topic;
		private final ImageView deleteTopic;
		private TextView notesBadge;

		private TopicsViewHolder(View itemView) {

			super(itemView);

			topic = itemView.findViewById(R.id.topic);
			deleteTopic = itemView.findViewById(R.id.delete);
			notesBadge = itemView.findViewById(R.id.notes_badge);

			/*itemView.setOnClickListener(
			cardView -> {
				Bundle bundle =  new Bundle();
				bundle.putString("source", "edit");
				bundle.putInt("topicId", topics.getTopicId());

				BottomSheetNotes bottomSheet = new BottomSheetNotes();
				bottomSheet.setArguments(bundle);
				bottomSheet.show(((FragmentActivity) ctx).getSupportFragmentManager(), "notesBottomSheet");
			});*/

			deleteTopic.setOnClickListener(
					itemDelete -> {
						MaterialAlertDialogBuilder materialAlertDialogBuilder =
								new MaterialAlertDialogBuilder(
										itemView.getContext(),
										com.google.android.material.R.style
												.ThemeOverlay_Material3_Dialog_Alert);

						materialAlertDialogBuilder
								.setMessage(
										itemView.getContext()
												.getString(R.string.delete_topic_dialog_message))
								.setPositiveButton(
										R.string.delete,
										(dialog, whichButton) ->
												deleteTopic(
														getAdapterPosition(), topics.getTopicId()))
								.setNeutralButton(R.string.cancel, null)
								.show();
					});
		}
	}

	private void deleteTopic(int position, int topicId) {

		TopicsApi topicsApi = BaseApi.getInstance(ctx, TopicsApi.class);
		assert topicsApi != null;
		topicsApi.deleteTopic(topicId);
		topicsList.remove(position);
		notifyItemRemoved(position);
		notifyItemRangeChanged(position, topicsList.size());
		Snackbar.info(
				ctx, view, button, ctx.getResources().getString(R.string.delete_topic_message));
	}

	@NonNull @Override
	public TopicsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		view =
				LayoutInflater.from(parent.getContext())
						.inflate(R.layout.list_topics, parent, false);
		return new TopicsViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull TopicsViewHolder holder, int position) {

		Topics currentItem = topicsList.get(position);
		holder.topics = currentItem;

		holder.topic.setText(currentItem.getTopic());
		holder.notesBadge.setText(
				String.valueOf(noteTopicsApi.checkNoteTopic(currentItem.getTopicId())));

		assert currentItem.getTopic() != null;
		if (currentItem.getTopic().equalsIgnoreCase("general")
				|| noteTopicsApi.checkNoteTopic(currentItem.getTopicId()) > 0) {
			holder.deleteTopic.setVisibility(View.GONE);
		}
	}

	@Override
	public int getItemCount() {
		return topicsList.size();
	}

	@SuppressLint("NotifyDataSetChanged")
	public void notifyDataChanged() {
		notifyDataSetChanged();
	}
}
