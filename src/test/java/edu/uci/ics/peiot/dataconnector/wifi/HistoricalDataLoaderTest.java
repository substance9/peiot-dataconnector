package edu.uci.ics.peiot.dataconnector.wifi;

import edu.uci.ics.peiot.dataconnector.wifi.model.RawConnectionEvent;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class HistoricalDataLoaderTest {
    @Test
    public void loadingDataTest(){
        int num = 100;
        String path = "/Users/guoxiwang/Workspace/wifi_data/raw_snmp_202_days";
        HistoricalDataLoader loader = new HistoricalDataLoader(path,num);
        ArrayList<RawConnectionEvent> ret = loader.loadDataToArray();
        assertEquals(num, ret.size());

    }
}