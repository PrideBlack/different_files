package pride;

import java.util.Comparator;

public class PairComporator implements Comparator<Pair> {

    @Override
    public int compare(Pair o1, Pair o2) {
        if(o1.x == o2.x && o1.y == o2.y) return 0;
        if((o1.x < o2.x) || ((o1.x == o2.x) && (o1.y < o2.y))) return -1;
        return 1;
    }
}
