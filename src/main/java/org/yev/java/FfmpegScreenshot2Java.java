package org.yev.java;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class provides simple static method to get binary data (images) from the video input (avi video, RTMP) read by RTMP
 * Under the hood external ffmpeg process is started and does all video processing stuff.
 * The integration point between external ffmpeg program and java is pipe.
 */
public class FfmpegScreenshot2Java
{

  private FfmpegScreenshot2Java(){
  }

  /**
   * Image file format spec. Last two bytes should have the following values: FF D9
   * http://de.wikipedia.org/wiki/JPEG_File_Interchange_Format
   */
  public static final int JPEG_FILE_EOF_PRE_LAST = 255; //FF
  public static final int JPEG_FILE_EOF_LAST = 217;//D9

  public static void runProcessing(final String absolutePathToFfmpegExecutable, final String videoInput) throws Exception
  {
    if (absolutePathToFfmpegExecutable == null || absolutePathToFfmpegExecutable.isEmpty()){
      throw new IllegalArgumentException("Path to ffmpeg executable can't be null or empty");
    }
    if (Files.notExists(Paths.get(absolutePathToFfmpegExecutable))){
      throw new FileNotFoundException(String.format("Absolute path %s is wrong",absolutePathToFfmpegExecutable));
    }
    if (!Files.isExecutable(Paths.get(absolutePathToFfmpegExecutable))){
      throw new RuntimeException(String.format("file %s is not executable",absolutePathToFfmpegExecutable));
    }

    /*
    1. image frequence - each 5 seconds (-r 0.2)
    2. scale output image to 280 pixels in weight and applying the ratio for height  (-vf scale=280:-1)
    3. compress a bit the image (-q 3)

    You can castomize this by applying all options you need - follow very good ffmpeg doc at http://www.ffmpeg.org/ffmpeg-all.html
     */
    final String ffmpegCmdLine = String.format("%s -analyzeduration 0 -threads 1 -i %s -r 0.2 -vf scale=280:-1 " +
            "-sws_flags lanczos -f image2pipe -q 3 -",absolutePathToFfmpegExecutable, videoInput);
    String[] data = ffmpegCmdLine.split("\\s+");
    List<String> toExec = new ArrayList(data.length);
    Collections.addAll(toExec, data);
    ProcessBuilder pb = new ProcessBuilder(toExec);

    //Uncomment it and change up to your env to see the logs from ffmpeg
    //pb.redirectError(new java.io.File("~/ffmpeg.log"))  ;

    Process ffmpegProcess = pb.start();

    InputStream is = new BufferedInputStream(ffmpegProcess.getInputStream());


    int currentByte;
    int index = 0;
    int prelastByte = -2;
    ByteArrayOutputStream output =   new ByteArrayOutputStream();
    while ((currentByte = is.read()) != -1){
      output.write(currentByte);

      if (prelastByte == JPEG_FILE_EOF_PRE_LAST && currentByte == JPEG_FILE_EOF_LAST){
        // no in output you get the image as a binary array. You can efficiently save it in the database, memcached or file
        Files.write(Paths.get("/tmp/ff_"+ index++ +".jpeg"), output.toByteArray());
        output.reset();
      }
      prelastByte = currentByte;
    }

  }

  public static void main(String[] args) throws Exception{
    runProcessing("/usr/local/bin/ffmpeg","rtmp://127.0.0.1/mediastream/yev2");
  }
}
