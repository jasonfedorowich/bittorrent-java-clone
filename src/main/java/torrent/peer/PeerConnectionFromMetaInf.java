package torrent.peer;

import torrent.file.MetaInfoFile;
import torrent.web.Tracker;

import java.util.List;

public class PeerConnectionFromMetaInf extends PeerConnection {

    private final MetaInfoFile metaInfoFile;

    public PeerConnectionFromMetaInf(String peer, MetaInfoFile metaInfoFile, String peerId) {
        this.metaInfoFile = metaInfoFile;
        super(peer, peerId);
    }

    public PeerConnectionFromMetaInf(String ip, int port, MetaInfoFile metaInfoFile, String peerId) {
        this.metaInfoFile = metaInfoFile;
        super(ip, port, peerId);
    }

    public PeerConnectionFromMetaInf(Tracker.Peer peer,MetaInfoFile metaInfoFile, String peerId) {
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
