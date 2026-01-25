package decoder;


import java.util.StringJoiner;

@Deprecated
public class Decoder {

    private int pos = 0;

    public String decode(String encodedString){
        switch(encodedString.charAt(pos)){
            case 'i':
                return decodeInteger(encodedString);
            case 'l':
                return decodeList(encodedString);
            case 'd':
                return decodeDict(encodedString);
            default:
                if(Character.isDigit(encodedString.charAt(pos))){
                    return decodeString(encodedString);
                }else{
                    throw new UnsupportedOperationException("Not supported yet.");
                }
        }
    }

    private String decodeDict(String encodedString) {
        StringJoiner joiner = new StringJoiner(",", "{", "}");
        pos++;
        while(pos < encodedString.length() && encodedString.charAt(pos) != 'e'){
            StringJoiner innerKeyValue = new StringJoiner(":");
            innerKeyValue.add(decode(encodedString));
            innerKeyValue.add(decode(encodedString));
            joiner.add(innerKeyValue.toString());
        }
        pos++;
        return joiner.toString();
    }


    private String decodeList(String encodedString) {
        StringJoiner joiner = new StringJoiner(",", "[", "]");
        pos++;
        while(pos<encodedString.length() && encodedString.charAt(pos) != 'e'){
            joiner.add(decode(encodedString));
        }
        pos++;
        return joiner.toString();
    }

    private String decodeString(String encodedString) {
        int start = pos;
        for(; pos < encodedString.length(); pos++) {
            if(encodedString.charAt(pos) == ':') {
                break;
            }
        }
        int length = Integer.parseInt(encodedString.substring(start, pos));
        StringJoiner joiner = new StringJoiner("", "\"", "\"");
        joiner.add(encodedString.substring(pos+1, pos+1+length));
        pos = pos + length + 1;
        return joiner.toString();
    }

    private String decodeInteger(String encodedString) {
        int firstEnd = encodedString.indexOf("e", pos);
        int start = pos+1;
        pos = firstEnd+1;
        return encodedString.substring(start, firstEnd);
    }


}
