# Sports Equipment Management System

**Group Members:** Ayan Shibli (24P-0750), Hamza Rana (24P-0587), Abdur Rehman (24P-0724)
**Course:** Software Design & Analysis | **Instructor:** Sir Umer Haroon

## Prerequisites
- Java 17 or higher
- Maven 3.8+

## Compile & Run
```bash
mvn clean javafx:run
```

## Demo Login Credentials

| Role        | Email              | Password   |
|-------------|-------------------|------------|
| Sports Head | admin@sems.edu    | Admin123!  |
| Student     | ali@sems.edu      | Student1!  |
| Teacher     | sir@sems.edu      | Teacher1!  |

## Design Patterns Used
This project was built adhering to strong Object-Oriented principles and incorporates the following design patterns:
- **Singleton:** Manages global state (`DatabaseManager`, `SessionManager`, `InventoryManager`) ensuring a single source of truth.
- **Observer:** Used for the notification system. The `InventoryManager` broadcasts events, and `NotificationObserver` listens to them independently.
- **Factory:** Centralizes and encapsulates the complex creation logic of `User` objects based on their roles (`UserFactory`).
- **Service/MVC Architecture:** Clean separation between Data Models, Business Logic (Services), and UI Views (JavaFX Screens).

## Features Demonstrated
1. **User Login with role-based navigation (UC-01)** — Students/Teachers see borrowing options; Sports Head sees admin approval queue
2. **Equipment Browsing and Borrow Request submission (UC-05)** — Search equipment, select item, choose return date, submit request
3. **Equipment Return with Condition Reporting (UC-07, UC-16)** — View active borrows, report condition (Good/Needs Inspection/Minor Repair/Major Repair), submit return

## Design Patterns Used
- **State** — Equipment lifecycle (Available → Borrowed → Available/Maintenance/Damaged) and Borrow Request lifecycle (Pending → Approved → Active → Completed)
- **Observer** — Notification system decoupled from business logic
- **Singleton** — SessionManager, InventoryManager, DatabaseManager (ensures single source of truth)
- **Factory** — UserFactory creates role-specific subclasses; EquipmentFactory creates equipment with correct initial state
- **MVC** — UI (screens) / Controller (services) / Model (domain classes) separation

## Color Palette (PALETA 5)
- Primary Blue: `#0080c8` — Buttons, headers
- Light Cyan: `#92dce5` — Hover states, secondary
- Off-White: `#f8f7f9` — Backgrounds
- Dark Navy: `#2b2d42` — Text, sidebar

## Project Structure
```
src/main/java/com/sems/
├── Main.java                    # Entry point
├── model/                       # Domain entities (User, Equipment, BorrowRequest, etc.)
├── model/enums/                 # Status enums (UserRole, EquipmentStatus, BorrowStatus, ConditionStatus)
├── state/equipment/             # State pattern — Equipment lifecycle
├── state/borrow/                # State pattern — Borrow request lifecycle
├── factory/                     # Factory pattern — UserFactory, EquipmentFactory
├── observer/                    # Observer pattern — NotificationObserver, OverdueAlertObserver
├── singleton/                   # Singletons — SessionManager, DatabaseManager, InventoryManager
├── service/                     # Business logic — AuthService, BorrowService, ReturnService
├── controller/                  # Controllers (lightweight, services handle logic)
└── ui/                          # JavaFX screens and components
    ├── App.java                 # Scene manager and navigation
    ├── components/              # Reusable: StatusBadge, AlertBanner
    └── screens/                 # LoginScreen, DashboardScreen, BorrowRequestScreen, ReturnScreen
```

## Demo Video
[
https://drive.google.com/file/d/1ybnHo3rDDvbwXP0cNNaUQtpDaXve5n0G/view?usp=drive_link
]
