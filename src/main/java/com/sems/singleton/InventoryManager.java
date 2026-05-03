// File: singleton/InventoryManager.java — Maps to: FR4, FR16, UC-04, UC-13
// Singleton + Observer: central inventory with observer notifications
package com.sems.singleton;

import com.sems.model.Equipment;
import com.sems.observer.SystemObserver;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Central inventory tracking. Notifies observers on state changes.
 * Uses CopyOnWriteArrayList for thread-safe observer management.
 */
public class InventoryManager {

    private static volatile InventoryManager instance;
    private final List<SystemObserver> observers;

    private InventoryManager() {
        this.observers = new CopyOnWriteArrayList<>();
    }

    public static InventoryManager getInstance() {
        if (instance == null) {
            synchronized (InventoryManager.class) {
                if (instance == null) {
                    instance = new InventoryManager();
                }
            }
        }
        return instance;
    }

    public static synchronized void reset() {
        instance = null;
    }

    /** Register an observer — safe, ignores null. */
    public void addObserver(SystemObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    /** Remove an observer — safe, ignores null. */
    public void removeObserver(SystemObserver observer) {
        if (observer != null) {
            observers.remove(observer);
        }
    }

    /** Notify all observers — crash-safe, one failing observer won't block others. */
    public void notifyObservers(String event, Object data) {
        for (SystemObserver observer : observers) {
            try {
                if (observer != null) {
                    observer.update(event, data);
                }
            } catch (Exception e) {
                System.err.println("[InventoryManager] Observer error: " + e.getMessage());
            }
        }
    }

    /** Check if equipment is low stock (< 2 available). */
    public boolean isLowStock(Equipment equipment) {
        if (equipment == null) return false;
        return equipment.isAvailable() && equipment.getQuantity() < 2;
    }
}
