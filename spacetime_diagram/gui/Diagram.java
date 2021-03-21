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

package spacetime_diagram.gui;

import java.awt.Canvas;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.AffineTransform;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import spacetime_diagram.LorentzTransform;
import spacetime_diagram.SpacetimeEvent;
import spacetime_diagram.SpacetimeTraveller;

import java.awt.*;

/**
 * A simple graph of position versus time on which to draw spacetime diagrams.
 * The speed of the observer for this diagram can be manipulated to see how it
 * affects the diagram.
 * 
 * Position (x) is on the horizontal axis and time (t) is on the vertical axis,
 * as is standard practice for spacetime diagrams.
 * 
 * @author Jonathan Elsner
 * @see SpacetimeDiagramGUI
 * @see LorentzTransform
 */
public class Diagram extends Canvas implements ComponentListener, ListDataListener {

    /**
     *
     */
    private static final long serialVersionUID = 564720322024437238L;

    // Size of the graph (and axes) inside the canvas

    /**
     * Width of the area of the Canvas where items are drawn
     */
    private int drawingWidth = 750;

    /**
     * Height of the area of the Canvas where items are drawn
     */
    private int drawingHeight = 500;

    /**
     * Pixels per unit of x and t
     */
    private double pixelsPerUnit = 25;

    // Padding between the graph and the full canvas size
    private int sidePadding = 10;
    private int bottomPadding = 10;
    private int topPadding = 10;

    /**
     * Whether or not to draw a light cone extending from the origin
     */
    private boolean drawLightCone = false;

    /**
     * Whether to draw labels for each element on the graph
     */
    private boolean drawLabels = true;

    // How the lines are painted
    private Stroke lineStroke = new BasicStroke(3);

    // Worldline colors
    private Color[] lineColors = new Color[] { Color.red, Color.blue, Color.green };

    // How fast the reference frame for the graph is moving
    private double referenceFrameBeta = 0.0;

    // The list of objects to draw on the graph
    private Iterable<SpacetimeEvent> objects;

    /**
     * Create a Spacetime diagram for the iterable of SpacetimeObjects. As the
     * iterable updates, changes will be reflected on the Diagram as soon as the
     * diagram is repainted.
     * 
     * @param objects the objects to draw on the spacetime diagram
     */
    public Diagram(Iterable<SpacetimeEvent> objects) {
        this.objects = objects;

        // Calculate the full dimensions of the starting canvas
        int defaultWidth = drawingWidth + 2 * sidePadding;
        int defaultHeight = drawingHeight + topPadding + bottomPadding;

        // Set the beginning size of the canvas
        this.setMinimumSize(new Dimension(defaultWidth, defaultHeight));
        this.setPreferredSize(new Dimension(defaultWidth, defaultHeight));

        // Draw a white background for the diagram
        this.setBackground(Color.white);

        // Detect when the canvas is resized, so we can rescale the drawings
        this.addComponentListener(this);
    }

    /**
     * Whether or not the Diagram draws text labels for each element on the graph
     * 
     * @return {@code true} if labels are drawn for SpacetimeEvents and
     *         SpacetimeTravellers, {@code false} otherwise
     */
    public boolean isDrawLabels() {
        return drawLabels;
    }

    /**
     * Sets whether labels are drawn on the graph.
     * 
     * @param drawLabels {@code true} to draw labels, {@code false} to hide labels
     */
    public void setDrawLabels(boolean drawLabels) {
        this.drawLabels = drawLabels;
        repaint();
    }

    /**
     * Whether a light cone is drawn from the origin
     * 
     * @return {@code true} if a light cone is drawn, {@code false} otherwise
     */
    public boolean isDrawLightCone() {
        return drawLightCone;
    }

    /**
     * Set whether a light cone is drawn from the origin. If true, a light cone will
     * be drawn.
     * 
     * @param drawLightCone if {@code true} a light cone will be drawn, if
     *                      {@code false} a light cone will not be drawn
     */
    public void setDrawLightCone(boolean drawLightCone) {
        this.drawLightCone = drawLightCone;
        repaint();
    }

