package org.example.p5_grafico.db;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Random;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class AES {
    public static String Encrypt(String text, String encryption_key){

        encryption_key = NormalizeKey(encryption_key);
        encryption_key = RandomizeKey(encryption_key);

        SecretKeySpec key = new SecretKeySpec(encryption_key.getBytes(), "AES");

        try{
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedText = cipher.doFinal(text.getBytes());
            return Base64.getEncoder().encodeToString(encryptedText);
        }catch(InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e){
            return null;
        }
    }

    public static String Decrypt(String text, String encryption_key){

        encryption_key = NormalizeKey(encryption_key);
        encryption_key = RandomizeKey(encryption_key);

        SecretKeySpec key = new SecretKeySpec(encryption_key.getBytes(), "AES");

        try{
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(text));
            return new String(plainText);
        }catch(InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e){
            return null;
        }
    }

    private static String NormalizeKey(String key){
        if(key.length() < 32){

            String newKey = key;

            int diff = 32 - key.length();

            do{
                newKey = newKey + key.substring(0, Math.min(diff, key.length()));
                diff = 32 - newKey.length();
            }while(newKey.length() < 32);

            key = newKey;

        }else if(key.length() > 32) key = key.substring(0, 31);

        return key;
    }
    private static String RandomizeKey(String key){

        char[] chars = key.toCharArray();
        long seed;

        try{
            seed = StringToLong(key);
        }catch(NoSuchAlgorithmException e){
            seed = 0;
        }

        Random random = new Random(seed);

        for (int i = chars.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1) % chars.length;
            char temp = chars[index];
            chars[index] = chars[i];
            chars[i] = temp;
        }

        return new String(chars);
    }
    private static long StringToLong(String input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(input.getBytes());
        BigInteger bigInt = new BigInteger(1, hash);
        return bigInt.longValue();
    }
}
