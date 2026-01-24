package decoder;

public class Decoder {

    static class StringDecoder {
        static String decode(String encodedString) {
            int firstColonIndex = 0;
            for(int i = 0; i < encodedString.length(); i++) {
                if(encodedString.charAt(i) == ':') {
                    firstColonIndex = i;
                    break;
                }
            }
            int length = Integer.parseInt(encodedString.substring(0, firstColonIndex));
            return encodedString.substring(firstColonIndex+1, firstColonIndex+1+length);
        }
    }

    static class IntegerDecoder {
        static String decode(String encodedString) {
            return encodedString.substring(1, encodedString.indexOf('e'));
        }
    }

    public String decode(String encodedString) {
        if(Character.isDigit(encodedString.charAt(0))) {
            return StringDecoder.decode(encodedString);
        }else if(encodedString.charAt(0) == 'i') {
            return IntegerDecoder.decode(encodedString);
        }else{
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
