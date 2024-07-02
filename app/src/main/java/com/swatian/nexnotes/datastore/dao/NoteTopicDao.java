package com.swatian.nexnotes.datastore.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.swatian.nexnotes.datastore.models.NoteTopics;
import com.swatian.nexnotes.datastore.models.Topics;
import java.util.List;

/**
 * @author mmarif
 */
@Dao
public interface NoteTopicDao {

	@Insert
	long insertNoteTopic(NoteTopics noteTopics);

	@Query("SELECT * FROM NoteTopics WHERE ntId = :ntId")
	Topics fetchNoteTopicById(int ntId);

	@Query("SELECT * FROM NoteTopics WHERE noteId = :noteId")
	NoteTopics fetchNoteTopicByNoteId(int noteId);

	@Query("SELECT * FROM NoteTopics ORDER BY ntId DESC")
	LiveData<List<NoteTopics>> fetchAllNoteTopics();

	@Query("DELETE FROM NoteTopics WHERE ntId = :ntId")
	void deleteNoteTopic(int ntId);

	@Query("DELETE FROM NoteTopics")
	void deleteAllNoteTopics();

	@Query("SELECT COUNT(ntId) FROM NoteTopics WHERE topicId = :topicId")
	Integer checkNoteTopic(int topicId);

	@Query("SELECT COUNT(ntId) FROM NoteTopics WHERE noteId = :noteId")
	Integer checkByNoteId(int noteId);

	@Query("SELECT topicId FROM NoteTopics WHERE noteId = :noteId")
	Integer getTopicId(int noteId);

	@Query(
			"SELECT t.topic FROM Topics as t INNER JOIN NoteTopics as nt ON t.topicId = nt.topicId WHERE nt.noteId = :noteId")
	String getTopicByJoin(int noteId);

	@Query(
			"SELECT t.color FROM Topics as t INNER JOIN NoteTopics as nt ON t.topicId = nt.topicId WHERE nt.noteId = :noteId")
	String getTopicColorByJoin(int noteId);

	@Query("UPDATE NoteTopics SET topicId = :topicId WHERE noteId = :noteId")
	Integer updateTopicId(int noteId, int topicId);

	@Query("SELECT COUNT(ntId) FROM NoteTopics")
	Integer getCount();

	@Query("DELETE FROM NoteTopics WHERE noteId = :noteId")
	void deleteNoteTopicByNoteId(int noteId);
}
