package torrent.web;


import decoder.ByteBendecoder;
import objects.BencodedDictionary;
import objects.BencodedObject;
import torrent.MetaInfoFile;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringJoiner;
import java.util.List;

import static utils.generate.RandomString.generatePeerId;

public class Tracker {

    public static class TrackerResponse {
        private List<Peer> peers;
        TrackerResponse(List<Peer> peers) {
            this.peers = peers;
        }

        public List<Peer> getPeers() {
            return peers;
        }
    }

    public static class Peer{
        private final List<Byte> bytes;

        Peer(List<Byte> bytes) {
            this.bytes = bytes;
        }

        public String getIp(){
            StringJoiner joiner = new StringJoiner(".");
            for(int i = 0; i < bytes.size() - 2; i++){
                if(bytes.get(i) < 0){
                    joiner.add(String.valueOf(bytes.get(i) + 256));
                }else{
                    joiner.add(String.valueOf(bytes.get(i)));
                }
            }
            return joiner.toString();
        }

        public int getPort(){
            int j = bytes.get(bytes.size()-1);
            int i = bytes.get(bytes.size()-2);
            int r = (i & 0xff) << 8;
            r = r | (j & 0xff);
            return r;
        }

        public String toString(){
            return getIp() + ":" + getPort();
        }

    }

    private final OkHttpClient client;
    private final Request getRequest;
    private final String peerId;


    public Tracker(MetaInfoFile metaInfoFile, String peerId) {
        client = new OkHttpClient();
        HttpUrl.Builder httpUrlBuilder = HttpUrl.parse(metaInfoFile.getAnnounce().getUrl()).newBuilder();
        httpUrlBuilder.addEncodedQueryParameter("info_hash", encodeInfoHash(metaInfoFile.getInfo().getInfoHashHex()));
        this.peerId = peerId;
        httpUrlBuilder.setQueryParameter("peer_id", this.peerId);
        httpUrlBuilder.setQueryParameter("port", "6881");
        httpUrlBuilder.setQueryParameter("uploaded", "0");
        httpUrlBuilder.setQueryParameter("downloaded", "0");
        httpUrlBuilder.setQueryParameter("left", String.valueOf(metaInfoFile.getInfo().getLength()));
        httpUrlBuilder.setQueryParameter("compact", "1");
        String url = httpUrlBuilder.build().toString();

        getRequest = new Request.Builder().url(url).get().build();
    }

    public Tracker(MetaInfoFile metaInfoFile) {
        this(metaInfoFile, generatePeerId());
    }

    private String encodeInfoHash(String infoHash) {
        StringJoiner stringJoiner = new StringJoiner("%", "%", "");
        for(int i = 0; i < infoHash.length(); i+=2) {
            stringJoiner.add(infoHash.charAt(i) + String.valueOf(infoHash.charAt(i+1)));
        }
       // return URLEncoder.encode(stringJoiner.toString());
        return stringJoiner.toString();
    }

    public String getPeerId() {
        return peerId;
    }

    public TrackerResponse track() {
        try {
            Response response = client.newCall(getRequest).execute();
            if(!response.isSuccessful()) throw new RuntimeException("Unexpected code " + response);
            byte[] bytes = response.body().bytes();
            ByteBendecoder bendecoder = new ByteBendecoder(bytes);
            BencodedDictionary bencodedDictionary = (BencodedDictionary) bendecoder.decode();
            List<Peer> peers = parsePeers(bencodedDictionary.get("peers"));
            return new TrackerResponse(peers);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Peer> parsePeers(BencodedObject peers) {
        List<Byte> bytes = peers.getBytes();
        List<Peer> peerList = new ArrayList<>();
        for(int i = 0; i < bytes.size(); i += 6) {
            peerList.add(new Peer(bytes.subList(i, i + 6)));
        }
        return peerList;
    }
}
