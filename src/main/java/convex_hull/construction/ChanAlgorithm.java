package convex_hull.construction;

import java.util.ArrayList;
import java.util.List;

/* Реализация алгоритма Чана:
1. Разбиваем точки на группы по m штук.
2. Для каждой группы строим выпуклую оболочку (алгоритм Грэхема).
3. Объединяем маленькие оболочки в одну (алгоритм Джарвиса с бинарным поиском).
4. Если объединение не удалось (сделали больше m шагов) - увеличиваем m. */

public class ChanAlgorithm {

    private long iterationCount;
    private long startTimeNano;

    public ChanAlgorithm() {
        this.iterationCount = 0;
    }

    public long getIterationCount() {
        return iterationCount;
    }

    public void resetIterationCount() {
        this.iterationCount = 0;
    }

    /* Запуск алгоритма Чана с итеративным подбором m */
    public List<Point> findConvexHull(List<Point> points) {
        resetIterationCount();

        // Итеративный подбор m: 4, 16, 256, 65536, ...
        for (int t = 1; t <= 20; t++) {
            int m = (int) Math.min(Math.pow(2, Math.pow(2, t)), points.size());

            List<Point> hull = findConvexHullWithM(points, m);
            if (hull != null) {
                return hull;
            }
        }

        // Если дошли сюда — ни одно m не сработало
        List<Point> result = GrahamScan.scan(points);
        iterationCount += GrahamScan.getIterationCount();
        return result;
    }

    /* Алгоритм Чана с фиксированным m */
    private List<Point> findConvexHullWithM(List<Point> points, int m) {
        int n = points.size();

        // Шаг 1: Разбиение на группы по m точек
        List<List<Point>> groups = new ArrayList<>();
        for (int i = 0; i < n; i += m) {
            int end = Math.min(i + m, n);
            groups.add(new ArrayList<>(points.subList(i, end)));
        }

        // Шаг 2: Построение малых оболочек (Грэхем)
        List<List<Point>> subHulls = new ArrayList<>();
        for (List<Point> group : groups) {
            List<Point> hull = GrahamScan.scan(group);
            subHulls.add(hull);
            iterationCount += GrahamScan.getIterationCount();
        }

        // Шаг 3: Объединение оболочек (Джарвис с ограничением m)
        List<Point> result = JarvisMarch.march(subHulls, m);
        iterationCount += JarvisMarch.getIterationCount();

        return result;
    }

    public void startTiming() {
        startTimeNano = System.nanoTime();
    }

    public double stopTiming() {
        long endTimeNano = System.nanoTime();
        return (endTimeNano - startTimeNano) / 1_000_000.0;
    }
}