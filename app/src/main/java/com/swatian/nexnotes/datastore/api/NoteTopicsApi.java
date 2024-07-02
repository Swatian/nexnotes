package com.swatian.nexnotes.datastore.api;

import android.content.Context;
import com.swatian.nexnotes.datastore.dao.NoteTopicDao;
import com.swatian.nexnotes.datastore.models.NoteTopics;

/**
 * @author mmarif
 */
public class NoteTopicsApi extends BaseApi {

	private final NoteTopicDao noteTopicDao;

	NoteTopicsApi(Context context) {
		super(context);
		noteTopicDao = nexNotesDatabase.noteTopicDao();
	}

	public long insertNoteTopic(int noteId, int topicId) {

		NoteTopics noteTopics = new NoteTopics();
		noteTopics.setNoteId(noteId);
		noteTopics.setTopicId(topicId);

		return insertNewTask(noteTopics);
	}

	public long insertNewTask(NoteTopics noteTopics) {
		return noteTopicDao.insertNoteTopic(noteTopics);
	}

	public Integer checkNoteTopic(int topicId) {
		return noteTopicDao.checkNoteTopic(topicId);
	}

	public Integer checkByNoteId(int noteId) {
		return noteTopicDao.checkByNoteId(noteId);
	}

	public Integer getTopicId(int noteId) {
		return noteTopicDao.getTopicId(noteId);
	}

	public String getTopicByJoin(int noteId) {
		return noteTopicDao.getTopicByJoin(noteId);
	}

	public String getTopicColorByJoin(int noteId) {
		return noteTopicDao.getTopicColorByJoin(noteId);
	}

	public Integer updateTopicId(int noteId, int topicId) {
		return noteTopicDao.updateTopicId(noteId, topicId);
	}

	public void deleteNoteTopicByNoteId(final int noteId) {
		final NoteTopics noteTopics = noteTopicDao.fetchNoteTopicByNoteId(noteId);

		if (noteTopics != null) {
			executorService.execute(() -> noteTopicDao.deleteNoteTopicByNoteId(noteId));
		}
	}

	public void deleteAllNoteTopics() {
		executorService.execute(noteTopicDao::deleteAllNoteTopics);
	}
}
