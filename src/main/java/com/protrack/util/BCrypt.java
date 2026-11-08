package com.protrack.util;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;

/**
 * BCrypt password hashing implementation.
 * Provides standard Blowfish-based crypt password hashing compatible with OpenBSD bcrypt.
 */
public class BCrypt {
    // BCrypt parameters
    private static final int GENSALT_DEFAULT_LOG2_ROUNDS = 10;
    private static final int BCRYPT_SALT_LEN = 16;
    private static final int BLOWFISH_NUM_ROUNDS = 16;

    // Table for mapping 6-bit numbers to Base64 characters
    private static final char[] BCRYPT_P_CHARS = {
            '.', '/', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
            'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
            'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
            'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9'
    };

    // Index table for reversing BCRYPT_P_CHARS
    private static final byte[] INDEX_64 = {
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,  0,  1,
            54, 55, 56, 57, 58, 59, 60, 61, 62, 63, -1, -1, -1, -1, -1, -1,
            -1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15, 16,
            17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, -1, -1, -1, -1, -1,
            -1, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42,
            43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, -1, -1, -1, -1, -1
    };

    // Standard Blowfish P-box and S-boxes initialization
    private static final int[] P_ORIG = {
            0x243f6a88, 0x85a308d3, 0x13198a2e, 0x03707344,
            0xa4093822, 0x299f31d0, 0x082efa98, 0xec4e6c89,
            0x452821e6, 0x38d01377, 0xbe5466cf, 0x34e90c6c,
            0xc0ac29b7, 0xc97c50dd, 0x3f84d5b5, 0xb5470917,
            0x9216d5d9, 0x8979fb1b
    };

