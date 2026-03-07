package torrent.download;

import torrent.file.MetaInfoFile;
import torrent.peer.PeerConnection;
import torrent.peer.PeerConnectionForMetaInf;
import torrent.web.Tracker;

import java.util.List;

public class DownloaderForMetaInf extends Downloader {

    private final MetaInfoFile metaInfoFile;
    public DownloaderForMetaInf(Tracker.TrackerResponse response, String peerId, MetaInfoFile metaInfoFile) {
        this.metaInfoFile = metaInfoFile;
        super(response, peerId);
    }

    public void openConnections(List<Tracker.Peer> peers) {
        for(Tracker.Peer peer : peers) {
            PeerConnection peerConnection = new PeerConnectionForMetaInf(peer, metaInfoFile, peerId);
            connections.add(peerConnection);
            peerConnection.handshake();
        }
    }

    public long getPiecesCount(){
        return metaInfoFile.getInfo().getPiecesCount();
    }

}
