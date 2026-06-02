package convex_hull.construction;

import java.util.Collection;
import java.util.Iterator;

public class GraphUtils {

    // Нахождение самой нижней (левой) точки среди всех точек
    public static Point getMinY(Collection<Point> points) {
        Iterator<Point> currentPoint = points.iterator();
        Point min = currentPoint.next();

        while (currentPoint.hasNext()) {
            Point point = currentPoint.next();
            if (point.y <= min.y) {
                if (point.y < min.y) {
                    min = point;
                } else if (point.x < min.x) { // point.y == min.y - выбираем самую левую
                    min = point;
                }
            }
        }

        return min;
    }

    /* У нас есть ломаная abc. Мы достраиваем её до параллелограмма, а затем считаем его
    площадь. Если площадь меньше 0, то был осуществлён поворот по часовой стрелки.
    Если площадь больше 0, то был осуществлён поворот по часовой стрелке. Если
    площадь равна 0, то звенья ломаной коллинеарны. */
    public static int counterclockwise(Point a, Point b, Point c) {
        // Формула ориентированной площади пар-ма через 3 точки на плоскости
        float area = (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x);
        if (area < 0) return -1; // clockwise
        if (area > 0) return 1; //  counterclockwise
        return 0; // collinear
    }

    public static double distSq(Point a, Point b) {
        return (a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y);
    }
}