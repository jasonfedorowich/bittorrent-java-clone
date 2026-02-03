package torrent;

import objects.BencodedDictionary;
import objects.BencodedString;
import utils.hash.Hash;

import java.util.ArrayList;
import java.util.List;

public class MetaInfoFile {

    public static class Info{
        private final long length;
        private final String name;
        private final long pieceLength;
        private final BencodedString pieces;
        private final BencodedDictionary info;


        public Info(BencodedDictionary dic) {
            this.length = Long.parseLong(dic.get("length").getString());
            this.name = dic.get("name").getString();
            this.pieceLength = Long.parseLong(dic.get("piece length").getString());
            this.pieces = (BencodedString) dic.get("pieces");
            this.info = dic;
        }

        public long getLength() {
            return length;
        }

        public long getPiecesCount(){
            return length / pieceLength + (length % pieceLength > 0 ? 1 : 0);
        }

        public String getName() {
            return name;
        }

        public long getPieceLength() {
            return pieceLength;
        }

        public BencodedString getPieces() {
            return pieces;
        }

        public String getInfoHashHex(){
           return Hash.hexify(Hash.hash(info));
        }

        public byte[] getInfoHash(){
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
