package com.swatian.nexnotes.datastore.db;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.swatian.nexnotes.datastore.dao.AppSettingsDao;
import com.swatian.nexnotes.datastore.dao.NoteTopicDao;
import com.swatian.nexnotes.datastore.dao.NotesDao;
import com.swatian.nexnotes.datastore.dao.TopicsDao;
import com.swatian.nexnotes.datastore.models.AppSettings;
import com.swatian.nexnotes.datastore.models.NoteTopics;
import com.swatian.nexnotes.datastore.models.Notes;
import com.swatian.nexnotes.datastore.models.Topics;

/**
 * @author mmarif
 */
@Database(
		entities = {Notes.class, Topics.class, AppSettings.class, NoteTopics.class},
		version = 1,
		exportSchema = false)
public abstract class NexNotesDatabase extends RoomDatabase {

	private static final String DB_NAME = "nexnotes";

	private static volatile NexNotesDatabase nexNotesDatabase;

	public static NexNotesDatabase getDatabaseInstance(Context context) {

		if (nexNotesDatabase == null) {
			synchronized (NexNotesDatabase.class) {
				if (nexNotesDatabase == null) {

					nexNotesDatabase =
							Room.databaseBuilder(context, NexNotesDatabase.class, DB_NAME)
									// .fallbackToDestructiveMigration()
									.allowMainThreadQueries()
									// .addMigrations(MIGRATION_1_2)
									.build();
				}
			}
		}

		return nexNotesDatabase;
	}

	public abstract NotesDao notesDao();

	public abstract TopicsDao topicsDao();

	public abstract AppSettingsDao appSettingsDao();

	public abstract NoteTopicDao noteTopicDao();
}
