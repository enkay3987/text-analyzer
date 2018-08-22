package com.analyzer.textanalyzer.datamodels;

import com.itextpdf.text.pdf.parser.Vector;

public interface TextChunk extends Comparable<TextChunk> {
    /**
     * @return the text captured by this chunk
     */
    String getText();

    /**
     * @return an object holding location data about this TextChunk
     */
    TextChunkLocation getLocation();

    /**
     * @return the start location of the text
     */
    Vector getStartLocation();

    /**
     * @return the end location of the text
     */
    Vector getEndLocation();

    /**
     * @return the width of a single space character as rendered by this chunk
     */
    float getCharSpaceWidth();

    /**
     * Computes the distance between the end of 'other' and the beginning of this chunk
     * in the direction of this chunk's orientation vector.  Note that it's a bad idea
     * to call this for chunks that aren't on the same line and orientation, but we don't
     * explicitly check for that condition for performance reasons.
     *
     * @param other the other {@link TextChunk}
     * @return the number of spaces between the end of 'other' and the beginning of this chunk
     */
    float distanceFromEndOf(TextChunk other);

    void printDiagnostics();

    boolean sameLine(TextChunk lastChunk);
}
