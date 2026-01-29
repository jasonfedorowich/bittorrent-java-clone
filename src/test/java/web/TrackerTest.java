package web;

import decoder.ByteBendecoder;
import decoder.ByteQueue;
import objects.BencodedDictionary;
import objects.BencodedObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import torrent.MetaInfoFile;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class TrackerTest {

    MetaInfoFile metaInfoFile;

    @BeforeEach
    void setUp() throws IOException {
        InputStream is = TrackerTest.class.getResourceAsStream("/sample.torrent");
        byte[] bytes = is.readAllBytes();
        ByteQueue queue = new ByteQueue(bytes);
        ByteBendecoder decoder = new ByteBendecoder(queue);
        BencodedObject object = decoder.decode();
        metaInfoFile = new MetaInfoFile((BencodedDictionary) object);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void track() {
        Tracker tracker = new Tracker(metaInfoFile);
        Tracker.TrackerResponse trackerResponse = tracker.track();
        Assertions.assertNotNull(trackerResponse);
    }
}