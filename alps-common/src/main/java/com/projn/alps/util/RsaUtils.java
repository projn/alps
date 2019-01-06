package com.projn.alps.util;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.DSAPrivateKeySpec;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

/**
 * rsa utils
 *
 * @author : sunyuecheng
 */
public final class RsaUtils {
    public static final int RSAKEY_BIT_1024 = 1024;

    public static final int RSAKEY_BIT_2048 = 2048;

    private static final int RSAKEY_TYPE_UNKNOWN = -1;

    private static final int RSAKEY_TYPE_PKCS8 = 0;

    private static final int RSAKEY_TYPE_RSA = 1;

    private static final int RSAKEY_TYPE_DSA = 2;

    private static final int RSA_PKCS1_PADDING_SIZE = 11;

    private static final String CIPHER = "RSA/None/PKCS1Padding";

    private static final long DECRYPT_FILE_MAX_PLAIN_SIZE = 50 * 1024 * 1024;

    private static final String RSA="RSA";

    private static final String DSA="DSA";

    private static final String CIPHER_PLUGIN="BC";

    private static final String BEGIN_RSA_PRIVATE_KEY ="-----BEGIN RSA PRIVATE KEY-----";
    private static final String END_RSA_PRIVATE_KEY ="-----END RSA PRIVATE KEY-----";
    private static final String BEGIN_DSA_PRIVATE_KEY ="-----BEGIN DSA PRIVATE KEY-----";
    private static final String END_DSA_PRIVATE_KEY ="-----END DSA PRIVATE KEY-----";
    private static final String BEGIN_PRIVATE_KEY ="-----BEGIN PRIVATE KEY-----";
    private static final String END_PRIVATE_KEY ="-----END PRIVATE KEY-----";

    private RsaUtils() {
    }

    private static void asn1Parse(byte[] b, List<BigInteger> integers) throws Exception {
        int pos = 0;
        while (pos < b.length) {
            byte tag = b[pos++];
            int length = b[pos++];
            if ((length & 0x80) != 0) {
                int extLen = 0;
                for (int i = 0; i < (length & 0x7F); i++) {
                    extLen = (extLen << 8) | (b[pos++] & 0xFF);
                }
                length = extLen;
            }
            byte[] contents = new byte[length];
            System.arraycopy(b, pos, contents, 0, length);
            pos += length;

            if (tag == 0x30) {
                asn1Parse(contents, integers);
            } else if (tag == 0x02) {
                BigInteger i = new BigInteger(contents);
                integers.add(i);
            } else {
                throw new KeyException("Unsupported ASN.1 tag " + tag + " encountered.");
            }
        }
    }

    /**
     * make r s a private key
     *
     * @param key :
     * @return PrivateKey :
     * @throws Exception :
     */
    public static PrivateKey makeRSAPrivateKey(String key) throws Exception {
        byte[] bytes = Base64.decodeBase64(key);

        KeyFactory kf = KeyFactory.getInstance(RSA);
        List<BigInteger> rsaIntegers = new ArrayList<BigInteger>();
        asn1Parse(bytes, rsaIntegers);
        if (rsaIntegers.size() < 8) {
            throw new KeyException("Do not appear to be a properly formatted RSA key.");
        }
        BigInteger publicExponent = rsaIntegers.get(2);
        BigInteger privateExponent = rsaIntegers.get(3);
        BigInteger modulus = rsaIntegers.get(1);
        BigInteger primeP = rsaIntegers.get(4);
        BigInteger primeQ = rsaIntegers.get(5);
        BigInteger primeExponentP = rsaIntegers.get(6);
        BigInteger primeExponentQ = rsaIntegers.get(7);
        BigInteger crtCoefficient = rsaIntegers.get(8);
        KeySpec spec = new RSAPrivateCrtKeySpec(modulus, publicExponent, privateExponent, primeP, primeQ,
                primeExponentP, primeExponentQ, crtCoefficient);
        if (kf == null) {
            throw new KeyException("Invaild key spec.");
        }
        return kf.generatePrivate(spec);
    }

