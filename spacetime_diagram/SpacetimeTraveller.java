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

/**
 * Represents a moving object in Spacetime
 * 
 * @author Jonathan Elsner
 */
public class SpacetimeTraveller extends SpacetimeEvent {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private double beta;

    /**
     * Constructs a new SpacetimeTraveller
     * 
     * @param name the name of the spacetime traveller
     * @param beta the speed of the traveller as a fraction of the speed of light
     * @param t    a t-coordinate of this traveller through which it passes on its
     *             journey
     * @param x    an x-coordinate of this traveller through which it passes on its
     *             journey
     */
    public SpacetimeTraveller(String name, double beta, double t, double x) {
        super(name, t, x);

        this.beta = beta;
    }

    /**
     * Returns the speed of this traveller according to a moving observer
     * 
     * @param observerBeta the speed of the observer as a fraction of the speed of
     *                     light
     * @return the speed of this traveller as a fraction of the speed of light
     */
    public double getBeta(double observerBeta) {
        return LorentzTransform.speedTransform(beta, observerBeta);
    }

    /**
     * Sets the speed of this traveller according to a moving observer. This will
     * change the speed of this traveller for all observers
     * 
     * @param observerBeta the speed of the observer as a fraction of the speed of
     *                     light
     * @param observedBeta the speed of the traveller according to the observer as a
     *                     fraction of the speed of light
     * @return the new speed of this traveller as a fraction of the speed of light
     */
    public double setBeta(double observerBeta, double observedBeta) {
        double beta = LorentzTransform.getRestBeta(observerBeta, observedBeta);

        if (Math.abs(beta) < 1) {
            this.beta = beta;
            return observedBeta;
        }

        return getBeta(observerBeta);
    }

    /**
     * Returns position of the traveller when {@code t = 0} according to a moving
     * observer
     * 
     * @param observerBeta the speed of the observer as a fraction of the speed of
     *                     light
     * @return the x-coordinate when {@code t = 0} according to the observer
     */
    public double getXIntercept(double observerBeta) {
        // Solve for the x-intercept
        return getX(observerBeta) - getBeta(observerBeta) * getT(observerBeta);
    }
}