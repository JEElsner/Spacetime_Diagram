package spacetime_diagram;

import java.util.UUID;

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
     * @param beta the speed of the moving reference frame according to the rest
     *             frame as a fraction of the speed of light
     * @param x    the x-coordinate in the moving reference frame
     * @param t    the t-coordinate in the moving reference frame
     * @return the t-coordinate in the rest frame
     */
    public static double restT(double beta, double x, double t) {
        return (c * t / lorentz_factor(beta) + beta * x) / c;
    }

    /**
     * Computes the x-coordinate in the rest frame from the x-coordinate in a moving
     * reference frame
     * 
     * @param beta the speed of the moving reference frame according to the rest
     *             frame as a fraction of the speed of light
     * @param x    the x-coordinate in the moving reference frame
     * @param t    the t-coordinate in the moving reference frame
     * @return the x-coordinate in the rest frame
     */
    public static double restX(double beta, double x, double t) {
        return (x / lorentz_factor(beta) + beta * c * t);
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

    /**
     * Represents an event that occurs in spacetime.
     * 
     * @author Jonathan Elsner
     */
    public static class SpacetimeObject {

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
        public SpacetimeObject(String name, double t, double x) {
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

    /**
     * Represents a moving object in Spacetime
     * 
     * @author Jonathan Elsner
     */
    public static class SpacetimeTraveller extends SpacetimeObject {
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
            double beta = getRestBeta(observerBeta, observedBeta);

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
            return (this.getX(observerBeta) / beta - this.getT(observerBeta)) * getBeta(observerBeta);
        }
    }
}
