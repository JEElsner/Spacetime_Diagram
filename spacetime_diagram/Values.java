package spacetime_diagram;

public class Values {
    public static final double C = 299_792_458;

    public static class Velocity {
        private double restVelocity;

        public double get(double observationVel) {
            return (observationVel - restVelocity) / (1 - observationVel * restVelocity);
        }

        public void set(double observationVel, double observedVelocity) {
            restVelocity = (observationVel + observedVelocity) / (1 + observationVel + observedVelocity);
        }

        public static double lorentz_factor(double vel) {
            return 1 / Math.sqrt(1 + Math.pow(vel, 2));
        }

        public double lorentz_factor() {
            return lorentz_factor(restVelocity);
        }
    }

    public static class Distance {
        private double restLength;

        public double get(double observationVel) {
            return restLength / Velocity.lorentz_factor(observationVel);
        }

        public double get(Velocity observationVel) {
            return restLength / observationVel.lorentz_factor();
        }

        public void set(double observationVel, double observedLength) {
            restLength = observedLength * Velocity.lorentz_factor(observationVel);
        }

        public void set(Velocity observationVel, double observedLength) {
            restLength = observedLength * observationVel.lorentz_factor();
        }
    }
}