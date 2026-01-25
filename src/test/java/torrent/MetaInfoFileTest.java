package torrent;

import decoder.BencodedDictionary;
import decoder.BencodedObject;
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
}