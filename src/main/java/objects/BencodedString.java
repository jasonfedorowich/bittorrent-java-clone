package objects;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class BencodedString extends BencodedObject implements Iterable<Byte>, Comparable<BencodedString> {

    private List<Byte> bytes;
    public BencodedString() {
        this.bytes = new ArrayList<>();
        super(ObjectType.STRING);
    }

    public BencodedString(String str) {
        super(ObjectType.STRING);
        this.bytes = new ArrayList<>();
        for(byte c: str.getBytes()){
            this.bytes.add(c);
        }
    }

    public void add(byte byteValue) {
        bytes.add(byteValue);
    }

    public int size(){
        return bytes.size();
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

    @Override
    public List<Byte> getBytes() {
        return bytes;
    }

    @Override
    public Iterator<Byte> iterator() {
        return bytes.iterator();
    }

    @Override
    public int compareTo(BencodedString o) {
        String left = this.getString();
        String right = o.getString();
        return left.compareTo(right);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BencodedString bytes1 = (BencodedString) o;
        String left = this.getString();
        String right = bytes1.getString();
        return Objects.equals(left, right);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getString());
    }
}
