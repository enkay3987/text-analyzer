package com.analyzer.textanalyzer.datamodels;

import java.util.*;

/**
 * Contains a group of text chunks stored according to a custom sorting criteria.
 */
public class GroupedTextChunks {
    private final List<TextChunk> textChunks = new ArrayList<>();
    private final Comparator<TextChunk> comparator;

    public GroupedTextChunks(Comparator<TextChunk> comparator) {
        this.comparator = comparator;
    }

    public void addTextChunk(TextChunk textChunk) {
        if(textChunk != null)
            this.textChunks.add(textChunk);
    }

    public void addTextChunks(Collection<TextChunk> textChunks) {
        if(textChunks != null)
            this.textChunks.addAll(textChunks);
    }

    public List<TextChunk> getTextChunks() {
        Collections.sort(textChunks, comparator);
        return textChunks;
    }

    public void printDiagnostics() {
        for(TextChunk textChunk : getTextChunks()) {
            textChunk.printDiagnostics();
        }
    }
}
