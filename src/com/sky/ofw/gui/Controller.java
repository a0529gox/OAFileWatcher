package com.sky.ofw.gui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.sky.ofw.data.OaPathGetter;
import com.sky.ofw.io.SettingsStream;
import com.sky.ofw.listener.DirectoryListener;
import com.sky.ofw.util.CommonUtils;
import com.sky.ofw.util.ValidateUtil;
import com.sky.ofw.vo.FileBean;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class Controller implements Initializable {
	
	Stage stage;
	@FXML
	Label messageBar;
    
	//Main tab START
    @FXML
    Button mainStartBtn;
    @FXML
    Button allFileOverrideBtn;
    @FXML
    TableView<FileBean> fileTableView;
    @FXML
    TableColumn<FileBean, String> fileNameCol;
    @FXML
    TableColumn<FileBean, String> fileDirCol;
    @FXML
    TableColumn<FileBean, String> modifyTypeCol;
    @FXML
    TableColumn<FileBean, ProgressBar> progressBarCol;
    @FXML
    TableColumn<FileBean, String> workspaceTimeCol;
    @FXML
    TableColumn<FileBean, String> serverTimeCol;
    @FXML
    TableColumn<FileBean, Button> fileOverrideBtnCol;
    @FXML
    TableColumn<FileBean, Button> fileCancelBtnCol;
    @FXML
    TableColumn<FileBean, Label> messageCol;
    
    protected Thread watchThread;
    private boolean isWatching = false;
    //Main tab END
    
    //Settings tab START
    @FXML
    Label clientPathLbl;
    @FXML
    TextField clientPathTxtF;
    @FXML
    Button clientPathDC;
    @FXML
    Label serverPathLbl;
    @FXML
    TextField serverPathTxtF;
    @FXML
    Button serverPathDC;
    @FXML
    Label appDataPathLbl;
    @FXML
    TextField appDataPathTxtF;
    @FXML
    Button appDataPathDC;
	@FXML
	CheckBox qibCb;
	@FXML
	CheckBox qicCb;
	@FXML
	CheckBox qieCb;
	@FXML
	CheckBox qifCb;
	@FXML
	CheckBox qihCb;
	@FXML
	CheckBox qiiCb;
	@FXML
	CheckBox qijCb;
	@FXML
	CheckBox qikCb;
	@FXML
	CheckBox qilCb;
	@FXML
	CheckBox qinCb;
	@FXML
	CheckBox qipCb;
	@FXML
	CheckBox qiqCb;
	@FXML
	CheckBox qiuCb;
	@FXML
	CheckBox qivCb;
	@FXML
	CheckBox qiwCb;
	@FXML
	CheckBox qiyCb;
    @FXML
    Button settingsSaveBtn;
	
	List<CheckBox> cbs = null;
    //Settings tab END
    
    
    public Controller() {
        ControllerHelper.getInstance().bind(this);
    }

    @Override
    public void initialize(URL url, ResourceBundle res) {
        initCbs();
    	initMainTab(url, res);
    	initSettingsTab(url, res);
    	initFromSettings();
    }
    
    protected void initMainTab(URL url, ResourceBundle res) {
    	fileNameCol.setCellValueFactory(new PropertyValueFactory<FileBean, String>("fileName"));
        fileDirCol.setCellValueFactory(new PropertyValueFactory<FileBean, String>("fileDir"));
        modifyTypeCol.setCellValueFactory(new PropertyValueFactory<FileBean, String>("modifyType"));
        progressBarCol.setCellValueFactory(new PropertyValueFactory<FileBean, ProgressBar>("progressBar"));
        workspaceTimeCol.setCellValueFactory(new PropertyValueFactory<FileBean, String>("workspaceTime"));
        serverTimeCol.setCellValueFactory(new PropertyValueFactory<FileBean, String>("serverTime"));
        fileOverrideBtnCol.setCellValueFactory(new PropertyValueFactory<FileBean, Button>("fileOverrideBtn"));
        fileCancelBtnCol.setCellValueFactory(new PropertyValueFactory<FileBean, Button>("fileCancelBtn"));
        messageCol.setCellValueFactory(new PropertyValueFactory<FileBean, Label>("message"));
        
        mainStartBtn.setOnAction((e) -> {
        	clearMessageBar();
        	
        	synchronized (this) {
                if (isWatching) {
                    mainStartBtn.setText("開始監控");
                    watchThread.interrupt();
                    isWatching = false;
                } else {
                	if (!ValidateUtil.isValidDirPath(clientPathTxtF.getText()) ||
                			!ValidateUtil.isValidDirPath(serverPathTxtF.getText())) {
                		messageBar.setText("路徑設置有誤");
                		return;
                	} else if (!ValidateUtil.isSelectedAtLeastOne(cbs)) {
                		messageBar.setText("請至少選擇一個系統");
                		return;
                	}
                	
    				Task<Void> task = new Task<Void>() {
    				    @Override
    				    protected Void call() throws Exception {
    				        DirectoryListener listener = new DirectoryListener();
				        	String client = clientPathTxtF.getText();
				        	String server = serverPathTxtF.getText();
				        	String appData = appDataPathTxtF.getText();
				        	
				        	OaPathGetter pathGetter = new OaPathGetter(
									        			CommonUtils.getPath(client), 
									        			CommonUtils.getPath(server), 
									        			CommonUtils.getPath(appData));
    				        
    				        for (CheckBox q : cbs) {
    				        	if (q.isSelected()) {
    				        		String q16 = q.getText();
	    				        	
	    				        	Map<Path, Path> paths = pathGetter.getPaths(q16);
	    				        			
	    				        	for (Path clientPath : paths.keySet()) {
	    				        		Path serverPath = paths.get(clientPath);
	    				        		
	    				        		if (ValidateUtil.isValidDirPath(clientPath) && 
	        				        			ValidateUtil.isValidDirPath(serverPath)) {
	        				        		listener.register(clientPath, serverPath);
	        				        	}
	    				        	}
					        	}
    				        }
    				        
    				        listener.run();
    				        return null;
    				    }
    				};
				
    				watchThread = new Thread(task);
    				watchThread.setDaemon(true);
    				watchThread.start();
    				
    				mainStartBtn.setText("中止監控");
    				isWatching = true;
              }
          }
        });
        
        allFileOverrideBtn.setOnAction((e) -> {
        	clearMessageBar();
        	
            for (FileBean bean : fileTableView.getItems()) {
            	bean.getFileOverrideBtn().fire();
            }
        });
    }

    protected void initSettingsTab(URL url, ResourceBundle res) {
    	clientPathDC.setOnAction((e) -> {
    		DirectoryChooser dc = new DirectoryChooser();
    		dc.setTitle("選擇Workspace路徑");
    		if (ValidateUtil.isValidDirPath(clientPathTxtF.getText())) {
    			dc.setInitialDirectory(new File(clientPathTxtF.getText()));
    		}
    		
    		File file = dc.showDialog(stage);
    		
    		if (file != null) {
    			clientPathTxtF.setText(file.getAbsolutePath());
    		}
    	});
        
        serverPathDC.setOnAction((e) -> {
            DirectoryChooser dc = new DirectoryChooser();
            dc.setTitle("選擇Server路徑");
            if (ValidateUtil.isValidDirPath(serverPathTxtF.getText())) {
                dc.setInitialDirectory(new File(serverPathTxtF.getText()));
            }
            
            File file = dc.showDialog(stage);
            
            if (file != null) {
                serverPathTxtF.setText(file.getAbsolutePath());
            }
        });
        
        appDataPathDC.setOnAction((e) -> {
            DirectoryChooser dc = new DirectoryChooser();
            dc.setTitle("選擇AppData路徑");
            if (ValidateUtil.isValidDirPath(appDataPathTxtF.getText())) {
                dc.setInitialDirectory(new File(appDataPathTxtF.getText()));
            }
            
            File file = dc.showDialog(stage);
            
            if (file != null) {
                appDataPathTxtF.setText(file.getAbsolutePath());
            }
        });
    	
    	settingsSaveBtn.setOnAction((e) -> {
    		clearMessageBar();
    		settingsSaveBtn.setDisable(true);
    		
    		boolean isValid = true;
    		String clientPath = clientPathTxtF.getText();
            String serverPath  = serverPathTxtF.getText();
            String appDataPath  = appDataPathTxtF.getText();
    		if (isValid && (clientPath == null || !new File(clientPath).isDirectory())) {
    			isValid = false;
    			messageBar.setText("不合法的workspace路徑");
    			
    		}
            if (isValid && (serverPath == null || !new File(serverPath).isDirectory())) {
                isValid = false;
                messageBar.setText("不合法的server路徑");
            }
            if (isValid && (appDataPath == null || !new File(appDataPath).isDirectory())) {
                isValid = false;
                messageBar.setText("不合法的appData路徑");
            }
			
			if (isValid) {
				Map<String, String> data = new HashMap<String, String>();
				data.put("CLIENT_PATH", clientPath);
                data.put("SERVER_PATH", serverPath);
                data.put("APPDATA_PATH", appDataPath);
				for (CheckBox q : cbs) {
					String key = q.getText().toUpperCase();
					String value = String.valueOf(q.isSelected()).toLowerCase();
					
					data.put(key, value);
				}
				
				try {
					SettingsStream.write(data);
				} catch (Exception e1) {
					messageBar.setText("儲存失敗");
		    		settingsSaveBtn.setDisable(false);
					e1.printStackTrace();
					return;
				}
				
				messageBar.setText("儲存成功");
			}
    		settingsSaveBtn.setDisable(false);
    	});
    }
    
    protected void initFromSettings() {
    	Map<String, String> data = null;
    	try {
			data = SettingsStream.read();
		} catch (IOException e) {
			messageBar.setText("初始設定檔錯誤");
			e.printStackTrace();
			return;
		}

    	clientPathTxtF.setText(data.get("CLIENT_PATH"));
        serverPathTxtF.setText(data.get("SERVER_PATH"));
        appDataPathTxtF.setText(data.get("APPDATA_PATH"));
    	for (CheckBox q : cbs) {
    		String key = q.getText().toUpperCase();
    		String value = data.get(key);
    		if ("true".equals(value)) {
    			q.setSelected(true);
    		}
    	}
    }
    
    public void clearMessageBar() {
    	messageBar.setText("");
    }
    
    protected void initCbs() {
    	cbs = new ArrayList<CheckBox>();
        cbs.add(qibCb);
        cbs.add(qicCb);
        cbs.add(qieCb);
        cbs.add(qifCb);
        cbs.add(qihCb);
        cbs.add(qiiCb);
        cbs.add(qijCb);
        cbs.add(qikCb);
        cbs.add(qilCb);
        cbs.add(qinCb);
        cbs.add(qipCb);
        cbs.add(qiqCb);
        cbs.add(qiuCb);
        cbs.add(qivCb);
        cbs.add(qiwCb);
        cbs.add(qiyCb);
    }
    
    public void setStage(Stage stage) {
    	if (this.stage == null) {
        	this.stage = stage;
    	}
    }
}
