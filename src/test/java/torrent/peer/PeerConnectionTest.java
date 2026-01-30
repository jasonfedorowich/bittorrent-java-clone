package torrent.peer;

import decoder.ByteBendecoder;
import decoder.ByteQueue;
import objects.BencodedDictionary;
import objects.BencodedObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import torrent.MetaInfoFile;
import torrent.web.Tracker;

import java.io.IOException;
import java.io.InputStream;


class PeerConnectionTest {

    private PeerConnection peerConnection;
    private MetaInfoFile metaInfoFile;

    @BeforeEach
    void setUp() throws IOException {
        InputStream is = PeerConnectionTest.class.getResourceAsStream("/sample.torrent");
        byte[] bytes = is.readAllBytes();
        ByteQueue queue = new ByteQueue(bytes);
        ByteBendecoder decoder = new ByteBendecoder(queue);
        BencodedObject object = decoder.decode();
        metaInfoFile = new MetaInfoFile((BencodedDictionary) object);
        Tracker tracker = new Tracker(metaInfoFile);
        Tracker.TrackerResponse response = tracker.track();
        Tracker.Peer peer = response.getPeers().get(0);
        peerConnection = new PeerConnection(peer.toString(), metaInfoFile, tracker.getPeerId());

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testHandshake() {
        String peer = peerConnection.handshake();
        Assertions.assertNotNull(peer);
    }
}