package com.cis084javaprogramming;

import java.io.*;
import java.util.*;
import javax.sound.sampled.*;
import java.lang.Math;
import org.json.simple.*;
import org.json.simple.parser.*;

// declares a class for the app
public class App {

    // private variables for the app
    private static Clip audioClip;

    // private static String basePath =
    // "C:/Users/gsolomon/Documents/GitHub/class-java/functions-and-procedures-lab1/demo/src/main/java/com/example/";

    private static String basePath = "C:\\Users\\Brendan\\Documents\\GitHub\\Java-Lab-Week-9-V2\\labweek9\\src\\main\\java\\com\\cis084javaprogramming";

    // "main" makes this class a java app that can be executed
    public static void main(final String[] args) {
        // test reading audio library from json file
        JSONArray library = readAudioLibrary();

        // create a scanner for user input
        Scanner input = new Scanner(System.in);

        String userInput = "";
        while (!userInput.equals("q")) {
            menu(library);

            // get input
            userInput = input.nextLine();

            // accept upper or lower case commands
            userInput = userInput.toLowerCase();

            // do something
            handleMenu(userInput, library, input);
        }

        // close the scanner
        input.close();
    }

    // print the menu
    public static void menu(JSONArray library) {
        System.out.println("---- SpotifyLikeApp ----");

        for (Integer i = 0; i < library.size(); i++) {
            JSONObject obj = (JSONObject) library.get(i);
            String name = (String) obj.get("name");
            System.out.printf("[%d] %s\n", i + 1, name);
        }

        System.out.println("[P]ause / Resume");
        System.out.println("[R]ewind");
        System.out.println("[Q]uit");

        System.out.println("");
        System.out.print("Enter q to Quit:");
    }

    /*
     * handles the user input for the app
     */
    public static void handleMenu(String userInput, JSONArray library, Scanner input) {
        // check if the input is a number, if not if it is q, then quit
        try {
            Integer number = Integer.parseInt(userInput);

            // subtract 1 from user input to match the indices
            number -= 1;

            // if number is 1-5 play the song
            if (number < library.size()) {
                play(library, number);
            }

            // if letter input, pursue according option or detect invalid input
        } catch (Exception e) {
            if (userInput.equals("q")) {
                System.out.println("Thank you for using the app.");
            } else if (userInput.equals("p")) {
                if (audioClip.isRunning()) {
                    System.out.println("The song is now paused.");
                } else {
                    System.out.println("The song is now resuming.");
                }
                pauseOrResume();
            } else if (userInput.equals("r")) {
                System.out.println("How far would you like to rewind (seconds)?");
                long userRewindTime = input.nextLong();
                rewind(userRewindTime);
            } else {
                System.out.printf("Error: %s is not a command\n", userInput);
            }
        }
    }

    // rewinds an audio file
    public static void rewind(Long userRewindTime) {
        // stops audio file and retrieves the microsecond position
        audioClip.stop();
        // calculates the rewinded position in the audio file
        long time = audioClip.getMicrosecondPosition() - (userRewindTime * (long) Math.pow(10, 6));
        // sets the position of the adjusted microsecond value and resumes playing from
        // adjusted position
        audioClip.setMicrosecondPosition(time);
        audioClip.start();
    }

    // pauses or resumes an audio file
    public static void pauseOrResume() {
        // checks if audio file is running
        if (audioClip.isRunning()) {
            // stops the audio file
            audioClip.stop();
        } else {
            // resumes audio file from the stopped position
            audioClip.start();
        }
    }

    // plays an audio file
    public static void play(JSONArray library, Integer songIndex) {
        // get the filePath and open the audio file
        JSONObject obj = (JSONObject) library.get(songIndex);
        final String fileName = (String) obj.get("fileName");
        final String filePath = basePath + "/wav/" + fileName;
        final File file = new File(filePath);

        // stop the current song from playing, before playing the next one
        if (audioClip != null) {
            audioClip.close();
        }

        try {
            // create clip
            audioClip = AudioSystem.getClip();

            // get input stream
            final AudioInputStream in = AudioSystem.getAudioInputStream(file);

            audioClip.open(in);
            audioClip.setMicrosecondPosition(0);
            audioClip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //
    // Func: readJSONFile
    // Desc: Reads a json file storing an array and returns an object
    // that can be iterated over
    //
    public static JSONArray readJSONArrayFile(String fileName) {
        // JSON parser object to parse read file
        JSONParser jsonParser = new JSONParser();

        JSONArray dataArray = null;

        try (FileReader reader = new FileReader(fileName)) {
            // Read JSON file
            Object obj = jsonParser.parse(reader);

            dataArray = (JSONArray) obj;
            // System.out.println(dataArray);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dataArray;
    }

    // read the audio library of music
    public static JSONArray readAudioLibrary() {
        final String jsonFileName = "audio-library.json";
        final String filePath = basePath + "/" + jsonFileName;

        JSONArray jsonData = readJSONArrayFile(filePath);

        // loop over list
        String name, artist, fileName;
        JSONObject obj;

        System.out.println("Reading the file " + filePath);

        for (Integer i = 0; i < jsonData.size(); i++) {
            // parse the object and pull out the name and birthday
            obj = (JSONObject) jsonData.get(i);
            name = (String) obj.get("name");
            artist = (String) obj.get("artist");
            fileName = (String) obj.get("filePath");

            System.out.println("\tname = " + name);
            System.out.println("\tartist = " + artist);
            System.out.println("\tfilePath = " + fileName);
        }

        return jsonData;
    }
}
