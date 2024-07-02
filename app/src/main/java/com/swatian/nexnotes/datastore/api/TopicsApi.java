package com.swatian.nexnotes.datastore.api;

import android.content.Context;
import androidx.lifecycle.LiveData;
import com.swatian.nexnotes.datastore.dao.TopicsDao;
import com.swatian.nexnotes.datastore.models.Topics;
import java.util.List;

/**
 * @author mmarif
 */
public class TopicsApi extends BaseApi {

	private final TopicsDao topicsDao;

	TopicsApi(Context context) {
		super(context);
		topicsDao = nexNotesDatabase.topicsDao();
	}

	public long insertTopic(String topic, String color, Integer datetime) {

		Topics topics = new Topics();
		topics.setTopic(topic);
		topics.setColor(color);
		topics.setDatetime(datetime);

		return insertTopicTask(topics);
	}

	public long insertTopicTask(Topics topics) {
		return topicsDao.insertTopic(topics);
	}

	public LiveData<List<Topics>> fetchAllTopics() {
		return topicsDao.fetchAllTopics();
	}

	public List<Topics> fetchTopics() {
		return topicsDao.fetchTopics();
	}

	public Integer getCount() {
		return topicsDao.getCount();
	}

	public Integer checkTopic(String topic) {
		return topicsDao.checkTopic(topic);
	}

	public Integer getTopicId(String topic) {
		return topicsDao.getTopicId(topic);
	}

	public String getTopicById(int topicId) {
		return topicsDao.getTopicById(topicId);
	}

	public void updateTopic(final String topic, String color, int topicId) {
		executorService.execute(() -> topicsDao.updateTopic(topic, color, topicId));
	}

	public void deleteTopic(final int topicId) {
		final Topics topics = topicsDao.fetchTopicById(topicId);

		if (topics != null) {
			executorService.execute(() -> topicsDao.deleteTopic(topicId));
		}
	}
}
