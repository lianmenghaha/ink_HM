package shape;

public class Pair {

    public double x,y;

    public boolean keep;

    public Pair(double x, double y){
        this.x = x;
        this.y = y;
        this.keep = true;
    }

    public boolean isKeep() {
        return keep;
    }

    public void setKeep(boolean keep) {
        this.keep = keep;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "Pair(" +
                 x +
                ", " + y +
                ')';
    }
}
