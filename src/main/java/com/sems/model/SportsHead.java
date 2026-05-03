// File: model/SportsHead.java — Maps to: UC-01, UC-08, FR1, FR2, FR10
package com.sems.model;

import com.sems.model.enums.UserRole;

/** Sports Head user — has admin privileges for approvals and management. */
public class SportsHead extends User {
    public SportsHead(String name, String email, String passwordHash) {
        super(name, email, passwordHash, UserRole.SPORTS_HEAD);
    }
}
