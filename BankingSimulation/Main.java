package BankingSimulation;

import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws IOException {
        var bank = new Bank();
        ArrayList<Transferer> transferers = new ArrayList<>();
        for (int i = 0; i < 75; i++)
            transferers.add(new Transferer(bank));
        for (var transferer : transferers)
            transferer.start();
    }
}
