package Allocator;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import Debugger.Logger;

public class MTAllocator implements Allocator {
    // Add a read-write lock to control access to the allocators map
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();

    public ConcurrentHashMap<String, STAllocator> allocators;
    ;

    private Logger logger;


    public MTAllocator() {
        allocators = new ConcurrentHashMap<>();
        logger = Logger.getInstance();
    }


    public STAllocator getAllocator(boolean createIfNotExists) {
        String threadName = Thread.currentThread().getName();

        STAllocator allocator;

        // Use the computeIfAbsent method to check if the allocator exists
        // and create it if it does not
        allocator = allocators.computeIfAbsent(threadName, (k) -> {
            if (createIfNotExists) {
                return new STAllocator();
            } else {
                throw new AllocatorException("Allocator does not exist");
            }
        });

        return allocator;
    }

    @Override
    public Long allocate(int size) {
        Long address;
        STAllocator allocator = getAllocator(true);

        if (allocator == null)
            throw new NullPointerException();
        address = allocator.allocate(size);
        return address;
    }

    @Override
    public void free(Long address) {
        boolean found = false;

        // Acquire a read lock on the allocators map
        rwLock.readLock().lock();
        try {
            // Iterate over the allocators map and call the free method
            // on the appropriate allocator
            for (STAllocator a : allocators.values()) {
                if (a.isAccessible(address)) {
                    a.free(address);
                    found = true;
                    break;
                }
            }
        } finally {
            // Release the read lock on the allocators map
            rwLock.readLock().unlock();
        }

        // If the address was not found in any allocator, log a warning
        if (!found) {
            logger.log("Address " + address + " not found in any allocator");
        }
    }


    @Override
    public Long reAllocate(Long oldAddress, int newSize) {
        // Acquire a write lock on the allocators map
        rwLock.writeLock().lock();
        try {
            // Iterate over the allocators map and call the free method
            // on the appropriate allocator
            for (STAllocator a : allocators.values()) {
                if (a.isAccessible(oldAddress)) {
                    a.free(oldAddress);
                    break;
                }
            }

            // Get the allocator of the current thread
            STAllocator allocator = getAllocator(true);
            if (allocator == null)
                throw new NullPointerException();

            // Allocate the new memory while synchronising on the allocator
            return allocator.allocate(newSize);
        } finally {
            // Release the write lock on the allocators map
            rwLock.writeLock().unlock();
        }
    }

    @Override
    public boolean isAccessible(Long address) {
        return isAccessible(address, 1);
    }


    @Override
    public boolean isAccessible(Long address, int size) {
        // Acquire a read lock on the allocators map before iterating over it
        rwLock.readLock().lock();
        try {
            // Iterate over the allocators map and check if the address is accessible
            // in any of the allocators
            for (STAllocator a : allocators.values()) {
                // Use a local variable to store the result of the isAccessible method
                boolean accessible = a.isAccessible(address);
                if (accessible) {
                    return true;
                }
            }
        } finally {
            // Release the read lock on the allocators map
            rwLock.readLock().unlock();
        }

        // If the address was not found in any allocator, return false
        return false;

    }

}