    private static final int[] S_ORIG = {
            0xd1310ba6, 0x98dfb5ac, 0x2ffd72db, 0xd01adfb7,
            0xb8e1afed, 0x6a267e96, 0xba7c9045, 0xf12c7f99,
            0x24a19947, 0xb3916cf7, 0x0801f2e2, 0x858efc16,
            0x636920d8, 0x71574e69, 0xa458fea3, 0xf4933d7e,
            0x0d95748f, 0x728eb658, 0x718bcd58, 0x82154aee,
            0x7b54a41d, 0xc25a59b5, 0x9c30d539, 0x2af26013,
            0xc5d1b023, 0x286085f0, 0xca417918, 0xb8db38ef,
            0x8e79dcb0, 0x603a180e, 0x6c9e0e8b, 0xb01e8a3e,
            0xd71577c1, 0xbd314b27, 0x78af2fda, 0x55605c60,
            0xe65525f3, 0xaa55ab94, 0x57489862, 0x63e81440,
            0x55ca396a, 0x2aab10b6, 0xb4cc5c34, 0x1141e8ce,
            0xa15486af, 0x7c72e993, 0xb3ee1411, 0x636fbc2a,
            0x2ba9c55d, 0x741831f6, 0xce5c3e16, 0x9b87931e,
            0xafd6ba33, 0x6c24cf5c, 0x7a325381, 0x28958677,
            0x3b8f4898, 0x6b4bb9af, 0xc4bfe81b, 0x66282193,
            0x61d809cc, 0xfb21a991, 0x487cac60, 0x5dec8032,
            0xef845d5d, 0xe98575b1, 0xdc262302, 0xeb651b88,
            0x23893e81, 0xd396acc5, 0x0f6d6ff3, 0x83f44239,
            0x2e0b4482, 0xa4842004, 0x69c8f04a, 0x9e1f9b5e,
            0x21c66842, 0xf6e96c9a, 0x670c9c61, 0xabd388f0,
            0x6a51a0d2, 0xd8542f68, 0x960fa728, 0xab5133a3,
            0x6eef0b6c, 0x137a3be4, 0xba3bf050, 0x7efb2bbe,
            0x9b57a780, 0x5682772a, 0x4f6a1293, 0xa0e19210,
            0xbcb61b38, 0x6bfd5db8, 0x43ab7cd7, 0xab0016e2,
            0x28a70c4e, 0x04e28a43, 0x039f25ba, 0x924d9ddc,
            0x85f4f242, 0x72c47900, 0x028321cb, 0x82b63386,
            0x4729bf25, 0xa22dd4bd, 0x79ca6f6d, 0x3b5477fb,
            0xfeb935b9, 0x436422e6, 0x67c29f37, 0x4e6fb14c,
            0x85721204, 0x77744f4f, 0xf2ac707e, 0x667793a9,
            0xa40b6438, 0xb0f8b888, 0x25918ab8, 0x517f60e9,
            0x17ac7094, 0x58f88a73, 0xb0e0a42d, 0x43da5e52,
            0xab3453ff, 0x45a4c7ec, 0xef44e8db, 0xaec3736a,
            0x52467e93, 0x4208560f, 0x83303093, 0x737d0963,
            0xcc65d43c, 0x000e0bfa, 0x44288e43, 0x40e73627,
            0xabab3883, 0x96bb87f7, 0xb4c81304, 0x0ec2070f,
            0x372b7427, 0x8026e87b, 0xe79fed65, 0x82ec5423,
            0x43865436, 0x11e425d3, 0xac6540d2, 0x70c640f6,
            0x4b7f43ab, 0xec57ce20, 0x5d44cccf, 0x73112006,
            0x4ae3e337, 0x88d75e7e, 0x8408d646, 0xa4f53e07,
            0x2963e614, 0x964cd13c, 0x86f0abc0, 0x427ce752,
            0xaeec73ed, 0x18864f14, 0xce8640fa, 0x9eef4830,
            0xc0c2049e, 0x052c77ce, 0x3644a875, 0xa44020c4,
            0x9fb43d84, 0xb6275c08, 0x417f6c9f, 0xd2c502cc,
            0x8fa164b8, 0xa37329c9, 0x9c656442, 0x3a5beacc,
            0xf454655f, 0x34b0041a, 0x4143042f, 0x9ffc140f,
            0x2630777c, 0xfec4682b, 0x8321e81a, 0x2fbec049,
            0xfa956c80, 0x4148a4c5, 0xdf610dff, 0x1643d052,
            0xb721a1f0, 0x41e6cd8f, 0xa511d53f, 0xaec05cbf,
            0x7bbd62ca, 0x307e72c3, 0x1f83d9b6, 0x5e7dffb3,
            0x5d388c6e, 0xa236164f, 0x48c424c4, 0xa4701970,
            0xeeeb61b2, 0x709e0ed9, 0xb0c219c4, 0x5af39447,
            0x0473e56e, 0x8a243070, 0xa7ac7362, 0xe307deae,
            0x2636534f, 0xc372b52c, 0xa72888c8, 0x5d3002b5,
            0x13d038f6, 0x3833aa60, 0x8334b362, 0x90204b12,
            0x43e06f32, 0xbbbf7a09, 0x547a4c4e, 0x1045b0d9,
            0xe27a1d95, 0x0da7a5a0, 0x41e29aac, 0x421a1431,
            0x70677641, 0x0c93cd71, 0x536130ab, 0x470ab794,
            0xa2029d34, 0xbe968e3a, 0x32805c66, 0x3e31e221,
            0x803231f2, 0x96f7e2fe, 0x5545fe13, 0x86494366,
            0xc001764e, 0xaecfe159, 0x057c45f3, 0x9f6410c4,
            0xa3f2e1e3, 0x00192538, 0x06635092, 0x0d28065b,
            0x3c64a670, 0x525730a6, 0xa4ac9165, 0xddfb511f,
            0x6b67d099, 0x117c0160, 0x9361a796, 0x8ca734e0,
            0x10cc76a1, 0x1c170a9e, 0x7ce6ef73, 0x1123d4c4
    };

    // Blowfish state
    private int[] P;
    private int[] S;

    private static final int BCRYPT_WORDS = 6;
    private static final int[] BF_CRYPT_CIPHERTEXT = {
            0x4f727068, 0x65616e42, 0x65686f6c,
            0x64657253, 0x63727970, 0x74486173
    };

    public BCrypt() {
        P = new int[18];
        S = new int[1024];
    }

    private void initKey() {
        System.arraycopy(P_ORIG, 0, P, 0, P_ORIG.length);
        System.arraycopy(S_ORIG, 0, S, 0, S_ORIG.length);
    }

    private int encipher(int lr0, int lr1, int[] out) {
        int l = lr0;
        int r = lr1;
        l ^= P[0];
        for (int i = 0; i <= 14; i += 2) {
            r ^= (((S[(l >>> 24) & 0xff] + S[0x100 + ((l >>> 16) & 0xff)]) ^ S[0x200 + ((l >>> 8) & 0xff)]) + S[0x300 + (l & 0xff)]) ^ P[i + 1];
            l ^= (((S[(r >>> 24) & 0xff] + S[0x100 + ((r >>> 16) & 0xff)]) ^ S[0x200 + ((r >>> 8) & 0xff)]) + S[0x300 + (r & 0xff)]) ^ P[i + 2];
        }
        r ^= (((S[(l >>> 24) & 0xff] + S[0x100 + ((l >>> 16) & 0xff)]) ^ S[0x200 + ((l >>> 8) & 0xff)]) + S[0x300 + (l & 0xff)]) ^ P[17];
        out[0] = r;
        out[1] = l;
        return 0;
    }

