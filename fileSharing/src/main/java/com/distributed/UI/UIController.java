
package com.distributed.UI;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.UUID;

import com.distributed.core.Neighbour;
import com.distributed.core.Node;
import com.distributed.models.SearchResult;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class UIController extends Thread implements Initializable {
	public Node node;
	public Button buttonSearch;
	public Button leaveButton;
	public Button buttonDownload;

	public Label labelAddress;
	public Label labelPort;
	public Label labelNeighbour;

	public TextArea areaAvailable;
	public TextArea areaSearch;
	public TextArea areaTable;
	public TextArea areaDownload;

	public TextField textSearch;
	public TextField textDownload;

	private int resultsCount = 0;

	@FXML
	private TableView<RoutingTableView> tablerouting;

	@FXML
	private TableColumn<RoutingTableView, String> rid;

	@FXML
	private TableColumn<RoutingTableView, String> address;

	@FXML
	private TableColumn<RoutingTableView, String> port;

	@FXML
	private TableColumn<RoutingTableView, String> pings;

	@FXML
	private TableView<SearchResultView> tablesearch;

	@FXML
	private TableColumn<SearchResultView, String> sid;

	@FXML
	private TableColumn<SearchResultView, String> filename;

	@FXML
	private TableColumn<SearchResultView, String> ipaddress;

	@FXML
	private TableColumn<SearchResultView, String> portNo;

	@FXML
	private TableColumn<SearchResultView, String> hops;

	@FXML
	private TableColumn<SearchResultView, String> time;

	ObservableList<RoutingTableView> routinglist = FXCollections.observableArrayList();
	ObservableList<SearchResultView> searchlist = FXCollections.observableArrayList();

	public void setData(String ipaddress, String port) {
		labelAddress.setText(ipaddress);
		areaAvailable.setText(getAvailableFiles());
		labelPort.setText(port);

	}

	public void setRoutingTable(ArrayList<Neighbour> table) {
		routinglist.clear();
		int count = 0;
		for (Neighbour n : table) {
			routinglist.add(new RoutingTableView(count, n.getAddress(), n.getPort(), n.getPingPongs()));
			count++;
		}

		tablerouting.setItems(routinglist);
		Platform.runLater(() -> {
			labelNeighbour.setText(String.valueOf(routinglist.size()));
		});

	}

	private ArrayList<Neighbour> getRoutingTable() {
		return node.getNeighbourList();
	}

	private String getAvailableFiles() {
		return node.getFileNames();
	}

	public void leaveAction() {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you leaving ?", ButtonType.YES, ButtonType.NO);
		alert.showAndWait();

		if (alert.getResult() == ButtonType.YES) {
			Platform.exit();
			node.unRegister();
			System.exit(0);
		}

	}

	@FXML
	public void searchAction(ActionEvent event) {

		searchlist.clear();
		tablesearch.setItems(searchlist);
		String data = textSearch.getText();
		if (data.equals(null) || data.isEmpty()) {
			// areaSearch.setText("select a number to download");
		} else {
			// buttonSearch.setDisable(true);
			ArrayList<SearchResult> results = node.doUISearch(data);
			this.resultsCount = results.size();

			if (resultsCount == 0) {
				// areaSearch.setText("Sorry, no files found");

			} else {

				int optionNo = 1;
				for (SearchResult s : results) {
					searchlist.add(new SearchResultView(optionNo, s.getFileName(), s.getAddress(), s.getPort(),
							s.getHops(), String.valueOf(s.getTimeElapsed()) + "ms"));
					optionNo++;
				}

				tablesearch.setItems(searchlist);
			}
			textSearch.setText("");
			// buttonSearch.setDisable(false);
		}

	}

	public void downloadAction() {

		String data = textDownload.getText();

		if (data.equals(null) || data.isEmpty()) {
			setDownloadLog("type something to search");
		} else {
			if (isStringInt(data)) {
				int intData = Integer.parseInt(data);
				if (intData <= resultsCount) {
					this.setDownloadLog("File starting to download.....");
					String filename = node.getFile(intData, this.areaDownload);
					if (filename != "") {
						node.addFile(filename);
						areaAvailable.setText(getAvailableFiles());
					}
					textDownload.setText("");
					this.resultsCount = 0;
				} else {
					setDownloadLog("Invalid option");
				}

			} else {
				setDownloadLog("Enter a valid integer");
			}

		}

	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		rid.setCellValueFactory(new PropertyValueFactory<RoutingTableView, String>("id"));
		address.setCellValueFactory(new PropertyValueFactory<RoutingTableView, String>("address"));
		port.setCellValueFactory(new PropertyValueFactory<RoutingTableView, String>("port"));
		pings.setCellValueFactory(new PropertyValueFactory<RoutingTableView, String>("pings"));

		sid.setCellValueFactory(new PropertyValueFactory<SearchResultView, String>("DownloadID"));
		filename.setCellValueFactory(new PropertyValueFactory<SearchResultView, String>("filename"));
		ipaddress.setCellValueFactory(new PropertyValueFactory<SearchResultView, String>("address"));
		portNo.setCellValueFactory(new PropertyValueFactory<SearchResultView, String>("portNo"));
		hops.setCellValueFactory(new PropertyValueFactory<SearchResultView, String>("hops"));
		time.setCellValueFactory(new PropertyValueFactory<SearchResultView, String>("time"));

		// buttonSearch.disableProperty().bind(textSearch.textProperty().isEmpty());

		String uniqueID = UUID.randomUUID().toString();
		try {
			node = new Node("node" + uniqueID);
		} catch (Exception e) {
			e.printStackTrace();
		}
		node.init();

		this.setData(node.getIpAddress(), String.valueOf(node.getPort()));
		this.setRoutingTable(this.getRoutingTable());

		Thread thread = new Thread("New Thread") {
			public void run() {
				int count = 0;
				while (true)
					try {
						this.sleep(1000);
						setRoutingTable(getRoutingTable());
						count++;
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
			}

		};
		thread.start();

	}

	private boolean isStringInt(String s) {
		try {
			Integer.parseInt(s);
			return true;
		} catch (NumberFormatException ex) {
			return false;
		}
	}

	private void setDownloadLog(String log) {
		this.areaDownload.setText(log);
	}

}
