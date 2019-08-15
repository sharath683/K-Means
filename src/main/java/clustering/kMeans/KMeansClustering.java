package clustering.kMeans;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class KMeansClustering {
    /**
     * computes the euclidean distance between two Points
     * @param a Point1
     * @param b Point2
     * @return Euclidean dostance between point1 and point2
     */
    public  static Double getDistance(CoOrdinate a , CoOrdinate b){
        return (a.x-b.x)*(a.x-b.x) + (a.y-b.y)*(a.y-b.y);
    }

    public static Integer getClusterIndex(CoOrdinate a, List<CoOrdinate> centroids) {
        Double minDist=Double.MAX_VALUE;
        Integer retIdx = -1;
        for(int i=0; i< centroids.size();i++) {
            Double dist = getDistance(a,centroids.get(i));
            if(dist<minDist) {
                minDist = dist;
                retIdx=i;
            }
        }
        return retIdx;
    }

    /**
     * Returns the list of centroids of each cluster after every iteration
     * @param clusters HashMap containing the clusterId as key and list of points as value
     * @return List of points as centroids of the corresponding cluster
     */
    public static List<CoOrdinate> getNewCentroids(HashMap<Integer,List<CoOrdinate>> clusters) {
        List<CoOrdinate> centroids = new ArrayList<CoOrdinate>();
        for(int i=0;i<clusters.size();i++){
            List<CoOrdinate> points = clusters.get(i);
            Double sumx=0.0,sumy=0.0;
            for(int k=0;k<points.size();k++) {
                sumx=points.get(k).x+sumx;
                sumy=points.get(k).y+sumy;
            }
            centroids.add(new CoOrdinate(sumx/points.size(),sumy/points.size()));
        }
        return centroids.stream().filter(point->point.x>0 && point.y>0).collect(Collectors.toList());
    }

    /**
     * Compares if the two set of centroids are equal between successive iterations or not
     * @param centroids List of centroid CoOrdinates in previous iteration
     * @param newCentroids List of centroids in the current iteration
     * @return True if there is convergence, else false
     */
    public static boolean compareCentroids(List<CoOrdinate> centroids, List<CoOrdinate> newCentroids){
        boolean result = true;
        for (int i=0;i<centroids.size();i++) {
            result = result && (centroids.get(i).x.equals(newCentroids.get(i).x)) && (centroids.get(i).y.equals(newCentroids.get(i).y)) ;
            if(result == false)
                return false;
        }
        return result;
    }

    /**
     *
     * @param points  list of CoOrdinate points to be clustered
     * @param numClusters Target number of clusters
     * @param maxIters Max number of iterations if the algorithm didn't converge
     * @return clusters, HashMap containing the clusterId as key and list of points as value
     */

    public static HashMap<Integer,List<CoOrdinate>> runKMeans(List<CoOrdinate> points,Integer numClusters,int maxIters) {
        int k=numClusters;
        List<CoOrdinate> centroids = new ArrayList<CoOrdinate>();
        HashMap<Integer,List<CoOrdinate>> clusters =  new HashMap<Integer,List<CoOrdinate>>();
        List<Double> centroidsX= new ArrayList<Double>() ;
        List<Double> centroidsY= new ArrayList<Double>() ;
        HashSet<Integer>hashSet=new HashSet<>();
        Random random = new Random();
        //now add random number to this set
        while(true)
        {
            hashSet.add(random.nextInt(points.size()));
            if(hashSet.size()==k)
                break;
        }
        List randList = new ArrayList<>(hashSet);
        for(int i=0;i<k;i++){
            clusters.put(i,new ArrayList<CoOrdinate>());
            centroids.add(points.get((int)randList.get(i)));
        }
        int iter = 0;
        boolean breakCondition=false;
        do{
            iter++;
            for(int i=0;i<k;i++){
                clusters.put(i,new ArrayList<CoOrdinate>());
            }
            for(int i=0;i<points.size();i++) {
                Integer clusterIdx = getClusterIndex(points.get(i),centroids);
                clusters.get(clusterIdx).add(points.get(i));
            }
            List<CoOrdinate> newCentroids  = getNewCentroids(clusters);
            breakCondition = compareCentroids(centroids,newCentroids);
            centroids = newCentroids;
            System.out.println("Centroids at the end of iteration: " + iter);
            for(int i=0;i<newCentroids.size();i++)
                System.out.println("centroid of cluster"+ (i+1)+": "+ centroids.get(i).toString());
        }while (!breakCondition && iter<maxIters);
        return clusters;
    }

    /**
     * Method to calculate the cost as the sum of euclidean distance between the points and their respective centroids
     * @param clusters : HashMap containing the clusterId as key and list of points as value
     * @return Double value, cost for the clustering
     */
    public static Double computeCost(HashMap<Integer,List<CoOrdinate>> clusters) {
        List<CoOrdinate> centroids = getNewCentroids(clusters);
        Double totalCost = 0.0;
        for(int i=0;i<centroids.size();i++) {
            for(int j=0;j<clusters.get(i).size();j++) {
                totalCost = totalCost+getDistance(centroids.get(i),clusters.get(i).get(j));
            }
        }
        return totalCost;
    }
    public static void main(String[] args) throws FileNotFoundException {
        HashMap<Integer,List<CoOrdinate>> finalClusters = new HashMap<>();
        List<CoOrdinate> points = new ArrayList<CoOrdinate>();
        List<CoOrdinate> centroids = new ArrayList<CoOrdinate>();

        int maxIters=200; //default max number of iterations if there is no convergence in the algorithm if not mentioned in the command line
        if(args.length>2)
            maxIters = Integer.parseInt(args[2]); //  max number of iterations passed in the command line
        int maxTries = 1; // hard coded this to 1 for now. Can be extended so that initial centroids are chosen at random and efficient clustering can be selected based on the distance scores.
        int numClusters = Integer.parseInt(args[0]); // number of clusters
        String filePath = args[1]; // path of the file where the co-ordinate data is present - each coordinate in the format (a,b),one co-ordinate for a single line
        File input = new File(filePath) ;
        Scanner scanner = new Scanner(input);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            points.add(new CoOrdinate(Double.parseDouble(line.split(",")[0]),Double.parseDouble(line.split(",")[0])));
        }

        Double costValue = Double.MAX_VALUE;
        for(int i=0;i<maxTries;i++) {
            HashMap clusters = runKMeans(points, numClusters, maxIters); // actual invoking of k-means iterations
            Double currentCost = computeCost(clusters);
            if(costValue>currentCost) {  // check the current cost of the clusters with the previous ones and update to the minimum cost, if the maxTries is defined more than 1.
                costValue = currentCost;
                finalClusters = clusters;
            }
        }
        for (int i=0;i<finalClusters.size();i++) {
            for(int j=0;j<finalClusters.get(i).size();j++){
                System.out.println(finalClusters.get(i).get(j).toString() + "===>  cluster_"+(i+1));
            }
        }

    }

}
