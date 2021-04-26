package com.ir;

import com.brettonw.bedrock.bag.BagArrayFrom;
import org.junit.jupiter.api.Test;
import org.orekit.data.DataContext;
import org.orekit.data.DataProvidersManager;
import org.orekit.data.DirectoryCrawler;

import java.io.File;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

public class TestSpaceTrack {

    public TestSpaceTrack () {
        var orekitDataMaster = new File("orekit-data-master");
        DataContext.getDefault().getDataProvidersManager().addProvider(new DirectoryCrawler(orekitDataMaster));
    }

    @Test
    public void constructorTest () {
        SpaceTrack spaceTrack = new SpaceTrack("dummy", "dummy");
    }

    @Test
    public void queryTest () {
        var spaceTrack = new SpaceTrack("bretton.wade@gmail.com", "xxx");
        var json = spaceTrack.doQuery ("gp/NORAD_CAT_ID/44238");
        assertNotNull(json);
        var bagArray = BagArrayFrom.string(json);
        assertNotNull(bagArray);
        assertEquals(1, bagArray.getCount());
        System.out.println("GP: " + bagArray.getBagObject (0));
    }

    @Test
    public void tleTest () {
        var spaceTrack = new SpaceTrack("bretton.wade@gmail.com", "xxx");
        var response = spaceTrack.getTLE (44238);
        assertNotNull(response);
        System.out.println("TLE: " + response);
    }
}
