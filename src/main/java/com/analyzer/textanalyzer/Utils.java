package com.analyzer.textanalyzer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Utils {
    public static void writeToFile(File fileToWriteInto, String fileContent) throws IOException {
        if(fileToWriteInto.exists())
            fileToWriteInto.delete();

        fileToWriteInto.createNewFile();
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(fileToWriteInto, true));
            bufferedWriter.append(fileContent);
        } finally {
            if(bufferedWriter != null)
                bufferedWriter.close();
        }
    }

    /**
     * @param int1
     * @param int2
     * @return comparison of the two integers
     */
    public static int compareInts(int int1, int int2) {
        return int1 == int2 ? 0 : int1 < int2 ? -1 : 1;
    }
}
