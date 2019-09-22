package mobius.common.engine.impl.persistence.entity;

import java.time.Instant;

public interface AuditInfo {
	String getCreatedBy();

	void setCreatedBy(String createdBy);

	String getUpdatedBy();

	void setUpdatedBy(String updatedBy);

	Instant getCreatedTime();

	void setCreatedTime(Instant createdDateTime);

	Instant getUpdatedTime();

	void setUpdatedTime(Instant updatedDateTime);
}
