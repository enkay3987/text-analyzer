package com.analyzer.textanalyzer.datamodels;

import com.itextpdf.text.pdf.parser.Vector;

/**
 * Represents a chunk of text, it's orientation, and location relative to the orientation vector
 */
public class BasicTextChunk implements TextChunk {
    /**
     * the text of the chunk
     */
    private final String text;
    private final TextChunkLocation location;

    public BasicTextChunk(String string, Vector startLocation, Vector endLocation, float charSpaceWidth) {
        this(string, new TextChunkLocationDefaultImpl(startLocation, endLocation, charSpaceWidth));
    }

    public BasicTextChunk(String string, TextChunkLocation loc) {
        this.text = string;
        this.location = loc;
    }

    /**
     * @return the text captured by this chunk
     */
    public String getText() {
        return text;
    }

    /**
     * @return an object holding location data about this TextChunk
     */
    public TextChunkLocation getLocation() {
        return location;
    }

    /**
     * @return the start location of the text
     */
    public Vector getStartLocation() {
        return location.getStartLocation();
    }

    /**
     * @return the end location of the text
     */
    public Vector getEndLocation() {
        return location.getEndLocation();
    }

    /**
     * @return the width of a single space character as rendered by this chunk
     */
    public float getCharSpaceWidth() {
        return location.getCharSpaceWidth();
    }

    /**
     * Computes the distance between the end of 'other' and the beginning of this chunk
     * in the direction of this chunk's orientation vector.  Note that it's a bad idea
     * to call this for chunks that aren't on the same line and orientation, but we don't
     * explicitly check for that condition for performance reasons.
     *
     * @param other the other {@link TextChunk}
     * @return the number of spaces between the end of 'other' and the beginning of this chunk
     */
    public float distanceFromEndOf(TextChunk other) {
        return location.distanceFromEndOf(other.getLocation());
    }

    public void printDiagnostics() {
        System.out.println("Text (@" + location.getStartLocation() + " -> " + location.getEndLocation() + "): " + text);
        System.out.println("orientationMagnitude: " + location.orientationMagnitude());
        System.out.println("distPerpendicular: " + location.distPerpendicular());
        System.out.println("distParallelStart: " + location.distParallelStart());
        System.out.println("distParallelEnd: " + location.distParallelEnd());
    }

    /**
     * Compares based on orientation, perpendicular distance, then parallel distance
     *
     * @param rhs the other object
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(TextChunk rhs) {
        return location.compareTo(rhs.getLocation());
    }

    public boolean sameLine(TextChunk lastChunk) {
        return getLocation().sameLine(lastChunk.getLocation());
    }
}
