package JiraModel;

import jakarta.persistence.*;

@Entity
@Table(name = "issue_value")
public class IssueValue {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ISSUE_VALUE_ID")
    private int issueValueId;
    @Column(name = "ISSUE_ID")
    private int issueId;
    @Column(name = "PARENT_NAME")
    private String parentName;
    @Column(name = "VALUE_NAME")
    private String valueName;
    @Column(name = "VALUE_VALUE", length = 65535)
    private String valueValue;

    public IssueValue(String valueName, String valueValue, String parentName, int issueId) {
        this.valueName = valueName;
        this.valueValue = valueValue;
        this.parentName = parentName;
        this.issueId = issueId;
    }

    public IssueValue() {}

    public String getValueName() {
        return valueName;
    }

    public void setValueName(String valueName) {
        this.valueName = valueName;
    }

    public String getValueValue() {
        return valueValue;
    }

    public void setValueValue(String valueValue) {
        this.valueValue = valueValue;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) { this.parentName = parentName; }

    public int getIssueId() {
        return issueId;
    }

    public void setIssueId(int issueId) {
        this.issueId = issueId;
    }
}
