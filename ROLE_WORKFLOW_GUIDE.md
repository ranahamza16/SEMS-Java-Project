# SEMS Role Workflow Guide

This document outlines the step-by-step workflows for each user role within the Sports Equipment Management System (SEMS), including what each role can do, their expected notifications, and their login credentials.

---

## 1. STUDENT WORKFLOW
**Login Credentials:** `ali@sems.edu` / `Student1!`

### What Students Can Do:
- View available equipment in the catalog
- Submit borrow requests for available equipment
- View their active borrows and overall history
- Return borrowed equipment with condition reports
- Receive notifications about the approval or rejection of their requests

### Step-by-Step Workflow:

**1. LOGIN**
- Open the application.
- Enter `ali@sems.edu` and `Student1!`.
- Click "Login". You are redirected to the Student Dashboard.

**2. BROWSE EQUIPMENT**
- Click the "Browse Equipment" quick action button or sidebar link.
- View the equipment grid, which displays status badges for all items.
- *Note:* Only items marked as `AVAILABLE` will have an active "Borrow" button.

**3. SUBMIT BORROW REQUEST**
- Click "Borrow" on the desired available equipment.
- Select your expected return date from the DatePicker.
- Click "Submit Request".
- You will see a success confirmation, and the request status becomes `PENDING`.

**4. WAIT FOR APPROVAL**
- Check the "My Active Borrows" section to see pending items.
- Keep an eye on your Notifications bell.
- **If approved:** The status changes to `ACTIVE` and you will receive an approval notification.
- **If rejected:** The equipment remains available, and you receive a notification detailing the rejection reason.

**5. RETURN EQUIPMENT**
- Click "Return Equipment" from the dashboard or sidebar.
- Select the equipment from your active borrows list.
- Fill out the condition report (e.g., Good, Needs Inspection, Minor/Major Repair).
- Add any relevant notes if damaged.
- Click "Submit Return". The equipment status updates automatically based on the reported condition.

**Expected Notifications for Student:**
- "Welcome to SEMS! You can borrow equipment from the catalog."
- "Your borrow request for [Equipment Name] is pending approval."
- "Your request for [Equipment Name] has been approved!"
- "Your request for [Equipment Name] was rejected. Reason: [Reason]"
- "You have overdue equipment. Please return immediately."

---

## 2. TEACHER WORKFLOW
**Login Credentials:** `sir@sems.edu` / `Teacher1!`

### What Teachers Can Do:
- Same as Students: view catalog, submit requests, view active borrows, and return equipment.
- The workflow mirrors the Student workflow.

### Step-by-Step Workflow:

**1. LOGIN**
- Open the application.
- Enter `sir@sems.edu` and `Teacher1!`.
- Click "Login". You are redirected to the Teacher Dashboard.

**2. BROWSE EQUIPMENT**
- Click "Browse Equipment".
- Search and filter items.

**3. SUBMIT BORROW REQUEST**
- Click "Borrow" on available equipment.
- Set the return date.
- Submit the request.

**4. VIEW ACTIVE BORROWS**
- Check the "My Active Borrows" section for current pending or active requests.

**5. RETURN EQUIPMENT**
- Click "Return Equipment", fill out the condition report, and submit.

**Expected Notifications for Teacher:**
- (Identical to Student notifications regarding their specific borrow requests).

---

## 3. SPORTS HEAD (ADMIN) WORKFLOW
**Login Credentials:** `admin@sems.edu` / `Admin123!`

### What Sports Head Can Do:
- View the complete dashboard with system-wide statistics.
- View and manage all equipment inventory (add, edit, retire, damage).
- Approve or reject all pending borrow requests.
- Receive system-wide alerts and notifications for new requests.
- Check borrowing history for the entire system.

### Step-by-Step Workflow:

**1. LOGIN**
- Open the application.
- Enter `admin@sems.edu` and `Admin123!`.
- Click "Login". You are redirected to the Sports Head Dashboard.

**2. VIEW DASHBOARD**
- Review the system stats: Equipment Available, Active Borrows, Pending Requests, and Unread Notifications.
- The "Pending Borrow Requests" section lists all requests waiting for approval.
- The "Recent Notifications" section shows system alerts and incoming request notices.

**3. APPROVE/REJECT REQUESTS**
- In the "Pending Borrow Requests" section, review incoming requests.
- **Click "Approve"**: The request status becomes `APPROVED`, equipment status updates to `BORROWED`, and a notification is dispatched to the borrower.
- **Click "Reject"**: The request status becomes `REJECTED`, the equipment remains `AVAILABLE`, and a notification is dispatched to the borrower.

**4. MANAGE EQUIPMENT**
- Click "Equipment" in the sidebar to open the Equipment Management Screen.
- View all inventory in a comprehensive table.
- **Add New Equipment:** Click the button, fill out Name, Category, Quantity, and Status, then save.
- **Edit Equipment:** Click "Edit", update the details, and save.
- **Retire Equipment:** Click "Retire" on outdated or permanently damaged gear to remove it from circulation.

**5. VIEW NOTIFICATIONS**
- Click the notification bell icon to mark all current notifications as read.
- View the full list of notifications directly on the dashboard.
- Expected to see new requests, low stock alerts, and overdue returns.

**6. CHECK BORROWING HISTORY**
- Click "History" or "My Borrows" to view all system transactions (borrow and return records).

**Expected Notifications for Sports Head:**
- "New borrow request from [Borrower Name] for [Equipment Name]"
- "Equipment [Equipment Name] is low in stock (Qty: [count])"
- "Overdue return: [Equipment] borrowed by [Name] (Due: [date])"
- "Equipment returned: [Equipment] by [Name] - Condition: [status]"

---

## Common Issues & Troubleshooting
- **Missing Notifications:** If you believe you submitted a request but don't see it, ensure you've refreshed the dashboard or navigated back to it to trigger the latest fetch from the database.
- **Pending Requests Disappearing:** If a request is approved or rejected, it will immediately disappear from the Pending Requests section.
- **Blank Spaces in UI:** The new notification rendering engine ensures that even read notifications are clearly visible with a white background and navy text, while unread notifications are highlighted in light blue.
