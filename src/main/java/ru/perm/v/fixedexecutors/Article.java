package ru.perm.v.fixedexecutors;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
public class Article {
	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	private String topic = "";
	private String content = "";

	public Article() {
	}

	public Article(Long id, String topic, String content) {
		this.id = id;
		this.topic = topic;
		this.content = content;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "Article{" +
				"id=" + id +
				", topic='" + topic + '\'' +
				", content='" + content + '\'' +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Article)) return false;

		Article article = (Article) o;

		return id != null ? id.equals(article.id) : article.id == null;
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}
}
