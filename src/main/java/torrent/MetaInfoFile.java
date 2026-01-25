package torrent;

import decoder.BencodedDictionary;
import decoder.BencodedObject;
import decoder.BencodedString;

public class MetaInfoFile {

    public static class Info{
        private final int length;
        private final String name;
        private final int pieceLength;
        private final BencodedString pieces;

        public Info(int length, String name, int pieceLength, BencodedString pieces) {
            this.length = length;
            this.name = name;
            this.pieceLength = pieceLength;
            this.pieces = pieces;
        }

        public Info(BencodedDictionary dic) {
            this.length = Integer.parseInt(dic.get("length").getString());
            this.name = dic.get("name").getString();
            this.pieceLength = Integer.parseInt(dic.get("piece length").getString());
            this.pieces = (BencodedString) dic.get("pieces");
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
