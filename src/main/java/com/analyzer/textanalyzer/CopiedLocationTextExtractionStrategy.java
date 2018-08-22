package com.analyzer.textanalyzer;

import com.analyzer.textanalyzer.datamodels.*;
import com.itextpdf.text.pdf.parser.*;

import java.util.*;

/**
 * <b>Development preview</b> - this class (and all of the parser classes) are still experiencing
 * heavy development, and are subject to change both behavior and interface.
 * <br>
 * A text extraction renderer that keeps track of relative position of text on page
 * The resultant text will be relatively consistent with the physical layout that most
 * PDF files have on screen.
 * <br>
 * This renderer keeps track of the orientation and distance (both perpendicular
 * and parallel) to the unit vector of the orientation.  Text is ordered by
 * orientation, then perpendicular, then parallel distance.  Text with the same
 * perpendicular distance, but different parallel distance is treated as being on
 * the same line.
 * <br>
 * This renderer also uses a simple strategy based on the font metrics to determine if
 * a blank space should be inserted into the output.
 *
 * @since 5.0.2
 */
public class CopiedLocationTextExtractionStrategy implements TextExtractionStrategy {
    /**
     * set to true for debugging
     */
    static boolean DUMP_STATE = false;

    /**
     * a summary of all found text
     */
    private final List<TextChunk> locationalResult = new ArrayList<>();

    private final TextChunkLocationStrategy tclStrat;

    /**
     * Creates a new text extraction renderer.
     */
    public CopiedLocationTextExtractionStrategy() {
        this(new TextChunkLocationStrategy() {
            public TextChunkLocation createLocation(TextRenderInfo renderInfo, LineSegment baseline) {
                return new TextChunkLocationDefaultImpl(baseline.getStartPoint(), baseline.getEndPoint(), renderInfo.getSingleSpaceWidth());
            }
        });
    }

    /**
     * Creates a new text extraction renderer, with a custom strategy for
     * creating new TextChunkLocation objects based on the input of the
     * TextRenderInfo.
     *
     * @param strat the custom strategy
     */
    public CopiedLocationTextExtractionStrategy(TextChunkLocationStrategy strat) {
        tclStrat = strat;
    }

    /**
     * @see com.itextpdf.text.pdf.parser.RenderListener#beginTextBlock()
     */
    public void beginTextBlock() {
    }

    /**
     * @see com.itextpdf.text.pdf.parser.RenderListener#endTextBlock()
     */
    public void endTextBlock() {
    }

    /**
     * @param str
     * @return true if the string starts with a space character, false if the string is empty or starts with a non-space character
     */
    private boolean startsWithSpace(String str) {
        if (str.length() == 0) return false;
        return str.charAt(0) == ' ';
    }

    /**
     * @param str
     * @return true if the string ends with a space character, false if the string is empty or ends with a non-space character
     */
    private boolean endsWithSpace(String str) {
        if (str.length() == 0) return false;
        return str.charAt(str.length() - 1) == ' ';
    }

    /**
     * Filters the provided list with the provided filter
     *
     * @param textChunks a list of all TextChunks that this strategy found during processing
     * @param filter     the filter to apply.  If null, filtering will be skipped.
     * @return the filtered list
     * @since 5.3.3
     */
    private List<TextChunk> filterTextChunks(List<TextChunk> textChunks, TextChunkFilter filter) {
        if (filter == null)
            return textChunks;

        List<TextChunk> filtered = new ArrayList<TextChunk>();
        for (TextChunk textChunk : textChunks) {
            if (filter.accept(textChunk))
                filtered.add(textChunk);
        }
        return filtered;
    }

