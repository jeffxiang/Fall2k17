import java.util.LinkedList;

/** A set of String values.
 *  @author Jeff Xiang
 */
class ECHashStringSet extends BSTStringSet implements StringSet {
    private static double MINLOAD = 0.2;
    private static double MAXLOAD = 5;

    private int _size;
    private LinkedList<String>[] _list;

    public ECHashStringSet() {
        _size = 0;
        _list = new LinkedList[(int) (1/MINLOAD)];
    }

    @Override
    public void put(String s) {
        if (load() > MAXLOAD) {
            resize();
        }
        if (s != null) {
            int bucket = tobucket(s.hashCode());
            if (_list[bucket] == null) {
                _list[bucket] = new LinkedList<>();
            }
            _list[bucket].add(s);
        }
        _size++;
    }

    @Override
    public boolean contains(String s) {
        int bucket = tobucket(s.hashCode());
        LinkedList chain = _list[bucket];
        return chain.contains(s);
    }

    public int tobucket(int hashcode) {
        int bucket;
        if (hashcode < 0) {
            int one = 1;
            one = one << 31;
            bucket = (hashcode ^ one) % _list.length;
        } else {
            bucket = hashcode % _list.length;
        }
        return bucket;
    }

    public void resize() {
        LinkedList<String>[] oldbuckets = _list;
        _list = new LinkedList[2 * oldbuckets.length];
        _size = 0;
        for (LinkedList<String> chain: oldbuckets) {
            if (chain != null) {
                for (String s: chain) {
                    put(s);
                }
            }
        }
    }

    public int size() {
        return _size;
    }

    public double load() {
        return (_size / _list.length);
    }
}
