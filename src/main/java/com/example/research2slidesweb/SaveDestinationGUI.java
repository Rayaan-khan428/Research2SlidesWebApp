package com.example.research2slidesweb;

public class SaveDestinationGUI {
	private static String directory = null;

    public static void setDestination(String directoryline) {
        directory = directoryline;
        System.out.println(directory);
    }

    public static String getDestination() {
        return directory;
    }
}
