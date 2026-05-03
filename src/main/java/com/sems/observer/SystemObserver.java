// File: observer/SystemObserver.java — Maps to: FR15, FR16, FR18
// Observer Pattern interface
package com.sems.observer;

/**
 * Observer interface — implemented by notification handlers.
 * Decouples event producers (services) from consumers (notifiers).
 */
public interface SystemObserver {
    void update(String event, Object data);
}
