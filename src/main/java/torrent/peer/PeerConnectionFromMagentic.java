package torrent.peer;

import decoder.ByteBendecoder;
import decoder.ByteQueue;
import encoder.Bencoder;
import objects.BencodedDictionary;
import objects.BencodedInteger;
import objects.BencodedObject;
import torrent.file.MetaInfoFile;
import torrent.magnet.MagneticLinkV1;
import torrent.web.Tracker;
import utils.hash.Hash;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.List;

public class PeerConnectionFromMagentic extends PeerConnection {

    private final MagneticLinkV1 magneticLink;
    private BencodedInteger extensionId;

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

    public void request() throws IOException {
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
            innerDictionary.put("ut_metadata",  new BencodedInteger(16));
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

            BencodedDictionary extensionHandshake = readExtensionHandshake(dataInputStream);
            BencodedDictionary inner = (BencodedDictionary) extensionHandshake.get("m");
            BencodedInteger extensionId = (BencodedInteger)inner.get("ut_metadata");
            this.extensionId = extensionId;
            handshake.setExtensionId(extensionId.toInteger());
            return handshake;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private BencodedDictionary readExtensionHandshake(DataInputStream dataInputStream) throws IOException {
        int size = dataInputStream.readInt();
        byte messageId = dataInputStream.readByte();
        if(messageId != 20){
            throw new RuntimeException("Invalid message id: " + messageId);
        }
        byte extensionId = dataInputStream.readByte();
        if(extensionId != 0){
            throw new RuntimeException("Invalid extension id: " + extensionId);
        }
        byte[] rawBytes = dataInputStream.readNBytes(size - 2);
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
