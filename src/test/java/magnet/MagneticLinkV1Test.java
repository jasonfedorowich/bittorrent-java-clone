package magnet;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MagneticLinkV1Test {

    MagneticLinkV1 magneticLinkV1;

    @BeforeEach
    void setUp() {
        magneticLinkV1 = new MagneticLinkV1("magnet:?xt=urn:btih:ad42ce8109f54c99613ce38f9b4d87e70f24a165&dn=magnet1.gif&tr=http%3A%2F%2Fbittorrent-test-tracker.codecrafters.io%2Fannounce");
    }

    @Test
    void testGetTracker() {
        Assertions.assertEquals("http://bittorrent-test-tracker.codecrafters.io/announce", magneticLinkV1.getTracker());
    }

    @Test
    void testGetName() {
        Assertions.assertEquals("magnet1.gif", magneticLinkV1.getName());
    }

    @Test
    void testGetInfoHash() {
        Assertions.assertEquals("ad42ce8109f54c99613ce38f9b4d87e70f24a165", magneticLinkV1.getInfoHash());
    }
}