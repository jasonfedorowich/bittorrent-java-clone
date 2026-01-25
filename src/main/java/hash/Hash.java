package hash;

import encoder.Bencoder;
import objects.BencodedObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class Hash {

    public static String hash(BencodedObject bencodedObject) {
        try {
            List<Byte> encoding = new Bencoder().encode(bencodedObject);
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] bytes = new byte[encoding.size()];
            for (int i = 0; i < encoding.size(); i++) {
                bytes[i] = encoding.get(i);
            }
            byte[] hash = md.digest(bytes);

            StringBuilder hexString = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();


        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String hexify(List<Byte> bytes) {
        StringBuilder hexString = new StringBuilder(2 * bytes.size());
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
