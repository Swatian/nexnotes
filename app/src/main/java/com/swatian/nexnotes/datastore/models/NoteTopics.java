package com.swatian.nexnotes.datastore.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable;

/**
 * @author mmarif
 */
@Entity(tableName = "NoteTopics")
public class NoteTopics implements Serializable {

	@PrimaryKey(autoGenerate = true)
	private int ntId;

	private int noteId;
	private int topicId;

	public int getNtId() {
		return ntId;
	}

	public void setNtId(int ntId) {
		this.ntId = ntId;
	}

	public int getNoteId() {
		return noteId;
	}

	public void setNoteId(int noteId) {
		this.noteId = noteId;
	}

	public int getTopicId() {
		return topicId;
	}

	public void setTopicId(int topicId) {
		this.topicId = topicId;
	}
}
