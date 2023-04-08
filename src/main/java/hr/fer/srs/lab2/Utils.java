package hr.fer.srs.lab2;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Map;

public class Utils {

    public static byte[] generateHash(char[] password, byte[] salt) {
        try {
            KeySpec spec = new PBEKeySpec(password, salt, 65536, 128);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            /*
            PBKDF2
            Stands for Password-based-Key-Derivative-Function, a successor of PBKDF1 and is used to implement a
            pseudorandom function, such as a cryptographic hash, cipher, or HMAC to the input password or passphrase
            along with a salt value and repeats the process many times to produce a derived key, which can then be
            used as a cryptographic key in subsequent operations.

            HMAC
            Stands for Keyed-Hash Message Authentication Code (HMAC) is a specific construction for calculating
            a message authentication code (MAC) involving a cryptographic hash function in combination with a secret
            cryptographic key. Any cryptographic hash function,may be used in the calculation of an HMAC; the resulting
            MAC algorithm is termed HMAC-MD5 or HMAC-SHA1 accordingly.

            SHA512
            hash function, could've used the sha1 which just produces a shorter hash
             */

            return factory.generateSecret(spec).getEncoded();

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            System.exit(1);
        }

        return null;
    }


    public static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        return salt;
    }


    public static void store(Map<String, byte[]> passwords, Map<String, byte[]> salts) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("data/passwords"));
        oos.writeObject(passwords);
        oos = new ObjectOutputStream(new FileOutputStream("data/salts"));
        oos.writeObject(salts);
        oos.close();
    }

    public static boolean passwordIsCorrectFormat(char[] password) {
        boolean hasUpperCase = false;
        boolean hasLowerCase = false;
        boolean hasNumber = false;
        boolean hasSpecialCharacter = false;

        for (char c : password) {
            if (Character.isUpperCase(c)) hasUpperCase = true;
            if (Character.isLowerCase(c)) hasLowerCase = true;
            if (Character.isDigit(c)) hasNumber = true;
            if (!Character.isDigit(c) || !Character.isLetter(c)) hasSpecialCharacter = true;
        }

        return hasUpperCase && hasLowerCase && hasNumber && hasSpecialCharacter;
    }


}
