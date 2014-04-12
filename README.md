java-ffmpeg-pipe
================

Simple yet powerful example of integration ffmpeg with java

================

In my last project I encountered the very intresting task: save the snapshots (images) from an RTMP stream into the 
memcached server.

The first solution was to use the file system as a temporary media for getting the images from ffmpeg.
Then the java service listens the events from the specific and once getting the event, read the file from FS and push the binary data to the memcached.
The problem is FS trigger. It was too fast. So the image wasn't completly written to the disk but Java service was already notified about the file creation.

We need to get rid of FS and read row binary data directly from ffmpeg.
We can do it with a pipe.
But how to distiguish differnt images in the pipe binary stream.

My hack is to use a particlur binary signature of the end of image format (png and jpeg). The valid image file has always 2 last bytes equal to FF D9

<img src="https://raw.github.com/yev/java-ffmpeg-pipe/master/doc/Java-Ffmpeg-Pipe.jpg" align="center"/>
