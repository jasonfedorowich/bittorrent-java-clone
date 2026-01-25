package objects;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringJoiner;

public class BencodedList extends BencodedObject implements Iterable<BencodedObject>{
    private ArrayList<BencodedObject> values;

    public BencodedList() {
        super(ObjectType.LIST);
        values = new ArrayList<>();
    }

    public void add(BencodedObject object) {
        values.add(object);
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(",", "[", "]");
        for(BencodedObject bencodedObject : values){
            joiner.add(bencodedObject.toString());
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
        for(BencodedObject bencodedObject : values){
            bytes.addAll(bencodedObject.getBytes());
        }
        return bytes;
    }

    @Override
    public Iterator<BencodedObject> iterator() {
        return values.iterator();
    }
}
