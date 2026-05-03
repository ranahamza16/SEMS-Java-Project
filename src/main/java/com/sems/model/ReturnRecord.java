// File: model/ReturnRecord.java — Maps to: UC-07, UC-16, FR8, FR17
package com.sems.model;

import com.sems.model.enums.ConditionStatus;
import java.time.LocalDate;
import java.util.UUID;

/** Records a completed return with condition assessment. */
public class ReturnRecord {
    private final String returnId;
    private final BorrowRequest borrowRequest;
    private final LocalDate returnDate;
    private final ConditionStatus condition;
    private final String notes;

    public ReturnRecord(BorrowRequest borrowRequest, ConditionStatus condition, String notes) {
        this.returnId = UUID.randomUUID().toString();
        this.borrowRequest = borrowRequest;
        this.returnDate = LocalDate.now();
        this.condition = (condition != null) ? condition : ConditionStatus.GOOD;
        this.notes = (notes != null) ? notes.trim() : "";
    }

    public String getReturnId() { return returnId != null ? returnId : ""; }
    public BorrowRequest getBorrowRequest() { return borrowRequest; }
    public LocalDate getReturnDate() { return returnDate != null ? returnDate : LocalDate.now(); }
    public ConditionStatus getCondition() { return condition != null ? condition : ConditionStatus.GOOD; }
    public String getNotes() { return notes != null ? notes : ""; }
}
