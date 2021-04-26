package com.ir;

import com.brettonw.bedrock.bag.BagArrayFrom;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

public class TestSpaceTrack {
    @Test
    public void constructorTest () {
        SpaceTrack spaceTrack = new SpaceTrack("dummy", "dummy");
    }

    @Test
    public void queryTest () {
        var spaceTrack = new SpaceTrack("bretton.wade@gmail.com", "xxx");
        var response = spaceTrack.doQuery ("gp/NORAD_CAT_ID/44238");
        assertNotNull(response);
        var json = new String(response, StandardCharsets.UTF_8);
        var bagArray = BagArrayFrom.string(json);
        assertNotNull(bagArray);
        assertEquals(1, bagArray.getCount());
        System.out.println(bagArray.getBagObject (0));
    }

    @Test
    public void gpTest () {
        var spaceTrack = new SpaceTrack("bretton.wade@gmail.com", "xxx");
        var response = spaceTrack.getGp (44238);
        assertNotNull(response);
        System.out.println(response);
    }
}
