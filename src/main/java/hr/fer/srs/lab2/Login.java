package hr.fer.srs.lab2;

import java.io.*;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public class Login {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream("data/passwords"));
        Map<String, byte[]> passwords = (Map) ois.readObject();

        ois = new ObjectInputStream(new FileInputStream("data/salts"));
        Map<String, byte[]> salts = (Map) ois.readObject();

        ois = new ObjectInputStream(new FileInputStream("data/usersChange"));
        Set<String> usersChange = (Set) ois.readObject();

        ois = new ObjectInputStream(new FileInputStream("data/commonPasswords10000"));
        Set<String> commonPasswords10000 = (Set) ois.readObject();

        ois.close();

        System.out.println("Password: ");
        char[] password = System.console().readPassword();

        if(!passwords.containsKey(args[0]) ||
                !Arrays.equals(passwords.get(args[0]), Utils.generateHash(password, salts.get(args[0])))) {
            System.out.println("Username or password incorrect.");
            return;
        }

        if (usersChange.contains(args[0])) {
            System.out.println("Admin requested password change.");

            System.out.println("Password: ");
            char[] newPassword = System.console().readPassword();

            System.out.println("Repeat password");
            char[] newPassword2 = System.console().readPassword();

            if (!Arrays.equals(newPassword, newPassword2)) {
                System.out.println("Password change failed. Password mismatch.");
                return;
            }

            if (commonPasswords10000.contains(String.valueOf(newPassword))) {
                System.out.println("Password change add failed. Passwords is on a list of 10_000 most commonly used passwords.");
                return;
            }

            if (newPassword.length < 8) {
                System.out.println("Password must be at least 8 characters long.");
                return;
            }

            if (!Utils.passwordIsCorrectFormat(newPassword)) {
                System.out.println("Password must contain a lowercase, uppercase, number and a digit!");
                return;
            }

            byte[] salt = salts.get(args[0]);
            byte[] hash = Utils.generateHash(newPassword, salt);
            if (Arrays.equals(hash, passwords.get(args[0]))) {
                System.out.println("Old password can't be new password!");
                return;
            }

            salt = Utils.generateSalt();
            hash = Utils.generateHash(newPassword, salt);

            passwords.put(args[0], hash);
            salts.put(args[0], salt);
            Utils.store(passwords, salts);

            usersChange.remove(args[0]);
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("data/usersChange"));
            oos.writeObject(usersChange);
            oos.close();

            System.out.println("Password change successful.");
        }

        System.out.println("Login successful.");

    }

}
