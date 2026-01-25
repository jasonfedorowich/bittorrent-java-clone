package encoder;

import objects.*;

import java.util.ArrayList;
import java.util.List;

public class Bencoder {

    private final List<Byte> encoding = new ArrayList<>();

    public List<Byte> encode(BencodedObject object) {
        doEncode(object);
        return encoding;
    }

    private void doEncode(BencodedObject object) {
        switch (object.getType()) {
            case LIST -> encodeList((BencodedList)object);
            case INT -> encodeInt((BencodedInteger)object);
            case STRING -> encodeString((BencodedString)object);
            case DICT -> encodeDict((BencodedDictionary)object);
        }
    }

    private void encodeDict(BencodedDictionary object) {
        encoding.add((byte) 'd');
        for(BencodedDictionary.KeyObject kv: object){
            doEncode(kv.getKey());
            doEncode(kv.getValue());
        }
        encoding.add((byte) 'e');
    }

    private void encodeString(BencodedString string) {
        int size = string.size();
        String stringSize = Integer.toString(size);
        for(char c: stringSize.toCharArray()) {
            encoding.add((byte) c);
        }
        encoding.add((byte) ':');
        encoding.addAll(string.getBytes());
    }

    private void encodeInt(BencodedInteger integer) {
        encoding.add((byte) 'i');
        encoding.addAll(integer.getBytes());
        encoding.add((byte) 'e');
    }

    private void encodeList(BencodedList list) {
        encoding.add((byte) 'l');
        for(BencodedObject obj: list){
            doEncode(obj);
        }
        encoding.add((byte) 'e');
    }

}
