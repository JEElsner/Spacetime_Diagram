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
 * Compute Lorentz Transformations for special relativity.
 * 
 * See {@link https://en.wikipedia.org/wiki/Lorentz_transformation} for more
 * information.
 * 
 * @author Jonathan Elsner
 */
public class LorentzTransform {

    // Yeah, these constants are a little ridiculous. But hey, if we suddenly
    // discover that the speed of light is actually 17, I want to be able to change
    // it in one place, and make everything work.
    public static final double C_1 = 1;
    public static final double EXACT_C = 299_792_458;
    public static final double APPROXIMATE_C = 3 * 10 ^ 8;

    // The current value of c we are using in our calculations
    private static double c = C_1;

    /**
     * The value of the speed of light used in our calculations
     * 
     * @return The value of the speed of light used in our calculations
     */
    public static double getC() {
        return c;
    }

    /**
     * Set the value of the speed of light
     * 
     * @param exact If true, uses the exact value of the speed of light
     *              {@code EXACT_C}, otherwise, we approximate the speed of light to
     *              {@code APPROXIMATE_C}
     * @param one   If we do not use the exact speed of light, and this is true,
     *              then {@code c = C_1}
     * 
     * @see EXACT_C
     * @see APPROXIMATE_C
     * @see C_1
     */
    public static void setC(boolean exact, boolean one) {
        if (exact) {
            c = EXACT_C;
        } else if (one) {
            c = C_1;
        } else {
            c = APPROXIMATE_C;
        }
    }

    /**
     * Is the exact value of C used
     * 
     * @return {@code true} if c is exact
     */
    public static boolean isExactC() {
        return c == EXACT_C;
    }

    /**
     * Is c equal to 1
     * 
     * @return {@code true} if c is one
     */
    public static boolean isC1() {
        return c == C_1;
    }

    /**
     * Is c approximated
     * 
     * @return {@code true} if c is approximated
     */
    public static boolean isApproximateC() {
        return c == APPROXIMATE_C;
    }

    /**
     * Computes the x-coordinate in a moving reference frame relative to a
     * stationary one.
     * 
     * @param beta the speed in the moving reference frame as a fraction of the
     *             speed of light
     * @param x    the x value in the initial reference frame
     * @param t    the t value in the initial reference frame
     * @return the x value in the moving reference frame
     */
    public static double xTransform(double beta, double x, double t) {
        return lorentz_factor(beta) * (x - beta * c * t);
    }

    /**
     * Computes the t-coordinate in a moving reference frame relative to a
     * stationary one
     * 
     * @param beta the speed of the moving reference frame as a fraction of the
     *             speed of light
     * @param x    the x-coordinate in the initial reference frame
     * @param t    the t-coordinate in the initial reference frame
     * @return the t-coordinate in the moving reference frame
     */
    public static double tTransform(double beta, double x, double t) {
        return lorentz_factor(beta) * (c * t - beta * x) / c;
    }

    /**
     * Computes the t-coordinate in the rest frame from the t-coordinate in a moving
     * reference frame
     * 
     * @param beta  the speed of the moving reference frame according to the rest
     *              frame as a fraction of the speed of light
     * @param restX the x-coordinate in the rest reference frame
     * @param t     the t-coordinate in the moving reference frame
     * @return the t-coordinate in the rest frame
     */
    public static double restT(double beta, double restX, double t) {
        return (c * t / lorentz_factor(beta) + beta * restX) / c;
    }

    /**
     * Computes the x-coordinate in the rest frame from the x-coordinate in a moving
     * reference frame
     * 
     * @param beta  the speed of the moving reference frame according to the rest
     *              frame as a fraction of the speed of light
     * @param x     the x-coordinate in the moving reference frame
     * @param restT the t-coordinate in the rest reference frame
     * @return the x-coordinate in the rest frame
     */
    public static double restX(double beta, double x, double restT) {
        return (x / lorentz_factor(beta) + beta * c * restT);
    }

    /**
     * Computes the Lorentz Factor (gamma) used in other calculations
     * {@link https://en.wikipedia.org/wiki/Lorentz_factor}
     * 
     * @param beta
     * @return the Lorentz Factor
     */
    public static double lorentz_factor(double beta) {
        return 1 / Math.sqrt(1 + Math.pow(beta, 2));
    }

    /**
     * Computes the speed (beta) of an object according to a moving reference frame
     * using the speed of the object in the rest frame.
     * 
     * @param betaInRestFrame the speed of the object in the rest frame as a
     *                        fraction of the speed of light
     * @param observerBeta    the speed of the moving frame as a fraction of the
     *                        speed of light
     * @return the fraction of the speed of light the object moves in the moving
     *         frame
     */
    public static double speedTransform(double betaInRestFrame, double observerBeta) {
        return (betaInRestFrame - observerBeta) / (1 - observerBeta * betaInRestFrame);
    }

    /**
     * Computes the speed (beta) of an object in the rest frame using the speed of
     * the object according to a moving reference frame
     * 
     * @param observerBeta the speed of the observer as a fraction of the speed of
     *                     light
     * @param observedBeta the speed of the object according to the moving observer
     *                     as a fraction of the speed of light
     * @return the fraction of the speed of light at which the object moves in the
     *         rest frame.
     */
    public static double getRestBeta(double observerBeta, double observedBeta) {
        return (observerBeta + observedBeta) / (1 + observerBeta * observedBeta);
    }
}
