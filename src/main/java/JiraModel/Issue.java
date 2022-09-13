package JiraModel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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

    public void setSelf(String self) {
        this.self = self;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
