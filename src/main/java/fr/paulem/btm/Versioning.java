package fr.paulem.btm;

import org.bukkit.Bukkit;

public class Versioning {
    public static boolean isPost17() {
        return isPost(17);
    }

    public static boolean isPost9() {
        return isPost(9);
    }

    private static boolean isPost(int v) {
        String version = Bukkit.getVersion();
        String[] mcParts = version.substring(version.indexOf("MC: ") + 4, version.length() - 1).split("\\.");
        return Integer.parseInt(mcParts[1]) > v || (Integer.parseInt(mcParts[1]) == v && Integer.parseInt(mcParts[2]) >= 1);
    }
}
