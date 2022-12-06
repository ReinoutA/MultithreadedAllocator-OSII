package Debugger;

import Allocator.BlockException;

public class Main {
    public static void main(String[] args) throws BlockException {
        System.out.println("============================================================================================================================");
        System.out.println("                                                        BLOCK TESTER");
        
        try {
            BlockTester blockTester = new BlockTester(false);
            blockTester.test();
        } catch(TesterException e) {
            System.out.println(e.getMessage());
            System.out.println("============================================================================================================================");
        }
        
        System.out.println("                                                        ARENA TESTER");

        try {
            ArenaTester arenaTester = new ArenaTester(false);
            arenaTester.test();
        } catch(TesterException e) {
            System.out.println(e.getMessage());
            System.out.println("============================================================================================================================");
        }

        System.out.println("                                                     STALLOCATOR TESTER");

        try {
            STAllocatorTester allocatorTester = new STAllocatorTester(false);
            allocatorTester.test();
        } catch(TesterException e) {
            System.out.println(e.getMessage());
            System.out.println("============================================================================================================================");
        }

        System.out.println("                                                      ALLOCATOR TESTER");

        try {
            AllocatorTester allocatorTester = new AllocatorTester(false);
            allocatorTester.test();
        } catch(TesterException e) {
            System.out.println(e.getMessage());
            System.out.println("============================================================================================================================");
        }
    }
}
