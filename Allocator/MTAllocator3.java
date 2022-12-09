//package Allocator;
//
//import java.util.concurrent.ConcurrentHashMap;
//
//import Debugger.Logger;
//
//public class MTAllocator3 implements Allocator {
//
//    public ConcurrentHashMap<String, STAllocator> allocators;
//    ;
//
//    private Logger logger;
//
//
//    public MTAllocator3() {
//        allocators = new ConcurrentHashMap<>();
//        logger = Logger.getInstance();
//    }
//
//
//    public STAllocator getAllocator(boolean createIfNotExists) {
//        String threadName = Thread.currentThread().getName();
//
//        STAllocator allocator;
//
//        // Use the computeIfAbsent method to check if the allocator exists
//        // and create it if it does not
//        allocator = allocators.computeIfAbsent(threadName, (k) -> {
//            if (createIfNotExists) {
//                return new STAllocator();
//            } else {
//                // Return null if the allocator does not exist and createIfNotExists is false
//                return null;
//            }
//        });
//
//        return allocator;
//    }
//
//    @Override
//    public Long allocate(int size) {
//        Long address;
//        STAllocator allocator = getAllocator(true);
//
//        if (allocator == null)
//            throw new NullPointerException();
//        address = allocator.allocate(size);
//        return address;
//    }
//
//
//    }