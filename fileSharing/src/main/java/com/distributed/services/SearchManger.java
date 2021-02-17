package com.distributed.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.distributed.commons.Constants;
import com.distributed.core.MessageInitiator;
import com.distributed.models.SearchResult;
import com.distributed.handlers.QueryHitHandler;
import com.distributed.utils.ConsoleTable;

public class SearchManger {
	  private MessageInitiator MessageInitiator;

	    private Map<Integer, SearchResult> fileDownloadOptions;

	    public SearchManger(MessageInitiator MessageInitiator) {
	        this.MessageInitiator = MessageInitiator;
	    }

	    public int doSearch(String keyword) {

	        Map<String, SearchResult> searchResults
	                = new HashMap<String, SearchResult>();

	        QueryHitHandler queryHitHandler = QueryHitHandler.getInstance();
	        queryHitHandler.setSearchResutls(searchResults);
	        queryHitHandler.setSearchInitiatedTime(System.currentTimeMillis());

	        this.MessageInitiator.doSearch(keyword);

	        System.out.println("Please be patient till the file results are returned ...");

	        try {
	            Thread.sleep(Constants.SEARCH_TIMEOUT);

	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }

	        printSearchResults(searchResults);
	        this.clearSearchResults();
	        return fileDownloadOptions.size();
	    }

	    public ArrayList<SearchResult> doUISearch(String keyword) {
	    	
	        Map<String, SearchResult> searchResults
	                = new HashMap<String, SearchResult>();

	        QueryHitHandler queryHitHandler = QueryHitHandler.getInstance();
	        queryHitHandler.setSearchResutls(searchResults);
	        queryHitHandler.setSearchInitiatedTime(System.currentTimeMillis());

	        this.MessageInitiator.doSearch(keyword);

	        System.out.println("Please be patient till the file results are returned ...");
	        
	        try {
	            Thread.sleep(Constants.SEARCH_TIMEOUT);

	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }

	        List<String> results = new ArrayList<String>();

	        int fileIndex = 1;

	        this.fileDownloadOptions = new HashMap<Integer, SearchResult>();
	        ArrayList<SearchResult> result= new ArrayList<>();
	        for (String s : searchResults.keySet()) {
	            SearchResult searchResult = searchResults.get(s);
	            /*String temp = "" + searchResult.getFileName() + "\t" +
	                    searchResult.getAddress() + ":" + searchResult.getPort() + "\t" +
	                    searchResult.getHops() + "\t" + searchResult.getTimeElapsed() + "ms";*/
	            this.fileDownloadOptions.put(fileIndex, searchResult);
	            result.add(searchResult);
	            fileIndex++;
	        }

	        this.clearSearchResults();

	        return result;
	    }

	    private void clearSearchResults() {
	        QueryHitHandler queryHitHandler = QueryHitHandler.getInstance();

	        queryHitHandler.setSearchResutls(null);
	    }

	    private void printSearchResults(Map<String, SearchResult> searchResults) {

	        System.out.println("\nFile search results : ");

	        ArrayList<String> headers = new ArrayList<String>();
	        headers.add("Option No");
	        headers.add("FileName");
	        headers.add("Source");
	        headers.add("QueryHit time (ms)");
	        headers.add("Hop count");

	        ArrayList<ArrayList<String>> content = new ArrayList<ArrayList<String>>();

	        int fileIndex = 1;

	        this.fileDownloadOptions = new HashMap<Integer, SearchResult>();

	        for (String s : searchResults.keySet()) {
	            SearchResult searchResult = searchResults.get(s);
	            this.fileDownloadOptions.put(fileIndex, searchResult);

	            ArrayList<String> row1 = new ArrayList<String>();
	            row1.add("" + fileIndex);
	            row1.add(searchResult.getFileName());
	            row1.add(searchResult.getAddress() + ":" + searchResult.getPort());
	            row1.add("" + searchResult.getTimeElapsed());
	            row1.add("" + searchResult.getHops());

	            content.add(row1);

	            fileIndex++;
	        }

	        if (fileDownloadOptions.size() == 0) {
	            System.out.println("Sorry. No files are found!!!");

	            return;
	        }

	        ConsoleTable ct = new ConsoleTable(headers, content);
	        ct.printTable();

	    }

	    public SearchResult getFileDetails(int fileIndex) {
	        return this.fileDownloadOptions.get(fileIndex);
	    }
	
}