    private void expandKey(byte[] key) {
        int keyIdx = 0;
        for (int i = 0; i < 18; i++) {
            int d = 0;
            for (int k = 0; k < 4; k++) {
                d = (d << 8) | (key[keyIdx] & 0xff);
                keyIdx = (keyIdx + 1) % key.length;
            }
            P[i] ^= d;
        }
        int[] lr = new int[2];
        for (int i = 0; i < 18; i += 2) {
            encipher(lr[0], lr[1], lr);
            P[i] = lr[0];
            P[i + 1] = lr[1];
        }
        for (int i = 0; i < 1024; i += 2) {
            encipher(lr[0], lr[1], lr);
            S[i] = lr[0];
            S[i + 1] = lr[1];
        }
    }

    private void eksExpandKey(byte[] salt, byte[] password) {
        int keyIdx = 0;
        for (int i = 0; i < 18; i++) {
            int d = 0;
            for (int k = 0; k < 4; k++) {
                d = (d << 8) | (password[keyIdx] & 0xff);
                keyIdx = (keyIdx + 1) % password.length;
            }
            P[i] ^= d;
        }

        int[] lr = new int[2];
        int saltIdx = 0;
        for (int i = 0; i < 18; i += 2) {
            int d0 = 0, d1 = 0;
            for (int k = 0; k < 4; k++) {
                d0 = (d0 << 8) | (salt[saltIdx] & 0xff);
                saltIdx = (saltIdx + 1) % salt.length;
            }
            for (int k = 0; k < 4; k++) {
                d1 = (d1 << 8) | (salt[saltIdx] & 0xff);
                saltIdx = (saltIdx + 1) % salt.length;
            }
            lr[0] ^= d0;
            lr[1] ^= d1;
            encipher(lr[0], lr[1], lr);
            P[i] = lr[0];
            P[i + 1] = lr[1];
        }

        for (int i = 0; i < 1024; i += 2) {
            int d0 = 0, d1 = 0;
            for (int k = 0; k < 4; k++) {
                d0 = (d0 << 8) | (salt[saltIdx] & 0xff);
                saltIdx = (saltIdx + 1) % salt.length;
            }
            for (int k = 0; k < 4; k++) {
                d1 = (d1 << 8) | (salt[saltIdx] & 0xff);
                saltIdx = (saltIdx + 1) % salt.length;
            }
            lr[0] ^= d0;
            lr[1] ^= d1;
            encipher(lr[0], lr[1], lr);
            S[i] = lr[0];
            S[i + 1] = lr[1];
        }
    }

    private byte[] crypt(byte[] password, byte[] salt, int logRounds) {
        initKey();
        eksExpandKey(salt, password);
        int rounds = 1 << logRounds;
        for (int i = 0; i < rounds; i++) {
            expandKey(password);
            expandKey(salt);
        }

        int[] cdata = new int[BCRYPT_WORDS];
        System.arraycopy(BF_CRYPT_CIPHERTEXT, 0, cdata, 0, BCRYPT_WORDS);
        int[] lr = new int[2];
        for (int i = 0; i < 64; i++) {
            for (int j = 0; j < (BCRYPT_WORDS / 2); j++) {
                encipher(cdata[2 * j], cdata[2 * j + 1], lr);
                cdata[2 * j] = lr[0];
                cdata[2 * j + 1] = lr[1];
            }
        }

        byte[] ret = new byte[cdata.length * 4];
        for (int i = 0, j = 0; i < cdata.length; i++) {
            ret[j++] = (byte) ((cdata[i] >>> 24) & 0xff);
            ret[j++] = (byte) ((cdata[i] >>> 16) & 0xff);
            ret[j++] = (byte) ((cdata[i] >>> 8) & 0xff);
            ret[j++] = (byte) (cdata[i] & 0xff);
        }
        return ret;
    }

