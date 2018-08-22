package com.analyzer.textanalyzer;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.LocationTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;

import java.io.File;
import java.io.IOException;

public class PdfReaderIText implements IPdfReader {

    @Override
    public String getPdfText(File pdfFile) throws IOException {
        PdfReader pdfReader = new PdfReader(pdfFile.getAbsolutePath());
        TextExtractionStrategy textExtractionStrategy = new CopiedLocationTextExtractionStrategy();
        String text = PdfTextExtractor.getTextFromPage(pdfReader, 1, textExtractionStrategy);
        String[] lines = text.split("\n");
        return text;
    }
}
