package SearchForLight;
import swiftbot.Button;
import swiftbot.ImageSize;
import swiftbot.SwiftBotAPI;
import swiftbot.Underlight;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Random;
import java.time.LocalDateTime;
import java.util.Scanner;
import java.io.FileWriter;

public class SearchForLight {
    private boolean running = true;
    private SwiftBotAPI swiftBot;
    private BufferedImage swiftCam;
    private boolean begin = false;
    private boolean wheelAssist = false;

    private int cycleNumber = 1;
    private long instanceStartTime = 0;
    private String instanceStartTimeStr = "";
    private int objectsEncountered = 0;

    private int lIntensity = -1;
    private int cIntensity = -1;
    private int rIntensity = -1;
    private int baseIntensity = -1;
    private final int thresholdConstant = 15;

    private int direction = -1;
    private final int LEFT = 0;
    private final int CENTRE = 1;
    private final int RIGHT = 2;
    private Random rand;

    private final float wheelMultiplier = 1/(50f/72f);

    //logging variables
    private int logFirstIntensity = -1;
    private int logMaxIntensity = -1;
    private int logLightDetectionCount = 0;
    private int logTotalDistance = 0;
    
    public void init() throws Exception {
        swiftBot = SwiftBotAPI.INSTANCE;
        swiftBot.enableButton(Button.A, () -> {
            begin = true;
        });
        swiftBot.enableButton(Button.B, () -> {
            wheelAssist = true;
        });
        swiftBot.enableButton(Button.X, () -> {
            emergencyExit();
        });
        rand = new Random();
        instanceStartTime = System.currentTimeMillis();
        instanceStartTimeStr = LocalDateTime.now().toString();
        new File("/data/home/pi/" + instanceStartTimeStr).mkdirs();

        while (!begin){
            Thread.sleep(50);
        }
        eventLogger("Begin event stream:");
        System.out.println("Initialising search for light!");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        beginSearch();

    }

    private void emergencyExit(){
        swiftBot.disableAllButtons();
        swiftBot.disableUnderlights();
        swiftBot.stopMove();
        System.exit(0);
    }

    private void beginSearch() throws Exception {
        while (running){
            takeImage();
            imageProcess();
            chooseDirection();
            moveTowardsLight();
            checkObjectDistance();
            terminationHandler();

            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            cycleNumber++;
        }


    }

    private void takeImage() throws Exception {
        swiftCam = swiftBot.takeGrayscaleStill(ImageSize.SQUARE_1080x1080);
        ImageIO.write(swiftCam, "jpg", new File("/data/home/pi/" + instanceStartTimeStr + "/currentImage" + cycleNumber + ".jpg"));
    }

    private void writeImage() throws Exception {
        swiftCam = swiftBot.takeStill(ImageSize.SQUARE_1080x1080);
        ImageIO.write(swiftCam, "jpg", new File("/data/home/pi/"+ instanceStartTimeStr +"/objectNo" + objectsEncountered + ".jpg"));

    }

    private void imageProcess() {

        int[][] pixelArray = acquireBrightnessArray();
        splitImageIntensities(pixelArray);
        baseIntensity = averageIntensity(pixelArray);
        if (cycleNumber == 1){
            logFirstIntensity = baseIntensity;
        }
        System.out.println("Intensity significance threshold: " + (baseIntensity + thresholdConstant));
        System.out.println("Light intensity breakdown:");
        System.out.println("Left: " + lIntensity);
        System.out.println("Centre: " + cIntensity);
        System.out.println("Right: " + rIntensity);

        if (logMaxIntensity < lIntensity){
            logMaxIntensity = lIntensity;
        }
        else if  (logMaxIntensity < cIntensity){
            logMaxIntensity = cIntensity;
        }
        else if  (logMaxIntensity < rIntensity){
            logMaxIntensity = rIntensity;
        }
    }

    private int[][] acquireBrightnessArray() {
        // gets the brightness values for each pixel and stores into an array
        int[][] pixelArray = new int[1080][1080];
        for (int i = 0; i < 1080; i++) {
            for (int j = 0; j < 1080; j++) {
                int pixel = swiftCam.getRGB(i,j);
                pixelArray[i][j] = (pixel) & 0xFF;
            }
        }
        return pixelArray;
    }

