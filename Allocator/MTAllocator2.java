package Allocator;

import java.util.concurrent.ConcurrentHashMap;

import Debugger.Logger;

public class MTAllocator2 implements Allocator {

    public ConcurrentHashMap<String, STAllocator> allocators;
    ;

    private Logger logger;


    public MTAllocator2() {
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

        // Synchronize on the allocators map to control access
        synchronized (allocators) {
            // Iterate over the allocators map and call the free method
            // on the appropriate allocator
            for (STAllocator a : allocators.values()) {
                if (a.isAccessible(address)) {
                    a.free(address);
                    found = true;
                    break;
                }
            }
        }

        // If the address was not found in any allocator, log a warning
        if (!found) {
            logger.log("Address " + address + " not found in any allocator");
        }
    }


    @Override
    public Long reAllocate(Long oldAddress, int newSize) {
        // Synchronize on the allocators map to control access
        synchronized (allocators) {
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
        }
    }

    @Override
    public boolean isAccessible(Long address) {
        return isAccessible(address, 1);
    }
    public boolean isAccessible(Long address, int size) {
        // Synchronize on the allocators map to control access
        synchronized (allocators) {
            // Iterate over the allocators map and check if the address is accessible
            // in any of the allocators
            for (STAllocator a : allocators.values()) {
                // Use a local variable to store the result of the isAccessible method
                boolean accessible = a.isAccessible(address);
                if (accessible) {
                    return true;
                }
            }
        }

        // If the address was not found in any allocator, return false
        return false;

    }

}

