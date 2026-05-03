// File: model/EquipmentCategory.java — Maps to: FR5, UC-19
// Composition with Equipment — instantiated inside Equipment constructor
package com.sems.model;

import java.util.UUID;

/**
 * Equipment category — composed inside Equipment (UML Composition).
 * Cannot exist independently without an Equipment parent.
 */
public class EquipmentCategory {

    private final String categoryId;
    private String name;
    private String description;

    public EquipmentCategory(String name, String description) {
        this.categoryId = UUID.randomUUID().toString();
        this.name = (name != null) ? name.trim() : "Uncategorized";
        this.description = (description != null) ? description.trim() : "";
    }

    public String getCategoryId() { return categoryId != null ? categoryId : ""; }
    public String getName() { return name != null ? name : "Uncategorized"; }
    public String getDescription() { return description != null ? description : ""; }

    public void setName(String name) { if (name != null) this.name = name.trim(); }
    public void setDescription(String desc) { if (desc != null) this.description = desc.trim(); }

    @Override
    public String toString() { return getName(); }
}
