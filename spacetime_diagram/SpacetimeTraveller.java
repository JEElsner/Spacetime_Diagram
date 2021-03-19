package spacetime_diagram;

/**
 * Represents a moving object in Spacetime
 * 
 * @author Jonathan Elsner
 */
public class SpacetimeTraveller extends SpacetimeEvent {
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
        return (this.getX(observerBeta) / beta - this.getT(observerBeta)) * getBeta(observerBeta);
    }
}