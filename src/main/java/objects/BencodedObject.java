package objects;

import java.util.List;

public abstract class BencodedObject {

    public enum ObjectType {
        LIST,
        STRING,
        INT,
        DICT
    }

    protected ObjectType type;

    public BencodedObject(ObjectType type) {
        this.type = type;
    }

    public ObjectType getType() {
        return type;
    }

    public abstract String getString();

    public abstract List<Byte> getBytes();

}