    private void splitImageIntensities(int[][] pixelArray) {
        //splits the image into 3 vertical sections
        int[][] columnLeft = new int[360][1080];
        for (int i = 0; i < 360; i++) {
            for (int j = 0; j < 1080; j++) {
                columnLeft[i][j] = pixelArray[i][j];
            }
        }
        int[][] columnCentre = new int[360][1080];
        for (int i = 360; i < 720; i++) {
            for (int j = 0; j < 1080; j++) {
                columnCentre[i-360][j] = pixelArray[i][j];
            }
        }
        int[][] columnRight = new int[360][1080];
        for (int i = 720; i < 1080; i++) {
            for (int j = 0; j < 1080; j++) {
                columnRight[i-720][j] = pixelArray[i][j];
            }
        }
        lIntensity = averageIntensity(columnLeft);
        cIntensity = averageIntensity(columnCentre);
        rIntensity = averageIntensity(columnRight);
    }

    private int averageIntensity(int[][] inputArray){
        int average = 0;
        for (int i = 0; i < inputArray.length; i++) {
            for (int j = 0; j < inputArray[i].length; j++) {
                average += inputArray[i][j];
            }
        }
        average /= (inputArray.length*inputArray[0].length);
        return average;
    }

    private void chooseDirection() throws IOException {
        //directions: l = 0, c = 1, r = 2
        if (cIntensity > lIntensity) {
            if  (rIntensity > cIntensity && (baseIntensity + thresholdConstant) <= rIntensity) {
                direction = RIGHT;
                logLightDetectionCount++;
                eventLogger("Light detection! Intensity: " + rIntensity);
            }
            else if (cIntensity > rIntensity && (baseIntensity + thresholdConstant) <= cIntensity) {
                direction = CENTRE;
                logLightDetectionCount++;
                eventLogger("Light detection! Intensity: " + cIntensity);
            }
            else if (cIntensity >= baseIntensity + thresholdConstant){
                direction = rand.nextInt(1,3);
                logLightDetectionCount++;
                eventLogger("Light detection! Intensity: " + cIntensity);
            }
            else {
                System.out.println("No significant light source detected, wandering...");
                direction = rand.nextInt(0,3);
            }
        }
        else if (lIntensity > cIntensity) {
            if  (rIntensity > lIntensity && (baseIntensity + thresholdConstant) <= rIntensity) {
                direction = RIGHT;
                logLightDetectionCount++;
                eventLogger("Light detection! Intensity: " + rIntensity);
            }
            else if (lIntensity > rIntensity && (baseIntensity + thresholdConstant) <= lIntensity) {
                direction = LEFT;
                logLightDetectionCount++;
                eventLogger("Light detection! Intensity: " + lIntensity);
            }
            else if (lIntensity >= (baseIntensity + thresholdConstant)){
                int tempInt = rand.nextInt(0,2);
                if (tempInt == 1){
                    direction = RIGHT;
                    logLightDetectionCount++;
                    eventLogger("Light detection! Intensity: " + rIntensity);
                }
                else {
                    direction = LEFT;
                    logLightDetectionCount++;
                    eventLogger("Light detection! Intensity: " + lIntensity);
                }

            }
            else{
                System.out.println("No significant light source detected, wandering...");
                direction = rand.nextInt(0,3);
            }
        }
        else if (lIntensity == cIntensity) {
            if  (rIntensity > lIntensity && (baseIntensity + thresholdConstant) <= rIntensity) {
                direction = RIGHT;
                logLightDetectionCount++;
                eventLogger("Light detection! Intensity: " + rIntensity);
            }
            else if (lIntensity >= (baseIntensity + thresholdConstant)){
                direction = rand.nextInt(0,2);
                logLightDetectionCount++;
                eventLogger("Light detection! Intensity: " + lIntensity);
            }
            else {
                System.out.println("No significant light source detected, wandering...");
                direction = rand.nextInt(0,3);
            }
        }

        if (direction == LEFT) {
            System.out.println("Chosen direction: Left" );
        }
        else if (direction == CENTRE) {
            System.out.println("Chosen direction: Centre" );
        }
        else if (direction == RIGHT) {
            System.out.println("Chosen direction: Right" );
        }

    }

    private void moveTowardsLight() throws InterruptedException, IOException {
        swiftBot.setUnderlight(Underlight.FRONT_LEFT, new int[]{0, 255, 0});
        swiftBot.setUnderlight(Underlight.FRONT_RIGHT, new int[]{0, 255, 0});
        switch (direction) {
            case LEFT:
                swiftBot.setUnderlight(Underlight.FRONT_RIGHT, new int[]{0, 0, 0});
                turnLeft();
                Thread.sleep(100);
                swiftBot.setUnderlight(Underlight.FRONT_RIGHT, new int[]{0, 255, 0});
                moveForward();
                Thread.sleep(1000);
                break;
            case CENTRE:
                moveForward();
                Thread.sleep(1000);
                break;
            case RIGHT:
                swiftBot.setUnderlight(Underlight.FRONT_LEFT, new int[]{0, 0, 0});
                turnRight();
                Thread.sleep(100);
                swiftBot.setUnderlight(Underlight.FRONT_LEFT, new int[]{0, 255, 0});
                moveForward();
                Thread.sleep(1000);
                break;
            default:
                System.out.println("Invalid direction");
                break;
        }
        swiftBot.setUnderlight(Underlight.FRONT_LEFT, new int[]{0, 0, 0});
        swiftBot.setUnderlight(Underlight.FRONT_RIGHT, new int[]{0, 0, 0});

    }

