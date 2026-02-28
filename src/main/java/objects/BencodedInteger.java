package objects;

import java.util.ArrayList;
import java.util.List;

public class BencodedInteger extends BencodedObject {
    private List<Byte> bytes;
    public BencodedInteger() {
        super(ObjectType.INT);
        bytes = new ArrayList<>();
    }

    public BencodedInteger(int value) {
        super(ObjectType.INT);
        bytes = new ArrayList<>();
        String str = Integer.toString(value);
        int i = 0;
        while(i < str.length()) {
            bytes.add((byte) str.charAt(i));
            i++;
        }
    }

    public void add(byte byteValue) {
        bytes.add(byteValue);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Byte b : bytes) {
            sb.append((char)b.byteValue());
        }
        return sb.toString();
    }

    @Override
    public String getString() {
        return toString();
    }

    @Override
    public List<Byte> getBytes() {
        return bytes;
    }
}
