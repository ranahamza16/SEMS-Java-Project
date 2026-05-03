// File: model/Student.java — Maps to: UC-01, UC-05, UC-06, FR1, FR6, FR7
package com.sems.model;

import com.sems.model.enums.UserRole;

/** Student user — can borrow equipment and submit return reports. */
public class Student extends User {
    public Student(String name, String email, String passwordHash) {
        super(name, email, passwordHash, UserRole.STUDENT);
    }
}
