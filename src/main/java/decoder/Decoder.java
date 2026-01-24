package decoder;


import java.util.StringJoiner;

public class Decoder {

    private int pos = 0;

    public String decode(String encodedString){
        switch(encodedString.charAt(pos)){
            case 'i':
                return decodeInteger(encodedString);
            case 'l':
                return decodeList(encodedString);
            default:
                if(Character.isDigit(encodedString.charAt(pos))){
                    return decodeString(encodedString);
                }else{
                    throw new UnsupportedOperationException("Not supported yet.");
                }
        }
    }

    private String decodeList(String encodedString) {
        StringJoiner joiner = new StringJoiner(",", "[", "]");
        pos++;
        for(; pos < encodedString.length() && encodedString.charAt(pos) != 'e'; pos++){
            joiner.add(decode(encodedString));
        }
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
        pos = pos + length;
        return joiner.toString();
    }

    private String decodeInteger(String encodedString) {
        int firstEnd = encodedString.indexOf("e", pos);
        int start = pos+1;
        pos = firstEnd+1;
        return encodedString.substring(start, firstEnd);
    }


//    public String decode(String encodedString) {
//        if(Character.isDigit(encodedString.charAt(0))) {
//            return StringDecoder.decode(encodedString);
//        }else if(encodedString.charAt(0) == 'i') {
//            return IntegerDecoder.decode(encodedString);
//        }else if(encodedString.charAt(0) == 'l') {
//            return ListDecoder.decode(encodedString);
//        }else{
//            throw new UnsupportedOperationException("Not supported yet.");
//        }
//    }
}
