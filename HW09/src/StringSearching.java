import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Your implementations of various string searching algorithms.
 *
 * @author Nate Knauf
 * @version 1.1
 */
public class StringSearching {

    /**
     * Knuth-Morris-Pratt (KMP) algorithm that relies on the failure table (also
     * called failure function). Works better with small alphabets.
     *
     * Make sure to implement the failure table before implementing this method.
     *
     * @throws IllegalArgumentException if the pattern is null or of length 0
     * @throws IllegalArgumentException if text is null
     * @param pattern the pattern you are searching for in a body of text
     * @param text the body of text where you search for pattern
     * @return list containing the starting index for each match found
     */
    public static List<Integer> kmp(CharSequence pattern, CharSequence text) {
        if (pattern == null || pattern.length() == 0) {
            throw new IllegalArgumentException(
                    "Cannot search for null pattern!");
        }
        if (text == null) {
            throw new IllegalArgumentException("Cannot search in null text!");
        }
        if (pattern.length() > text.length()) {
            return new LinkedList<Integer>();
        }
        int[] table = buildFailureTable(pattern);
        List<Integer> matches = new LinkedList<Integer>();
        int i = 0;
        int j = 0;
        while (i <= text.length() - pattern.length()) {
            while (j < pattern.length()
                    && text.charAt(i + j) == pattern.charAt(j)) {
                j++;
            }
            if (j == 0) {
                i++;
            } else {
                if (j == pattern.length()) {
                    matches.add(i);
                }
                int next = table[j - 1];
                i = i + j - next;
                j = next;
            }
        }
        return matches;
    }

    /**
     * Builds failure table that will be used to run the Knuth-Morris-Pratt
     * (KMP) algorithm.
     *
     * The table built should be the length of the input text.
     *
     * Note that a given index i will be the largest prefix of the pattern
     * indices [0..i] that is also a suffix of the pattern indices [1..i].
     * This means that index 0 of the returned table will always be equal to 0
     *
     * Ex. ababac
     *
     * table[0] = 0
     * table[1] = 0
     * table[2] = 1
     * table[3] = 2
     * table[4] = 3
     * table[5] = 0
     *
     * If the pattern is empty, return an empty array.
     *
     * @throws IllegalArgumentException if the pattern is null
     * @param pattern a {@code CharSequence} you're building a failure table for
     * @return integer array holding your failure table
     */
    public static int[] buildFailureTable(CharSequence pattern) {
        if (pattern == null) {
            throw new IllegalArgumentException(
                    "Cannot build failure table for null pattern!");
        }
        int[] table = new int[pattern.length()];
        if (pattern.length() == 0) {
            return table;
        }
        int i = 0;
        int j = 1;
        table[0] = 0;
        char itar = pattern.charAt(i);
        char jtar = 0;
        if (j < pattern.length()) {
            jtar = pattern.charAt(j);
        }
        while (j < pattern.length()) {
            if (i == j || itar == jtar) {
                table[j++] = ++i;
                if (j < pattern.length()) {
                    if (i == j - 1) {
                        itar = jtar;
                    } else {
                        itar = pattern.charAt(i);
                    }
                    jtar = pattern.charAt(j);
                }
            } else {
                if (i == 0) {
                    table[j++] = 0;
                    if (j < pattern.length()) {
                        jtar = pattern.charAt(j);
                    }
                } else {
                    i = table[i - 1];
                    itar = pattern.charAt(i);
                }
            }
        }
        return table;
    }

