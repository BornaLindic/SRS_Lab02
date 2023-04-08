package hr.fer.srs.lab2;

import java.io.*;
import java.util.*;

public class Init {

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        Map<String, byte[]> passwords = new HashMap<>();
        Map<String, byte[]> salts = new HashMap<>();
        Set<String> usersChange = new HashSet<>();

        Utils.store(passwords, salts);

        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("data/usersChange"));
        oos.writeObject(usersChange);
        oos.close();

        Scanner sc = new Scanner(new File("data/commonPasswords10000list.txt"));
        String line;
        Set<String> commonPasswords10000 = new HashSet<>();
        while (sc.hasNextLine()) {
            line = sc.nextLine();
            commonPasswords10000.add(line);
        }
        sc.close();

        oos = new ObjectOutputStream(new FileOutputStream("data/commonPasswords10000"));
        oos.writeObject(commonPasswords10000);
        oos.close();
    }

}
