package torrent.download;

import torrent.magnet.MagneticLinkV1;
import torrent.peer.PeerConnection;
import torrent.peer.PeerConnectionForMagentic;
import torrent.peer.PeerConnectionForMetaInf;
import torrent.web.Tracker;

import java.io.IOException;
import java.util.List;

public class DownloadForMagnetic extends Downloader {

    private final MagneticLinkV1 magneticLinkV1;
    private PeerConnectionForMagentic.MagneticInfo magneticInfo;

    public DownloadForMagnetic(Tracker.TrackerResponse response, String peerId, MagneticLinkV1 magneticLinkV1) {
        this.magneticLinkV1 = magneticLinkV1;
        super(response, peerId);
    }

    @Override
    public void openConnections(List<Tracker.Peer> peers) {
        for(Tracker.Peer peer : peers) {
            PeerConnectionForMagentic peerConnection = new PeerConnectionForMagentic(peer, magneticLinkV1, peerId);
            connections.add(peerConnection);
            peerConnection.handshakeWithExtension();
            try {
                //todo do we need to align that all info's from peers are the same?
                magneticInfo = peerConnection.request();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

    @Override
    public long getPiecesCount() {
        return this.magneticInfo.getPiecesCount();
    }
}
