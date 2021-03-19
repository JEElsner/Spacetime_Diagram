package spacetime_diagram;

import java.util.UUID;

/**
 * Represents an event that occurs in spacetime.
 * 
 * @author Jonathan Elsner
 */
public class SpacetimeEvent {

    private UUID uuid;

    private String name;
    private double t;
    private double x;

    /**
     * Constructs a new SpacetimeEvent
     * 
     * @param name the name of the event
     * @param t    the time at which the event occurs
     * @param x    the position at which the event occurs
     */
    public SpacetimeEvent(String name, double t, double x) {
        this.name = name;
        this.t = t;
        this.x = x;

        uuid = UUID.randomUUID();
    }

    /**
     * Returns the name of the event
     * 
     * @return the name of this SpacetimeEvent
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this event
     * 
     * @param name the new name for this event
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the time of this event for an observer moving with speed beta
     * 
     * @param observerBeta the speed of the observer as a fraction of the speed of
     *                     light
     * @return the t-coordinate for this event
     */
    public double getT(double observerBeta) {
        return LorentzTransform.tTransform(observerBeta, x, t);
    }

    /**
     * Set the time of this event for an observer moving with speed beta. This will
     * change the time of the event for all observers
     * 
     * @param observerBeta the speed of the observer as a fraction of the speed of
     *                     light
     * @param observedT    the new t-coordinate of this event
     */
    public void setT(double observerBeta, double observedT) {
        this.t = LorentzTransform.restT(observerBeta, x, observedT);
    }

    /**
     * Returns the location of this event for an observer moving with speed beta
     * 
     * @param observerBeta the speed of the observer as a fraction of the speed of
     *                     light
     * @return the x-coordinate of this event
     */
    public double getX(double observerBeta) {
        return LorentzTransform.xTransform(observerBeta, x, t);
    }

    /**
     * Sets the location of this event according to an observer moving with speed
     * beta. This will change the location of the event for all observers
     * 
     * @param observerBeta the speed of the observer as a fraction of the speed of
     *                     light
     * @param observedX    the new x-coordinate of this event
     */
    public void setX(double observerBeta, double observedX) {
        this.x = LorentzTransform.restX(observerBeta, observedX, t);
    }

    /**
     * Returns the string representation of this SpacetimeEvent. May not be unique
     * among all SpacetimeEvents.
     * 
     * @return the string representation of this SpacetimeEvent
     */
    public String toString() {
        return name;
    }

    /**
     * Returns the UUID for this SpacetimeEvent
     * 
     * @return the UUID for this event
     */
    public UUID getUUID() {
        return uuid;
    }

}