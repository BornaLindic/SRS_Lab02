package hr.fer.srs.lab2;

import java.io.*;
import java.util.*;

public class Usermgmt {

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

        switch (args[0]) {
            case "add": {
                if (passwords.containsKey(args[1])) {
                    System.out.println("User already exists!");
                    return;
                }

                System.out.println("Password: ");
                char[] password = System.console().readPassword();

                System.out.println("Repeat password");
                char[] password2 = System.console().readPassword();

                if (!Arrays.equals(password, password2)) {
                    System.out.println("User add failed. Password mismatch.");
                    return;
                }

                if (commonPasswords10000.contains(String.valueOf(password))) {
                    System.out.println("User add failed. Passwords is on a list of 10_000 most commonly used passwords.");
                    return;
                }

                if (password.length < 8) {
                    System.out.println("Password must be at least 8 characters long.");
                    return;
                }

                if (!Utils.passwordIsCorrectFormat(password)) {
                    System.out.println("Password must contain a lowercase, uppercase, number and a digit!");
                    return;
                }

                byte[] salt = Utils.generateSalt();
                byte[] hash = Utils.generateHash(password, salt);

                passwords.put(args[1], hash);
                salts.put(args[1], salt);
                Utils.store(passwords, salts);

                System.out.println("User " + args[1] + " successfuly added.");
                return;
            }
            case "passwd": {
                if (!passwords.containsKey(args[1])) {
                    System.out.println("Password change failed. Requested user doesn't exist.");
                    return;
                }

                System.out.println("Password: ");
                char[] newPassword = System.console().readPassword();

                System.out.println("Repeat password");
                char[] newPassword2 = System.console().readPassword();

                if (!Arrays.equals(newPassword, newPassword2)) {
                    System.out.println("Password change failed. Password mismatch.");
                    return;
                }

                byte[] salt = salts.get(args[1]);
                byte[] hash = Utils.generateHash(newPassword, salt);
                if (Arrays.equals(hash, passwords.get(args[1]))) {
                    System.out.println("Old password can't be new password!");
                    return;
                }

                salt = Utils.generateSalt();
                hash = Utils.generateHash(newPassword, salt);

                passwords.put(args[1], hash);
                salts.put(args[1], salt)
                Utils.store(passwords, salts);

                System.out.println("Password change successful.");
                return;
            }
            case "forcepass": {
                if (!passwords.containsKey(args[1])) {
                    System.out.println("User doesn't exist.");
                    return;
                }

                usersChange.add(args[1]);

                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("data/usersChange"));
                oos.writeObject(usersChange);
                oos.close();

                System.out.println("User will be requested to change password on next login.");
                return;
            }
            case "del": {
                passwords.remove(args[1]);
                salts.remove(args[1]);
                Utils.store(passwords, salts);

                usersChange.remove(args[1]);
                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("data/usersChange"));
                oos.writeObject(usersChange);
                oos.close();

                System.out.println("User successfully removed.");
                return;
            }
            default:
                System.out.println("Invalid parameter!");
        }


    }

}
