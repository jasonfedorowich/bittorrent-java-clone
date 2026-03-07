package torrent.peer;

import decoder.ByteBendecoder;
import decoder.ByteQueue;
import encoder.Bencoder;
import objects.BencodedDictionary;
import objects.BencodedInteger;
import objects.BencodedObject;
import objects.BencodedString;
import torrent.magnet.MagneticLinkV1;
import torrent.web.Tracker;
import utils.hash.Hash;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PeerConnectionFromMagentic extends PeerConnection {

    public static class MagneticInfo{

        private final long pieceLength;
        private final BencodedString pieces;
        private final String name;
        private final long length;

        public MagneticInfo(BencodedDictionary contents) {
            this.pieceLength = ((BencodedInteger)contents.get("piece length")).toInteger();
            this.name = ((BencodedString)contents.get("name")).getString();
            this.length = ((BencodedInteger)contents.get("length")).toInteger();
            this.pieces = (BencodedString)contents.get("pieces");
        }

        public List<String> getPiecesHashes(){
            int i = 0;
            List<String> hashes = new ArrayList<>();
            while(i < pieces.size()){
                List<Byte> bytes = pieces.getBytes().subList(i, i + 20);
                hashes.add(Hash.hexify(bytes));
                i += 20;
            }
            return hashes;
        }

        public long getPieceLength() {
            return pieceLength;
        }

        public BencodedString getPieces() {
            return pieces;
        }

        public String getName() {
            return name;
        }

        public long getLength() {
            return length;
        }
    }

    private final MagneticLinkV1 magneticLink;
    private BencodedInteger extensionId;
    //todo change this
    private Integer peerExtensionId = 16;
    private MagneticInfo magneticInfo;

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

    public MagneticInfo request() throws IOException {
        if(this.magneticInfo == null) {
            sendRequest();
            this.magneticInfo = receiveResponse();
        }
        return magneticInfo;
    }

    private MagneticInfo receiveResponse() throws IOException {
        byte[] rawBytes = readExtensionMessage(peerExtensionId.byteValue());
        ByteQueue queue = new ByteQueue(rawBytes);
        ByteBendecoder decoder = new ByteBendecoder(queue);
        BencodedDictionary dict = (BencodedDictionary)decoder.decode();
        validateRequest(dict);
        return new MagneticInfo((BencodedDictionary)decoder.decode());

    }

    private void validateRequest(BencodedDictionary dict) {
        BencodedInteger msgType = (BencodedInteger)dict.get("msg_type");
        if(!msgType.toInteger().equals(1)) throw new RuntimeException("Invalid msg_type");

    }

    private byte[] readExtensionMessage(byte expectedExtensionId) throws IOException {
        int size = dataInputStream.readInt();
        byte messageId = dataInputStream.readByte();
        if(messageId != 20){
            throw new RuntimeException("Invalid message id: " + messageId);
        }
        byte extensionId = dataInputStream.readByte();
        if(extensionId != expectedExtensionId){
            throw new RuntimeException("Invalid extension id: " + extensionId);
        }
        return dataInputStream.readNBytes(size - 2);
    }

    private void sendRequest() throws IOException {
        BencodedDictionary request = new BencodedDictionary();
        request.put("msg_type", new BencodedInteger(0));
        request.put("piece", new BencodedInteger(0));

        Bencoder bencoder = new Bencoder();
        List<Byte> bytes = bencoder.encode(request);
        dataOutputStream.writeInt(bytes.size() + 2);
        dataOutputStream.write(20);
        dataOutputStream.write(extensionId.toInteger().byteValue());

        for (Byte b : bytes) {
            dataOutputStream.write(b);
        }
        dataOutputStream.flush();
    }

    public HandshakeMessage handshakeWithExtension(){
        HandshakeMessage handshake = baseHandShake(true);
        if(!handshake.doesSupportExtension()) throw new RuntimeException("Handshake does not support extensions");

        try {
            bitField(dataInputStream);
            BencodedDictionary bencodedDictionary = new BencodedDictionary();
            BencodedDictionary innerDictionary = new BencodedDictionary();
            innerDictionary.put("ut_metadata",  new BencodedInteger(peerExtensionId));
            bencodedDictionary.put("m", innerDictionary);

            Bencoder bencoder = new Bencoder();
            List<Byte> bytes = bencoder.encode(bencodedDictionary);

            dataOutputStream.writeInt(bytes.size() + 1 + 1);
            dataOutputStream.write(20);
            dataOutputStream.write(0);

            for(int i = 0; i < bytes.size(); ++i){
                dataOutputStream.write(bytes.get(i));
            }
            dataOutputStream.flush();

            BencodedDictionary extensionHandshake = readExtensionHandshake();
            BencodedDictionary inner = (BencodedDictionary) extensionHandshake.get("m");
            BencodedInteger extensionId = (BencodedInteger)inner.get("ut_metadata");
            this.extensionId = extensionId;
            handshake.setExtensionId(extensionId.toInteger());
            return handshake;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private BencodedDictionary readExtensionHandshake() throws IOException {
        byte[] rawBytes = readExtensionMessage((byte) 0);
        ByteQueue queue = new ByteQueue(rawBytes);
        ByteBendecoder decoder = new ByteBendecoder(queue);
        BencodedObject object = decoder.decode();
        return (BencodedDictionary) object;
    }

    @Override
    public byte[] getInfoHash() {
        return Hash.deHexify(magneticLink.getInfoHash());
    }

    @Override
    public long getPieceLength() {
        return magneticInfo.getPieceLength();
    }

    @Override
    public long getLength() {
        return magneticInfo.getLength();
    }

    @Override
    public List<String> getPiecesHashes() {
        return magneticInfo.getPiecesHashes();
    }
}
