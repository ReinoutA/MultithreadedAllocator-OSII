package Allocator;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import Debugger.Logger;

public class MTAllocator implements Allocator {
    public ConcurrentHashMap<String, STAllocator> allocators;;

    private Logger logger;

    private ReadWriteLock lock;

    public MTAllocator() {
        allocators = new ConcurrentHashMap<>();
        logger = Logger.getInstance();
        lock = new ReentrantReadWriteLock();
    }

    public STAllocator getAllocator(boolean createIfNotExists) {
        String threadName = Thread.currentThread().getName();

        lock.readLock().lock();
        STAllocator allocator;
        synchronized(allocators) {
            allocator = allocators.get(threadName);
        }
        lock.readLock().unlock();

        if(allocator == null) {
            if(createIfNotExists) {
                allocator = new STAllocator();
                lock.writeLock().lock();
                allocators.put(threadName, allocator);
                lock.writeLock().unlock();
            } else throw new AllocatorException("Allocator does not exist");
        }

        return allocator;
    }

    @Override
    public Long allocate(int size) {
        Long address;
        STAllocator allocator = getAllocator(true);

        if(allocator == null)
            throw new NullPointerException();
        address = allocator.allocate(size);
        return address;
    }

    @Override
    public void free(Long address) throws AllocatorException {
        try {
            // Get the allocator of the thread that allocated the address
            STAllocator allocator = getAllocator(false);

            synchronized(allocator) {
                allocator.free(address);    // Free the address while synchronising on the allocator
            }
        } catch(AllocatorException e) {
            boolean found;
            // Release the read lock on the lock field before trying to synchronize on the STAllocator instances
            lock.readLock().unlock();
            // Acquire a lock on the allocators map before iterating over it
            lock.writeLock().lock();
            // If not found in the own allocator search in all other allocators and try to free the address
            for(STAllocator allocator : allocators.values()) {
                try {
                    found = true;
                    synchronized(allocator) {
                        allocator.free(address);
                    }
                } catch(AllocatorException e2) {
                    found = false;
                }
                if(found)
                    break;
            }
            // Release the lock on the allocators map
            lock.writeLock().unlock();
        }
    }


    @Override
    public Long reAllocate(Long oldAddress, int newSize) {
        free(oldAddress);
        return allocate(newSize);
    }
    @Override
    public boolean isAccessible(Long address) {
        return isAccessible(address, 1);
    }
    @Override
    public boolean isAccessible(Long address, int size) {
        // Acquire a lock on the allocators map before iterating over it
        lock.readLock().lock();
        for(STAllocator a : allocators.values()) {
            synchronized(a) {
                if(a.isAccessible(address, size)) {
                    lock.readLock().unlock();
                    return true;
                }
            }
        }
        // Release the lock on the allocators map
        lock.readLock().unlock();
        return false;
    }

}







