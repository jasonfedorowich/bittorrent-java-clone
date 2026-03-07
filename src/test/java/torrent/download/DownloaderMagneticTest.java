package torrent.download;

import decoder.ByteBendecoder;
import decoder.ByteQueue;
import objects.BencodedDictionary;
import objects.BencodedObject;
import org.junit.jupiter.api.*;
import torrent.file.MetaInfoFile;
import torrent.magnet.MagneticLinkV1;
import torrent.web.Tracker;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


class DownloaderMagneticTest {

    private MagneticLinkV1 magneticLink;
    private Downloader downloader;

    @BeforeEach
    void setUp() throws IOException {
        magneticLink = new MagneticLinkV1("magnet:?xt=urn:btih:ad42ce8109f54c99613ce38f9b4d87e70f24a165&dn=magnet1.gif&tr=http%3A%2F%2Fbittorrent-test-tracker.codecrafters.io%2Fannounce");
        Tracker tracker = new Tracker(magneticLink);
        Tracker.TrackerResponse response = tracker.track();
        Tracker.Peer peer = response.getPeers().get(0);
        downloader = new DownloadForMagnetic(response, tracker.getPeerId(), magneticLink);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @Disabled
    void download() throws FileNotFoundException, InterruptedException {
        String path = DownloaderMagneticTest.class.getResource("/test1.file").getPath();
        Assertions.assertDoesNotThrow(() -> {
            downloader.download(path);
        });
    }
}