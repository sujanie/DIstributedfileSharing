package com.distributed.handlers;

import com.distributed.commons.Constants;
import com.distributed.models.ChannelMessage;
import com.distributed.services.FileManager;
import com.distributed.core.Neighbour;
import com.distributed.models.RoutingTable;
import com.distributed.services.TimeoutManager;
import com.distributed.utils.StringEncoderDecoder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;
import java.util.Random; 


public class SearchQueryHandler implements AbstractResponseHandler, AbstractRequestHandler {

    private final Logger LOG = Logger.getLogger(SearchQueryHandler.class.getName());

    private RoutingTable routingTable;

    private BlockingQueue<ChannelMessage> channelOut;

    private TimeoutManager timeoutManager;

    private static SearchQueryHandler searchQueryHandler;

    private FileManager fileManager;

    private SearchQueryHandler(){
        fileManager = FileManager.getInstance("");
    }

    public synchronized static SearchQueryHandler getInstance(){
        if (searchQueryHandler == null){
            searchQueryHandler = new SearchQueryHandler();
        }
        return searchQueryHandler;
    }

    public void doSearch(String keyword) {

        String payload = String.format(Constants.QUERY_FORMAT,
                this.routingTable.getAddress(),
                this.routingTable.getPort(),
                StringEncoderDecoder.encode(keyword),
                Constants.HOP_COUNT
               );

        String rawMessage = String.format(Constants.MSG_FORMAT, payload.length() + 5, payload);

        ChannelMessage initialMessage = new ChannelMessage(
                this.routingTable.getAddress(),
                this.routingTable.getPort(),
                rawMessage);

        this.handleResponse(initialMessage);
    }

    @Override
    public void sendRequest(ChannelMessage message) {
        try {
            channelOut.put(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init(RoutingTable routingTable, BlockingQueue<ChannelMessage> channelOut,
                     TimeoutManager timeoutManager) {
        this.routingTable = routingTable;
        this.channelOut = channelOut;
        this.timeoutManager = timeoutManager;
    }

    @Override
    public void handleResponse(ChannelMessage message) {
        LOG.fine("Received SER : " + message.getMessage()
                + " from: " + message.getAddress()
                + " port: " + message.getPort());

        StringTokenizer stringToken = new StringTokenizer(message.getMessage(), " ");

        String length = stringToken.nextToken();
        String keyword = stringToken.nextToken();
        String address = stringToken.nextToken().trim();
        int port = Integer.parseInt(stringToken.nextToken().trim());

        String fileName = StringEncoderDecoder.decode(stringToken.nextToken().trim());
        int hops = Integer.parseInt(stringToken.nextToken().trim());

        //search for the file in the current node
        Set<String> resultSet = fileManager.searchForFile(fileName);
        int fileNamesCount = resultSet.size();

        if (fileNamesCount != 0) {

            StringBuilder fileNamesString = new StringBuilder("");

            Iterator<String> itr = resultSet.iterator();

            while(itr.hasNext()){
                fileNamesString.append(StringEncoderDecoder.encode(itr.next()) + " ");
            }

            String payload = String.format(Constants.QUERY_HIT_FORMAT,
                    fileNamesCount,
                    routingTable.getAddress(),
                    routingTable.getPort(),
                    Constants.HOP_COUNT- hops,
                    fileNamesString.toString());

            String rawMessage = String.format(Constants.MSG_FORMAT, payload.length() + 5, payload);

            ChannelMessage queryHitMessage = new ChannelMessage(address,
                    port,
                    rawMessage);

            this.sendRequest(queryHitMessage);
            
        }

        //if the hop count is greater than zero send the message to all neighbours again
        // initaiates random walk
        if (fileNamesCount == 0 && hops > 0){
        	Random rand = new Random(); 
        	
        	String payload = String.format(Constants.QUERY_FORMAT,
                    address,
                    port,
                    StringEncoderDecoder.encode(fileName),
                    hops - 1);

            String rawMessage = String.format(Constants.MSG_FORMAT, payload.length() + 5, payload);

            ArrayList<Neighbour> neighbours = this.routingTable.getNeighbours();
            int neighbour_count=neighbours.size();
            if(neighbour_count == 0){
            	
            }else if (neighbour_count >2) {
            	int rand_int_1 = rand.nextInt(neighbour_count); 
            	int rand_int_2 = rand.nextInt(neighbour_count); 
            	Neighbour neighbour1 = neighbours.get(rand_int_1);
            	Neighbour neighbour2 = neighbours.get(rand_int_2);
            	if (neighbour1.getAddress().equals(message.getAddress())
                        && neighbour1.getClientPort() == message.getPort()) {
            		rand_int_1 = rand.nextInt(neighbour_count); 
            		neighbour1 = neighbours.get(rand_int_1);
            	}
            	if (neighbour2.getAddress().equals(message.getAddress())
                        && neighbour2.getClientPort() == message.getPort()) {
            		rand_int_2 = rand.nextInt(neighbour_count); 
            		neighbour2 = neighbours.get(rand_int_2);
            	}
            	
            	ChannelMessage queryMessage1 = new ChannelMessage(neighbour1.getAddress(),
                        neighbour1.getPort(),
                        rawMessage);

                this.sendRequest(queryMessage1);
                
                ChannelMessage queryMessage2 = new ChannelMessage(neighbour2.getAddress(),
                        neighbour2.getPort(),
                        rawMessage);

                this.sendRequest(queryMessage2);
            }else {
            	int rand_int = rand.nextInt(neighbour_count); 
            	Neighbour neighbour = neighbours.get(rand_int);
            	if (neighbour.getAddress().equals(message.getAddress())
                        && neighbour.getClientPort() == message.getPort()) {
            		rand_int = rand.nextInt(neighbour_count); 
            		neighbour = neighbours.get(rand_int);
            	}
            	ChannelMessage queryMessage = new ChannelMessage(neighbour.getAddress(),
                        neighbour.getPort(),
                        rawMessage);

                this.sendRequest(queryMessage);
            	
            }
            
            
            // Generate random integers in range 0 to 999 
            
            

         /*   //skip sending search query to the same node again
            if (neighbour.getAddress().equals(message.getAddress())
                    && neighbour.getClientPort() == message.getPort()) {
                continue;
            }*/

            
            
            
        }
    }
}
