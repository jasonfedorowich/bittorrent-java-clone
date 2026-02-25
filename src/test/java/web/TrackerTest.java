package web;

import decoder.ByteBendecoder;
import decoder.ByteQueue;
import torrent.magnet.MagneticLinkV1;
import objects.BencodedDictionary;
import objects.BencodedObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import torrent.file.MetaInfoFile;
import torrent.web.Tracker;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

class TrackerTest {

    MetaInfoFile metaInfoFile;
    MagneticLinkV1 magneticLinkV1;

    @BeforeEach
    void setUp() throws IOException {
        InputStream is = TrackerTest.class.getResourceAsStream("/sample.torrent");
        byte[] bytes = is.readAllBytes();
        ByteQueue queue = new ByteQueue(bytes);
        ByteBendecoder decoder = new ByteBendecoder(queue);
        BencodedObject object = decoder.decode();
        metaInfoFile = new MetaInfoFile((BencodedDictionary) object);
        magneticLinkV1 = new MagneticLinkV1("magnet:?xt=urn:btih:ad42ce8109f54c99613ce38f9b4d87e70f24a165&dn=magnet1.gif&tr=http%3A%2F%2Fbittorrent-test-tracker.codecrafters.io%2Fannounce");
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

    @Test
    void testMagenticLink() {
        Tracker tracker = new Tracker(magneticLinkV1);
        Tracker.TrackerResponse trackerResponse = tracker.track();
        Assertions.assertNotNull(trackerResponse);
        List<Tracker.Peer> peers = trackerResponse.getPeers();
        Assertions.assertNotNull(peers);
    }
}