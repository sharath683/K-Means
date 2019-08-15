import clustering.kMeans.CoOrdinate;
import clustering.kMeans.KMeansClustering;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class KMeansClusteringTest {
    @Test
    public void testSampleKMeans() {
        List<Double> xList = Arrays.asList(1.0, 2.0, 12.0, 14.0);
        List<Double> yList  = Arrays.asList(1.0, 1.0, 2.0, 2.0);
        HashMap<Integer, List<CoOrdinate>> finalClusters = new HashMap<>();
        List<CoOrdinate> points = new ArrayList<CoOrdinate>();
        List<CoOrdinate> centroids = new ArrayList<CoOrdinate>();

        for(int i=0;i<xList.size();i++) {
            points.add(new CoOrdinate(xList.get(i),yList.get(i)));
        }
        HashMap<Integer,List<CoOrdinate>> clusters = KMeansClustering.runKMeans(points,2,10);
        assertEquals(clusters.size(), 2);
        assertEquals(clusters.get(0).size(),2);
        assertEquals(clusters.get(1).size(),2);
    }
}
