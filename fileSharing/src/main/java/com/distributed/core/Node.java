package com.distributed.core;

import javafx.scene.control.TextArea;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import com.distributed.commons.Constants;
import com.distributed.ftp.FTPClient;
import com.distributed.ftp.FTPServer;
import com.distributed.models.SearchResult;
import com.distributed.services.BSClient;
import com.distributed.services.FileManager;
import com.distributed.core.MessageInitiator;
/*
import com.semicolon.ds.Constants;
import com.semicolon.ds.comms.BSClient;
import com.semicolon.ds.comms.ftp.FTPClient;
import com.semicolon.ds.comms.ftp.FTPServer;
import com.semicolon.ds.core.FileManager;
import com.semicolon.ds.core.MessageBroker;
import com.semicolon.ds.core.SearchManager;
import com.semicolon.ds.core.SearchResult;
*/
import com.distributed.services.SearchManger;
import com.distributed.core.Neighbour;

public class Node {
	private final Logger LOG = Logger.getLogger(Node.class.getName());

    private String userName;
    private String ipAddress;
    private int port;
    
    private BSClient bsClient;
    private MessageInitiator messageBroker;
    private SearchManger searchManager;
    private FTPServer ftpServer;

    public Node (String userName) throws Exception {

        try (final DatagramSocket socket = new DatagramSocket()){
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            this.ipAddress = socket.getLocalAddress().getHostAddress();

        } catch (Exception e){
            throw new RuntimeException("Could not find host address");
        }

        this.userName = userName;
        this.port = getFreePort();
        
        
        this.ftpServer = new FTPServer( this.port + Constants.FTP_PORT_OFFSET, userName);
        Thread t = new Thread(ftpServer);
        t.start();

        this.bsClient = new BSClient();
        this.messageBroker = new MessageInitiator(ipAddress, port);

        this.searchManager = new SearchManger(this.messageBroker);

        messageBroker.start();

        LOG.fine("Node initiated on IP :" + ipAddress + " and Port :" + port);

    }



    public void init() {
        List<InetSocketAddress> targets = this.register();
        if(targets != null) {
            for (InetSocketAddress target: targets) {
                messageBroker.sendPing(target.getAddress().toString().substring(1), target.getPort());
            }
        }
    }

    private List<InetSocketAddress> register() {
        List<InetSocketAddress> targets = null;

        try{
            targets = this.bsClient.register(this.userName, this.ipAddress, this.port);

        } catch (IOException e) {
            LOG.severe("Registering Node failed");
            e.printStackTrace();
        }
        return targets;

    }

    public void unRegister() {
        try{
            this.bsClient.unRegister(this.userName, this.ipAddress, this.port);
            this.messageBroker.sendLeave();

        } catch (IOException e) {
            LOG.severe("Un-Registering Gnode failed");
            e.printStackTrace();
        }
    }

    public int doSearch(String keyword){
        return this.searchManager.doSearch(keyword);
    }

    public ArrayList<SearchResult> doUISearch(String keyword) {
        return this.searchManager.doUISearch(keyword);
    }

    public void getFile(int fileOption) {
        try {
            SearchResult fileDetail = this.searchManager.getFileDetails(fileOption);
            System.out.println("The file you requested is " + fileDetail.getFileName());
            FTPClient ftpClient = new FTPClient(fileDetail.getAddress(), fileDetail.getTcpPort(),
                    fileDetail.getFileName());

            System.out.println("Waiting for file download...");
            Thread.sleep(Constants.FILE_DOWNLOAD_TIMEOUT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getFile(int fileOption, TextArea textArea) {
        try {
            SearchResult fileDetail = this.searchManager.getFileDetails(fileOption);
            System.out.println("The file you requested is " + fileDetail.getFileName());
            FTPClient ftpClient = new FTPClient(fileDetail.getAddress(), fileDetail.getTcpPort(),
                    fileDetail.getFileName(),textArea);
            return fileDetail.getFileName();

        } catch (Exception e) {
            e.printStackTrace();
        }
		return "";
    }

    public String getUserName() {
        return userName;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getPort(){
        return port;
    }

    private int getFreePort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            socket.setReuseAddress(true);
            int port = socket.getLocalPort();
            try {
                socket.close();
            } catch (IOException e) {
                // Ignore IOException on close()
            }
            return port;
        } catch (IOException e) {
            LOG.severe("Getting free port failed");
            throw new RuntimeException("Getting free port failed");
        }
    }

    public void printRoutingTable(){
        this.messageBroker.getRoutingTable().print();
    }

    public String getRoutingTable() {
       return this.messageBroker.getRoutingTable().toString();
    }
    public ArrayList<Neighbour> getNeighbourList() {
    	return this.messageBroker.getRoutingTable().getNeighbourList();
    }
    
    
    public String getFileNames() {
        return this.messageBroker.getFiles();
    }
    public boolean addFile(String fileName) {
    	return this.messageBroker.addFile(fileName);
    }
	}


