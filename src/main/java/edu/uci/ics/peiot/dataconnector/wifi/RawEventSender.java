package edu.uci.ics.peiot.dataconnector.wifi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.peiot.dataconnector.wifi.model.RawConnectionEvent;
import edu.uci.ics.peiot.dataconnector.wifi.model.RawConnectionEventMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class RawEventSender  {

    private ZContext context;
    private ZMQ.Socket publisher;
    private int port;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final ObjectMapper mapper = new ObjectMapper();

    public RawEventSender(int port) {
        this.port = port;
    }


    public void send(RawConnectionEvent evt){
        RawConnectionEventMsg evtMsg = new RawConnectionEventMsg(evt);
        String msg = null;
        try {
            msg = mapper.writeValueAsString(evtMsg);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        logger.debug(msg);

        publisher.send(msg.getBytes(ZMQ.CHARSET), 0);
    }

    public  void init(){
        context = new ZContext();
        publisher = context.createSocket(SocketType.PUB);
        publisher.bind("tcp://*:" + String.valueOf(port));

    }

}