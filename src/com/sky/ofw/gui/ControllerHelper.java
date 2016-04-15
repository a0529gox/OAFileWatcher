package com.sky.ofw.gui;

import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.sky.ofw.data.PathRegister;
import com.sky.ofw.util.DateFormatUtil;
import com.sky.ofw.util.NodeFadeUtil;
import com.sky.ofw.vo.FileBean;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableRow;

public class ControllerHelper {
	
	private static ControllerHelper self;
	
	private Controller ctrl;

	private ControllerHelper() {
		// TODO Auto-generated constructor stub
	}
	
	public static ControllerHelper getInstance() {
		if (self == null) {
			self = new ControllerHelper();
		}
		return self;
	}

	public void bind(Controller ctrl) {
		this.ctrl = ctrl;
	}
	
	public void addFileTableCol(Path dir, WatchEvent<Path> ev) {
		 WatchEvent.Kind<Path> kind = ev.kind();
         
         Path fileName = ev.context();
         Path fullPath = dir.resolve(fileName);
         
         removeSameFileAlreadyInView(fullPath);
         
         File clientFile = fullPath.toFile();
         Path serverDir = PathRegister.getInstance().get(dir);
         File serverFile = new File(serverDir.toString() + File.separator + fileName.toString());
         
         boolean isValid = true;
         if (isValid) {
        	 isValid = !(clientFile.isDirectory() && kind == ENTRY_MODIFY);
         }
         if (isValid) {
        	 isValid = !(kind == ENTRY_DELETE && !serverFile.exists());
         }
         
         if (isValid) {
	         FileBean bean = new FileBean();
	         bean.setFileName(fileName.toString());
	         bean.setFileDir(fullPath.toString());
	         bean.setKind(kind);
	         bean.setProgressBar(new ProgressBar());
	         bean.setMessage(new Label(""));
	         
	         if (clientFile.exists()) {
	             bean.setWorkspaceTime(DateFormatUtil.toFullTime(new Date(fullPath.toFile().lastModified())));
	         }
	         
	         bean.setServerDir(serverFile.getAbsolutePath());
	         if (serverFile.exists()) {
	             bean.setServerTime(DateFormatUtil.toFullTime(new Date(serverFile.lastModified())));
	         }
	         bean.setFileOverrideBtn(new Button("覆寫"));
	         bean.getFileOverrideBtn().setOnAction((e) -> {
	        	 fileOverrideBtnOnActionEvent(bean, e);
	         });
	         
	         bean.setFileCancelBtn(new Button("關閉"));
	         bean.getFileCancelBtn().setOnAction((e) -> {
	        	fileCancelBtnOnActionEvent(bean, e);
	         });
	         
	         ctrl.fileTableView.getItems().add(bean);
         }
	}
	
	protected void removeSameFileAlreadyInView(Path fullPath) {
    	FileBean same = null;
    	for (FileBean bean : ctrl.fileTableView.getItems()) {
    		if (fullPath.toString().equals(bean.getFileDir())) {
    			same = bean;
    			break;
    		}
    	}
    	
    	ctrl.fileTableView.getItems().remove(same);
    }
	
	protected void fileOverrideBtnOnActionEvent(FileBean bean, ActionEvent e) {
		Task<Void> task = new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				boolean success = false;
				String message = null;
				
				bean.getFileCancelBtn().setDisable(true);
				bean.getFileOverrideBtn().setDisable(true);
				
				updateMessage("");
				updateProgress(0, 100);
				
				try {
					File clientFile = new File(bean.getFileDir());
					File serverFile = new File(bean.getServerDir());
					if (bean.getKind() == ENTRY_DELETE) {
						boolean deleted = FileUtils.deleteQuietly(serverFile);
						if (!deleted) {
							updateMessage("刪除失敗");
						}
						success = deleted;
					} else {
						if (clientFile.isFile()) {
							if (!serverFile.exists()) {
								serverFile.createNewFile();
							}
							
							//copy file
							FileInputStream fis = null;
							FileOutputStream fos = null;
							FileChannel input = null;
							FileChannel output = null;
							try {
							    fis = new FileInputStream(clientFile);
							    fos = new FileOutputStream(serverFile);
							    input = fis.getChannel();
							    output = fos.getChannel();
							    long size = input.size();
							    long pos = 0L;
							    long count = 0L;
							    while (pos < size) {
							        count = size - pos > 31457280L ? 31457280L : size - pos;
							        pos += output.transferFrom(input, pos, count);
							        
							        updateProgress(pos, size);
							    }
							} finally {
							    IOUtils.closeQuietly(output);
							    IOUtils.closeQuietly(fos);
							    IOUtils.closeQuietly(input);
							    IOUtils.closeQuietly(fis);
							}

							success = true;
						} else {
							if (!serverFile.exists()) {
								serverFile.mkdir();
							}

							success = true;
						}
					}
				} catch (Exception e) {
					message = "發生錯誤";
					e.printStackTrace();
				}
				
				if (success) {
					bean.getFileCancelBtn().setDisable(false);
					updateMessage("執行成功");
					updateProgress(1, 1);
					
					Thread.sleep(3000);
					
					//fade out START
					TableRow<FileBean> row = getCurrentRow(bean);
					
					long fadeTime = 1000;
					NodeFadeUtil.fadeOut(row, fadeTime);
		            
		            Thread.sleep(fadeTime);
					ctrl.fileTableView.getItems().remove(bean);
					
					//再顯示出來，不然該行會永久隱藏
					NodeFadeUtil.fadeIn(row, 1);
					
				} else {
					bean.getFileCancelBtn().setDisable(false);
					bean.getFileOverrideBtn().setDisable(false);
					if (message != null) {
						updateMessage(message);
					}
				}
				return null;
			}
		};
		
		bean.getProgressBar().progressProperty().bind(task.progressProperty());
		bean.getMessage().textProperty().bind(task.messageProperty());
		
		Thread thread = new Thread(task);
		thread.setDaemon(true);
		thread.start();
	}
	
	protected void fileCancelBtnOnActionEvent(FileBean bean, ActionEvent e) {
		ctrl.fileTableView.getItems().remove(bean);
	}
	
	@SuppressWarnings("unchecked")
	public TableRow<FileBean> getCurrentRow(FileBean bean) {
		TableRow<FileBean> row = null;
		int i = 0;
		for (Node n: ctrl.fileTableView.lookupAll("TableRow")) {
			if (ctrl.fileTableView.getItems().get(i).equals(bean)) {
				row = (TableRow<FileBean>) n;
				break;
			}
			i++;
		}
		return row;
	}
}
