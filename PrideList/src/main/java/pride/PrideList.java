package pride;

import java.util.Comparator;
import java.util.Optional;

public class PrideList<T> implements AdvancedList<T>, AuthorHolder {

    private Object[] array;
    private int size;
    private int memory_size;
    private static final int DEFAULT_MEMORY_SIZE = 4;
    private int random = 1;

    public PrideList() {
        size = 0;
        memory_size = DEFAULT_MEMORY_SIZE;
        array = new Object[DEFAULT_MEMORY_SIZE];
    }

    private int calcMemSize(int n) {
        int result = 1;
        while(result <= n) result *= 2;
        return result;
    }

    public PrideList(int n) {
        size = n;
        memory_size = calcMemSize(n);
        array = new Object[memory_size];
    }

    private void resize(int n) {
        if(n < 0) n = 0;
        int new_size = n;
        int new_mem_size = calcMemSize(n);
        Object[] new_array = new Object[new_mem_size];
        int min_size = Math.min(new_size, size);
        for(int i = 0; i < min_size; i++) {
            new_array[i] = array[i];
        }
        size = new_size;
        memory_size = new_mem_size;
        array = new_array;
    }

    @Override
    public void add(T item) {
        if(size >= memory_size) resize(size);
        array[size] = item;
        size++;
    }

    @Override
    public void insert(int index, T item) throws Exception {
        if(index < 0 || index > size) throw new IndexOutOfBoundsException();
        if(size == memory_size) resize(size + 1);
        for(int i = size - 1; i >= index; i--) {
            array[i] = array[i - 1];
        }
        array[index] = item;
        size++;
    }

    @Override
    public void remove(int index) throws Exception {
        if(index < 0 || index >= size) throw new IndexOutOfBoundsException();
        for(int i = index; i + 1 < size; i++) {
            array[i] = array[i + 1];
        }
        size--;
        array[size] = null;
        if(size * 3 < memory_size) resize(size);
    }

    @Override
    public Optional<T> get(int index) {
        if(index < 0 || index > size) return Optional.empty();
        else return Optional.of((T)array[index]);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void addAll(SimpleList<T> list) {
        for(int i = 0; i < list.size(); i++) {
            add(list.get(i).get());
        }
    }

    @Override
    public int first(T item) {
        for(int i = 0; i < size; i++) {
            if(array[i].equals(item)) return i;
        }
        return -1;
    }

    @Override
    public int last(T item) {
        for(int i = size - 1; i >= 0; i--) {
            if(array[i].equals(item)) return i;
        }
        return -1;
    }

    @Override
    public boolean contains(T item) {
        return first(item) != -1;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    private int nextRand(int x, int MOD) {
        int a = 11337;
        int b = 117877;
        random = random * a + b;
        return Math.abs(random % MOD);
    }

    public void linearCongruentShuffle() {
        int rand = nextRand(1, size);
        for(int i = size - 1; i > 0; i--) {
            rand = nextRand(rand, i);
            int j = rand;
            T swap = (T)array[i];
            array[i] = array[j];
            array[j] = swap;
        }
    }

    @Override
    public AdvancedList<T> shuffle() {
        PrideList<T> result = copy();
        result.linearCongruentShuffle();
        return result;
    }

    public void set(int i, T val) {
        if(i < 0 || i >= size) return;
        array[i] = val;
    }

    private void heapify(int i, int n, Comparator<T> comparator) {
        int largest = i;
        int left = 2 * i + 1;
        int right = 2 * i + 2;
        if(left < n && comparator.compare((T)array[largest], (T)array[left]) == -1) {
            largest = left;
        }
        if(right < n && comparator.compare((T)array[largest], (T)array[right]) == -1) {
            largest = right;
        }
        if(largest != i) {
            T temp = (T)array[i];
            array[i] = array[largest];
            array[largest] = temp;
            heapify(largest, n , comparator);
        }
    }

    public void heapSort(Comparator<T> comparator) {
        for(int i = size / 2 - 1; i >= 0; i--) {
            heapify(i, size, comparator);
        }
        for(int i = size - 1; i >= 0; i--) {
            T temp = (T)array[i];
            array[i] = array[0];
            array[0] = temp;
            heapify(0,i, comparator);
        }
    }

    private void mergeSort(Object[] temp, int l, int r, Comparator<T> comparator) {
        if (r - l <= 1) return;
        int m = (l + r) / 2;
        mergeSort(temp, l, m, comparator);
        mergeSort(temp, m, r, comparator);
        int i = l, j = m, k = l;
        while (i < m && j < r) {
            if (comparator.compare((T)array[i], (T)array[j]) == -1) {
                temp[k++] = array[i++];
            }
            else {
                temp[k++] = array[j++];
            }
        }
        while (i < m) {
            temp[k++] = array[i++];
        }
        while (j < r) {
            temp[k++] = array[j++];
        }
        for(i = l; i < r; i++) {
            array[i] = temp[i];
        }
    }

    public void mergeSort(Comparator<T> comparator) {
        Object[] temp = array.clone();
        mergeSort(temp, 0,size, comparator);
    }

    public PrideList<T> copy() {
        PrideList<T> result = new PrideList<>(size);
        for(int i = 0; i < size ; i++) {
            result.set(i, (T)array[i]);
        }
        return result;
    }

    @Override
    public AdvancedList<T> sort(Comparator<T> comparator) {
        PrideList<T> tree = copy();
        tree.mergeSort(comparator);
        //tree.heapSort(comparator);
        return tree;
    }

    @Override
    public String author() {
        String name = "Andrey Duvanov";
        return name;
    }
}
