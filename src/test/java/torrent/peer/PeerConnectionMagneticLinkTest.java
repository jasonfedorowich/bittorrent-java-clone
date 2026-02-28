package torrent.peer;

import torrent.magnet.MagneticLinkV1;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import torrent.web.Tracker;

import java.io.IOException;


class PeerConnectionMagneticLinkTest {

    private PeerConnection peerConnection;
    private MagneticLinkV1 magneticLink;

    @BeforeEach
    void setUp() throws IOException {
        magneticLink = new MagneticLinkV1("magnet:?xt=urn:btih:ad42ce8109f54c99613ce38f9b4d87e70f24a165&dn=magnet1.gif&tr=http%3A%2F%2Fbittorrent-test-tracker.codecrafters.io%2Fannounce");
        Tracker tracker = new Tracker(magneticLink);
        Tracker.TrackerResponse response = tracker.track();
        Tracker.Peer peer = response.getPeers().get(0);
        peerConnection = new PeerConnectionFromMagentic(peer.toString(), magneticLink, tracker.getPeerId());

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testHandshake() {
        String peer = peerConnection.handshakeWithExtension();
        Assertions.assertNotNull(peer);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2})
    @Disabled
    void testDownloadPiece(int pieceNumber) {
        String peer = peerConnection.handshake();
        Assertions.assertNotNull(peer);

        peerConnection.downloadPiece(pieceNumber, PeerConnectionMagneticLinkTest.class.getResource("/test.file").getPath());

    }
}