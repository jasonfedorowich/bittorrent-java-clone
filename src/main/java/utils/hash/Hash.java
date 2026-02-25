package utils.hash;

import encoder.Bencoder;
import objects.BencodedObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class Hash {

    public static byte[] hash(BencodedObject bencodedObject) {
        try {
            List<Byte> encoding = new Bencoder().encode(bencodedObject);
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] bytes = new byte[encoding.size()];
            for (int i = 0; i < encoding.size(); i++) {
                bytes[i] = encoding.get(i);
            }
            return md.digest(bytes);

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

    public static byte[] deHexify(String hexString) {
        List<Byte> bytes = new ArrayList<>();
        for (int i = 0; i < hexString.length(); i += 2) {
            bytes.add((byte) Integer.parseInt(hexString.substring(i, i + 2), 16));
        }
        byte[] byteArray = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i++) {
            byteArray[i] = bytes.get(i);
        }
        return byteArray;
    }

    public static String hexify(byte[] bytes) {
        StringBuilder hexString = new StringBuilder(2 * bytes.length);
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static String hash(byte[] bytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            return hexify(md.digest(bytes));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
