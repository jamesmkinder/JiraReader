package JiraModel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.sql.Timestamp;

/**
 * Model class to represent an issue's changelog.
 */

@Entity
@Table(name = "changelog")
public class ChangeLog {
    @Id
    @Column(name = "CHANGELOG_ID")
    private int id;
    @Column(name = "AUTHOR")
    private String author;
    @Column(name = "FIELD")
    private String field;
    @Column(name = "FROM_STRING", length = 65535)
    private String from;
    @Column(name = "TO_STRING", length = 65535)
    private String to;
    @Column(name = "CREATED")
    private Timestamp timestamp;
    @Column(name = "ISSUE_ID")
    private int issueID;

    public ChangeLog(int id, String author, String field, String from, String to, Timestamp timestamp, int issueID) {
        this.id = id;
        this.author = author;
        this.field = field;
        this.from = from;
        this.to = to;
        this.timestamp = timestamp;
        this.issueID = issueID;
    }

    public ChangeLog() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public int getIssueID() {
        return issueID;
    }

    public void setIssueID(int issueID) {
        this.issueID = issueID;
    }
}