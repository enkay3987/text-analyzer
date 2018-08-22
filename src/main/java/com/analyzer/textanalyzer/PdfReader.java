package com.analyzer.textanalyzer;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;

public class PdfReader implements IPdfReader {

    public String getPdfText(File pdfFile) throws IOException {
        PDDocument pdDocument = PDDocument.load(pdfFile);
        return new PDFTextStripper().getText(pdDocument);
    }
}
