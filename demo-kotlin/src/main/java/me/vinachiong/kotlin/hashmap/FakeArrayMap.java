package me.vinachiong.kotlin.hashmap;

import java.util.ConcurrentModificationException;

/**
 * @author vina.chiong
 * @version v1.0.0
 */
public class FakeArrayMap<K, V> {
    int[] mHashes;
    Object[] mArray;
    int mSize; // 当前元素个数
    static final int BASE_SIZE = 4;


    public FakeArrayMap(int size){
        mHashes = new int[size];
        mArray = new Object[size << 1];
    }

    public V put(K key, V value) {
        int h = System.identityHashCode(key);
        int osize = mSize;

        int index = indexOf(key, h);

        // 该key已存在
        if (index >= 0) {
            index = (index<<1) + 1;
            final V old = (V)mArray[index];
            mArray[index] = value;
            return old;
        }

        index = ~index;
        if (osize >= mHashes.length) { // 元素个数 超了 数组长度
            // TODO 补充
        }

        if (index < osize) { // index 小于元素个数
            // TODO 补充
        }

        int x, y, z;
        mHashes[(x = index)] = h;
        mArray[(y = index<<1)] = key;
        mArray[(z = (index<<1) + 1)] = value;
        mSize++;

        StringBuilder sb = new StringBuilder();
        sb.append("[").append(key).append(":").append(value).append("]:")
                .append(x).append(", ")
                .append(y).append(", ")
                .append(z).append("\n");
        System.out.println(sb);
        return null;
    }

    int indexOf(Object key, int hash) {
        final int N = mSize;

        // Important fast case: if nothing is in here, nothing to look for.
        if (N == 0) {
            return ~0; // aka -1
        }

        int index = binarySearchHashes(mHashes, N, hash);

        // If the hash code wasn't found, then we have no entry for this key.
        if (index < 0) {
            return index;
        }

        // If the key at the returned index matches, that's what we want.
        if (key.equals(mArray[index<<1])) {
            return index;
        }

        // Search for a matching key after the index.
        int end;
        for (end = index + 1; end < N && mHashes[end] == hash; end++) {
            if (key.equals(mArray[end << 1])) return end;
        }

        // Search for a matching key before the index.
        for (int i = index - 1; i >= 0 && mHashes[i] == hash; i--) {
            if (key.equals(mArray[i << 1])) return i;
        }

        // Key not found -- return negative value indicating where a
        // new entry for this key should go.  We use the end of the
        // hash chain to reduce the number of array entries that will
        // need to be copied when inserting.
        return ~end;
    }

    private static int binarySearchHashes(int[] hashes, int N, int hash) {
        try {
            return binarySearch(hashes, N, hash);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ConcurrentModificationException();
        }
    }

    // This is Arrays.binarySearch(), but doesn't do any argument validation.
    static int binarySearch(int[] array, int size, int value) {
        int lo = 0;
        int hi = size - 1;

        while (lo <= hi) {
            final int mid = (lo + hi) >>> 1;
            final int midVal = array[mid];

            if (midVal < value) {
                lo = mid + 1;
            } else if (midVal > value) {
                hi = mid - 1;
            } else {
                return mid;  // value found
            }
        }
        return ~lo;  // value not present
    }

    public void print() {
        final StringBuilder sb = new StringBuilder();
        sb.append("mHashes.size = " + mHashes.length).append("\n");
        sb.append("mArrays.size = " + mArray.length).append("\n");


        System.out.println(sb.toString());
    }
}
