package torrent;

import objects.BencodedDictionary;
import objects.BencodedObject;
import decoder.ByteQueue;
import decoder.ByteBendecoder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

class MetaInfoFileTest {

    ByteBendecoder decoder;

    @BeforeEach
    void setUp() throws IOException {
        InputStream is = MetaInfoFileTest.class.getResourceAsStream("/sample.torrent");
        byte[] bytes = is.readAllBytes();
        ByteQueue queue = new ByteQueue(bytes);
        decoder = new ByteBendecoder(queue);

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testValidParse(){
        BencodedObject object = decoder.decode();
        MetaInfoFile metaInfoFile = new MetaInfoFile((BencodedDictionary) object);

        Assertions.assertNotNull(metaInfoFile);
        Assertions.assertEquals("http://bittorrent-test-tracker.codecrafters.io/announce", metaInfoFile.getAnnounce().getUrl());
        Assertions.assertEquals(92063, metaInfoFile.getInfo().getLength());
    }

    @Test
    void testSha1OfInfo(){
        BencodedObject object = decoder.decode();
        MetaInfoFile metaInfoFile = new MetaInfoFile((BencodedDictionary) object);
        String sha1 = metaInfoFile.getInfo().getHash();
        Assertions.assertNotNull(sha1);
        Assertions.assertEquals("d69f91e6b2ae4c542468d1073a71d4ea13879a7f", sha1);
    }
}