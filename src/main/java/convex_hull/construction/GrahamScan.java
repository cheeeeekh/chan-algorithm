package convex_hull.construction;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

/* Реализация алгоритма Грэхэма:
1. Найдём самую нижнюю (левую) точку p.
2. Отсортируем относительно полярного угла от точки p.
3. Итерируем points в отсортированном порядке и добавляем в hull, но в случае, когда получим не
counterclockwise поворот, отбрасываем point.
3.1. Отдельно рассматриваем случаи, когда изи точек получаем коллинеарные векторы. */

public class GrahamScan {

    private static long iterationCount = 0;

    public static void resetIterationCount() {
        iterationCount = 0;
    }

    public static long getIterationCount() {
        return iterationCount;
    }

    /* Сортировка по углу, оличающая GrahamScan от JarvisMarch.
    Параметр ref будет равен начальной точке */
    private static void sortByAngle(List<Point> points, Point ref) {
        Collections.sort(points, (b, c) -> {
            ++iterationCount; // считаем каждое сравнение в сортировке

            // точку p сразу добавляем в hull
            if (b.equals(ref)) return -1; // если b == ref, то точка b точно <= точки c, сортируем
            if (c.equals(ref)) return 1; // если c == ref, то точка c точно <= точки b, сортируем

            int ccw = GraphUtils.counterclockwise(ref, b, c);

            if (ccw == 0) {
                /* Обрабатываем коллинеарные точки */
                if (b.x == c.x) {
                    /* Обрабатываем редкий случай, когда точки совпадают по координате X, но
                    разные по Y. Делаем проверку и берём ближнюю по Y точку */
                    return (b.y < c.y) ? -1 : 1;
                    // Из двух коллинеарных точек меньшей считаем ту, что ближе к начальной точке
                } else  if (((b.x > c.x) && (b.y > c.y)) || ((b.x < c.x) && (b.y > c.y))) {
                    return 1;
                } else {
                    return -1;
                }
            } else {
                return ccw * (-1);
            }
        });
    }

    // Сам алгоритм
    public static List<Point> scan(List<Point> points) {
        resetIterationCount();

        Deque<Point> stack = new ArrayDeque<>();

        if (points.isEmpty()) {
            return new ArrayList<>();
        }

        if (points.size() == 1) {
            return new ArrayList<>(List.of(points.getFirst()));
        }

        // ищем самую нижнюю (левую) точку
        Point min = GraphUtils.getMinY(points);
        sortByAngle(points, min); // сортируем по полярному углу

        /* Самая последняя точка может совпадать с первой, если все точки одинаковы,
        нужна проверка */
        if (points.getLast().equals(min)) {
            return new ArrayList<>(List.of(min));
        }

        stack.push(points.get(0)); // 1 точку гарантированно добавляем в оболочку
        stack.push(points.get(1)); // добавляем следующую точку (если что, удалим)

        for (int i = 2; i < points.size(); ++i) {
            Point next = points.get(i);
            Point p = stack.pop();

            while (stack.peek() != null && GraphUtils.counterclockwise(stack.peek(), p, next) <= 0) {
                ++iterationCount;
                p = stack.pop(); // удаляем точку, в которой совершается поворот по часовой стрелке
            }

            stack.push(p);
            stack.push(points.get(i));
        }

        return new ArrayList<>(stack);
    }
}