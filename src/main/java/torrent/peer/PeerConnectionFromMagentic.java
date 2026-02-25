package torrent.peer;

import torrent.file.MetaInfoFile;
import torrent.magnet.MagneticLinkV1;
import torrent.web.Tracker;
import utils.hash.Hash;

import java.util.List;

public class PeerConnectionFromMagentic extends PeerConnection {

    private final MagneticLinkV1 magneticLink;

    public PeerConnectionFromMagentic(String peer, MagneticLinkV1 magneticLinkV1, String peerId) {
        this.magneticLink = magneticLinkV1;
        super(peer, peerId);
    }

    public PeerConnectionFromMagentic(String ip, int port, MagneticLinkV1 magneticLinkV1, String peerId) {
        this.magneticLink = magneticLinkV1;
        super(ip, port, peerId);
    }

    public PeerConnectionFromMagentic(Tracker.Peer peer, MagneticLinkV1 magneticLinkV1, String peerId) {
        this.magneticLink = magneticLinkV1;
        super(peer, peerId);
    }

    @Override
    public byte[] getInfoHash() {
        return Hash.deHexify(magneticLink.getInfoHash());
    }

    @Override
    public long getPieceLength() {
        return 0;
    }

    @Override
    public long getLength() {
        return 0;
    }

    @Override
    public List<String> getPiecesHashes() {
        return List.of();
    }
}
