# SEMS - Feature Documentation

## Overview
The Sports Equipment Management System (SEMS) is a JavaFX application designed for managing the borrowing and returning of sports equipment at an educational institution. This document details the core features implemented.

## 1. Authentication System
* **Feature Description**: Secure, robust login system separating roles between Students, Teachers, and the Admin (Sports Head).
* **Architecture**: Uses `AuthService` and `SessionManager` (Singleton) to persist user sessions globally. 
* **Validation**: Null-safe, with exception handling on all inputs. Failed attempts show non-intrusive alert banners instead of modal dialogs.
* **Roles**:
  * `STUDENT` & `TEACHER`: Can borrow and return equipment.
  * `ADMIN` (Sports Head): Has global oversight, can approve/reject requests, and manage equipment inventory.

## 2. Dashboard
* **Feature Description**: Role-specific home screen summarizing system state.
* **Architecture**: Driven by `DashboardService` which aggregates stats via `DatabaseManager`.
* **Details**:
  * Displays "Total Equipment", "Available", "Active Borrows", and "Pending Requests".
  * Admin sees an actionable list of "Pending Requests" requiring approval/rejection.
  * Provides quick access sidebar navigation to Equipment, My Borrows, and Returns.
  * Dynamic Notification Badge (Bell Icon) indicating unread notifications.

## 3. Equipment Catalog & Borrowing
* **Feature Description**: Allows users to view available equipment and submit borrow requests.
* **Architecture**: `BorrowRequestScreen` and `EquipmentService` interface with the core `BorrowService`.
* **Details**:
  * Equipment rendered in responsive, scrollable grids.
  * Searching and filtering capabilities.
  * Submit logic defaults to a 7-day borrow period.
  * Submissions update inventory state locally and generate notifications for the Sports Head.

## 4. Equipment Management (Admin)
* **Feature Description**: Administrative CRUD operations for equipment inventory.
* **Architecture**: Accessible exclusively to `ADMIN` users via `EquipmentManagementScreen`.
* **Details**:
  * Allows creating new equipment (Name, Category, Quantity).
  * Safely transitioning equipment into a `RetiredState` (using the State Pattern) without breaking existing active borrow references.

## 5. My Borrows & Returning
* **Feature Description**: User portal to track personal active borrows and process returns.
* **Architecture**: Combines `MyBorrowsScreen` for tracking and `ReturnScreen` for processing. `ReturnService` validates conditions.
* **Details**:
  * Highlights `OVERDUE` items in bold red (`#C62828`).
  * Return process requires a Condition Report (`GOOD`, `NEEDS_INSPECTION`, `MINOR_REPAIR`, `MAJOR_REPAIR`).
  * If a damaged condition is selected, the system mandates a descriptive text note.

## 6. Real-Time Notifications
* **Feature Description**: Alerts for system events like request approvals, rejections, or new requests.
* **Architecture**: Uses the Observer Pattern where `BorrowService` subjects trigger updates to `NotificationObserver`s.
* **Details**:
  * Displays a chronological list of recent notifications.
  * Unread count presented directly in the top navigation bar.
  * Clicking the notification bell marks all as read and reloads the dashboard dynamically.

## UX/UI Design Adherence
* Implemented using a strict **PALETA 5** color scheme (`#2b2d42` dark navy, `#8d99ae` slate, `#edf2f4` off-white, `#ef233c` primary red, `#d90429` active red).
* Component overflows handled via JavaFX `ScrollPane` with `ScrollBarPolicy.AS_NEEDED` to prevent cropping on high-data screens.
* CSS `global.css` utilized to enforce dark typography on light backgrounds to ensure WCAG AA contrast ratio compliance.
