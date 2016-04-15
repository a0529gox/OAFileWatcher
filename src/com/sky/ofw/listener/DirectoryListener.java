package com.sky.ofw.listener;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;

import com.sky.ofw.data.PathRegister;
import com.sky.ofw.gui.ControllerHelper;

public class DirectoryListener {
    
    private WatchService watcher = null;

    public DirectoryListener() throws Exception {
        watcher = FileSystems.getDefault().newWatchService();
    }

    public static void main(String[] args) throws Exception {
        new Thread(() -> {
            try {
                new DirectoryListener().run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    public void run() throws Exception {
        
        while (true) {
            try {
                WatchKey key = watcher.take();
                
                Path dir = (Path) key.watchable();
                
                for (WatchEvent<?> event : key.pollEvents()) {

                    @SuppressWarnings("unchecked")
                    WatchEvent<Path> ev = (WatchEvent<Path>)event;
                    
                	ControllerHelper.getInstance().addFileTableCol(dir, ev);
                    
                    boolean resetSuccess = key.reset();
                    if (!resetSuccess) {
                        System.out.println("reset failed.");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public void register(Path client, Path server) throws Exception {
    	
        Files.walkFileTree(client, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
                
                try {
					String dirPath = dir.toString();
					String serverPathStr = server.toString() + dirPath.substring(client.toString().length());
					Path serverDir = new File(serverPathStr).toPath();
					
					PathRegister.getInstance().register(dir, serverDir);
				} catch (Exception e) {
					e.printStackTrace();
				}
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