    /**
     * make private key
     *
     * @param key :
     * @return PrivateKey :
     * @throws Exception :
     */
    public static PrivateKey makePrivateKey(String key) throws Exception {
        String[] lines = key.trim().split("\n");
        if (lines.length < 2) {
            return null;
        }

        int rsakeyType = RSAKEY_TYPE_UNKNOWN;
        if (BEGIN_RSA_PRIVATE_KEY.equals(lines[0])
                && END_RSA_PRIVATE_KEY.equals(lines[lines.length - 1])) {
            rsakeyType = RSAKEY_TYPE_RSA;
        } else if (BEGIN_DSA_PRIVATE_KEY.equals(lines[0])
                && END_DSA_PRIVATE_KEY.equals(lines[lines.length - 1])) {
            rsakeyType = RSAKEY_TYPE_DSA;
        } else if (BEGIN_PRIVATE_KEY.equals(lines[0])
                && END_PRIVATE_KEY.equals(lines[lines.length - 1])) {
            rsakeyType = RSAKEY_TYPE_PKCS8;
        } else {
            return null;
        }

        StringBuffer buf = new StringBuffer();
        for (int i = 1; i < lines.length - 1; i++) {
            buf.append(lines[i]);
        }
        byte[] bytes = Base64.decodeBase64(buf.toString());

        KeyFactory kf = null;
        KeySpec spec = null;

        if (rsakeyType == RSAKEY_TYPE_PKCS8) {
            kf = KeyFactory.getInstance(RSA);
            spec = new PKCS8EncodedKeySpec(bytes);
        } else if (rsakeyType == RSAKEY_TYPE_RSA) {
            // PKCS#1 format
            kf = KeyFactory.getInstance(RSA);
            List<BigInteger> rsaIntegers = new ArrayList<BigInteger>();
            asn1Parse(bytes, rsaIntegers);
            if (rsaIntegers.size() < 8) {
                throw new KeyException("Do not appear to be a properly formatted RSA key.");
            }
            BigInteger publicExponent = rsaIntegers.get(2);
            BigInteger privateExponent = rsaIntegers.get(3);
            BigInteger modulus = rsaIntegers.get(1);
            BigInteger primeP = rsaIntegers.get(4);
            BigInteger primeQ = rsaIntegers.get(5);
            BigInteger primeExponentP = rsaIntegers.get(6);
            BigInteger primeExponentQ = rsaIntegers.get(7);
            BigInteger crtCoefficient = rsaIntegers.get(8);
            spec = new RSAPrivateCrtKeySpec(modulus, publicExponent, privateExponent, primeP, primeQ, primeExponentP,
                    primeExponentQ, crtCoefficient);
        } else if (rsakeyType == RSAKEY_TYPE_DSA) {
            kf = KeyFactory.getInstance(DSA);
            List<BigInteger> dsaIntegers = new ArrayList<BigInteger>();
            asn1Parse(bytes, dsaIntegers);
            if (dsaIntegers.size() < 5) {
                throw new KeyException("Do not appear to be a properly formatted DSA key.");
            }
            BigInteger privateExponent = dsaIntegers.get(1);
            //BigInteger publicExponent = dsaIntegers.get(2);
            BigInteger p = dsaIntegers.get(3);
            BigInteger q = dsaIntegers.get(4);
            BigInteger g = dsaIntegers.get(5);
            spec = new DSAPrivateKeySpec(privateExponent, p, q, g);
        }

        if (spec != null) {
            return kf.generatePrivate(spec);
        }

        return null;
    }

    /**
     * decrypt
     *
     * @param encryptKey :
     * @param keyBits    :
     * @param encrypted  :
     * @param offset     :
     * @param length     :
     * @return byte[] :
     * @throws Exception :
     */
    public static byte[] decrypt(Key encryptKey, int keyBits, byte[] encrypted, int offset, int length)
            throws Exception {
        int plainLength = (encrypted[offset] & 0xFF) | ((encrypted[offset + 1] << 8) & 0xff00)
                | ((encrypted[offset + 2] << 16) & 0xff0000)
                | ((encrypted[offset + 3] << 24) & 0xff000000);
        int blockLength = keyBits / 8;
        if (length % blockLength != 4) {
            return null;
        }

        Security.addProvider(new BouncyCastleProvider());
        Cipher cipher = Cipher.getInstance(CIPHER, CIPHER_PLUGIN);
        cipher.init(Cipher.DECRYPT_MODE, encryptKey);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int decryptedLength = 0;
        for (int begin = offset + 4; begin < length; begin += blockLength, plainLength -= decryptedLength) {
            byte[] plain = cipher.doFinal(encrypted, begin, blockLength);

            decryptedLength = Math.min(plain.length, plainLength);
            baos.write(plain, 0, decryptedLength);
        }

        return baos.toByteArray();
    }

