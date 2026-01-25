package decoder;

import java.util.ArrayList;
import java.util.List;

public class BencodedString extends BencodedObject {
    private List<Byte> bytes;
    public BencodedString() {
        this.bytes = new ArrayList<>();
        super(ObjectType.STRING);
    }

    public void add(byte byteValue) {
        bytes.add(byteValue);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Byte byteValue : bytes) {
            sb.append((char)byteValue.byteValue());
        }
        return String.format("\"%s\"", sb);
    }

    @Override
    public String getString() {
        StringBuilder sb = new StringBuilder();
        for (Byte byteValue : bytes) {
            sb.append((char)byteValue.byteValue());
        }
        return sb.toString();
    }
}
