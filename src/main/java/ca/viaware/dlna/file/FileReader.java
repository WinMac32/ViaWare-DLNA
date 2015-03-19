package ca.viaware.dlna.file;

import java.io.*;

public class FileReader {

    public static String readFile(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

        String lines = "";
        String line;
        while ((line = reader.readLine()) != null) {
            lines += line + "\n";
        }
        return lines;
    }

    public static byte[] readFileRaw(File file) throws IOException {
        InputStream input = new FileInputStream(file);
        byte[] bytes = new byte[(int)file.length()];
        input.read(bytes);
        return bytes;
    }

}
