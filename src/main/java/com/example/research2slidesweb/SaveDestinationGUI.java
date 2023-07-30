package com.example.research2slidesweb;

public class SaveDestinationGUI {
	private static String directory = null;

    public static void setDestination(String directoryline) {
        directory = directoryline;
    }

    public static String getDestination() {
        return directory;
    }
}
