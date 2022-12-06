package Allocator;

import java.util.NavigableMap;
import java.util.TreeMap;
import java.lang.Math;
import java.util.concurrent.ConcurrentHashMap;

import Debugger.Logger;

public class STAllocator implements Allocator {
    private ConcurrentHashMap<Integer, Arena> arenas;

    private Logger logger;

    public STAllocator() {
        arenas = new ConcurrentHashMap<>();
        this.logger = Logger.getInstance();
    }

    private int baseTwo(int number) {
        return (int) (Math.pow(2, Math.ceil(Math.log(number) / Math.log(2))));
    }

    private int baseBlockSize(int number) {
        return (int) (Block.UNIT_BLOCK_SIZE * Math.ceil((double) number / (double) Block.UNIT_BLOCK_SIZE));
    }

     public Long  allocate(int size) throws AllocatorException {
        // If the size is illegal throw an exception
        if(size <= 0)
            throw new AllocatorException("Size can't be negative or zero");
        
        int roundedSizeBaseTwo = baseTwo(size);
        int roundedSizeBaseBlockSize = baseBlockSize(size);

        // Get the arena with the given size
        Arena arena = arenas.get(roundedSizeBaseTwo);

        if(arena == null)
            arena = arenas.get(roundedSizeBaseBlockSize);
        
        // If the arena doesn't exist, create it    
        if(arena == null) {
            if(size > Block.UNIT_BLOCK_SIZE) {
                arena = new Arena(roundedSizeBaseBlockSize);
                arenas.put(roundedSizeBaseBlockSize, arena);
            } else {
                arena = new Arena(Block.UNIT_BLOCK_SIZE, roundedSizeBaseTwo);
                arenas.put(roundedSizeBaseTwo, arena);
            }

        }

        // Allocate a new block from the arena
        Long address = arena.allocate();
        return address;
    }

    public void free(Long address) throws AllocatorException {
        Arena arena = null;

        try {
            for(Arena a : arenas.values()) {
                if(a.isAccessible(address, 1)) {
                    arena = a;
                    arena.free(address);
                    return;
                }
            }
        } catch(ArenaException e) {
            arenas.remove(arena.getPageSize());
            return;
        }

        throw new AllocatorException("Address is not allocated");
    }

    public Long reAllocate(Long oldAddress, int newSize) throws AllocatorException {
        // Check if the new size is valid
        if(newSize <= 0)
            throw new AllocatorException("Size can't be negative or zero");

        Arena arena = null;

        // Get the arena where the address is present
        for(Arena a : arenas.values()) {
            if(a.isAccessible(oldAddress, 1)) {
                arena = a;
                break;
            }
        }

        // If the arena is null, the address is not allocated and thus cannot be reallocated
        if(arena == null)
            throw new AllocatorException("Address is not allocated");

        // Get the old size of the allocation
        int oldSize = arena.getPageSize();
        
        // If the old size is greater or the same as the new size, return the old address
        if(oldSize >= newSize)
            return oldAddress;

        // Free the old address
        free(oldAddress);

        // Allocate a new address with the new size and return it
        return allocate(newSize);
    }

    public boolean isAccessible(Long address) {
        return isAccessible(address, 1);
    }

    public boolean isAccessible(Long address, int size) {
        for(Arena arena : arenas.values()) {
            if(arena.isAccessible(address, size)) {
                return true;
            }
        }
        
        return false;
    }

    @Override
    public String toString() {
        return arenas + "";
    }

}