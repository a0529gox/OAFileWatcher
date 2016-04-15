package com.sky.ofw.vo;

import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.WatchEvent;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

public class FileBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String fileName;
	private String fileDir;
	private WatchEvent.Kind<Path> kind;
	private String workspaceTime;
	private String serverTime;
	
	private ProgressBar progressBar;
	private Button fileOverrideBtn;
	private Button fileCancelBtn;
	private Label message;
	
	//Transient
	private String serverDir;

	public FileBean() {
		// TODO Auto-generated constructor stub
	}


	public String getFileName() {
		return fileName;
	}


	public void setFileName(String fileName) {
		this.fileName = fileName;
	}


	public String getFileDir() {
		return fileDir;
	}


	public void setFileDir(String fileDir) {
		this.fileDir = fileDir;
	}


	public String getModifyType() {
		if (kind == java.nio.file.StandardWatchEventKinds.ENTRY_CREATE) {
			return "新增";
		} else if (kind == java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY) {
			return "更新";
		} else if (kind == java.nio.file.StandardWatchEventKinds.ENTRY_DELETE) {
			return "刪除";
		}
		return null;
	}


	public WatchEvent.Kind<Path> getKind() {
		return kind;
	}


	public void setKind(WatchEvent.Kind<Path> kind) {
		this.kind = kind;
	}


	public String getWorkspaceTime() {
		return workspaceTime;
	}


	public void setWorkspaceTime(String workspaceTime) {
		this.workspaceTime = workspaceTime;
	}


	public String getServerTime() {
		return serverTime;
	}


	public void setServerTime(String serverTime) {
		this.serverTime = serverTime;
	}


	public ProgressBar getProgressBar() {
		return progressBar;
	}


	public void setProgressBar(ProgressBar progressBar) {
		this.progressBar = progressBar;
	}


	public String getServerDir() {
		return serverDir;
	}


	public void setServerDir(String serverDir) {
		this.serverDir = serverDir;
	}


	public Button getFileOverrideBtn() {
		return fileOverrideBtn;
	}


	public void setFileOverrideBtn(Button fileOverrideBtn) {
		this.fileOverrideBtn = fileOverrideBtn;
	}


	public Button getFileCancelBtn() {
		return fileCancelBtn;
	}


	public void setFileCancelBtn(Button fileCancelBtn) {
		this.fileCancelBtn = fileCancelBtn;
	}


	public Label getMessage() {
		return message;
	}


	public void setMessage(Label message) {
		this.message = message;
	}

	
}
