package torrent.download;

import torrent.MetaInfoFile;
import torrent.peer.PeerConnection;
import torrent.web.Tracker;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Downloader implements AutoCloseable {


    class Worker{
        private final PeerConnection peerConnection;
        Worker(PeerConnection peerConnection) {
            this.peerConnection = peerConnection;
        }

        public Runnable doWork(Deque<Integer> work, Map<Integer, byte[]> buffer) {
            return () -> {
                while(!work.isEmpty()) {
                    int p = work.poll();
                    System.out.println("Worker " + p + " started");
                    try{
                        byte[] bytes = peerConnection.downloadPiece(p);
                        buffer.put(p, bytes);
                    }catch (Exception e){
                        System.out.println("Error downloading piece " + p);
                        work.add(p);
                    }

                    System.out.println("Worker " + p + " finished");

                }
                System.out.println("Worker " + Thread.currentThread().threadId() + " finalized");
            };
        }
    }
    private final List<PeerConnection> connections = new ArrayList<>();
    private final MetaInfoFile metaInfoFile;
    private final String peerId;

    public Downloader(Tracker.TrackerResponse response, MetaInfoFile metaInfoFile, String peerId) {
        this.peerId = peerId;
        this.metaInfoFile = metaInfoFile;
        openConnections(response.getPeers());
    }

    private void openConnections(List<Tracker.Peer> peers) {
        for(Tracker.Peer peer : peers) {
            PeerConnection peerConnection = new PeerConnection(peer, metaInfoFile, peerId);
            connections.add(peerConnection);
            peerConnection.handshake();
        }
    }



    @Override
    public void close() throws Exception {
        for(PeerConnection peerConnection : connections) {
            peerConnection.close();
        }
    }

    private Runnable bufferHandle(Map<Integer, byte[]> buffer, AtomicInteger index, FileOutputStream fileOutputStream, int n) {
        return () -> {
            while(index.get() < n) {
                if(!buffer.isEmpty() && buffer.containsKey(index.get())) {
                    try {
                        System.out.println("Buffer " + index.get());
                        System.out.println("Buffered bytes of size: " + buffer.get(index.get()).length);
                        fileOutputStream.write(buffer.get(index.getAndIncrement()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            System.out.println("Finished buffering");


        };
    }

    public void download(String outputFileName) throws IOException, InterruptedException {
        Deque<Integer> work = new ConcurrentLinkedDeque<>();
        int n = (int)metaInfoFile.getInfo().getPiecesCount();
        for(int i = 0; i < n; i++) {
            work.add(i);
        }
        System.out.println("Downloading " + work.size() + " pieces");

        Map<Integer, byte[]> buffer = Collections.synchronizedMap(new HashMap<>());
        AtomicInteger index = new AtomicInteger(0);
        FileOutputStream fileOutputStream = new FileOutputStream(outputFileName);


        Thread bufferThread = new Thread(bufferHandle(buffer, index, fileOutputStream, n));
        bufferThread.start();
        ExecutorService executorService = Executors.newFixedThreadPool(connections.size());

        for(PeerConnection connection : this.connections) {
            Worker worker = new Worker(connection);
            Runnable workUnit = worker.doWork(work, buffer);
            executorService.execute(workUnit);
        }
        bufferThread.join();

    }

}
