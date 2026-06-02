package convex_hull.construction;

import java.io.IOException;
import java.io.File;
import java.io.FileWriter;

public class FileGenerator {
    public static int[][] generateFile(int n) {
        int[][] a = new int[n][2];
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < 2; ++j) {
                a[i][j] = (int) (Math.random() * 100000);
            }
        }
        return a;
    }

    public static void main(String[] args) throws IOException {
        for (int k = 1; k < 101; ++k) {
            int[][] m = generateFile(k * 100);
            File file = new File("src/main/java/convex_hull/construction/data/data" + k);
            FileWriter writer = new FileWriter(file);
            for (int i = 0; i < m.length; ++i) {
                for (int j = 0; j < m[i].length; ++j) {
                    writer.write(m[i][j] + " ");
                }
                writer.write("\n");
            }
            writer.close();
        }
    }
}