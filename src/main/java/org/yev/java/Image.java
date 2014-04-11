package org.yev.java;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created with IntelliJ IDEA.
 * User: dev
 * Date: 05/04/14
 * Time: 18:21
 * To change this template use File | Settings | File Templates.
 */
public class Image {
  public static void main(String[] args) throws IOException {
    byte[] arr = Files.readAllBytes(Paths.get("/Users/dev/Desktop/yev.jpg"));
    System.out.println("-2"+arr[arr.length -2]);
    System.out.println("-1"+arr[arr.length -1]);
  }
}
