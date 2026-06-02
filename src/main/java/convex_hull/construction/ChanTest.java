package convex_hull.construction;

import java.io.*;
import java.util.*;

public class ChanTest {

    public static void main(String[] args) {
        // Размеры от 100 до 10000 с шагом 100
        List<Integer> sizes = new ArrayList<>();
        for (int size = 100; size <= 10001; size += 100) {
            sizes.add(size);
        }

        System.out.println("Запуск алгоритма Чана");
        System.out.println("===========================================================");
        System.out.printf("%-10s %-15s %-15s %-15s%n",
                "Размер", "Время (мс)", "Итерации", "Размер оболочки");
        System.out.println("===========================================================");

        for (int n : sizes) {
            try {
                // Читаем данные из файла, созданного FileGenerator
                String filename = "src/main/java/convex_hull/construction/data/data" + (n / 100);
                List<Point> points = readPointsFromFile(filename);

                // Запуск алгоритма
                ChanAlgorithm chan = new ChanAlgorithm();

                chan.startTiming();
                List<Point> hull = chan.findConvexHull(points);
                double timeMs = chan.stopTiming();
                long iterations = chan.getIterationCount();

                System.out.printf("%-10d %-15.3f %-15d %-15d%n",
                        n, timeMs, iterations, hull.size());

            } catch (Exception e) {
                System.out.println("Ошибка для n = " + n);
                e.printStackTrace();
            }
        }
    }

    private static List<Point> readPointsFromFile(String filename) throws IOException {
        List<Point> points = new ArrayList<>();
        Scanner scanner = new Scanner(new File(filename));
        while (scanner.hasNext()) {
            float x = scanner.nextFloat();
            float y = scanner.nextFloat();
            points.add(new Point(x, y));
        }
        scanner.close();
        return points;
    }
}