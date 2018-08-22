package com.analyzer.textanalyzer;

import java.io.File;
import java.io.IOException;

public interface IPdfReader {

    String getPdfText(File pdfFile) throws IOException;
}
