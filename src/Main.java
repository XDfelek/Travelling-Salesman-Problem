import java.io.PrintWriter;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Instant startTimer = Instant.now();
        long start = System.currentTimeMillis();

        System.out.println("Podaj nazwę pliku do odczytania ze ścieżki .../location_data_files/: ");
        //Scanner dodać tutaj w razie potrzeby
        Scanner scan = new Scanner(System.in);
        //String nazwaPliku = scan.nextLine();
        //String nazwaPliku = "bier127.tsp";
        String nazwaPliku = "pr144.tsp";
        System.out.println("Podaj czas działania programu (w sekundach): ");

        //int programRuntimeInSeconds = scan.nextInt();
        int programRuntimeInSeconds = 30; //czas dzialania programu w sekundach
        System.out.println("Program będzie się wykonywał w przybliżonym czasie "+ programRuntimeInSeconds + " sekund.");

        TravelPath travelPath = new TravelPath(nazwaPliku);
        NodeListAndTotalDistance best = new NodeListAndTotalDistance();
        travelPath.setPopulationAmount(2000);
        travelPath.setCrossingAmount(500);
        travelPath.setMutationAmount(100);
        travelPath.setGenerationAmount(10000);



        long end = start + programRuntimeInSeconds * 1000;
        travelPath.breed();
        best = travelPath.generationOfPaths.getFirst();

        while (System.currentTimeMillis() < end) {
            travelPath.breed();
            if (travelPath.generationOfPaths.getFirst().getTotalDistance() < best.getTotalDistance()) {
                best = travelPath.generationOfPaths.getFirst();
            }
            travelPath.generationOfPaths.clear();
        }

        String strNew = best.toString().replace("[", "");
        String strNewNew = strNew.replace("]", "");
        System.out.println(strNewNew);
        try (PrintWriter out = new PrintWriter("best.txt")) {
            out.println(strNewNew);
            out.close();
        } catch (Exception e){
            e.printStackTrace();
        }

        Instant endTimer = Instant.now();
        long milli = ChronoUnit.MILLIS.between(startTimer, endTimer);
        System.out.format(Locale.ENGLISH, "elapsed time: %.3f s%n", milli / 1000.0);

    }
}
