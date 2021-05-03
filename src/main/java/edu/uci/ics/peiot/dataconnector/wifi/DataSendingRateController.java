package edu.uci.ics.peiot.dataconnector.wifi;

import edu.uci.ics.peiot.dataconnector.wifi.model.RawConnectionEvent;

import java.util.ArrayList;

public class DataSendingRateController {
    private ArrayList<RawConnectionEvent> dataArray;
    private RawEventSender sender;
    private String workload;
    private int rate;

    public DataSendingRateController(ArrayList<RawConnectionEvent> dataArray, RawEventSender sender, String workload, int rate) {
        this.dataArray = dataArray;
        this.sender = sender;
        this.workload = workload;
        this.rate = rate;
    }

    public void startSending(){
        //naive mode
        for (int i = 0; i < dataArray.size(); i++)
            sender.send(dataArray.get(i));
    }
}
