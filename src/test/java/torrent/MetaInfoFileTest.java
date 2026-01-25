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
import java.util.ArrayList;
import java.util.List;

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
    void testSha1fInfo(){
        BencodedObject object = decoder.decode();
        MetaInfoFile metaInfoFile = new MetaInfoFile((BencodedDictionary) object);
        String sha1 = metaInfoFile.getInfo().getInfoHash();
        Assertions.assertNotNull(sha1);
        Assertions.assertEquals("d69f91e6b2ae4c542468d1073a71d4ea13879a7f", sha1);
    }

    @Test
    void testShaHashOfPieces(){
        BencodedObject object = decoder.decode();
        MetaInfoFile metaInfoFile = new MetaInfoFile((BencodedDictionary) object);
        List<String> hashes = metaInfoFile.getInfo().getPiecesHashes();
        Assertions.assertNotNull(hashes);

        Assertions.assertEquals("e876f67a2a8886e8f36b136726c30fa29703022d", hashes.get(0));
        Assertions.assertEquals("6e2275e604a0766656736e81ff10b55204ad8d35", hashes.get(1));
        Assertions.assertEquals("f00d937a0213df1982bc8d097227ad9e909acc17", hashes.get(2));
    }

}