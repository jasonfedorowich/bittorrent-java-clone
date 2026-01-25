package decoder;

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

}
