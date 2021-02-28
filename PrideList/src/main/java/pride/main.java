package pride;

import java.util.ArrayList;

public class main {

    private static int nextRand(int x, int MOD) {
        int a = 11337;
        int b = 117877;
        return (x * a + b) % MOD;
    }

    private static PrideList<Pair> gen(int n) {
        PrideList<Pair> a = new PrideList<Pair>();
        int MOD = 1000000007;
        int random = 29052000;
        for(int i = 0; i < n; i++) {
            random = nextRand(random, MOD);
            int val1 = random;
            random = nextRand(random, MOD);
            int val2 = random;
            a.add(new Pair(val1, val2));
        }
        return a;
    }

    private static void checkSorts() {
        int l = 1; int r = 1000000;
        while(l + 1 < r) {
            int m = (l + r) / 2;
            PrideList<Pair> a = gen(m);
            PrideList<Pair> b = a.copy();
            long start = System.currentTimeMillis();
            b.mergeSort(new PairComporator());
            long end = System.currentTimeMillis();
            long mt = end - start;
            b = a.copy();
            start = System.currentTimeMillis();
            b.heapSort(new PairComporator());
            end = System.currentTimeMillis();
            long ht = end - start;
            System.out.println("N = " + m + " mergeSort : " + mt + "ms heapSort : " + ht + "ms");
            if(mt >= ht) l = m;
            else r = m;
        }
        System.out.println("limit : " + l);
    }

    public static void main(String[] args) {

    }

}
