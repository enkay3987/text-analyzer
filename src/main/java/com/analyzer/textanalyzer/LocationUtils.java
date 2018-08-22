package com.analyzer.textanalyzer;

import com.analyzer.textanalyzer.datamodels.GroupedTextChunks;
import com.analyzer.textanalyzer.datamodels.TextChunk;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class LocationUtils {
    /**
     * Compares the text chunks from 2 horizontal lines and returns a similarity score.
     * @param line1
     * @param line2
     * @return
     */
    public static float getHorizontalSimilarityScore(GroupedTextChunks line1, GroupedTextChunks line2) {
        Set<Float> line1ParallelStarts = getDistanceParallelStarts(line1.getTextChunks());
        Set<Float> line1ParallelEnds = getDistanceParallelEnds(line1.getTextChunks());
        Set<Float> line2ParallelStarts = getDistanceParallelStarts(line2.getTextChunks());
        Set<Float> line2ParallelEnds = getDistanceParallelEnds(line2.getTextChunks());

        int noOfline1TextChunks = line1.getTextChunks().size();
        int noOfline2TextChunks = line2.getTextChunks().size();

        line1ParallelStarts.removeAll(line2ParallelStarts);
        line1ParallelEnds.removeAll(line2ParallelEnds);

        int parallelStartMatches = noOfline1TextChunks - line1ParallelStarts.size();
        int parallelEndMatches = noOfline2TextChunks - line2ParallelEnds.size();

        return (parallelStartMatches + parallelEndMatches) / (float) Math.max(noOfline1TextChunks, noOfline2TextChunks);
    }

    private static Set<Float> getDistanceParallelStarts(Collection<TextChunk> textChunks) {
        Set<Float> distanceParallelStarts = new HashSet<>();
        for(TextChunk textChunk : textChunks) {
            distanceParallelStarts.add(textChunk.getLocation().distParallelStart());
        }
        return distanceParallelStarts;
    }

    private static Set<Float> getDistanceParallelEnds(Collection<TextChunk> textChunks) {
        Set<Float> distanceParallelEnds = new HashSet<>();
        for(TextChunk textChunk : textChunks) {
            distanceParallelEnds.add(textChunk.getLocation().distParallelEnd());
        }
        return distanceParallelEnds;
    }
}
