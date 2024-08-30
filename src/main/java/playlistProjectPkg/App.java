package playlistProjectPkg;

import java.io.IOException;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.NotSupportedException;
import com.mpatric.mp3agic.UnsupportedTagException;

/**
 * Hello world!
 *
 */
public class App {
    public static void main( String[] args ) throws UnsupportedTagException, InvalidDataException, IOException, NotSupportedException, InterruptedException {
   	  PlaylistGenerator playlistGenerator = new PlaylistGenerator();
   	  playlistGenerator.start();
    }
}
  //OBS: I menyer där flera saker kan anges samtidigt separeras dessa med spaces!!!!!!