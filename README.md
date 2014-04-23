java-ffmpeg-pipe
================

Simple yet powerful example of integration ffmpeg with java


<div width="100%" align="center">
<img src="https://raw.github.com/yev/java-ffmpeg-pipe/master/doc/Java-Ffmpeg-Pipe.jpg" align="center"/>
</div>

================

In my last project I encountered the very intresting task: save the snapshots (images) from an RTMP stream into the 
memcached server.

The first solution was to use the file system as a temporary media storage for getting the images from ffmpeg.
Then the java service listens for the events from the specific folder and once getting the event, read the file from FS and push the binary data to the memcached.
The problem is FS triggering event speed. It was too fast, al least for ext3. So the image wasn't completly written to the disk but Java service was already notified about the file creation. And binary data send to memcached was truncated and the image wasn't displayed properly.

We needed to get rid of FS and read row binary data directly from ffmpeg and push it to ff.
We can do it with a pipe.
But how to distiguish differnt images in the pipe binary stream.

Solution
----------

My hack is to use a particlur binary signature of the end of image format (png and jpeg). The valid image file has always 2 last bytes equal to FF D9
