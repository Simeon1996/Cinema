package system.cinema.utils;

import java.util.*;

public class Helper {

    /**
     * It accepts an uppercase strings separated by _
     * and we normalize it to become a human readable string
     *
     * E.g. MALL_OF_SOFIA => Mall Of Sofia
     *
     * @param identity
     *
     * @return String
     */
    public static String normalizeStringSensitivity(String identity, boolean allWordsUppercase)
    {
        String[] elements = identity.split("_");

        StringJoiner sj = new StringJoiner(" ");

        for (String name : elements) {
            sj.add(name.substring(0, 1) + name.substring(1).toLowerCase());
        }

        return allWordsUppercase ? sj.toString().toUpperCase() : sj.toString();
    }
}