    /**
     * Determines if a space character should be inserted between a previous chunk and the current chunk.
     * This method is exposed as a callback so subclasses can fine time the algorithm for determining whether a space should be inserted or not.
     * By default, this method will insert a space if the there is a gap of more than half the font space character width between the end of the
     * previous chunk and the beginning of the current chunk.  It will also indicate that a space is needed if the starting point of the new chunk
     * appears *before* the end of the previous chunk (i.e. overlapping text).
     *
     * @param chunk         the new chunk being evaluated
     * @param previousChunk the chunk that appeared immediately before the current chunk
     * @return true if the two chunks represent different words (i.e. should have a space between them).  False otherwise.
     */
    protected boolean isChunkAtWordBoundary(TextChunk chunk, TextChunk previousChunk) {
        return chunk.getLocation().isAtWordBoundary(previousChunk.getLocation());
    }

    /**
     * Gets text that meets the specified filter
     * If multiple text extractions will be performed for the same page (i.e. for different physical regions of the page),
     * filtering at this level is more efficient than filtering using {@link FilteredRenderListener} - but not nearly as powerful
     * because most of the RenderInfo state is not captured in {@link TextChunk}
     *
     * @param chunkFilter the filter to to apply
     * @return the text results so far, filtered using the specified filter
     */
//    public String getResultantText(TextChunkFilter chunkFilter) {
//        if (DUMP_STATE) dumpState();
//
//        List<TextChunk> filteredTextChunks = filterTextChunks(locationalResult, chunkFilter);
//        Collections.sort(filteredTextChunks);
//
//        Map<Integer, GroupedTextChunks> sameHorizontalLineTextChunks = new LinkedHashMap<>();
//        // Create a map of all the text chunks in the same horizontal line grouped together
//        for(TextChunk textChunk : filteredTextChunks) {
//            Integer textChunkPerperdicularDistance = textChunk.getLocation().distPerpendicular();
//            if(!sameHorizontalLineTextChunks.containsKey(textChunkPerperdicularDistance))
//                sameHorizontalLineTextChunks.put(textChunkPerperdicularDistance, new GroupedTextChunks(new SameHorizontalLineTextChunkComparator()));
//
//            sameHorizontalLineTextChunks.get(textChunkPerperdicularDistance).addTextChunk(textChunk);
//        }
//
//        Map<Float, GroupedTextChunks> sameVerticalLineTextChunks = new LinkedHashMap<>();
//        // Create a map of all the text chunks in the same horizontal line grouped together
//        for(TextChunk textChunk : filteredTextChunks) {
//            Float textChunkParallelStart = textChunk.getLocation().distParallelStart();
//            if(!sameVerticalLineTextChunks.containsKey(textChunkParallelStart))
//                sameVerticalLineTextChunks.put(textChunkParallelStart, new GroupedTextChunks(new SameHorizontalLineTextChunkComparator()));
//
//            sameVerticalLineTextChunks.get(textChunkParallelStart).addTextChunk(textChunk);
//        }
//
//        for(GroupedTextChunks groupedTextChunks : sameVerticalLineTextChunks.values()) {
//            groupedTextChunks.printDiagnostics();
//            System.out.println("************");
//        }
//
//        Integer sameLineCoordinateConsideration = 10;
//
//        List<GroupedTextChunks> combinedLinesTextChunks = new ArrayList<>();
//        Map.Entry<Integer, GroupedTextChunks> previousLineTextChunksEntry = null;
//        for(Map.Entry<Integer, GroupedTextChunks> currentLineTextChunksEntry : sameHorizontalLineTextChunks.entrySet()) {
//            // Either this is the first line or the difference in perpendicular distance between previous and current line falls out of same line consideration, then add a new
//            // line
//            if(previousLineTextChunksEntry == null || ((Math.abs(previousLineTextChunksEntry.getKey() - currentLineTextChunksEntry.getKey()) > sameLineCoordinateConsideration))) {
//                combinedLinesTextChunks.add(new GroupedTextChunks(new MultipleLineTextChunkComparator()));
//            }
//            // Add the text chunks from the line in consideration to the combined line structure
//            GroupedTextChunks currentLineTextChunks = combinedLinesTextChunks.get(combinedLinesTextChunks.size() - 1);
//            currentLineTextChunks.addTextChunks(currentLineTextChunksEntry.getValue().getTextChunks());
//
//            previousLineTextChunksEntry = currentLineTextChunksEntry;
//        }
//
//        StringBuilder sb = new StringBuilder();
//
//        for(GroupedTextChunks groupedTextChunks : combinedLinesTextChunks) {
//            for(TextChunk textChunk : groupedTextChunks.getTextChunks()) {
//                sb.append(textChunk.text);
//                sb.append(' ');
//            }
//            sb.append('\n');
//        }
//        return sb.toString();
//    }

