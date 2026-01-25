package objects;

import java.util.ArrayList;
import java.util.List;

public class BencodedInteger extends BencodedObject {
    private List<Byte> bytes;
    public BencodedInteger() {
        super(ObjectType.INT);
        bytes = new ArrayList<>();
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
