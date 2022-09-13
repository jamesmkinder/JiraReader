package JiraModel;

import java.io.Serializable;
import java.util.Objects;

public class IssueValueId implements Serializable {
    private String valueName;
    private int issueId;
    private String parentName;

    public IssueValueId(String valueName, int issueId, String parentName) {
        this.valueName = valueName;
        this.issueId = issueId;
        this.parentName = parentName;
    }
    public IssueValueId() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IssueValueId that = (IssueValueId) o;
        return Objects.equals(valueName, that.valueName) && issueId == that.issueId && Objects.equals(parentName, that.parentName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valueName, issueId, parentName);
    }
}