    /**
     * decrypt file
     *
     * @param encryptedFilepath :
     * @param plainFilepath     :
     * @param encryptKey        :
     * @param keyBits           :
     * @return boolean :
     * @throws Exception :
     */
    public static boolean decryptFile(String encryptedFilepath, String plainFilepath, Key encryptKey, int keyBits)
            throws Exception {
        int blockLength = keyBits / 8;

        Security.addProvider(new BouncyCastleProvider());
        Cipher cipher = Cipher.getInstance(CIPHER, CIPHER_PLUGIN);
        cipher.init(Cipher.DECRYPT_MODE, encryptKey);

        File inFile = new File(encryptedFilepath);
        File outFile = new File(plainFilepath);
        long fileSize = inFile.length();
        if (fileSize <= 4 || fileSize % blockLength != 4) {
            return false;
        }
        boolean ok = false;
        try(FileInputStream fis = new FileInputStream(inFile);FileOutputStream fos = new FileOutputStream(outFile)) {

            byte[] head = new byte[4];
            int readSize = fis.read(head, 0, 4);
            if (readSize != 4) {
                return false;
            }

            int plainLength = (head[0] & 0xFF) | ((head[1] << 8) & 0xff00)
                    | ((head[2] << 16) & 0xff0000)
                    | ((head[3] << 24) & 0xff000000);

            if (plainLength > DECRYPT_FILE_MAX_PLAIN_SIZE) {
                return false;
            }

            byte[] encrypted = new byte[blockLength];

            int decryptedLength = 0;
            readSize = fis.read(encrypted, 0, blockLength);
            for (; readSize == blockLength; plainLength -= decryptedLength) {
                byte[] plain = cipher.doFinal(encrypted, 0, blockLength);
                decryptedLength = Math.min(plain.length, plainLength);

                fos.write(plain, 0, decryptedLength);

                readSize = fis.read(encrypted, 0, blockLength);
            }

            if (plainLength != 0) {
                return false;
            }

            ok = true;
        } finally {
            if (!ok) {
                boolean ret = outFile.delete();
            }
        }

        return ok;
    }

    /**
     * encrypt
     *
     * @param encryptKey :
     * @param keyBits    :
     * @param plain      :
     * @param offset     :
     * @param length     :
     * @return byte[] :
     * @throws Exception :
     */
    public static byte[] encrypt(Key encryptKey, int keyBits, byte[] plain, int offset, int length)
            throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        byte[] head = new byte[4];
        head[0] = (byte) (length & 0xFF);
        head[1] = (byte) ((length >> 8) & 0xFF);
        head[2] = (byte) ((length >> 16) & 0xFF);
        head[3] = (byte) ((length >> 24) & 0xFF);
        int blockLength = keyBits / 8 - RSA_PKCS1_PADDING_SIZE;

        baos.write(head);

        Security.addProvider(new BouncyCastleProvider());
        Cipher cipher = Cipher.getInstance(CIPHER, CIPHER_PLUGIN);
        cipher.init(Cipher.ENCRYPT_MODE, encryptKey);

        int left = length;
        for (int begin = 0; begin < length; begin += blockLength, left -= blockLength) {
            int toEncryptLength = Math.min(blockLength, left);
            byte[] encrypted = cipher.doFinal(plain, begin, toEncryptLength);
            baos.write(encrypted);
        }

        return baos.toByteArray();
    }

    /**
     * make public key
     *
     * @param key :
     * @return PublicKey :
     * @throws Exception :
     */
    public static PublicKey makePublicKey(String key)
            throws Exception {
        byte[] keyBytes = Base64.decodeBase64(key);
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory factory = KeyFactory.getInstance(RSA);

        return factory.generatePublic(x509EncodedKeySpec);
    }

}
