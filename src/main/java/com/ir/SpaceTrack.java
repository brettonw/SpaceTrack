package com.ir;

import com.brettonw.bedrock.bag.BagArrayFrom;
import com.brettonw.bedrock.bag.BagObject;
import com.brettonw.bedrock.bag.formats.MimeType;

import javax.net.ssl.HttpsURLConnection;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.orekit.propagation.analytical.tle.TLE;

public class SpaceTrack {
    private static final String BASE_URL = "https://www.space-track.org";
    private static final String LOGIN_PATH = "/ajaxauth/login";
    private static final String QUERY_PATH = "/basicspacedata/query/class/";

    private final String loginIdentity;
    private final String loginPassword;

    public SpaceTrack(String username, String password) {
        loginIdentity = username;
        loginPassword = password;
    }

    // doPost: POSTS a raw data request to an HTTP server and returns the raw
    //         data result
    // NOTE:   these are raw bytes, not strings! If you want to use strings,
    //         they should be encoded/decoded (UTF-8 is always a good choice).
    private byte[] doPost (String url, byte[] postData) {
        try {
            // build the connection to POST the login and query
            var connection = (HttpsURLConnection) (new URL(url).openConnection());
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            try (var outputStream = connection.getOutputStream()) {
                outputStream.write(postData);
            }

            // if the POST succeeded, read the response
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (var inputStream = connection.getInputStream()) {
                    return inputStream.readAllBytes();
                }
            }
        } catch (Exception exception) {
            System.out.println("Exception: " + exception);
        }
        return null;
    }

    private BagObject buildQueryObject (String query) {
        return BagObject
                .open("identity", loginIdentity)
                .put ("password", loginPassword)
                .put ("query", BASE_URL + QUERY_PATH + query);
    }

    // doQuery takes the query portion of the URL you want to execute, and returns one of three results:
    // a) NULL - something went wrong with the post or the response
    // b) {"error":"You must be logged in to complete this action"} - your credentials are invalid
    // c) [{"CCSDS_OMM_VERS":"2.0","COMMENT":"GENERATED VIA SPAC... - an array of the tracking elements requested
    public String doQuery (String query) {
        var postData = buildQueryObject (query).toString(MimeType.URL).getBytes(StandardCharsets.UTF_8);
        return new String (doPost (BASE_URL + LOGIN_PATH, postData), StandardCharsets.UTF_8);
    }

    // return a bag object representing the requested object
    public BagObject getGP (int noradCatalogId) {
        var bagArray = BagArrayFrom.url(BASE_URL + LOGIN_PATH,
                buildQueryObject("gp/NORAD_CAT_ID/" + noradCatalogId),
                MimeType.URL);
        return ((bagArray != null) && (bagArray.getCount() > 0)) ? bagArray.getBagObject(0) : null;
    }

    public TLE getTLE (int noradCatalogId) {
        var tle = getGP(noradCatalogId);
        return (tle != null) ? new TLE (tle.getString("TLE_LINE1"), tle.getString("TLE_LINE2")) : null;
    }
}
