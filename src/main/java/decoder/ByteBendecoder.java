package decoder;


import objects.*;

import java.nio.charset.StandardCharsets;

public class ByteBendecoder {

    private final ByteQueue byteQueue;

    public ByteBendecoder(ByteQueue byteQueue) {
        this.byteQueue = byteQueue;
    }

    public ByteBendecoder(String bencodedString) {
        byteQueue = new ByteQueue(bencodedString.getBytes(StandardCharsets.UTF_8));
    }

    public ByteBendecoder(byte[] bytes) {
        byteQueue = new ByteQueue(bytes);
    }

    public BencodedObject decode(){
        if(byteQueue.isEmpty()){
            //todo for now this just returns null
            return null;
        }
        switch(byteQueue.peek()){
            case 'i':
                return decodeInteger();
            case 'l':
                return decodeList();
            case 'd':
                return decodeDict();
            default:
                if(Character.isDigit(byteQueue.peek())){
                    return decodeString();
                }else{
                    throw new UnsupportedOperationException("Not supported yet.");
                }
        }
    }

    private BencodedDictionary decodeDict() {
        BencodedDictionary bencodedDictionary = new BencodedDictionary();
        byteQueue.pop();
        while(!byteQueue.isEmpty() && byteQueue.peek() != 'e'){
            BencodedObject bencodedObject = decode();
            if(!(bencodedObject instanceof BencodedString)){
                throw new RuntimeException("Keys must be strings in dictionaries.");
            }
            //todo
            bencodedDictionary.put((BencodedString) bencodedObject, decode());
        }
        byteQueue.pop();
        return bencodedDictionary;
    }


    private BencodedList decodeList() {
        byteQueue.pop();
        BencodedList bencodedList = new BencodedList();

        while(!byteQueue.isEmpty() && byteQueue.peek() != 'e'){
            bencodedList.add(decode());
        }
        byteQueue.pop();
        return bencodedList;
    }

    private BencodedString decodeString() {
        StringBuilder sb = new StringBuilder();
        BencodedString bencodedString = new BencodedString();
        while(!byteQueue.isEmpty() && byteQueue.peek() != ':'){
            sb.append((char) byteQueue.pop());
        }
        byteQueue.pop();
        int length = Integer.parseInt(sb.toString());
        while(length > 0){
            bencodedString.add(byteQueue.pop());
            length--;
        }

        return bencodedString;
    }

    private BencodedInteger decodeInteger() {
        byteQueue.pop();
        BencodedInteger bencodedInteger = new BencodedInteger();
        while(!byteQueue.isEmpty() && byteQueue.peek() != 'e'){
            bencodedInteger.add(byteQueue.pop());
        }
        byteQueue.pop();
        return bencodedInteger;
    }


}
