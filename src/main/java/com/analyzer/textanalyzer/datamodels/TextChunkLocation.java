package com.analyzer.textanalyzer.datamodels;

import com.itextpdf.text.pdf.parser.Vector;

/**
 * Provides the location information for a text chunk.
 */
public interface TextChunkLocation extends Comparable<TextChunkLocation> {
    float distParallelEnd();

    float distParallelStart();

    int distPerpendicular();

    float getCharSpaceWidth();

    Vector getEndLocation();

    Vector getStartLocation();

    int orientationMagnitude();

    boolean sameLine(TextChunkLocation as);

    float distanceFromEndOf(TextChunkLocation other);

    boolean isAtWordBoundary(TextChunkLocation previous);
}
