package Debugger;

import Allocator.STAllocator;
import java.util.Random;

public class STAllocatorTester {
    private STAllocator allocator;

    private Long address;

    private Logger logger;

    private boolean debug;

    private int size;

    private String[] states;
    
    public STAllocatorTester(boolean debug) {
        allocator = new STAllocator();
        address = null;
        logger = Logger.getInstance();
        this.debug = debug;
    }

    public void testRange(Long startAddress, int range, boolean condition) throws TesterException {
        if(allocator.isAccessible(startAddress, range) != condition) {
            printStates();
            logger.log("Expected " + condition + " for address " + startAddress
                            + (range <= 1 ? "" : " and range " + range + " with size " + size), 1);
                            throw new TesterException("                                                         TEST FAILED");
        }
    }

    private void printStates() {
        for(String s : states) {
            if(s != null) logger.log(s,1);
        }
    }

    public void test() throws TesterException {
        int[] sizes = {1,2,4,8,16,32,64,128,256,512,1024,2048,4096,8192,12288};

        for(int i = 0; i < sizes.length; i++) {
            size = sizes[i];
            int originalSize = size;
            states = new String[4];

            /*
             * ===========================================================================
             * ==============================ALLOCATION TEST==============================
             * ===========================================================================
             */

            address = allocator.allocate(size);
            states[0] = allocator.toString();

            // Enkele adressen checken
            for(int offset = 0; offset < size; offset++)
                testRange(address + offset, 1, true);

            int range = (int) Math.ceil(size / 2);
            range = range == 0 ? 1 : range;
            
            // Range Onder->In checken
            testRange(address - 1, 2, false);
                        
            // Range In->In checken
            testRange(address, size, true);

            // Range In->Boven checken
            testRange(address, size + 1, false);

            System.out.println("PASSED: Pagesize " + originalSize + " MALLOC");

            /*
             * ===========================================================================
             * ============================REALLOCATION TEST 1============================
             * ===========================================================================
             */
            
            size *= 2;
            address = allocator.reAllocate(address, size);
            states[1] = allocator.toString();
            
            // Enkele adressen checken
            for(int offset = 0; offset < size; offset++) {
                testRange(address + offset, 1, true);
            }     

            // Range Onder->In checken
            testRange(address - 1, 2, false);
                        
            // Range In->In checken
            testRange(address, size, true);

            // Range In->Boven checken
            testRange(address, size + 1, false);

            System.out.println("PASSED: Pagesize " + originalSize + " REALLOC+");

            /*
             * ===========================================================================
             * ============================REALLOCATION TEST 2============================
             * ===========================================================================
             */

            if(size != 2) {
                size /= 4;
                address = allocator.reAllocate(address, size);
                states[2] = allocator.toString();
                
                // Enkele adressen checken
                for(int offset = 0; offset < size; offset++) {
                    testRange(address + offset, 1, true);
                }

                // Range Onder->In checken
                testRange(address - 1, 2, false);
                            
                // Range In->In checken
                testRange(address, size, true);

                System.out.println("PASSED: Pagesize " + originalSize + " REALLOC-");

            }

            /*
             * ===========================================================================
             * =================================FREE TEST=================================
             * ===========================================================================
             */

            allocator.free(address);
            states[3] = allocator.toString();

            // Enkele adressen checken
            for(int offset = 0; offset < size; offset++)
                testRange(address + offset, 1, false);

            // Range Onder->In checken
            testRange(address - 1, 2, false);
                        
            // Range In->In checken
            testRange(address, size, false);

            // Range In->Boven checken
            testRange(address, size + 1, false);

            System.out.println("PASSED: Pagesize " + originalSize + " FREE");
            
        }

        Random random = new Random();

        int amountOfTests = 1000;

        Long[] addresses = new Long[amountOfTests];
        int[] sizesList = new int[amountOfTests];

        
        for(int i = 0; i < amountOfTests; i++) {
            size = random.nextInt(1, 20000);
            states = new String[4];

            states[0] = allocator.toString();
            address = allocator.allocate(size);
            states[0] = allocator.toString();

            testRange(address, size, true);

            addresses[i] = address;
        }

        System.out.println("PASSED: " + amountOfTests + " RANDOM MALLOC");

        for(int i = 0; i < amountOfTests; i++) {
            Long addr = addresses[i];
            size = random.nextInt(1, 20000);
            states = new String[4];

            states[0] = allocator.toString();
            address = allocator.reAllocate(addr, size);
            states[1] = allocator.toString();
            
            testRange(address, size, true);
            
            addresses[i] = address;
            sizesList[i] = size;
        }

        System.out.println("PASSED: " + amountOfTests + " RANDOM REALLOC");

        for(int i = 0; i < amountOfTests; i++) {
            states = new String[4];

            Long addr = addresses[i];
            
            states[0] = allocator.toString();
            allocator.free(addr);
            states[1] = allocator.toString();

            size = sizesList[i];
            testRange(addr, size, false);
        }

        System.out.println("PASSED: " + amountOfTests + " RANDOM FREE");
        
        throw new TesterException("                                                ALL STALLOCATOR TESTS PASSED");
    }
}