    private void moveForward() throws IOException {
        //equivalent to 16cm movement
        if (wheelAssist){
            swiftBot.move(50, (int) (50*wheelMultiplier), 1000);
        }
        else{swiftBot.move(50, 50, 1000);

        }
        eventLogger("Moved 16cm forward");
        logTotalDistance+=16;

    }

    private void turnLeft() throws IOException {
        if (wheelAssist){
            swiftBot.move(-50, (int) (50*wheelMultiplier), 500);
        }
        else{
            swiftBot.move(-50, 50, 300);
        }
        eventLogger("Turned left 30 degrees");
    }

    private void turnRight() throws IOException {
        if (wheelAssist){
            swiftBot.move(50, (int) (-50*wheelMultiplier), 500);
        }
        else{
            swiftBot.move(50, -50, 300);
        }
        eventLogger("Turned right 30 degrees");
    }

    private void checkObjectDistance() throws Exception {
        double objectDistance = swiftBot.useUltrasound();
        if (objectDistance < 50) {
            System.out.println("Object in Swiftbot's path, avoiding...");
            System.out.println("Distance to object: " + objectDistance + "cm");
            objectsEncountered++;
            writeImage();

            if (lIntensity > rIntensity) {
                direction = LEFT;
                moveAwayObject();
            }
            else if (lIntensity < rIntensity) {
                direction = RIGHT;
                moveAwayObject();
            }
            else {
                int tempInt = rand.nextInt(0,2);
                if (tempInt == 1){
                    direction = RIGHT;
                }
                else {
                    direction = LEFT;
                }
                moveAwayObject();
            }
        }

    }

    private void moveAwayObject() throws InterruptedException, IOException {
        swiftBot.setUnderlight(Underlight.FRONT_LEFT, new int[]{255, 0, 0});
        swiftBot.setUnderlight(Underlight.FRONT_RIGHT, new int[]{255, 0, 0});
        switch (direction) {
            case LEFT:
                turnLeft();
                Thread.sleep(1000);
                moveForward();
                Thread.sleep(1000);
                break;
            case RIGHT:
                turnRight();
                Thread.sleep(1000);
                moveForward();
                Thread.sleep(1000);
                break;
            default:
                System.out.println("Invalid direction");
                break;
        }
        swiftBot.setUnderlight(Underlight.FRONT_LEFT, new int[]{0, 0, 0});
        swiftBot.setUnderlight(Underlight.FRONT_RIGHT, new int[]{0, 0, 0});

    }

    private void terminationHandler() throws IOException {
        long timeDifference = instanceStartTime + 300000;
        if (objectsEncountered >= 5 && timeDifference > System.currentTimeMillis()) {
            boolean terminate = false;
            Scanner uInput = new Scanner(System.in);
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println("5 objects detected, requesting user input to terminate:");
            System.out.println("Please enter 'TERMINATE' to end search.");
            while (!terminate){
                String prompt = uInput.nextLine();
                if (Objects.equals(prompt, "TERMINATE")){
                    terminate = true;
                }
                else{
                    System.out.println("Invalid input. Please enter 'TERMINATE' (case sensitive).");
                }
            }
            finalEventLogger();
            running = false;
        }
    }

    private void eventLogger(String event) throws IOException {
        FileWriter filewriter = new FileWriter("/data/home/pi/"+ instanceStartTimeStr +"/log.txt", true);
        filewriter.write("\n"+event);
        filewriter.close();
    }
    private void finalEventLogger() throws IOException {
        FileWriter filewriter = new FileWriter("/data/home/pi/"+ instanceStartTimeStr +"/log.txt", true);
        filewriter.write("\n End event stream");
        filewriter.write("\n Starting light intensity: " + logFirstIntensity);
        filewriter.write("\n Brightest light intensity found: " + logMaxIntensity);
        filewriter.write("\n Number of times light found: " +logLightDetectionCount);
        filewriter.write("\n Duration of execution: " + (System.currentTimeMillis() - instanceStartTime) + "ms");
        filewriter.write("\n Total distance travelled: " + logTotalDistance +"cm");
        filewriter.write("\n Number of objects detected: " + objectsEncountered);
        filewriter.write("\n Image and log filepath: /data/home/pi/"+ instanceStartTimeStr);
        filewriter.close();
    }
}


