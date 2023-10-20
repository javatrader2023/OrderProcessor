package handler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import util.OpeningVolume;

public class MarketDataSystemHandler {

    /**
     * Generates dummy Open MarketData
     * @return openingVolumeMap
     */
    public HashMap<String, OpeningVolume> generateOpenMarketData(){
        Random random = new Random();
        OpeningVolume IBM = new OpeningVolume("IBM.N", random.nextInt(100));
        OpeningVolume AAPL = new OpeningVolume("AAPL.O", random.nextInt(100));
        OpeningVolume MSFT = new OpeningVolume("MSFT.O", random.nextInt(100));
        OpeningVolume AMZN = new OpeningVolume("AMZN.O", random.nextInt(100));
        OpeningVolume GOOGL = new OpeningVolume("GOOGL.N", random.nextInt(100));
        OpeningVolume TSLA = new OpeningVolume("TSLA.O", random.nextInt(100));
        OpeningVolume META = new OpeningVolume("META.O", random.nextInt(100));
        OpeningVolume C = new OpeningVolume("C.N");     // No equilibrium matching in Open Auction
        OpeningVolume JPM = new OpeningVolume("JPM.N", random.nextInt(100));
        OpeningVolume MS = new OpeningVolume("MS.N", random.nextInt(100));

        HashMap<String, OpeningVolume> openingVolumeMap = new HashMap<String, OpeningVolume>();
        openingVolumeMap.put(IBM.getStockSymbol(), IBM);
        openingVolumeMap.put(AAPL.getStockSymbol(), AAPL);
        openingVolumeMap.put(MSFT.getStockSymbol(), MSFT);
        openingVolumeMap.put(AMZN.getStockSymbol(), AMZN);
        openingVolumeMap.put(GOOGL.getStockSymbol(), GOOGL);
        openingVolumeMap.put(TSLA.getStockSymbol(), TSLA);
        openingVolumeMap.put(META.getStockSymbol(), META);
        openingVolumeMap.put(C.getStockSymbol(), C);
        openingVolumeMap.put(JPM.getStockSymbol(), JPM);
        openingVolumeMap.put(MS.getStockSymbol(), MS);

        for (HashMap.Entry<String, OpeningVolume> entry : openingVolumeMap.entrySet()) {
            String stockSymbol = entry.getKey();
            OpeningVolume OpeningVolume = entry.getValue();

            System.out.println("GenerateOpenMarketData INFO: Received market data " + stockSymbol + " with open volume " + OpeningVolume.getVolume() + " shares");
        }

        openingVolumeMap = validateOpenMarketData(openingVolumeMap);
        return openingVolumeMap;
    }

    /**
     * Validates OpenMarketData for data integrity. Remove bogus OpenMarketData from the HashMap.
     * @param openingVolumeMap
     * @return openingVolumeMap
     */
    protected HashMap<String, OpeningVolume> validateOpenMarketData(HashMap<String, OpeningVolume> openingVolumeMap){
        ArrayList<String> removedKey = new ArrayList<>();

        for (HashMap.Entry<String, OpeningVolume> entry : openingVolumeMap.entrySet()) {
            String stockSymbol = entry.getKey();
            OpeningVolume openingVolume = entry.getValue();

            if (openingVolume.getStockSymbol() == null || openingVolume.getStockSymbol().trim().isEmpty()) {
                System.out.println("ValidateOpenMarketData ERROR: MarketData stockSymbol is null or empty... " + openingVolume.getStockSymbol() + " with open volume " + openingVolume.getVolume() + " shares");
                removedKey.add(stockSymbol);
            }
            if (openingVolume.getVolume() < 0) { // open volume = 0 is OK, means no matching
                System.out.println("ValidateOpenMarketData ERROR: MarketData open volume equal or less than ZERO... " + openingVolume.getStockSymbol() + " with open volume " + openingVolume.getVolume() + " shares");
                removedKey.add(stockSymbol);
            }
        }

        for (String stockSymbol: removedKey){
            System.out.println("ValidateOpenMarketData WARNING: Removing market data... " + stockSymbol);
            openingVolumeMap.remove(stockSymbol);
        }
        return openingVolumeMap;
    }
}
