import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class TravelPath {

    private int populationAmount = 10; // amount of paths for a generation (default amount is 10)
    private int generationAmount; // amount of generations, possibly infinite, or at least as far as the program goes
    private int crossingAmount = 3;
    private int mutationAmount = 1;
    private final List<Integer> coordinatesXlist = new ArrayList<>();
    private final List<Integer> coordinatesYlist = new ArrayList<>();
    private final List<Integer> orderOfNodes = new ArrayList<>();
    public int defaultNodeCount;
    public List<NodeListAndTotalDistance> generationOfPaths = new ArrayList<>();

    public void setPopulationAmount(int populationAmount) {
        this.populationAmount = populationAmount;
    }

    public void setGenerationAmount(int generationAmount) {
        this.generationAmount = generationAmount;
    }

    public void setCrossingAmount(int crossingAmount) {
        this.crossingAmount = crossingAmount;
    }

    public void setMutationAmount(int mutationAmount) {
        this.mutationAmount = mutationAmount;
    }

    public TravelPath(String nazwaPliku) {

        // Get the current working directory
        String currentDir = System.getProperty("user.dir");

        // Build the full file path using the current directory
        String fileName = currentDir + "/location_data_files/" + nazwaPliku;

        try (BufferedReader inputFile = new BufferedReader(new FileReader(fileName))) {
            // Check if the file is open (replaced null check with try-with-resources)
            String line;
            boolean inNodeCoordSection = false;

            // Count the number of nodes in the file
            while ((line = inputFile.readLine()) != null) {
                if (line.contains("DIMENSION")) {
                    String[] parts = line.split(":");
                    defaultNodeCount = Integer.parseInt(parts[1].trim());
                } else if (line.contains("NODE_COORD_SECTION")) {
                    inNodeCoordSection = true;
                    break;
                }
            }

            if (!inNodeCoordSection) {
                System.err.println("NODE_COORD_SECTION not found in the file.");
                System.exit(1);
            }

            // Read the node coordinates section
            for (int i = 0; i < defaultNodeCount; ++i) {
                line = inputFile.readLine();

                // Check if the line is not empty
                if (line != null && !line.trim().isEmpty()) {
                    // Using regex "\\s+" as a separator for split
                    String[] coordinates = line.trim().split("\\s+");

                    if (coordinates.length >= 3) {
                        int first = Integer.parseInt(coordinates[0]) - 1; // minus jeden dla łatwiejszego działania programu TODO pod koniec dodać jeden to wyniku końcowego
                        int second = Integer.parseInt(coordinates[1]);
                        int third = Integer.parseInt(coordinates[2]);

                        // Add the second and third numbers to ArrayLists
                        orderOfNodes.add(first);
                        coordinatesXlist.add(second);
                        coordinatesYlist.add(third);
                    } else {
                        System.err.println("Invalid number of coordinates in line: " + line);
                        // Handle the error or continue as appropriate
                    }
                }
            }

            // Now you can use the coordinatesXlist and coordinatesYlist ArrayLists as needed.

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    int calculateDistance(int x1, int x2, int y1, int y2) {
        int x1y1 = x1 - y1;
        int x2y2 = x2 - y2;
        return (Math.abs(x1y1) + Math.abs(x2y2)); // odl(x,y) = x-y = |x1-y1|+|x2-y2| : Norma Miejska
    }

    int calculateDistance(int nodeNo1, int nodeNo2) {
        return calculateDistance(coordinatesXlist.get(nodeNo1), coordinatesYlist.get(nodeNo1), coordinatesXlist.get(nodeNo2), coordinatesYlist.get(nodeNo2));
    }

    void crossInGeneration() {
        Random rand = new Random();
        for (int i = 0; i < crossingAmount; i++) {
            int list1ToCross = rand.nextInt(generationOfPaths.size());
            int list2ToCross = rand.nextInt(generationOfPaths.size());
            int half = generationOfPaths.get(list1ToCross).getNodeList().size() / 2;

            // Pobierz pierwszą połowę elementów z listy1
            List<Integer> firstHalf = generationOfPaths.get(list1ToCross).getNodeList().subList(0, half);

            // Usuń z listy2 elementy, które już są w pierwszej połowie
            Set<Integer> setFirstHalf = new HashSet<>(firstHalf);
            List<Integer> secondList = new ArrayList<>(generationOfPaths.get(list2ToCross).getNodeList());
            secondList.removeAll(setFirstHalf);

            // Złącz listy
            List<Integer> crossedList = new ArrayList<>(firstHalf);
            crossedList.addAll(secondList);

            // DOdaj listę z odległością do generacji
            NodeListAndTotalDistance n = new NodeListAndTotalDistance();
            n.setNodeList(new ArrayList<>(crossedList));
            int tempTotalDistance = 0;
            for (int j = 0; j < n.getNodeList().size(); j++) {
                if (j == (crossedList.size() - 1)) {
                    tempTotalDistance += calculateDistance(crossedList.getLast(), crossedList.getFirst());
                } else {
                    tempTotalDistance += calculateDistance(crossedList.get(j), crossedList.get(j + 1));
                }
            }
            n.setTotalDistance(tempTotalDistance);
            generationOfPaths.add(n);
        }
    }

    void mutateInGeneration() {
        Random rand = new Random();
        for (int i = 0; i < mutationAmount; i++) {
            int listToMutate = rand.nextInt(generationOfPaths.size());
            Collections.swap(generationOfPaths.get(listToMutate).getNodeList(), rand.nextInt(generationOfPaths.get(listToMutate).getNodeList().size()), rand.nextInt(generationOfPaths.get(listToMutate).getNodeList().size()));
        }
    }

    void breed() {
        for (int i = 0; i < populationAmount; i++) {
            List<Integer> tempShuffledList = new ArrayList<>(orderOfNodes);
            Collections.shuffle(tempShuffledList);
            NodeListAndTotalDistance n = new NodeListAndTotalDistance();
            n.setNodeList(new ArrayList<>(tempShuffledList));
            int tempTotalDistance = 0;
            for (int j = 0; j < n.getNodeList().size(); j++) {
                if (j == (tempShuffledList.size() - 1)) {
                    tempTotalDistance += calculateDistance(tempShuffledList.getLast(), tempShuffledList.getFirst());
                } else {
                    tempTotalDistance += calculateDistance(tempShuffledList.get(j), tempShuffledList.get(j + 1));
                }
            }
            n.setTotalDistance(tempTotalDistance);
            generationOfPaths.add(n);
        }
        for (int i = 0; i < generationAmount; i++) {
            this.crossInGeneration();
            this.mutateInGeneration();
            this.sortCurrentGeneration();
            this.cutCurrentGeneration();
        }

    }


    void sortCurrentGeneration() {
        Collections.sort(generationOfPaths, Comparator.comparingInt(NodeListAndTotalDistance::getTotalDistance));
    }

    void cutCurrentGeneration() {
        generationOfPaths.subList(populationAmount, generationOfPaths.size()).clear();
    }

    void printCurrentGeneration() {
        for (int i = 0; i < 10; i++) {
            System.out.println("Lista NodeAndTotalDistanceToNext nr. " + (i + 1));
            for (int j = 0; j < generationOfPaths.get(i).getNodeList().size(); j++) {
                System.out.print(generationOfPaths.get(i).getNodeList().get(j) + ",  ");
            }
            System.out.println();
            System.out.println("ilość node'ów w tym NodeAndTotalDistanceToNext: " + generationOfPaths.get(i).getNodeList().size());
            System.out.println("totalny dystans przebyty: " + generationOfPaths.get(i).getTotalDistance());
        }
    }
}