    public String getResultantText(TextChunkFilter chunkFilter) {
        if (DUMP_STATE) dumpState();

        List<TextChunk> filteredTextChunks = filterTextChunks(locationalResult, chunkFilter);
        Collections.sort(filteredTextChunks);

        Map<Integer, GroupedTextChunks> sameHorizontalLineTextChunks = new LinkedHashMap<>();
        // Create a map of all the text chunks in the same horizontal line grouped together
        for(TextChunk textChunk : filteredTextChunks) {
            Integer textChunkPerperdicularDistance = textChunk.getLocation().distPerpendicular();
            if(!sameHorizontalLineTextChunks.containsKey(textChunkPerperdicularDistance))
                sameHorizontalLineTextChunks.put(textChunkPerperdicularDistance, new GroupedTextChunks(new SameHorizontalLineTextChunkComparator()));

            sameHorizontalLineTextChunks.get(textChunkPerperdicularDistance).addTextChunk(textChunk);
        }

        Integer sameLineCoordinateConsideration = 3;

        List<GroupedTextChunks> combinedLinesTextChunks = new ArrayList<>();
        Map.Entry<Integer, GroupedTextChunks> previousLineTextChunksEntry = null;
        for(Map.Entry<Integer, GroupedTextChunks> currentLineTextChunksEntry : sameHorizontalLineTextChunks.entrySet()) {
            // Either this is the first line or the difference in perpendicular distance between previous and current line falls out of same line consideration, then add a new
            // line
            if(previousLineTextChunksEntry == null || ((Math.abs(previousLineTextChunksEntry.getKey() - currentLineTextChunksEntry.getKey()) > sameLineCoordinateConsideration))) {
                combinedLinesTextChunks.add(new GroupedTextChunks(new MultipleLineTextChunkComparator()));
            }
            // Add the text chunks from the line in consideration to the combined line structure
            GroupedTextChunks currentLineTextChunks = combinedLinesTextChunks.get(combinedLinesTextChunks.size() - 1);
            currentLineTextChunks.addTextChunks(currentLineTextChunksEntry.getValue().getTextChunks());

            previousLineTextChunksEntry = currentLineTextChunksEntry;
        }

        List<GroupedTextChunks> horizontalLineTextChunks = new ArrayList<>();
        GroupedTextChunks previousLineChunks = null;
        for(GroupedTextChunks currentLineChunks : combinedLinesTextChunks) {
            // TODO: Revisit comparator
//            GroupedTextChunks multipleLineTextChunks = new GroupedTextChunks(new SameHorizontalLineTextChunkComparator());

            if(previousLineChunks != null) {
                float score = LocationUtils.getHorizontalSimilarityScore(previousLineChunks, currentLineChunks);
                System.out.println("Line 1: ");
                previousLineChunks.printDiagnostics();
                System.out.println("\nLine 2: ");
                currentLineChunks.printDiagnostics();
                System.out.println("\nScore: " + score);
                System.out.println("*********");
            }
            previousLineChunks = currentLineChunks;
        }

        Map<Float, GroupedTextChunks> sameVerticalLineTextChunks = new LinkedHashMap<>();
        // Create a map of all the text chunks in the same horizontal line grouped together
        for(TextChunk textChunk : filteredTextChunks) {
            Float textChunkParallelStart = textChunk.getLocation().distParallelStart();
            if(!sameVerticalLineTextChunks.containsKey(textChunkParallelStart))
                sameVerticalLineTextChunks.put(textChunkParallelStart, new GroupedTextChunks(new SameHorizontalLineTextChunkComparator()));

            sameVerticalLineTextChunks.get(textChunkParallelStart).addTextChunk(textChunk);
        }

        /*for(GroupedTextChunks groupedTextChunks : sameVerticalLineTextChunks.values()) {
            groupedTextChunks.printDiagnostics();
            System.out.println("************");
        }*/



        StringBuilder sb = new StringBuilder();

        for(GroupedTextChunks groupedTextChunks : combinedLinesTextChunks) {
            for(TextChunk textChunk : groupedTextChunks.getTextChunks()) {
                sb.append(textChunk.getText());
                sb.append(' ');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    /**
     * Returns the result so far.
     *
     * @return a String with the resulting text.
     */
    public String getResultantText() {

        return getResultantText(null);

    }

    /**
     * Used for debugging only
     */
    private void dumpState() {
        Collections.sort(locationalResult);
        for (TextChunk location : locationalResult) {
            location.printDiagnostics();

            System.out.println();
        }
    }

    /**
     * @see com.itextpdf.text.pdf.parser.RenderListener#renderText(com.itextpdf.text.pdf.parser.TextRenderInfo)
     */
    public void renderText(TextRenderInfo renderInfo) {
        LineSegment segment = renderInfo.getBaseline();
        if (renderInfo.getRise() != 0) { // remove the rise from the baseline - we do this because the text from a super/subscript render operations should probably be considered as part of the baseline of the text the super/sub is relative to
            Matrix riseOffsetTransform = new Matrix(0, -renderInfo.getRise());
            segment = segment.transformBy(riseOffsetTransform);
        }
        TextChunk tc = new BasicTextChunk(renderInfo.getText(), tclStrat.createLocation(renderInfo, segment));
        locationalResult.add(tc);
    }

    public static interface TextChunkLocationStrategy {
        TextChunkLocation createLocation(TextRenderInfo renderInfo, LineSegment baseline);
    }

    public static class SameHorizontalLineTextChunkComparator implements Comparator<TextChunk> {
        @Override
        public int compare(TextChunk o1, TextChunk o2) {
            if (o1 == o2) return 0; // not really needed, but just in case

            int rslt;
            rslt = Utils.compareInts(o1.getLocation().distPerpendicular(), o2.getLocation().distPerpendicular());
            if (rslt != 0) return rslt;

            return Float.compare(o1.getLocation().distParallelStart(), o2.getLocation().distParallelStart());
        }
    }

    public static class SameVerticalLineTextChunkComparator implements Comparator<TextChunk> {
        @Override
        public int compare(TextChunk o1, TextChunk o2) {
            if (o1 == o2) return 0; // not really needed, but just in case

            int rslt;
            rslt = Float.compare(o1.getLocation().distParallelStart(), o2.getLocation().distParallelStart());
            if (rslt != 0) return rslt;

            return Utils.compareInts(o1.getLocation().distPerpendicular(), o2.getLocation().distPerpendicular());
        }
    }

    public static class MultipleLineTextChunkComparator implements Comparator<TextChunk> {
        @Override
        public int compare(TextChunk o1, TextChunk o2) {
            if (o1 == o2) return 0; // not really needed, but just in case

            return Float.compare(o1.getLocation().distParallelStart(), o2.getLocation().distParallelStart());
        }
    }



    /**
     * no-op method - this renderer isn't interested in image events
     *
     * @see com.itextpdf.text.pdf.parser.RenderListener#renderImage(com.itextpdf.text.pdf.parser.ImageRenderInfo)
     * @since 5.0.1
     */
    public void renderImage(ImageRenderInfo renderInfo) {
        // do nothing
    }

    /**
     * Specifies a filter for filtering {@link TextChunk} objects during text extraction
     *
     * @since 5.3.3
     */
    public static interface TextChunkFilter {
        /**
         * @param textChunk the chunk to check
         * @return true if the chunk should be allowed
         */
        public boolean accept(TextChunk textChunk);
    }
}