    private static String encodeBase64(byte[] d, int len) {
        StringBuilder rs = new StringBuilder();
        int c1, c2;
        int off = 0;
        while (off < len) {
            c1 = d[off++] & 0xff;
            rs.append(BCRYPT_P_CHARS[(c1 >> 2) & 0x3f]);
            c1 = (c1 & 0x03) << 4;
            if (off >= len) {
                rs.append(BCRYPT_P_CHARS[c1 & 0x3f]);
                break;
            }
            c2 = d[off++] & 0xff;
            c1 |= (c2 >> 4) & 0x0f;
            rs.append(BCRYPT_P_CHARS[c1 & 0x3f]);
            c1 = (c2 & 0x0f) << 2;
            if (off >= len) {
                rs.append(BCRYPT_P_CHARS[c1 & 0x3f]);
                break;
            }
            c2 = d[off++] & 0xff;
            c1 |= (c2 >> 6) & 0x03;
            rs.append(BCRYPT_P_CHARS[c1 & 0x3f]);
            rs.append(BCRYPT_P_CHARS[c2 & 0x3f]);
        }
        return rs.toString();
    }

    private static byte[] decodeBase64(String s, int maxLen) {
        int off = 0;
        int slen = s.length();
        int olen = 0;
        byte[] rs = new byte[maxLen];
        byte c1, c2, c3, c4, o;

        while (off < slen - 1 && olen < maxLen) {
            c1 = (byte) (INDEX_64[s.charAt(off++) & 0x7f]);
            c2 = (byte) (INDEX_64[s.charAt(off++) & 0x7f]);
            if (c1 == -1 || c2 == -1) break;
            o = (byte) ((c1 << 2) | ((c2 & 0x30) >> 4));
            rs[olen++] = o;
            if (olen >= maxLen || off >= slen) break;
            c3 = (byte) (INDEX_64[s.charAt(off++) & 0x7f]);
            if (c3 == -1) break;
            o = (byte) (((c2 & 0x0f) << 4) | ((c3 & 0x3c) >> 2));
            rs[olen++] = o;
            if (olen >= maxLen || off >= slen) break;
            c4 = (byte) (INDEX_64[s.charAt(off++) & 0x7f]);
            o = (byte) (((c3 & 0x03) << 6) | c4);
            rs[olen++] = o;
        }
        return rs;
    }

    public static String gensalt(int logRounds) {
        if (logRounds < 4 || logRounds > 31) {
            throw new IllegalArgumentException("Invalid log_rounds: " + logRounds);
        }
        SecureRandom random = new SecureRandom();
        byte[] rnd = new byte[BCRYPT_SALT_LEN];
        random.nextBytes(rnd);

        StringBuilder rs = new StringBuilder();
        rs.append("$2a$");
        if (logRounds < 10) rs.append("0");
        rs.append(logRounds);
        rs.append("$");
        rs.append(encodeBase64(rnd, rnd.length));
        return rs.toString();
    }

    public static String gensalt() {
        return gensalt(GENSALT_DEFAULT_LOG2_ROUNDS);
    }

    public static String hashpw(String password, String salt) {
        if (password == null || salt == null) {
            throw new IllegalArgumentException("Password or salt cannot be null");
        }
        if (salt.length() < 28) {
            throw new IllegalArgumentException("Invalid salt length");
        }
        if (salt.charAt(0) != '$' || salt.charAt(1) != '2' || (salt.charAt(2) != 'a' && salt.charAt(2) != 'b') || salt.charAt(3) != '$') {
            throw new IllegalArgumentException("Invalid salt version");
        }

        int logRounds = Integer.parseInt(salt.substring(4, 6));
        int realSaltStart = salt.lastIndexOf('$') + 1;
        String realSaltStr = salt.substring(realSaltStart, realSaltStart + 22);

        byte[] passwordBytes;
        try {
            byte[] rawBytes = password.getBytes("UTF-8");
            passwordBytes = new byte[rawBytes.length + 1];
            System.arraycopy(rawBytes, 0, passwordBytes, 0, rawBytes.length);
            passwordBytes[rawBytes.length] = 0; // null termination
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 not supported", e);
        }

        byte[] saltBytes = decodeBase64(realSaltStr, BCRYPT_SALT_LEN);

        BCrypt B = new BCrypt();
        byte[] hashed = B.crypt(passwordBytes, saltBytes, logRounds);

        StringBuilder res = new StringBuilder();
        res.append(salt.substring(0, realSaltStart));
        res.append(realSaltStr);
        res.append(encodeBase64(hashed, BF_CRYPT_CIPHERTEXT.length * 4 - 1));
        return res.toString();
    }

    public static boolean checkpw(String plaintext, String hashed) {
        if (plaintext == null || hashed == null) return false;
        try {
            return hashpw(plaintext, hashed).equals(hashed);
        } catch (Exception e) {
            return false;
        }
    }
}
