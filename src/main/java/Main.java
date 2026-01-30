import decoder.ByteBendecoder;
import decoder.ByteQueue;
import decoder.Decoder;
import objects.BencodedDictionary;
import objects.BencodedObject;
import torrent.MetaInfoFile;
import torrent.peer.PeerConnection;
import torrent.web.Tracker;
import utils.generate.RandomString;
// import com.dampcake.bencode.Bencode; - available if you need it!

void main(String[] args) throws Exception {

    switch (args[0]) {
        case "decode":
            decode(args);
            break;
        case "info":
            info(args);
            break;
        case "peers":
            peers(args);
            break;
        case "handshake":
            handshake(args);
            break;

    }

}

private static void handshake(String[] args) throws IOException {
    String fileName = args[1];
    String peerIpAndPort = args[2];
    byte[] file = Files.readAllBytes(Path.of(fileName));
    MetaInfoFile metaInfoFile = getMetaInfoFile(file);
    String peerId = RandomString.generatePeerId();
    try (PeerConnection peerConnection = new PeerConnection(peerIpAndPort, metaInfoFile, peerId)) {
        String handshake = peerConnection.handshake();
        System.out.printf("Peer ID: %s\n", handshake);
    } catch (Exception e) {
        IO.println("Error handshake: " + e.getMessage());
    }
}

private static void decode(String[] args) {
    String bencodedValue = args[1];
    String decoded;
    try {
        decoded = decodeBencode(bencodedValue);
    } catch (RuntimeException e) {
        IO.println(e.getMessage());
        return;
    }
    IO.println(decoded);
}

private static void info(String[] args) throws IOException {
    String fileName = args[1];
    byte[] file = Files.readAllBytes(Path.of(fileName));
    MetaInfoFile metaInfoFile = getMetaInfoFile(file);
    System.out.printf("Tracker URL: %s", metaInfoFile.getAnnounce().getUrl());
    System.out.printf("Length: %d", metaInfoFile.getInfo().getLength());
    System.out.printf("Info Hash: %s", metaInfoFile.getInfo().getInfoHashHex());
    System.out.printf("Piece Length: %d", metaInfoFile.getInfo().getPieceLength());
    IO.println("Piece Hashes:");
    for (String hash : metaInfoFile.getInfo().getPiecesHashes()) {
        IO.println(hash);
    }
}

private static String decodeBencode(String bencodedString) {
    return new Decoder().decode(bencodedString);
}

private static MetaInfoFile getMetaInfoFile(byte[] bytes) {
    ByteQueue queue = new ByteQueue(bytes);
    ByteBendecoder decoder = new ByteBendecoder(queue);
    BencodedObject object = decoder.decode();
    return new MetaInfoFile((BencodedDictionary) object);
}

private static Tracker getTracker(MetaInfoFile metaInfoFile) {
    return new Tracker(metaInfoFile);
}

private static void peers(String[] args) throws IOException {
    String fileName = args[1];
    byte[] file = Files.readAllBytes(Path.of(fileName));
    MetaInfoFile metaInfoFile = getMetaInfoFile(file);
    Tracker tracker = getTracker(metaInfoFile);
    Tracker.TrackerResponse trackerResponse = tracker.track();
    for (Tracker.Peer peer : trackerResponse.getPeers()) {
        IO.println(peer.toString());
    }
}
