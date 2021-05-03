package edu.uci.ics.peiot.dataconnector.wifi;

import edu.uci.ics.peiot.dataconnector.wifi.model.MacAddress;
import edu.uci.ics.peiot.dataconnector.wifi.model.RawConnectionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HistoricalDataLoader {
    private String inDataDir;
    private int numOfData;
    private UCIWifiParser parser;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    public HistoricalDataLoader(String inDataDir, int numOfData) {
        this.inDataDir = inDataDir;
        this.numOfData = numOfData;
        this.parser = new UCIWifiParser();
    }

    public ArrayList<RawConnectionEvent> loadDataToArray(){
        int dataCount = 0;
        ArrayList<RawConnectionEvent> dataArray = new ArrayList<RawConnectionEvent>(numOfData);
        try (Stream<Path> walk = Files.walk(Paths.get(inDataDir))) {

            List<String> result = walk.map(x -> x.toString())
                    .filter(f -> f.endsWith("txt")).collect(Collectors.toList());

            for(int i = 0; i < result.size(); i++){
                System.out.println("Processing "+ result.get(i));
                if (dataCount >= numOfData){
                    return dataArray;
                }
                else{
                    dataCount = addRawDataToArray(dataArray, result.get(i), dataCount);
                }
                if (i==100){
                    return dataArray;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataArray;
    }

    private int addRawDataToArray(ArrayList<RawConnectionEvent> dataArray, String rawDataFilePath, int dataCount){
        try {

            BufferedReader br = new BufferedReader(new FileReader(rawDataFilePath));

            String st;
            RawConnectionEvent evt = null;
            while ((st = br.readLine()) != null){
                evt = strToRawConnEvt(st);
                if (evt == null){
                    continue;
                }
                dataArray.add(evt);
                dataCount = dataCount + 1;
                if (dataCount >= numOfData){
                    return dataCount;
                }
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return dataCount;
    }

    private RawConnectionEvent strToRawConnEvt(String evtStr){
        RawConnectionEvent rawEvt;
        if(evtStr.length() == 0){
            return null;
        }
        if (evtStr == null) {
            logger.error("Empty Event String, skip");
            return null;
        }
        try{
            rawEvt = parser.parse(evtStr);
        }catch(Exception e){
            e.printStackTrace();
            logger.error("Unable to parse the event string: " + evtStr + " , skip");
            return null;
        }

        if (rawEvt == null){
            logger.error("Unable to parse the event string: " + evtStr + " , skip");
            return null;
        }
        return rawEvt;
    }

}
