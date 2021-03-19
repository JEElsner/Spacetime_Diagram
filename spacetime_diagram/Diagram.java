package spacetime_diagram;

import java.awt.Canvas;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.AffineTransform;

import spacetime_diagram.LorentzTransform.SpacetimeObject;
import spacetime_diagram.LorentzTransform.SpacetimeTraveller;

import java.awt.*;

public class Diagram extends Canvas implements ComponentListener {

    /**
     *
     */
    private static final long serialVersionUID = 564720322024437238L;

    // Size of the graph (and axes) inside the canvas
    private int drawingWidth = 750;
    private int drawingHeight = 500;

    // Padding between the graph and the full canvas size
    private int sidePadding = 20;
    private int bottomPadding = 30;
    private int topPadding = 10;

    // How the lines are painted
    private Stroke lineStroke = new BasicStroke(3);

    // Worldline colors
    private Color[] lineColors = new Color[] { Color.red, Color.blue, Color.green };

    // How fast the reference frame for the graph is moving
    private double referenceFrameBeta = 0.0;

    // The list of objects to draw on the graph
    private Iterable<SpacetimeObject> objects;

    public Diagram(Iterable<SpacetimeObject> objects) {
        this.objects = objects;

        int defaultWidth = drawingWidth + 2 * sidePadding;
        int defaultHeight = drawingHeight + topPadding + bottomPadding;

        this.setMinimumSize(new Dimension(defaultWidth, defaultHeight));
        this.setPreferredSize(new Dimension(defaultWidth, defaultHeight));

        this.setBackground(Color.white);

        this.addComponentListener(this);
    }

    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        AffineTransform transform = g2d.getTransform();

        // Flip y-axis so this behaves like "normal" cartesian coordinates
        transform.concatenate(AffineTransform.getScaleInstance(1, -1));
        // Translate y-axis down so positive values are actually visible
        // Translate x-axis over so the y-axis is centered
        transform.translate(this.getWidth() / 2, -(drawingHeight + topPadding));

        // Apply transform
        g2d.transform(transform);

        g2d.setStroke(lineStroke);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw x-axis
        g.setColor(Color.BLACK);
        g.drawLine(-drawingWidth / 2, 0, drawingWidth / 2, 0);
        // Draw y-axis
        g.drawLine(0, 0, 0, drawingHeight);

        // Draw light-cone from origin
        g.setColor(Color.yellow);
        drawWorldLine(g2d, 0, -1);
        drawWorldLine(g2d, 0, 1);

        int color = 0;
        for (SpacetimeObject obj : objects) {
            g2d.setColor(lineColors[(color++ % lineColors.length)]);

            if (obj instanceof SpacetimeTraveller) {
                SpacetimeTraveller traveller = (SpacetimeTraveller) obj;

                double travellerBeta = traveller.getBeta(referenceFrameBeta);
                int travellerIntercept = (int) Math.round(traveller.getXIntercept(referenceFrameBeta));

                drawWorldLine(g2d, travellerIntercept, travellerBeta);
            } else {
                int radius = 5;

                int x = (int) Math.round(obj.getX(referenceFrameBeta));
                int t = (int) Math.round(obj.getT(referenceFrameBeta));

                g2d.fillOval(x - radius, t - radius, radius * 2, radius * 2);
            }
        }
    }

    public void drawWorldLine(Graphics2D g2d, int startX, double beta) {
        int dt = drawingHeight;
        int dx = (int) Math.round(dt * beta);

        // Keep the lines from extending beyond the left and right edges of the graph so
        // everything looks pretty.
        if (Math.abs(startX + dx) > drawingWidth / 2) {
            dx = (int) Math.copySign(drawingWidth / 2, dx) - startX;
            dt = (int) Math.round(dx / beta);
        }

        g2d.drawLine(startX, 0, startX + dx, dt);
    }

    public double getReferenceFrameBeta() {
        return referenceFrameBeta;
    }

    public void setReferenceFrameBeta(double referenceFrameBeta) {
        this.referenceFrameBeta = referenceFrameBeta;
    }

    @Override
    public void componentResized(ComponentEvent e) {
        drawingWidth = this.getWidth() - 2 * sidePadding;
        drawingHeight = this.getHeight() - topPadding - bottomPadding;
    }

    @Override
    public void componentMoved(ComponentEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void componentShown(ComponentEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void componentHidden(ComponentEvent e) {
        // TODO Auto-generated method stub

    }
}
