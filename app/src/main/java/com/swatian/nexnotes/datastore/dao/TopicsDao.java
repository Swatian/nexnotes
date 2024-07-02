package com.swatian.nexnotes.datastore.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.swatian.nexnotes.datastore.models.Topics;
import java.util.List;

/**
 * @author mmarif
 */
@Dao
public interface TopicsDao {

	@Insert
	long insertTopic(Topics topics);

	@Query("SELECT * FROM Topics WHERE topicId = :topicId")
	Topics fetchTopicById(int topicId);

	@Query("SELECT * FROM Topics ORDER BY datetime DESC, topicId DESC")
	LiveData<List<Topics>> fetchAllTopics();

	@Query("SELECT * FROM Topics ORDER BY datetime ASC, topicId ASC")
	List<Topics> fetchTopics();

	@Query("DELETE FROM Topics WHERE topicId = :topicId")
	void deleteTopic(int topicId);

	@Query("SELECT COUNT(topicId) FROM Topics")
	Integer getCount();

	@Query("SELECT COUNT(topicId) FROM Topics WHERE topic = :topic")
	Integer checkTopic(String topic);

	@Query("SELECT topicId FROM Topics WHERE topic = :topic")
	Integer getTopicId(String topic);

	@Query("SELECT topic FROM Topics WHERE topicId = :topicId")
	String getTopicById(int topicId);

	@Query("UPDATE Topics SET topic = :topic, color = :color WHERE topicId = :topicId")
	void updateTopic(String topic, String color, int topicId);
}
