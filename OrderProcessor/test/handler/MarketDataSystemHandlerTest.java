package handler;

import org.junit.Assert;
import org.junit.Test;
import util.OpeningVolume;

import java.util.HashMap;

public class MarketDataSystemHandlerTest {

    /**
     * Case 1: Instrument is "". Result false.
     * Case 2: Instrument is null. Result false.
     * Case 3: Instrument is "   ". Result false.
     * Case 4: Instrument FB.O has volume less than ZERO. Result false.
     * Case 5: Instrument IBM.N has volume ZERO. Result true. (No open volume is OK.)
     */
    @Test
    public void validateOpenMarketData() {
        HashMap<String, OpeningVolume> openingVolumeMap = new HashMap<>();
        OpeningVolume openingVolume1 = new OpeningVolume("", 3);
        OpeningVolume openingVolume2 = new OpeningVolume(null, 3);
        OpeningVolume openingVolume3 = new OpeningVolume("  ", 3);
        OpeningVolume openingVolume4 = new OpeningVolume("FB.O", -1);
        OpeningVolume openingVolume5 = new OpeningVolume("IBM.N", 0);

        openingVolumeMap.put(openingVolume1.getStockSymbol(), openingVolume1);
        openingVolumeMap.put(openingVolume2.getStockSymbol(), openingVolume2);
        openingVolumeMap.put(openingVolume3.getStockSymbol(), openingVolume3);
        openingVolumeMap.put(openingVolume4.getStockSymbol(), openingVolume4);
        openingVolumeMap.put(openingVolume5.getStockSymbol(), openingVolume5);

        MarketDataSystemHandler marketDataSystemHandler = new MarketDataSystemHandler();
        HashMap<String, OpeningVolume> validatedMap = marketDataSystemHandler.validateOpenMarketData(openingVolumeMap);

        Assert.assertEquals("Map size should be 1", 1,validatedMap.size());
        Assert.assertTrue("IBM.N is a valid entry", validatedMap.containsKey("IBM.N"));
    }
}
