package spacetime_diagram;

import java.util.UUID;

/**
 * Compute Lorentz Transformations for special relativity
 * 
 * @author Jonathan Elsner
 */
public class LorentzTransform {

    private static final double C_1 = 1;
    private static final double EXACT_C = 299_792_458;
    private static final double APPROXIMATE_C = 3 * 10 ^ 8;

    private static double c = C_1;

    /**
     * @return The value of the speed of light used in our calculations
     */
    public static double getC() {
        return c;
    }

    public static void setC(boolean exact, boolean one) {
        if (exact) {
            c = EXACT_C;
        } else if (one) {
            c = C_1;
        } else {
            c = APPROXIMATE_C;
        }
    }

    public static boolean isExactC() {
        return c == EXACT_C;
    }

    public static boolean isC1() {
        return c == C_1;
    }

    public static boolean isApproximateC() {
        return c == APPROXIMATE_C;
    }

    public static double xTransform(double beta, double x, double t) {
        return lorentz_factor(beta) * (x - beta * c * t);
    }

    public static double tTransform(double beta, double x, double t) {
        return lorentz_factor(beta) * (c * t - beta * x) / c;
    }

    public static double restT(double beta, double x, double t) {
        return (c * t / lorentz_factor(beta) + beta * x) / c;
    }

    public static double restX(double beta, double x, double t) {
        return (x / lorentz_factor(beta) + beta * c * t);
    }

    public static double lorentz_factor(double beta) {
        return 1 / Math.sqrt(1 + Math.pow(beta, 2));
    }

    public static double speedTransform(double betaInRestFrame, double observerBeta) {
        return (betaInRestFrame - observerBeta) / (1 - observerBeta * betaInRestFrame);
    }

    public static double getRestBeta(double observerBeta, double observedBeta) {
        return (observerBeta + observedBeta) / (1 + observerBeta * observedBeta);
    }

    public static class SpacetimeObject {

        private UUID uuid;

        private String name;
        private double t;
        private double x;

        public SpacetimeObject(String name, double t, double x) {
            this.name = name;
            this.t = t;
            this.x = x;

            uuid = UUID.randomUUID();
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getT(double observerBeta) {
            return LorentzTransform.tTransform(observerBeta, x, t);
        }

        public void setT(double observerBeta, double observedT) {
            this.t = LorentzTransform.restT(observerBeta, x, observedT);
        }

        public double getX(double observerBeta) {
            return LorentzTransform.xTransform(observerBeta, x, t);
        }

        public void setX(double observerBeta, double observedX) {
            this.x = LorentzTransform.restX(observerBeta, observedX, t);
        }

        public String toString() {
            return name;
        }

        public UUID getUUID() {
            return uuid;
        }

    }

    public static class SpacetimeTraveller extends SpacetimeObject {
        private double beta;

        public SpacetimeTraveller(String name, double beta, double t, double x) {
            super(name, t, x);

            this.beta = beta;
        }

        public double getBeta(double observerBeta) {
            return LorentzTransform.speedTransform(beta, observerBeta);
        }

        public double setBeta(double observerBeta, double observedBeta) {
            double beta = getRestBeta(observerBeta, observedBeta);

            if (Math.abs(beta) < 1) {
                this.beta = beta;
                return observedBeta;
            }

            return getBeta(observerBeta);
        }

        public double getXIntercept(double observerBeta) {
            // Solve for the x-intercept
            return (this.getX(observerBeta) / beta - this.getT(observerBeta)) * getBeta(observerBeta);
        }
    }
}
