package edu.uci.ics.peiot.dataconnector.wifi;

import edu.uci.ics.peiot.dataconnector.wifi.model.RawConnectionEvent;
import edu.uci.ics.peiot.dataconnector.wifi.util.StringTrimUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

public class UCIWifiParser {

    //Properties
    private static String PARSER_PROPERTIES_FILE = "uci-wifi-parser.properties";
    private static Properties props;

    //Logging
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    //Parsing hash map
    private static HashMap<String, String> parsingMap = new HashMap<String, String>();

    public UCIWifiParser(){
        // Read properties file
        InputStream propStream = UCIWifiParser.class.getClassLoader().getResourceAsStream(PARSER_PROPERTIES_FILE);
        props = new Properties();
        try {
            props.load(propStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        init();
    }

    private void init(){
        // Populate parsing hash map
        for (String key : props.stringPropertyNames()) {
            String value = props.getProperty(key);
            parsingMap.put(key, value);
        }
    }

    public RawConnectionEvent parse(String evtStr){
        RawConnectionEvent rawEvt = new RawConnectionEvent();
        HashMap<String, String> rawDict = new HashMap<String, String>();


        // First split the event string line into different attribute pieces. The splitter is tab character.
        String[] evtStrArray = evtStr.split("\t");
//        for (String s : evtStrArray){
//            logger.debug(s + '\n');
//        }

        // Iterate through all attribute pieces, each attribute is a string that represents key value pair connected by "="
        // keyValStr example:  SNMPv2-SMI::enterprises.14179.2.6.2.36.0 = INTEGER: 0

        for (String keyValStr : evtStrArray){
            keyValStr = keyValStr.trim();

            // Split each key value pair
            String[] keyValPairArray = keyValStr.split("=", 2);

            // Check the availability of this attribute
            if (keyValPairArray.length == 2){
                rawDict.put(keyValPairArray[0].trim(), keyValPairArray[1].trim());
            } else if (keyValPairArray.length == 1) {
                rawDict.put(keyValPairArray[0].trim(), "");
            } else {
                logger.error("number of parts (divided by =) for section is wrong: ");
                logger.error(evtStr);
                return null;
            }
        }

        if (!rawDict.containsKey(parsingMap.get("apId"))){
            logger.error("Cannot Find SNMP key for apId");
            return null;
        }
        if (!rawDict.containsKey(parsingMap.get("apMac"))){
            logger.error("Cannot Find SNMP key for apMac");
            return null;
        }
        if (!rawDict.containsKey(parsingMap.get("clientMac"))){
            logger.error("Cannot Find SNMP key for clientMac");
            return null;
        }
        

        String apIdRaw = rawDict.get(parsingMap.get("apId"));
        String apIdParsed = StringTrimUtil.trimTrailingCharacter(
                            StringTrimUtil.trimLeadingCharacter(
                            apIdRaw.split(":")[1].trim(),
                                    '\"'),
                                    '\"');
        rawEvt.setApId(apIdParsed);

        String apMacRaw = rawDict.get(parsingMap.get("apMac"));
        String apMacParsed = apMacRaw.split(":")[1].trim();
        rawEvt.setApMacWithStr(apMacParsed);

        String clientMacRaw = rawDict.get(parsingMap.get("clientMac"));
        String clientMacParsed = clientMacRaw.split(":")[1].trim();
        rawEvt.setClientMacWithStr(clientMacParsed);
        rawEvt.getClientMac().setInitHashId();

        return rawEvt;
    }
}