    /**
     * Boyer Moore algorithm that relies on last occurrence table. Works better
     * with large alphabets.
     *
     * Make sure to implement the last occurrence table before implementing this
     * method.
     *
     * @throws IllegalArgumentException if the pattern is null or of length 0
     * @throws IllegalArgumentException if text is null
     * @param pattern the pattern you are searching for in a body of text
     * @param text the body of text where you search for the pattern
     * @return list containing the starting index for each match found
     */
    public static List<Integer> boyerMoore(CharSequence pattern,
            CharSequence text) {
        if (pattern == null || pattern.length() == 0) {
            throw new IllegalArgumentException(
                    "Cannot search for null pattern!");
        }
        if (text == null) {
            throw new IllegalArgumentException("Cannot search in null text!");
        }
        List<Integer> matches = new LinkedList<Integer>();
        if (pattern.length() > text.length()) {
            return matches;
        }
        Map<Character, Integer> lastTable = buildLastTable(pattern);
        int i = 0;
        int shift = 0;
        int j = 0;
        char curr;
        while (i <= text.length() - pattern.length()) {
            j = pattern.length() - 1;
            curr = text.charAt(i + j);
            while (j >= 0 && curr == pattern.charAt(j)) {
                j -= 1;
                if (j >= 0) {
                    curr = text.charAt(i + j);
                }
            }
            if (j == -1) {
                matches.add(i);
                i++;
            } else {
                if (lastTable.containsKey(curr)) {
                    shift = lastTable.get(curr);
                } else {
                    shift = -1;
                }
                if (shift < j) {
                    i = i + (j - shift);
                } else {
                    i++;
                }
            }
        }
        return matches;
    }

    /**
     * Builds last occurrence table that will be used to run the Boyer Moore
     * algorithm.
     *
     * Note that each char x will have an entry at table.get(x).
     * Each entry should be the last index of x where x is a particular
     * character in your pattern.
     * If x is not in the pattern, then the table will not contain the key x,
     * and you will have to check for that in your Boyer Moore implementation.
     *
     * Ex. octocat
     *
     * table.get(o) = 3
     * table.get(c) = 4
     * table.get(t) = 6
     * table.get(a) = 5
     * table.get(everything else) = null, which you will interpret in
     * Boyer-Moore as -1
     *
     * If the pattern is empty, return an empty map.
     *
     * @throws IllegalArgumentException if the pattern is null
     * @param pattern a {@code CharSequence} you are building last table for
     * @return a Map with keys of all of the characters in the pattern mapping
     *         to their last occurrence in the pattern
     */
    public static Map<Character, Integer> buildLastTable(CharSequence pattern) {
        if (pattern == null) {
            throw new IllegalArgumentException("Pattern is null!");
        }
        Map<Character, Integer> table = new HashMap<Character, Integer>();
        int t = 0;
        for (int i = pattern.length() - 1; i >= 0; i--) {
            char x = pattern.charAt(i);
            t++;
            if (!table.containsKey(x)) {
                table.put(x, i);
            }
        }
        return table;
    }

    /**
     * Prime base used for Rabin-Karp hashing.
     * DO NOT EDIT!
     */
    private static final int BASE = 599;

    /**
     * Runs Rabin-Karp algorithm. Generate the pattern hash, and compare it with
     * the hash from a substring of text that's the same length as the pattern.
     * If the two hashes match, compare their individual characters, else update
     * the text hash and continue.
     *
     * @throws IllegalArgumentException if the pattern is null or of length 0
     * @throws IllegalArgumentException if text is null
     * @param pattern a string you're searching for in a body of text
     * @param text the body of text where you search for pattern
     * @return list containing the starting index for each match found
     */
    public static List<Integer> rabinKarp(CharSequence pattern,
            CharSequence text) {
        if (pattern == null || pattern.length() == 0) {
            throw new IllegalArgumentException(
                    "Cannot search for null pattern!");
        }
        if (text == null) {
            throw new IllegalArgumentException("Cannot search in null text!");
        }
        List<Integer> matches = new LinkedList<Integer>();
        if (pattern.length() > text.length()) {
            return matches;
        }
        int patternHash = generateHash(pattern, pattern.length());
        int textHash = generateHash(text, pattern.length());
        int i = 0;
        int j = 0;
        while (i <= text.length() - pattern.length()) {
            if (i > 0) {
                textHash = updateHash(textHash, pattern.length(),
                        text.charAt(i - 1),
                        text.charAt(i + pattern.length() - 1));
            }
            if (patternHash == textHash) {
                j = 0;
                while (j < pattern.length()
                        && text.charAt(i + j) == pattern.charAt(j)) {
                    j++;
                }
                if (j == pattern.length()) {
                    matches.add(i);
                }
            }
            i++;
        }
        return matches;
    }

