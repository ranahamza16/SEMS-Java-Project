// File: model/BorrowRequest.java — Maps to: UC-05, UC-07, FR6, FR11, FR15
// Association: User ↔ Equipment — crash-safe with null checks everywhere
package com.sems.model;

import com.sems.model.enums.BorrowStatus;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Represents a borrow request linking a User to Equipment.
 * State: PENDING → APPROVED → ACTIVE → COMPLETED (or OVERDUE → COMPLETED).
 */
public class BorrowRequest {

    private final String requestId;
    private final User borrower;
    private final Equipment equipment;
    private final LocalDate requestDate;
    private LocalDate returnDate;
    private LocalDate actualReturnDate;
    private BorrowStatus status;

    public BorrowRequest(User borrower, Equipment equipment, LocalDate returnDate) {
        this.requestId = UUID.randomUUID().toString();
        this.borrower = borrower;
        this.equipment = equipment;
        this.requestDate = LocalDate.now();
        this.returnDate = (returnDate != null) ? returnDate : LocalDate.now().plusDays(7);
        this.actualReturnDate = null;
        this.status = BorrowStatus.PENDING;
    }

    // --- Getters (null-safe) ---
    public String getRequestId() { return requestId != null ? requestId : ""; }
    public User getBorrower() { return borrower; }
    public Equipment getEquipment() { return equipment; }
    public LocalDate getRequestDate() { return requestDate != null ? requestDate : LocalDate.now(); }
    public LocalDate getReturnDate() { return returnDate != null ? returnDate : LocalDate.now().plusDays(7); }
    public LocalDate getActualReturnDate() { return actualReturnDate; }
    public BorrowStatus getStatus() { return status != null ? status : BorrowStatus.PENDING; }

    // --- Setters ---
    public void setReturnDate(LocalDate date) { if (date != null) this.returnDate = date; }
    public void setActualReturnDate(LocalDate date) { this.actualReturnDate = date; }
    public void setStatus(BorrowStatus status) { if (status != null) this.status = status; }

    /** Check if this borrow is overdue based on current date. */
    public boolean isOverdue() {
        try {
            return (status == BorrowStatus.ACTIVE || status == BorrowStatus.OVERDUE)
                    && returnDate != null
                    && LocalDate.now().isAfter(returnDate);
        } catch (Exception e) {
            return false; // Safe default
        }
    }

    /** Get the borrower's name safely. */
    public String getBorrowerName() {
        return (borrower != null) ? borrower.getName() : "Unknown";
    }

    /** Get the equipment name safely. */
    public String getEquipmentName() {
        return (equipment != null) ? equipment.getEquipmentName() : "Unknown";
    }

    @Override
    public String toString() {
        return getEquipmentName() + " — " + getBorrowerName()
                + " [" + getStatus().getDisplayName() + "]"
                + " Due: " + getReturnDate();
    }
}
