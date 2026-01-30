package torrent.peer;

import torrent.MetaInfoFile;
import utils.hash.Hash;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

public class PeerConnection implements AutoCloseable {

    static class HandshakeMessage {
        private final byte[] peerId;
        HandshakeMessage(byte[] bytes) {
            peerId = Arrays.copyOfRange(bytes, 48, 68);
        }

        public byte[] getPeerId() {
            return peerId;
        }
    }

    private final Socket socket;
    private final OutputStream outputStream;
    private final InputStream inputStream;
    private final MetaInfoFile metaInfoFile;
    private final String peerId;

    public PeerConnection(String peer, MetaInfoFile metaInfoFile, String peerId) {
        String[] parts = peer.split(":");
        String ip = parts[0];
        int port = Integer.parseInt(parts[1]);
        this.metaInfoFile = metaInfoFile;
        this.peerId = peerId;
        try {
            socket = new Socket(ip, port);
            outputStream = socket.getOutputStream();
            inputStream = socket.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public String handshake(){
        try {
            byte[] message = handshakeMessage();
            outputStream.write(message);
            byte[] handshakeResponse = inputStream.readAllBytes();
            HandshakeMessage handshakeMessage = new HandshakeMessage(handshakeResponse);
            byte[] peerId = handshakeMessage.getPeerId();
            return Hash.hexify(peerId);
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] handshakeMessage(){
        byte[] message = new byte[68];
        int i = 0;
        message[i++] = (byte) 19;
        String protocol = "BitTorrent protocol";
        for(byte b : protocol.getBytes()){
            message[i++] = b;
        }
        i += 8;
        byte[] hash = metaInfoFile.getInfo().getInfoHash();
        for(byte b : hash){
            message[i++] = b;
        }
        for(byte b: peerId.getBytes()){
            message[i++] = b;
        }
        return message;

    }


    @Override
    public void close() throws Exception {
        outputStream.close();
        socket.close();
    }
}
