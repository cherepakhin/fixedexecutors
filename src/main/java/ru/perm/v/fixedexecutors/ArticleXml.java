package ru.perm.v.fixedexecutors;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("article")
public class ArticleXml {
    @XStreamAlias("id")
    private Long id = 0L;
    private String topic = "";
    private String content = "";

    public ArticleXml() {
    }

    public ArticleXml(Long id, String topic, String content) {
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
        return "ArticleXml{" +
                "id=" + id +
                ", topic='" + topic + '\'' +
                ", content='" + content + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArticleXml)) return false;

        ArticleXml that = (ArticleXml) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (topic != null ? !topic.equals(that.topic) : that.topic != null) return false;
        return content != null ? content.equals(that.content) : that.content == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (topic != null ? topic.hashCode() : 0);
        result = 31 * result + (content != null ? content.hashCode() : 0);
        return result;
    }
}
