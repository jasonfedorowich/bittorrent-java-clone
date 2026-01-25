import decoder.*;
import torrent.MetaInfoFile;

import java.nio.file.Files;
import java.nio.file.Path;
// import com.dampcake.bencode.Bencode; - available if you need it!

public class Main {

  public static void main(String[] args) throws Exception {
    // You can use print statements as follows for debugging, they'll be visible when running tests.
    System.err.println("Logs from your program will appear here!");
    
    String command = args[0];
    if("decode".equals(command)) {
      //  TODO: Uncomment the code below to pass the first stage
        String bencodedValue = args[1];
        String decoded;
        try {
          decoded = decodeBencode(bencodedValue);
        } catch(RuntimeException e) {
          System.out.println(e.getMessage());
          return;
        }
        System.out.println(decoded);

    } else if("info".equals(command)) {
        String fileName = args[1];
        byte[] file = Files.readAllBytes(Path.of(fileName));
        MetaInfoFile metaInfoFile = getMetaInfoFile(file);
        System.out.printf("Tracker URL: %s", metaInfoFile.getAnnounce().getUrl());
        System.out.printf("Length: %d", metaInfoFile.getInfo().getLength());
    }
    else {
      System.out.println("Unknown command: " + command);
    }

  }

  static String decodeBencode(String bencodedString) {
      return new Decoder().decode(bencodedString);
  }

  static MetaInfoFile getMetaInfoFile(byte[] bytes) {
      ByteQueue queue = new ByteQueue(bytes);
      ByteBendecoder decoder = new ByteBendecoder(queue);
      BencodedObject object = decoder.decode();
      return new MetaInfoFile((BencodedDictionary) object);
  }
}
