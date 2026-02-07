package torrent.download;

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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


class DownloaderTest {

    private MetaInfoFile metaInfoFile;
    private Downloader downloader;

    @BeforeEach
    void setUp() throws IOException {
        InputStream is = DownloaderTest.class.getResourceAsStream("/sample.torrent");
        byte[] bytes = is.readAllBytes();
        ByteQueue queue = new ByteQueue(bytes);
        ByteBendecoder decoder = new ByteBendecoder(queue);
        BencodedObject object = decoder.decode();
        metaInfoFile = new MetaInfoFile((BencodedDictionary) object);
        Tracker tracker = new Tracker(metaInfoFile);
        Tracker.TrackerResponse response = tracker.track();
        downloader = new Downloader(response, metaInfoFile, tracker.getPeerId());
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void download() throws FileNotFoundException, InterruptedException {
        String path = DownloaderTest.class.getResource("/test1.file").getPath();
        Assertions.assertDoesNotThrow(() -> {
            downloader.download(path);
        });
    }
}