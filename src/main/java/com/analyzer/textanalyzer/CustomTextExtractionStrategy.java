package com.analyzer.textanalyzer;

import com.itextpdf.text.pdf.parser.LineSegment;
import com.itextpdf.text.pdf.parser.LocationTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextRenderInfo;
import com.itextpdf.text.pdf.parser.Vector;

import java.util.Collections;
import java.util.List;

public class CustomTextExtractionStrategy extends LocationTextExtractionStrategy {
    @Override
    public void renderText(TextRenderInfo renderInfo) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(renderInfo.getText());
        buffer.append(" : ");
        buffer.append(getLineSegmentYCoordinate(renderInfo.getBaseline()));
        buffer.append(" - ");
        buffer.append(getLineSegmentYCoordinate(renderInfo.getAscentLine()));
        System.out.println(buffer.toString());
        super.renderText(renderInfo);
    }

    private float getLineSegmentYCoordinate(LineSegment lineSegment) {
        return getVectorYCoordinate(lineSegment.getStartPoint());
    }

    private float getVectorYCoordinate(Vector vector) {
        return vector.get(Vector.I2);
    }
}
