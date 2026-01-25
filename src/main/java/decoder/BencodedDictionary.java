package decoder;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.TreeMap;

public class BencodedDictionary extends BencodedObject {

    private final TreeMap<String, BencodedObject> dict;

    public BencodedDictionary() {
        super(ObjectType.DICT);
        dict = new TreeMap<>();
    }

    public void put(String string, BencodedObject decode) {
        dict.put(string, decode);
    }

    public BencodedObject get(String string) {
        return dict.get(String.format("\"%s\"", string));
    }
    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(",", "{", "}");
        for(Map.Entry<String, BencodedObject> entry : dict.entrySet()) {
            joiner.add(entry.getKey() + ":" + entry.getValue());
        }
        return joiner.toString();
    }


    @Override
    public String getString() {
        return toString();
    }
}
