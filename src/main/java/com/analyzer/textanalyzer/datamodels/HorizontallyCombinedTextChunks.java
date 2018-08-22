package com.analyzer.textanalyzer.datamodels;

import com.itextpdf.text.pdf.parser.Vector;

import java.util.ArrayList;
import java.util.List;

public class HorizontallyCombinedTextChunks implements TextChunk {
    List<TextChunk> textChunks = new ArrayList<>();

    /**
     * Adds the new text chunk.
     * @param textChunk
     * @return true if the text chunk is on the same line as the other text chunks, false otherwise
     */
    public boolean addTextChunk(TextChunk textChunk) {
        if(!textChunks.isEmpty() && !textChunks.get(0).getLocation().sameLine(textChunk.getLocation()))
            return false;
        textChunks.add(textChunk);
        return true;
    }

    @Override
    public String getText() {
        return null;
    }

    @Override
    public TextChunkLocation getLocation() {
        return null;
    }

    @Override
    public Vector getStartLocation() {
        return null;
    }

    @Override
    public Vector getEndLocation() {
        return null;
    }

    @Override
    public float getCharSpaceWidth() {
        return 0;
    }

    @Override
    public float distanceFromEndOf(TextChunk other) {
        return 0;
    }

    @Override
    public void printDiagnostics() {

    }

    @Override
    public boolean sameLine(TextChunk lastChunk) {
        return false;
    }

    @Override
    public int compareTo(TextChunk o) {
        return 0;
    }
}
