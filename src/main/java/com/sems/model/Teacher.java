// File: model/Teacher.java — Maps to: UC-01, UC-05, UC-06, FR1, FR6, FR7
package com.sems.model;

import com.sems.model.enums.UserRole;

/** Teacher user — can borrow equipment and submit return reports. */
public class Teacher extends User {
    public Teacher(String name, String email, String passwordHash) {
        super(name, email, passwordHash, UserRole.TEACHER);
    }
}
