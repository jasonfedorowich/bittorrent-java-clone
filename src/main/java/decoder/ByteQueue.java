package decoder;

import java.util.LinkedList;
import java.util.Queue;

public class ByteQueue {

    private Queue<Byte> queue;

    public ByteQueue(byte[] bytes) {
        queue = new LinkedList<>();
        for(byte b : bytes){
            queue.add(b);
        }
    }

    public byte peek(){
        return queue.peek();
    }
    public byte pop(){
        return queue.poll();
    }

    public boolean isEmpty(){
        return queue.isEmpty();
    }
    public int size(){
        return queue.size();
    }

}
