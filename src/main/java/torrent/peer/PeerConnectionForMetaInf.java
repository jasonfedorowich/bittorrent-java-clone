package torrent.peer;

import torrent.file.MetaInfoFile;
import torrent.web.Tracker;

import java.util.List;

public class PeerConnectionForMetaInf extends PeerConnection {

    private final MetaInfoFile metaInfoFile;

    public PeerConnectionForMetaInf(String peer, MetaInfoFile metaInfoFile, String peerId) {
        this.metaInfoFile = metaInfoFile;
        super(peer, peerId);
    }

    public PeerConnectionForMetaInf(String ip, int port, MetaInfoFile metaInfoFile, String peerId) {
        this.metaInfoFile = metaInfoFile;
        super(ip, port, peerId);
    }

    public PeerConnectionForMetaInf(Tracker.Peer peer, MetaInfoFile metaInfoFile, String peerId) {
        this.metaInfoFile = metaInfoFile;
        super(peer, peerId);
    }

    @Override
    public byte[] getInfoHash() {
        return metaInfoFile.getInfo().getInfoHash();
    }

    @Override
    public long getPieceLength() {
        return metaInfoFile.getInfo().getPieceLength();
    }

    @Override
    public long getLength() {
        return  metaInfoFile.getInfo().getLength();
    }

    @Override
    public List<String> getPiecesHashes() {
        return metaInfoFile.getInfo().getPiecesHashes();
    }
}
