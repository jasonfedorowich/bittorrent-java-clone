package encoder;

import decoder.ByteBendecoder;
import decoder.ByteQueue;
import objects.BencodedDictionary;
import objects.BencodedObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

class BencoderTest {

    ByteBendecoder decoder;

    @BeforeEach
    void setUp() throws IOException {
        InputStream is = BencoderTest.class.getResourceAsStream("/sample.torrent");
        byte[] bytes = is.readAllBytes();
        ByteQueue queue = new ByteQueue(bytes);
        decoder = new ByteBendecoder(queue);


    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testEncode() throws IOException {
        BencodedObject bencodedObject = decoder.decode();
        Bencoder encoder = new Bencoder();
        BencodedDictionary dictionary = (BencodedDictionary) bencodedObject;
        BencodedObject object = dictionary.get("info");
        List<Byte> encodedInfo = encoder.encode(object);
        String sha = getSha1OfInfo(encodedInfo);

        InputStream stream = BencoderTest.class.getResourceAsStream("/encodedinfo.txt");
        byte[] bytes = stream.readAllBytes();

        for(int i=0; i<50; i++) {
           Assertions.assertEquals(bytes[i], encodedInfo.get(i));
        }

    }

    static public String getSha1OfInfo(List<Byte> info) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] bytes = new byte[info.size()];
            for (int i = 0; i < info.size(); i++) {
                bytes[i] = info.get(i);
            }
            byte[] hash = md.digest(bytes);

            StringBuilder hexString = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();


        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}