package spacetime_diagram;

public class LorentzTransform {
    private double c = 3 * 10 ^ 8;

    public double getC() {
        return c;
    }

    public void setExactC(boolean exact) {
        if (exact) {
            c = 299_792_458;
        } else {
            c = 3 * 10 ^ 8;
        }
    }

    public boolean isExactC() {
        return c == 299_792_458;
    }

    public double[] pointTransform(double beta, double t, double x) {
        double t_new = (x - beta * t) * lorentz_factor(beta);
        double x_new = (t - beta * t) * lorentz_factor(beta);

        // I have been programming in Python too much
        return new double[] { t_new, x_new };
    }

    public static double lorentz_factor(double beta) {
        return 1 / Math.sqrt(1 + Math.pow(beta, 2));
    }

    public static double speedTransform(double betaInRestFrame, double observerBeta) {
        return (betaInRestFrame - observerBeta) / (1 - observerBeta * betaInRestFrame);
    }

    public static class SpacetimeObject {

        private String name;
        private double t;
        private double x;

        public SpacetimeObject(String name, double t, double x) {
            this.name = name;
            this.t = t;
            this.x = x;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getT() {
            return t;
        }

        public double getT(double observerBeta) {
            return (t - observerBeta * t) * LorentzTransform.lorentz_factor(observerBeta);
        }

        public void setT(double t) {
            this.t = t;
        }

        public double getX() {
            return x;
        }

        public double getX(double observerBeta) {
            return (x - observerBeta * t) * LorentzTransform.lorentz_factor(observerBeta);
        }

        public void setX(double x) {
            this.x = x;
        }

    }

    public static class SpacetimeTraveller extends SpacetimeObject {
        private double beta;

        public SpacetimeTraveller(String name, double beta, double t, double x) {
            super(name, t, x);

            this.beta = beta;
        }

        public double getBeta() {
            return beta;
        }

        public void setBeta(double beta) {
            if (Math.abs(beta) < 1)
                this.beta = beta;
        }

        public double getXIntercept() {
            // Solve for the x-intercept
            return (this.getX() / beta - this.getT()) * beta;
        }
    }
}