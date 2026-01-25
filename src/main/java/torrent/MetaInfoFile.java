package torrent;

import encoder.Bencoder;
import hash.Hash;
import objects.BencodedDictionary;
import objects.BencodedString;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class MetaInfoFile {

    public static class Info{
        private final int length;
        private final String name;
        private final int pieceLength;
        private final BencodedString pieces;
        private final BencodedDictionary info;


        public Info(BencodedDictionary dic) {
            this.length = Integer.parseInt(dic.get("length").getString());
            this.name = dic.get("name").getString();
            this.pieceLength = Integer.parseInt(dic.get("piece length").getString());
            this.pieces = (BencodedString) dic.get("pieces");
            this.info = dic;
        }

        public int getLength() {
            return length;
        }

        public String getName() {
            return name;
        }

        public int getPieceLength() {
            return pieceLength;
        }

        public BencodedString getPieces() {
            return pieces;
        }

        public String getInfoHash(){
           return Hash.hash(info);
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


    }

    public static class Announce{
        private final String url;

        public Announce(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }
    }

    private final Info info;
    private final Announce announce;

    public MetaInfoFile(BencodedDictionary dictionary) {
        this.info = new Info((BencodedDictionary) dictionary.get("info"));
        this.announce = new Announce(dictionary.get("announce").getString());

    }

    public Info getInfo() {
        return info;
    }

    public Announce getAnnounce() {
        return announce;
    }
}