    /**
     * Hash function used for Rabin-Karp. The formula for hashing a string is:
     *
     * sum of: c * BASE ^ (pattern.length - 1 - i), where c is the integer
     * value of the current character, and i is the index of the character
     *
     * For example: Hashing "bunn" as a substring of "bunny" with base 599 hash
     * = b * 599 ^ 3 + u * 599 ^ 2 + n * 599 ^ 1 + n * 599 ^ 0 = 98 * 599 ^ 3 +
     * 117 * 599 ^ 2 + 110 * 599 ^ 1 + 110 * 599 ^ 0 = 21104382019
     *
     * However, note that that will roll over to -370454461, because the largest
     * number that can be represented by an int is 2147483647.
     *
     * Do NOT use {@code Math.pow()} in this method.
     *
     * @throws IllegalArgumentException if current is null
     * @throws IllegalArgumentException if length is negative, 0, or greater
     *     than the length of current
     * @param current substring you are generating hash function for
     * @param length the length of the string you want to generate the hash for,
     * starting from index 0. For example, if length is 4 but current's length
     * is 6, then you include indices 0-3 in your hash (and pretend the actual
     * length is 4)
     * @return hash of the substring
     */
    public static int generateHash(CharSequence current, int length) {
        if (current == null) {
            throw new IllegalArgumentException(
                    "Cannot generate hash of null string!");
        }
        if (length < 0) {
            throw new IllegalArgumentException("Desired Length is negative!");
        }
        if (length == 0) {
            throw new IllegalArgumentException("Desired Length is zero!");
        }
        if (length > current.length()) {
            throw new IllegalArgumentException(
                    "Desired Length greater than the string's length!");
        }
        int sum = 0;
        for (int i = 0; i < length; i++) {
            sum += current.charAt(i) * pow(BASE, length - i - 1);
        }
        return sum;
    }

    /**
     * Updates a hash in constant time to avoid constantly recalculating
     * entire hash. To update the hash:
     *
     *  remove the oldChar times BASE raised to the length - 1, multiply by
     *  BASE, and add the newChar.
     *
     * For example: Shifting from "bunn" to "unny" in "bunny" with base 599
     * hash("unny") = (hash("bunn") - b * 599 ^ 3) * 599 + y * 599 ^ 0 =
     * (-370454461 - 98 * 599 ^ 3) * 599 + 121 * 599 ^ 0 = -12838241666916
     *
     * However, the number will roll over to 1904016410.
     *
     * The computation of BASE raised to length - 1 may require O(log n) time,
     * but the method should otherwise run in O(1).
     *
     * Do NOT use {@code Math.pow()} in this method. We have provided a pow()
     * method for you to use.
     *
     * @throws IllegalArgumentException if length is negative or 0
     * @param oldHash hash generated by generateHash
     * @param length length of pattern/substring of text
     * @param oldChar character we want to remove from hashed substring
     * @param newChar character we want to add to hashed substring
     * @return updated hash of this substring
     */
    public static int updateHash(int oldHash, int length, char oldChar,
            char newChar) {
        return BASE * (oldHash - (oldChar * pow(BASE, length - 1))) + newChar;
    }

    /**
     * Calculate the result of a number raised to a power.
     *
     * DO NOT MODIFY THIS METHOD.
     *
     * @throws IllegalArgumentException if both {@code base} and {@code exp} are
     * 0
     * @throws IllegalArgumentException if {@code exp} is negative
     * @param base base of the number
     * @param exp power to raise the base to. Must be 0 or greater.
     * @return result of the base raised to that power
     */
    private static int pow(int base, int exp) {
        if (exp < 0) {
            throw new IllegalArgumentException("Exponent cannot be negative.");
        } else if (base == 0 && exp == 0) {
            throw new IllegalArgumentException(
                    "Both base and exponent cannot be 0.");
        } else if (exp == 0) {
            return 1;
        } else if (exp == 1) {
            return base;
        }
        int halfPow = pow(base, exp / 2);
        if (exp % 2 == 0) {
            return halfPow * halfPow;
        } else {
            return halfPow * pow(base, (exp / 2) + 1);
        }
    }
}
