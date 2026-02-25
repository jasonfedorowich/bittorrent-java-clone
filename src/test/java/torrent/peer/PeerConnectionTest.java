package torrent.peer;

import decoder.ByteBendecoder;
import decoder.ByteQueue;
import objects.BencodedDictionary;
import objects.BencodedObject;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import torrent.file.MetaInfoFile;
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
        peerConnection = new PeerConnectionFromMetaInf(peer.toString(), metaInfoFile, tracker.getPeerId());

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testHandshake() {
        String peer = peerConnection.handshake();
        Assertions.assertNotNull(peer);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2})
    @Disabled
    void testDownloadPiece(int pieceNumber) {
        String peer = peerConnection.handshake();
        Assertions.assertNotNull(peer);

        peerConnection.downloadPiece(pieceNumber, PeerConnectionTest.class.getResource("/test.file").getPath());

    }
}