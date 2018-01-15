package com.ymy.common;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

public class FileUtils {
    public static String getFile(String fileName) {
        StringBuilder result = new StringBuilder("");
        URL resource = ClassLoader.getSystemResource(fileName);
        File file = new File(resource.getFile());
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                result.append(line).append("\n");
            }
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();

    }
}
