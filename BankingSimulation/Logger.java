package BankingSimulation;

import java.io.FileWriter;
import java.io.IOException;

public class Logger extends Thread {
    private Bank bank;

    public Logger (Bank bank) throws IOException {
        this.bank = bank;
    }

    FileWriter fw = new FileWriter("log.txt");




    @Override
    public void run() {
        long oldNumTransactionsProcessed = 0;
        long oldNumTransactionsInserted = 0;
        var oldTime = System.currentTimeMillis();
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            var numTransactionsProcessed = bank.getNumTransactionsProcessed();
            var numTransactionsInserted = bank.getNumTransactionsInserted();
            var sleepTime = System.currentTimeMillis() - oldTime;
            var change = numTransactionsProcessed - oldNumTransactionsProcessed;
            System.out.println("Bank is processing " + ((float)change)/(sleepTime/1000) + " transactions/s.");
            System.out.println("\tNumber of transactions still in-flight: " + (numTransactionsInserted - numTransactionsProcessed) + ".");
            change = numTransactionsInserted - oldNumTransactionsInserted;
            System.out.println("\tBank is receiving " +  ((float)change)/(sleepTime/1000)  + " transactions/s.");
            System.out.println("\tAverage allocation size: " + Transaction.totalSize.get()/Transaction.numTransactions.get());
            System.out.println("\t% large allocations : " + (100f*Transaction.numTransactionsBiggerThan2Pages.get())/((float)Transaction.numTransactions.get()) + "%");
            //System.out.println(((float)change)/(sleepTime/1000)); //new
            oldNumTransactionsProcessed = numTransactionsProcessed;
            oldNumTransactionsInserted = numTransactionsInserted;
            oldTime = System.currentTimeMillis();
        }

    }
}
