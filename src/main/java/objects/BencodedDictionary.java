package objects;

import java.util.*;

public class BencodedDictionary extends BencodedObject implements Iterable<BencodedDictionary.KeyObject>{

    private final TreeMap<BencodedString, BencodedObject> dict;

    public BencodedDictionary() {
        super(ObjectType.DICT);
        dict = new TreeMap<>();
    }

    public void put(BencodedString string, BencodedObject decode) {
        dict.put(string, decode);
    }

    public BencodedObject get(String string) {
        return dict.get(new BencodedString(string));
        //return dict.get(String.format("\"%s\"", string));
    }
    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(",", "{", "}");
        for(Map.Entry<BencodedString, BencodedObject> entry : dict.entrySet()) {
            joiner.add(entry.getKey() + ":" + entry.getValue());
        }
        return joiner.toString();
    }


    @Override
    public String getString() {
        return toString();
    }

    @Override
    public List<Byte> getBytes() {
        List<Byte> bytes = new ArrayList<>();
        for(Map.Entry<BencodedString, BencodedObject> entry : dict.entrySet()) {
            for(byte b : entry.getKey().getBytes()){
                bytes.add(b);
            }
            bytes.addAll(entry.getValue().getBytes());
        }
        return bytes;
    }

    @Override
    public Iterator<KeyObject> iterator() {
        List<KeyObject> keys = new ArrayList<>();
        for(Map.Entry<BencodedString, BencodedObject> entry : dict.entrySet()) {
            keys.add(new KeyObject(entry.getKey(), entry.getValue()));
        }
        return keys.iterator();
    }

    public static class KeyObject {
        private final BencodedString key;
        private final BencodedObject value;
        public KeyObject(BencodedString key, BencodedObject value) {
            this.key = key;
            this.value = value;
        }

        public BencodedString getKey() {
            return key;
        }

        public BencodedObject getValue() {
            return value;
        }
    }

}
