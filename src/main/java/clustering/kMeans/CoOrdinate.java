package clustering.kMeans;

public class CoOrdinate {
    Double x;
    Double y;
    public  CoOrdinate(Double a, Double b) {
        x=a;
        y=b;
    }
    @Override
    public String toString(){
        return "("+this.x +","+this.y+")";
    }
}
