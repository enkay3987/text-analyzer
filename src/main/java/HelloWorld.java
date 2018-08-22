import com.analyzer.textanalyzer.IPdfReader;
import com.analyzer.textanalyzer.PdfReader;
import com.analyzer.textanalyzer.PdfReaderIText;
import com.analyzer.textanalyzer.Utils;

import java.io.File;
import java.io.IOException;

public class HelloWorld {

    public static void main(String[] args) throws IOException {
        if(args.length == 0)
            System.out.println("Usage: <command> <pdf_file_input_directory> <output_directory>");

        IPdfReader pdfReader = new PdfReaderIText();
        // Fetch the input directory
        File pdfInputDirectory = new File(args[0]);
        if(!pdfInputDirectory.exists())
            throw new IllegalArgumentException("PDF input files directory does not exist");
        if(!pdfInputDirectory.isDirectory())
            throw new IllegalArgumentException("PDF input files directory is not a directory");

        // Fetch the output directory
        File pdfOutputDirectory = new File(args[1]);
        if(!pdfOutputDirectory.exists())
            throw new IllegalArgumentException("Output files directory does not exist");
        if(!pdfOutputDirectory.isDirectory())
            throw new IllegalArgumentException("Output files directory is not a directory");

        File[] pdfFiles = pdfInputDirectory.listFiles();
        if(pdfFiles == null)
            System.out.println("No files to convert");
        else {
            for (File pdfFile : pdfFiles) {
                if(pdfFile.isFile()) {
                    System.out.println("Converting " + pdfFile.getName());
                    String pdfText = pdfReader.getPdfText(pdfFile);
                    Utils.writeToFile(new File(pdfOutputDirectory, getTextFileName(pdfFile.getName())), pdfText);
                } else {
                    System.out.println("Skipping " + pdfFile.getName() + ". Not a file");
                }
            }
            System.out.println("All files converted");
        }
    }

    private static String getTextFileName(String filename) {
        return filename + ".txt";
    }
}
