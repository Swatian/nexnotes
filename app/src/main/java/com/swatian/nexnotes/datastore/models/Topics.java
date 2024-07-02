package com.swatian.nexnotes.datastore.models;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable;

/**
 * @author mmarif
 */
@Entity(tableName = "Topics")
public class Topics implements Serializable {

	@PrimaryKey(autoGenerate = true)
	private int topicId;

	@Nullable private String topic;
	private String color;
	private Integer datetime;

	public int getTopicId() {
		return topicId;
	}

	public void setTopicId(int topicId) {
		this.topicId = topicId;
	}

	@Nullable public String getTopic() {
		return topic;
	}

	public void setTopic(@Nullable String topic) {
		this.topic = topic;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public Integer getDatetime() {
		return datetime;
	}

	public void setDatetime(Integer datetime) {
		this.datetime = datetime;
	}
}
