package torrent.peer;

import error.PieceHashException;
import torrent.MetaInfoFile;
import torrent.web.Tracker;
import utils.hash.Hash;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;

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
    static class PayloadMessage {
        private final int index;
        private final int begin;
        private final byte[] block;


        public PayloadMessage(DataInputStream dataInputStream, int length) throws IOException {
            index = dataInputStream.readInt();
            begin = dataInputStream.readInt();
            block = new byte[length - 9];
            dataInputStream.readFully(block);
        }

        int index(){
            return index;
        }

        int begin(){
            return begin;
        }
        byte[] block(){
            return block;
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
        this(ip, port, metaInfoFile, peerId);
    }

    public PeerConnection(String ip, int port, MetaInfoFile metaInfoFile, String peerId) {
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

    public PeerConnection(Tracker.Peer peer, MetaInfoFile metaInfoFile, String peerId) {
        this(peer.getIp(), peer.getPort(), metaInfoFile, peerId);
    }


    public String handshake(){
        try {
            byte[] message = handshakeMessage();
            outputStream.write(message);
            outputStream.flush();
            byte[] handshakeResponse = inputStream.readNBytes(68);
            HandshakeMessage handshakeMessage = new HandshakeMessage(handshakeResponse);
            byte[] peerId = handshakeMessage.getPeerId();
            return Hash.hexify(peerId);
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void downloadPiece(int index, String output){
        try {
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            DataInputStream dataInputStream = new DataInputStream(inputStream);

            bitField(dataInputStream);
            interest(dataOutputStream);
            unchoke(dataInputStream);

            long pieceSize = getPieceLength(index);
            int blockSize = 16 * 1024;
            int n = (int)Math.ceil((double)pieceSize / blockSize);

            Map<Integer, byte[]> blocks = new TreeMap<>();

            for(int i = 0; i < n; i++){
                int offset = i * blockSize;
                int block = (int)Math.min(blockSize, pieceSize - offset);
                sendRequest(index, offset, block, dataOutputStream);
            }


            while(n > 0){
                int size = dataInputStream.readInt();
                byte messageId = dataInputStream.readByte();
                if(messageId != 7) throw new RuntimeException("Invalid message id: " + messageId);
                PayloadMessage payloadMessage = read(dataInputStream, size);
                blocks.put(payloadMessage.begin(), payloadMessage.block());
                n--;
            }

            byte[] piece = concat(blocks);
            assertHash(piece, index);

            try(FileOutputStream fileOutputStream = new FileOutputStream(output)){
                fileOutputStream.write(piece);
                fileOutputStream.flush();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void unchoke(DataInputStream dataInputStream) throws IOException {
        int size = dataInputStream.readInt();
        byte messageId = dataInputStream.readByte();
        if(messageId != 1) throw new RuntimeException("Invalid message id: " + messageId);
    }

    private void bitField(DataInputStream dataInputStream) throws IOException {
        int size = dataInputStream.readInt();
        byte messageId = dataInputStream.readByte();
        if(messageId != 5) throw new RuntimeException("Invalid message id: " + messageId);
        dataInputStream.readNBytes(size - 1);
    }

    private PayloadMessage read(DataInputStream dataInputStream, int length) throws IOException {
        return new PayloadMessage(dataInputStream, length);
    }

    private void sendRequest(int index, int begin, int request, DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeInt(13);
        dataOutputStream.write((byte) 6);
        dataOutputStream.writeInt(index);
        dataOutputStream.writeInt(begin);
        dataOutputStream.writeInt(request);
        dataOutputStream.flush();
    }

    private void interest(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeInt(1);
        dataOutputStream.writeByte(2);
        dataOutputStream.flush();
    }


    private void assertHash(byte[] piece, int index) {
        String hash = Hash.hash(piece);
        String expectedHash = metaInfoFile.getInfo().getPiecesHashes().get(index);
        if(!expectedHash.equals(hash)) throw new PieceHashException(expectedHash, hash);
    }

    private byte[] concat(Map<Integer, byte[]> blocks) {
        int size = 0;
        for(Map.Entry<Integer, byte[]> entry : blocks.entrySet()){
            size += entry.getValue().length;
        }
        byte[] data = new byte[size];
        int pos = 0;
        for(Map.Entry<Integer, byte[]> entry : blocks.entrySet()){
            System.arraycopy(entry.getValue(), 0, data, pos, entry.getValue().length);
            pos += entry.getValue().length;
        }
        return data;
    }

    private long getPieceLength(int index) {
        return Math.min(metaInfoFile.getInfo().getPieceLength(), metaInfoFile.getInfo().getLength() - ((long) index * metaInfoFile.getInfo().getPieceLength()));
    }

    private byte[] handshakeMessage() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write((byte)19);
        byteArrayOutputStream.write("BitTorrent protocol".getBytes(StandardCharsets.UTF_8));
        byteArrayOutputStream.write(new byte[8]);
        byteArrayOutputStream.write(metaInfoFile.getInfo().getInfoHash());
        byteArrayOutputStream.write(peerId.getBytes(StandardCharsets.UTF_8));
        return byteArrayOutputStream.toByteArray();

    }


    @Override
    public void close() throws Exception {
        outputStream.close();
        socket.close();
    }
}
