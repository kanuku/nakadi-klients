package org.zalando.nakadi.client.java.model;

import org.zalando.nakadi.client.java.enumerator.BatchItemPublishingStatus;
import org.zalando.nakadi.client.java.enumerator.BatchItemStep;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A status corresponding to one individual Event's publishing attempt.
 *
 */
public class BatchItemResponse {
	private final String eid;
	private final BatchItemPublishingStatus publishingStatus;
	private final BatchItemStep step;
	private final String detail;

	/**
	 * A status corresponding to one individual Event's publishing attempt.
	 * 
	 * @param eid
	 *            Eid of the corresponding item. Will be absent if missing on
	 *            the incoming Event.
	 * @param publishingStatus
	 *            Indicator of the submission of the Event within a Batch. -
	 *            SUBMITTED indicates successful submission, including commit on
	 *            he underlying broker. - FAILED indicates the message
	 *            submission was not possible and can be resubmitted if so
	 *            desired. - ABORTED indicates that the submission of this item
	 *            was not attempted any further due to a failure on another item
	 *            in the batch.
	 * @param step
	 *            Indicator of the step in the pulbishing process this Event
	 *            reached. In Items that FAILED means the step of the failure. -
	 *            NONE indicates that nothing was yet attempted for the
	 *            publishing of this Event. Should be present only in the case
	 *            of aborting the publishing during the validation of another
	 *            (previous) Event. - VALIDATING, ENRICHING, PARTITIONING and
	 *            PUBLISHING indicate all the corresponding steps of the
	 *            publishing process.
	 * @param detail
	 *            Human readable information about the failure on this item.
	 *            Items that are not SUBMITTED should have a description.
	 *
	 */
	@JsonCreator
	public BatchItemResponse(
			@JsonProperty("eid") String eid,
			@JsonProperty("publishing_status") BatchItemPublishingStatus publishingStatus,
			@JsonProperty("step") BatchItemStep step,
			@JsonProperty("detail") String detail) {
		this.eid = eid;
		this.publishingStatus = publishingStatus;
		this.step = step;
		this.detail = detail;
	}

	public String getEid() {
		return eid;
	}

	public BatchItemPublishingStatus getPublishingStatus() {
		return publishingStatus;
	}

	public BatchItemStep getStep() {
		return step;
	}

	public String getDetail() {
		return detail;
	}
	
	

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((detail == null) ? 0 : detail.hashCode());
        result = prime * result + ((eid == null) ? 0 : eid.hashCode());
        result = prime * result + ((publishingStatus == null) ? 0 : publishingStatus.hashCode());
        result = prime * result + ((step == null) ? 0 : step.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BatchItemResponse other = (BatchItemResponse) obj;
        if (detail == null) {
            if (other.detail != null)
                return false;
        } else if (!detail.equals(other.detail))
            return false;
        if (eid == null) {
            if (other.eid != null)
                return false;
        } else if (!eid.equals(other.eid))
            return false;
        if (publishingStatus != other.publishingStatus)
            return false;
        if (step != other.step)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "BatchItemResponse [eid=" + eid + ", publishingStatus=" + publishingStatus + ", step=" + step + ", detail=" + detail + "]";
    }

}
