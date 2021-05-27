package com.pasindu.dev.assignment.node_registry;

import okhttp3.*;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

import static java.nio.file.StandardWatchEventKinds.*;

@Component
@Scope("application")
public class FileWatcherService implements Runnable {
    @Override
    public void run() {
        longBackgorund();
    }

    protected void longBackgorund() {
        System.out.println("Thread Running ---------- ");
        try {
            fileWatcher();
            Thread.sleep(1000);
        }
        catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    private void fileWatcher() {
        WatchService watcher = null;
        try {
            watcher = FileSystems.getDefault().newWatchService();
            Path dir = Paths.get("E:/DC_Assignment/file_set/upload_files/upload");
            dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);

            System.out.println("Watch Service registered for dir: " + dir.getFileName());

            while (true) {
                WatchKey key;
                try {
                    key = watcher.take();
                } catch (InterruptedException ex) {
                    return;
                }
                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
                    @SuppressWarnings("unchecked")
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path fileName = ev.context();

                    if(kind.name().equals("ENTRY_CREATE")) {
                        cacheFileToOtherNodes(fileName);
                    }
                    System.out.println(kind.name() + ": " + fileName);

                    if (kind == ENTRY_MODIFY &&
                            fileName.toString().equals("DirectoryWatchDemo.java")) {
                        System.out.println("My source file has changed!!!");
                    }
                }
                boolean valid = key.reset();
                if (!valid) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cacheFileToOtherNodes(Path fileName) throws Exception{
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file",fileName.toString(),
                        RequestBody.create(MediaType.parse("application/octet-stream"),
                                new File("/C:/Users/Pasindu Weerakoon/Desktop/"+fileName)))
                .build();
        Request request1 = new Request.Builder()
                .url("http://localhost:6061/node-service/upload")
                .method("POST", body)
                .build();
        Request request2 = new Request.Builder()
                .url("http://localhost:6062/node-service/upload")
                .method("POST", body)
                .build();
        Request request3 = new Request.Builder()
                .url("http://localhost:6063/node-service/upload")
                .method("POST", body)
                .build();
        Request request4 = new Request.Builder()
                .url("http://localhost:6064/node-service/upload")
                .method("POST", body)
                .build();
        Request request5 = new Request.Builder()
                .url("http://localhost:6065/node-service/upload")
                .method("POST", body)
                .build();
        Response response1 = client.newCall(request1).execute();
        Response response2 = client.newCall(request2).execute();
        Response response3 = client.newCall(request3).execute();
        Response response4 = client.newCall(request4).execute();
        Response response5 = client.newCall(request5).execute();
    }
}
