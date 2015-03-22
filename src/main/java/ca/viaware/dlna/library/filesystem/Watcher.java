package ca.viaware.dlna.library.filesystem;

import ca.viaware.api.logging.Log;
import ca.viaware.dlna.library.EntryType;
import ca.viaware.dlna.library.Library;
import ca.viaware.dlna.library.model.LibraryEntry;
import ca.viaware.dlna.library.model.LibraryFactory;
import ca.viaware.dlna.library.model.LibraryInstanceRunner;
import jdk.nashorn.internal.runtime.regexp.JoniRegExp;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.nio.file.StandardWatchEventKinds.*;

public class Watcher {

    private WatchService watchService;
    private Map<WatchKey, Path> keys;

    public Watcher() throws IOException {
        this.watchService = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<WatchKey, Path>();
    }

    private void register(Path dir) {
        try {
            keys.put(dir.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY), dir);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public void registerEntries() {
        Library.runInstance(new LibraryInstanceRunner() {
            @Override
            public Object run(LibraryFactory factory) {
                ArrayList<LibraryEntry> folderEntries = factory.getAllOfType(EntryType.CONTAINER);

                for (LibraryEntry e : folderEntries) {
                    Path dir = e.getLocation().toPath();
                    register(dir);
                }
                return null;
            }
        });
    }

    private void recurseFolder(Path folder) throws IOException {
        Files.walkFileTree(folder, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                register(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private void remove(final File f) {
        Library.runInstance(new LibraryInstanceRunner() {
            @Override
            public Object run(LibraryFactory factory) {
                LibraryEntry e = factory.getByFile(f);
                if (e != null) {
                    factory.remove(e, true);
                }
                return null;
            }
        });
    }

    private void add(final File f) {
        Library.runInstance(new LibraryInstanceRunner() {
            @Override
            public Object run(LibraryFactory factory) {
                factory.addFileNow(f);
                return null;
            }
        });

        if (f.isDirectory()) {
            for (File file : f.listFiles()) {
                add(file);
            }
        }
    }

    public void run() {
        registerEntries();

        while (true) {
            try {
                WatchKey key = watchService.take();
                Path dir = keys.get(key);

                if (dir == null) {
                    Log.error("Unknown watch key...");
                    continue;
                }

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind kind = event.kind();

                    if (kind == OVERFLOW) continue;

                    @SuppressWarnings("unchecked")
                    WatchEvent<Path> fileEvent = (WatchEvent<Path>) event;
                    Path fileName = fileEvent.context();

                    Path filePath = dir.resolve(fileName);
                    if (Files.isDirectory(filePath) && kind == ENTRY_CREATE) {
                        Log.info("New directory was created %0", filePath.toAbsolutePath());
                        add(filePath.toFile());
                        //Register new directory, and explore it
                        try {
                            recurseFolder(filePath);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (kind == ENTRY_DELETE) {
                        Log.info("File deleted %0", filePath.toAbsolutePath());
                        remove(filePath.toFile());
                    } else if (kind == ENTRY_CREATE) {
                        Log.info("File created %0", filePath.toAbsolutePath());
                        add(filePath.toFile());
                    }

                }

                boolean valid = key.reset();
                if (!valid) {
                    Log.info("The file with path %0 is no longer available", keys.get(key).toAbsolutePath());
                    remove(keys.get(key).toFile());

                    keys.remove(key);

                    if (keys.isEmpty()) break;
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

}
