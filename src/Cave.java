import java.io.*;
import java.util.*;
import java.util.stream.Collectors;


/**
 *
 * Wynikiem miał być pojedyńczy wynik, jednak zwróciłem liste wyników ze względu na wielokrotność wyników o takiej samej wadze
 *
 */
public class Cave {

    public static void main(String[] args) throws IOException {
        Map<Integer, List<List<Integer>>> solutions = new HashMap();

        BufferedReader bufferedReader = new BufferedReader(new FileReader("./resources/CAV.IN"));

        int numberOfVertexes = Integer.parseInt(bufferedReader.readLine().split(" ")[0]);
        int[][] adjacencyMatrix = readInputFile(bufferedReader, numberOfVertexes);

        int[] path = new int[numberOfVertexes];
        path[0] = 0;

        for (int i = 1; i < numberOfVertexes; i++)
            path[i] = -1;

        hamiltonianPath(adjacencyMatrix, solutions, path, 1, 0);
        saveResultsToFile(solutions);
    }

    private static int[][] readInputFile(BufferedReader bufferedReader, int numberOfVertexes) throws IOException {
        int[][] adjacencyMatix = new int[numberOfVertexes][numberOfVertexes];

        for (int i = 0; i < (3 * numberOfVertexes / 2); ++i) {
            String[] edgeData = bufferedReader.readLine().split(" ");

            adjacencyMatix[Integer.parseInt(edgeData[0]) -1][Integer.parseInt(edgeData[1]) -1] = "0".equals(edgeData[2]) ? 1 : 2; // if path is hard then 2 else 1
            adjacencyMatix[Integer.parseInt(edgeData[1]) -1][Integer.parseInt(edgeData[0]) -1] = "0".equals(edgeData[2]) ? 1 : 2; // graph considered bi-directional/ graph considered bi-directional
        }

        return adjacencyMatix;

    }

    private static void hamiltonianPath(int[][] adjacencyMatrix, Map<Integer,List<List<Integer>>> solutions, int[] path, Integer depth, Integer sum) {
        do {
            nextVertex(adjacencyMatrix, path, depth);
            if (path[depth] == -1) {
                return;
            } else if (depth + 1 == path.length) {
                Integer weight = calculateWeight(adjacencyMatrix, path);
                solutions.computeIfAbsent(weight, k -> new ArrayList<>());
                solutions.get(weight).add(Arrays.stream(path).boxed().collect(Collectors.toList()));
            } else {
                hamiltonianPath(adjacencyMatrix, solutions, path, depth + 1, sum);
            }

        } while (true);
    }

    private static void nextVertex(int[][] adjacencyMatrix, int[] path, Integer depth) {
        do {
            path[depth] = (path[depth] + 2 )% (path.length + 1) -1;
            if (path[depth] == -1)
                return ;
            if (adjacencyMatrix[path[depth - 1]][ path[depth]] != 0) {
                for (int i = 0; i < depth; ++i) {
                    if (path[i] == path[depth])
                        break;
                    if (i == depth -1 &&
                            (i < path.length -1 ||
                                    (depth == path.length -1 && adjacencyMatrix[path[path.length -1]][path[0]] != 0))) {

                        return;
                    }
                }
            }
        } while (true);
    }

    private static Integer calculateWeight(int[][] adjacencyMatrix, int [] path) {
        int sum  = 0;
        for (int i = 0; i < path.length -1; ++i) {
         sum += adjacencyMatrix[path[i]][path[i +1]];
        }

        sum += adjacencyMatrix[path[path.length -1]][0];

        return sum;
    }

    private static void saveResultsToFile(Map<Integer, List<List<Integer>>> solutions) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("./resources/CAV.OUT"));

        solutions.forEach((key, value) -> value.forEach(list -> {
            try {
                bufferedWriter.write(String.format("Amount of hard paths : %d path: %s %n", ((key  - value.size() ) / 2) - 1, list.stream()
                        .map(v ->  v + 1)
                        .map(String::valueOf)
                        .collect(Collectors.joining(" "))));

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }));

        bufferedWriter.close();
    }

}

