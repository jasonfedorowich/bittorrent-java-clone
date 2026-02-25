package torrent.magnet;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class MagneticLinkV1 {

    private String tracker;
    private String name;
    private String infoHash;

    public MagneticLinkV1(String link) {
        String[] tokens = link.split("[?]");
        if(!tokens[0].equals("magnet:")) {
            throw new IllegalArgumentException();
        }
        parseKeys(tokens[1]);
    }

    private void parseKeys(String token) {
        String[] keyAndValue = token.split("&");
        Map<String, String> map = new HashMap<>();
        for(int i = 0; i < keyAndValue.length; i++) {
            String[] set = keyAndValue[i].split("=");
            map.put(set[0], set[1]);
        }
        tracker = URLDecoder.decode(map.getOrDefault("tr", ""), StandardCharsets.UTF_8);
        name = map.getOrDefault("dn", "");
        String xt = map.getOrDefault("xt", "");

        if(!xt.startsWith("urn:btih:")) {
            throw new IllegalArgumentException();
        }
        infoHash = xt.substring("urn:btih:".length());
    }

    public String getTracker() {
        return tracker;
    }

    public String getName() {
        return name;
    }

    public String getInfoHash() {
        return infoHash;
    }
}
