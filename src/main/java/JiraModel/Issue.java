package JiraModel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Model class to represent a Jira issue.
 */

@Entity
@Table(name = "issue")
public class Issue {
    @Id
    @Column(name = "ISSUE_ID")
    private int id;
    @Column(name = "SELF")
    private String self;
    @Column(name = "ISSUE_KEY")
    private String key;

    public Issue(String self, int id, String key) {
        this.self = self;
        this.id = id;
        this.key = key;
    }
    public Issue() {}

    public String getSelf() {
        return self;
    }

    public int getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

}