    /**
     * Draw the Spacetime diagram with the specified Graphics instance.
     * 
     * @param g the Graphics2D instance with which to draw the spacetime diagram
     */
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        Font font = g2d.getFont();
        AffineTransform transform = g2d.getTransform();

        // Flip y-axis so this behaves like "normal" cartesian coordinates
        transform.concatenate(AffineTransform.getScaleInstance(1, -1));
        // Translate y-axis down so positive values are actually visible
        // Translate x-axis over so the y-axis is centered
        transform.translate(this.getWidth() / 2, -(drawingHeight + topPadding));

        // Apply transform
        g2d.transform(transform);
        g2d.setFont(font.deriveFont(AffineTransform.getScaleInstance(1, -1)));

        g2d.setStroke(lineStroke);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw x-axis
        g.setColor(Color.BLACK);
        g.drawLine(-drawingWidth / 2, 0, drawingWidth / 2, 0);
        // Draw y-axis
        g.drawLine(0, 0, 0, drawingHeight);

        // Draw light-cone from origin
        if (drawLightCone) {
            g.setColor(Color.yellow);
            drawWorldLine(g2d, 0, -1);
            drawWorldLine(g2d, 0, 1);
        }

        // Draw all of the SpacetimeObjects
        int color = 0;
        for (SpacetimeEvent obj : objects) {
            int x = (int) Math.round(obj.getX(referenceFrameBeta) * pixelsPerUnit);
            int t = (int) Math.round(obj.getT(referenceFrameBeta) * pixelsPerUnit);
            int radius = 5;

            g2d.setColor(lineColors[(color++ % lineColors.length)]);

            // Draw a worldline if the object moves
            if (obj instanceof SpacetimeTraveller) {
                SpacetimeTraveller traveller = (SpacetimeTraveller) obj;

                // Find the speed and x-intercept of the traveller
                double travellerBeta = traveller.getBeta(referenceFrameBeta);
                int travellerIntercept = (int) Math.round(traveller.getXIntercept(referenceFrameBeta) * pixelsPerUnit);

                drawWorldLine(g2d, travellerIntercept, travellerBeta);
            } else { // Draw a dot if the object is an event
                g2d.fillOval(x - radius, t - radius, radius * 2, radius * 2);
            }

            if (drawLabels) {
                g2d.setColor(Color.black);
                g2d.drawString(obj.getName(), x + (int) (radius * 1.1), t + (int) (radius * 1.1));
            }
        }
    }

    /**
     * Draw a worldline with the specified Graphics instance
     * 
     * @param g2d    the Graphics2D instance with which to draw the world line
     * @param startX the x-intercept from which to draw the worldline
     * @param beta   the speed of the traveller along this world line. The slope of
     *               the worldline will be {@code 1/beta}
     */
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

    /**
     * Returns the speed of the observer who drew this reference frame
     * 
     * @return the speed of the observer who drew this reference frame as a fraction
     *         of the speed of light
     */
    public double getReferenceFrameBeta() {
        return referenceFrameBeta;
    }

    /**
     * Set the speed of the observer who will draw the Spacetime diagram
     * 
     * @param referenceFrameBeta the fraction of the speed of light at which the
     *                           observer drawing this diagram is travelling.
     */
    public void setReferenceFrameBeta(double referenceFrameBeta) {
        this.referenceFrameBeta = referenceFrameBeta;
    }

    @Override
    public void componentResized(ComponentEvent e) {
        double oldWidth = drawingWidth;

        drawingWidth = this.getWidth() - 2 * sidePadding;
        drawingHeight = this.getHeight() - topPadding - bottomPadding;

        // Scale units with resize
        pixelsPerUnit = pixelsPerUnit * drawingWidth / oldWidth;
    }

    @Override
    public void componentMoved(ComponentEvent e) {
    }

    @Override
    public void componentShown(ComponentEvent e) {
    }

    @Override
    public void componentHidden(ComponentEvent e) {
    }

    @Override
    public void intervalAdded(ListDataEvent e) {
        repaint();
    }

    @Override
    public void intervalRemoved(ListDataEvent e) {
        repaint();
    }

    @Override
    public void contentsChanged(ListDataEvent e) {
        repaint();
    }
}
