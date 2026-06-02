package convex_hull.construction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/* Реализация алгоритма Джарвиса:
1. Найдём самую нижнюю (левую) точку p.
2. Потом, без всяких сортировок, итерируем по массиву points с целью найти точку,
образующую минимальный полярный угол с предыдущей точкой, добавляем её в null. Таким образом
итерируем до момента, пока не вернёмся в начальную точку p. */

public class JarvisMarch {

    private static long iterationCount = 0;

    public static void resetIterationCount() {
        iterationCount = 0;
    }

    public static long getIterationCount() {
        return iterationCount;
    }

    /* Обычный алгоритм Джарвиса для точек
    Сложность: O(n * h) */
    public static List<Point> march(Collection<Point> points) {
        resetIterationCount();

        List<Point> hull = new ArrayList<>();

        Point startingPoint = GraphUtils.getMinY(points); // ищем самую нижнюю (левую) точку
        hull.add(startingPoint);

        Point prevVertex = startingPoint;

        while (true) {
            Point candidate = null;
            /* Пройдёмся по всему List<Point> и найдём точку с наименьшим полярным углом */
            for (Point point : points) {

                if (candidate == null) {
                    candidate = point;
                    continue;
                }

                if (point.equals(prevVertex)) continue;

                ++iterationCount;
                int ccw = GraphUtils.counterclockwise(prevVertex, candidate, point);

                if (ccw == 0 &&
                        GraphUtils.distSq(prevVertex, candidate) < GraphUtils.distSq(prevVertex, point)) {
                    candidate = point; // если векторы коллинеарны, то сравниваем их длины
                } else if (ccw < 0) {
                    /* если происходит clockwise поворот, то point имеет меньший полярный угол
                    относительно preVertex, чем candidate */
                    candidate = point;
                }
            }

            if (candidate.equals(startingPoint)) break; // вернулись к стартовой точке, дело сделано

            hull.add(candidate);
            prevVertex = candidate;
        }

        return hull;
    }

    /*  Алгоритм Джарвиса для оболочек (для алгоритма Чана)
    Сложность: O(h * (n/m) * log m)
    @param subHulls - список малых выпуклых оболочек
    @param m - максимальное количество шагов
    @return - выпуклая оболочка или null, если не замкнулась за m шагов */
    public static List<Point> march(List<List<Point>> subHulls, int m) {

        resetIterationCount();

        if (subHulls.isEmpty()) return null;

        Point startPoint = findLeftmostLowestPoint(subHulls);

        List<Point> hull = new ArrayList<>();

        Point prevVertex = startPoint;
        hull.add(startPoint);

        for (int step = 0; step < m; ++step) {
            Point candidate = null;

            List<Point> vars = new ArrayList<>();

            for (List<Point> subHull : subHulls) {
                Point current = binarySearch(subHull, prevVertex);
                vars.add(current);
            }

            for (Point point : vars) {

                if (candidate == null) {
                    candidate = point;
                    continue;
                }

                if (point.equals(prevVertex)) continue;

                ++iterationCount;
                int ccw = GraphUtils.counterclockwise(prevVertex, candidate, point);
                if (ccw == 0) {
                    if (GraphUtils.distSq(prevVertex, candidate) < GraphUtils.distSq(prevVertex, point)) {
                        candidate = point;
                    }
                } else if (ccw < 0) {
                    if (!point.equals(startPoint)) {
                        candidate = point;
                    }
                }
            }

            if (candidate.equals(startPoint)) return hull;

            hull.add(candidate);

            prevVertex = candidate;
        }

        return null;
    }

    /* Находит самую левую нижнюю точку */
    private static Point findLeftmostLowestPoint(List<List<Point>> subHulls) {
        Point leftmost = null;
        for (List<Point> hull : subHulls) {
            for (Point p : hull) {
                if (leftmost == null ||
                        p.y < leftmost.y ||
                        (p.y == leftmost.y && p.x < leftmost.x)) {
                    leftmost = p;
                }
            }
        }
        return leftmost;
    }

    /* Бинарный поиск опорной точки в выпуклой оболочке */
    private static Point binarySearch(List<Point> hull, Point current) {
        int size = hull.size();
        if (size == 0) return null;
        if (size == 1) return hull.get(0);

        int idx = hull.indexOf(current);
        if (idx != -1) {
            return hull.get((idx + 1) % size);
        }

        int left = 0;
        int right = size;

        while (left < right) {
            ++iterationCount; // считаем шаг бинарного поиска

            int mid = (left + right) / 2;

            Point midPoint = hull.get(mid);
            Point prev = hull.get((mid - 1 + size) % size);
            Point next = hull.get((mid + 1) % size);

            int ccwPrev = GraphUtils.counterclockwise(current, midPoint, prev);
            int ccwNext = GraphUtils.counterclockwise(current, midPoint, next);

            // midPoint является опорной?
            if (ccwPrev >= 0 && ccwNext > 0) {
                return midPoint;
            }
            if (ccwPrev >= 0 && ccwNext == 0) {
                return next;
            }

            int ccwMid = GraphUtils.counterclockwise(current, hull.get(left), midPoint);

            if (ccwMid > 0) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        return hull.get(left % size);
    }
}