/*   
Copyright (C) 2021  Jonathan Elsner

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package spacetime_diagram;

import java.io.Serializable;
import java.util.UUID;

/**
 * Represents an event that occurs in spacetime.
 * 
 * @author Jonathan Elsner
 */
public class SpacetimeEvent implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

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
        double currentX = getX(observerBeta);

        this.x = LorentzTransform.xTransform(-observerBeta, currentX, observedT);
        this.t = LorentzTransform.tTransform(-observerBeta, currentX, observedT);
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
        double currentT = getT(observerBeta);

        this.x = LorentzTransform.xTransform(-observerBeta, observedX, currentT);
        this.t = LorentzTransform.tTransform(-observerBeta, observedX, currentT);
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