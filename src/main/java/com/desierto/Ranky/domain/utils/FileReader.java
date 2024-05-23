package com.desierto.Ranky.domain.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileReader {

  public static String read(String path) {
    try {
      return Files.readString(
          Paths.get(path));
    } catch (IOException e) {
      return "We couldn't retrieve your message correctly. Contact the code owners please.";
    }
  }

}
