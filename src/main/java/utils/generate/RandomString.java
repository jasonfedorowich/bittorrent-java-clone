package utils.generate;

import java.util.Random;

public class RandomString {

    public static String generatePeerId() {
        String data = "abcdefghijklmnopqrstuvwxyz";
        StringBuilder random = new Random()
                .ints(20)
                .collect(StringBuilder::new, (x, y)->{
                    x.append(data.charAt(Math.abs(y % data.length())));
                }, (x, y)->{

                });
        return random.toString();

    }

}
