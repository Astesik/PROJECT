package com.example.project.utils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class PasswordUtils {

    private static final int SALT_LENGTH = 16;        // Rozmiar soli (w bajtach)
    private static final int KEY_LENGTH = 256;       // Rozmiar generowanego klucza (w bitach)
    private static final int ITERATIONS = 10000;     // Liczba iteracji do zwiększenia kosztu

    /**
     * Hashuje hasło z wykorzystaniem algorytmu PBKDF2.
     * @param password Hasło w postaci tekstowej.
     * @return Zwraca zahashowane hasło w formacie: SALT:HASH
     */
    public static String hashPassword(String password) {
        try {
            // Tworzenie losowej soli.
            byte[] salt = generateSalt();

            // Hashowanie hasła z użyciem soli.
            byte[] hashedPassword = hashPasswordWithPBKDF2(password, salt);

            // Kodowanie soli i hashowanego hasła do Base64 dla łatwego przechowywania w bazie.
            String encodedSalt = Base64.getEncoder().encodeToString(salt);
            String encodedHash = Base64.getEncoder().encodeToString(hashedPassword);

            // Zwracamy hash w formacie: SALT:HASH
            return encodedSalt + ":" + encodedHash;

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Błąd podczas hashowania hasła", e);
        }
    }

    /**
     * Weryfikacja hasła (porównanie podanego przez użytkownika.
     * @param password Hasło wprowadzone przez użytkownika.
     * @param storedPassword Hasło zapisane w bazie danych (format SALT:HASH).
     * @return `true` jeśli hasło pasuje, w przeciwnym razie `false`.
     */
    public static boolean verifyPassword(String password, String storedPassword) {
        try {
            // Rozdzielenie soli i hash'a
            String[] parts = storedPassword.split(":");
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] storedHash = Base64.getDecoder().decode(parts[1]);

            // Hashowanie hasła użytkownika z tym samym solą
            byte[] userHash = hashPasswordWithPBKDF2(password, salt);

            // Porównanie hash'y bajt po bajcie
            return slowEquals(userHash, storedHash);

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Błąd podczas weryfikacji hasła", e);
        }
    }

    /**
     * Generuje losową sól.
     * @return Tablica bajtów reprezentująca sól.
     */
    private static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return salt;
    }

    /**
     * Hashuje hasło z podaną solą, wykorzystując PBKDF2.
     * @param password Hasło w postaci tekstowej.
     * @param salt Sól w postaci tablicy bajtów.
     * @return Hashowane hasło jako tablica bajtów.
     */
    private static byte[] hashPasswordWithPBKDF2(String password, byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        return skf.generateSecret(spec).getEncoded();
    }

    /**
     * Bezpieczne porównywanie dwóch tablic bajtów (zabezpieczenie przed timing attack).
     * @param a Pierwsza tablica bajtów.
     * @param b Druga tablica bajtów.
     * @return `true` jeśli tablice są identyczne, w przeciwnym razie `false`.
     */
    private static boolean slowEquals(byte[] a, byte[] b) {
        int diff = a.length ^ b.length; // XOR długości
        for (int i = 0; i < a.length && i < b.length; i++) {
            diff |= a[i] ^ b[i]; // XOR zawartości
        }
        return diff == 0;
    }
}