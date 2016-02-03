package com.example.hexi.canvastest.concurrent;

import java.io.Serializable;
import java.util.AbstractQueue;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by hexi on 15/11/18.
 */
public class PriorityBlockingDeque<E> extends AbstractQueue<E> implements BlockingDeque<E>, Serializable {

    private static final long serialVersionUID = 772285010852407824L;

    private static final int DEFAULT_INITIAL_CAPACITY = 11;

    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    private static enum Level
    {
        MIN, MAX
    };

    private transient Object[] deque;

    private final Comparator<? super E> comparator;

    private int size = 0;

    /**
     * Lock used for all public operations
     */
    private final ReentrantLock lock;

    /**
     * Condition for blocking when empty
     */
    private final Condition notEmpty;

    /**
     * Spinlock for allocation, acquired via CAS.
     */
    private transient volatile AtomicInteger allocationSpinLock = new AtomicInteger(0);

    public PriorityBlockingDeque()
    {
        this(DEFAULT_INITIAL_CAPACITY,null);
    }

    public PriorityBlockingDeque(int initialCapacity)
    {
        this(initialCapacity,null);
    }

    public PriorityBlockingDeque(int initialCapacity, Comparator<? super E> comparator)
    {
        // Note: This restriction of at least one is not actually needed,
        // but continues for 1.5 compatibility
        if (initialCapacity < 1)
            throw new IllegalArgumentException();
        this.lock = new ReentrantLock();
        this.notEmpty = lock.newCondition();
        this.comparator = comparator;
        this.deque = new Object[initialCapacity];
    }

    @SuppressWarnings("unchecked")
    public PriorityBlockingDeque(Collection<? extends E> c)
    {
        this.lock = new ReentrantLock();
        this.notEmpty = lock.newCondition();
        if (c instanceof SortedSet<?>)
        {
            SortedSet<? extends E> ss = (SortedSet<? extends E>) c;
            this.comparator = (Comparator<? super E>) ss.comparator();
            addAll(ss);
        }
        else if (c instanceof PriorityDeque<?>)
        {
            PriorityDeque<? extends E> pq = (PriorityDeque<? extends E>) c;
            this.comparator = (Comparator<? super E>) pq.comparator();
            initFromPriorityDeque(pq);
        }
        else if (c instanceof PriorityBlockingDeque<?>)
        {
            PriorityBlockingDeque<? extends E> pq = (PriorityBlockingDeque<? extends E>) c;
            this.comparator = (Comparator<? super E>) pq.comparator();
            initFromPriorityBlockingDeque(pq);
        }
        else
        {
            this.comparator = null;
            addAll(c);
        }
    }

    @SuppressWarnings("unchecked")
    public PriorityBlockingDeque(PriorityDeque<? extends E> c)
    {
        this.lock = new ReentrantLock();
        this.notEmpty = lock.newCondition();
        this.comparator = (Comparator<? super E>) c.comparator();
        initFromPriorityDeque(c);
    }

    @SuppressWarnings("unchecked")
    public PriorityBlockingDeque(PriorityBlockingDeque<? extends E> c)
    {
        this.lock = new ReentrantLock();
        this.notEmpty = lock.newCondition();
        this.comparator = (Comparator<? super E>) c.comparator();
        initFromPriorityBlockingDeque(c);
    }

    @SuppressWarnings("unchecked")
    public PriorityBlockingDeque(SortedSet<? extends E> c)
    {
        this.lock = new ReentrantLock();
        this.notEmpty = lock.newCondition();
        this.comparator = (Comparator<? super E>) c.comparator();
        addAll(c);
    }

    private void initFromPriorityDeque(PriorityDeque<? extends E> c)
    {
        if (c.getClass() == PriorityDeque.class)
        {
            deque = c.toArray();
            size = c.size();
        }
        else
        {
            addAll(c);
        }
    }

    private void initFromPriorityBlockingDeque(PriorityBlockingDeque<? extends E> c)
    {
        if (c.getClass() == PriorityBlockingDeque.class)
        {
            deque = c.toArray();
            size = c.size();
        }
        else
        {
            addAll(c);
        }
    }

    /**
     * Tries to grow array to accommodate at least one more element
     * (but normally expand by about 50%), giving up (allowing retry)
     * on contention (which we expect to be rare). Call only while
     * holding lock.
     *
     * @param array the heap array
     * @param oldCap the length of the array
     */
    private void tryGrow(Object[] array, int oldCap) {
        lock.unlock(); // must release and then re-acquire main lock
        Object[] newArray = null;
        if (allocationSpinLock.get() == 0 &&
                allocationSpinLock.compareAndSet(0, 1)) {
            try {
                int newCap = oldCap + ((oldCap < 64) ?
                        (oldCap + 2) : // grow faster if small
                        (oldCap >> 1));
                if (newCap - MAX_ARRAY_SIZE > 0) {    // possible overflow
                    int minCap = oldCap + 1;
                    if (minCap < 0 || minCap > MAX_ARRAY_SIZE)
                        throw new OutOfMemoryError();
                    newCap = MAX_ARRAY_SIZE;
                }
                if (newCap > oldCap && deque == array)
                    newArray = new Object[newCap];
            } finally {
                allocationSpinLock.set(0);
            }
        }
        if (newArray == null) // back off if another thread is allocating
            Thread.yield();
        lock.lock();
        if (newArray != null && deque == array) {
            deque = newArray;
            System.arraycopy(array, 0, newArray, 0, oldCap);
        }
    }

    @Override
    public int remainingCapacity()
    {
        return Integer.MAX_VALUE;
    }

    @Override
    public int drainTo(Collection<? super E> c)
    {
        if (c == null)
            throw new NullPointerException();
        if (c == this)
            throw new IllegalArgumentException();
        final ReentrantLock lock = this.lock;
        lock.lock();
        try
        {
            int n = 0;
            E e;
            while ((e = removeAt(0)) != null)
            {
                c.add(e);
                ++n;
            }
            return n;
        }
        finally
        {
            lock.unlock();
        }
    }

    @Override
    public int drainTo(Collection<? super E> c, int maxElements)
    {
        if (c == null)
            throw new NullPointerException();
        if (c == this)
            throw new IllegalArgumentException();
        if (maxElements <= 0)
            return 0;
        final ReentrantLock lock = this.lock;
        lock.lock();
        try
        {
            int n = 0;
            E e;
            while (n < maxElements && (e = removeAt(0)) != null)
            {
                c.add(e);
                ++n;
            }
            return n;
        }
        finally
        {
            lock.unlock();
        }
    }

    @Override
    public E removeFirst()
    {
        E x = pollFirst();
        if (x != null)
            return x;
        else
            throw new NoSuchElementException();
    }

    @Override
    public E removeLast()
    {
        E x = pollLast();
        if (x != null)
            return x;
        else
            throw new NoSuchElementException();
    }

    @Override
    public E pollFirst()
    {
        final ReentrantLock lock = this.lock;
        lock.lock();
        E result;
        try
        {
            result = removeAt(0);
        }
        finally
        {
            lock.unlock();
        }
        return result;
    }

    @Override
    public E pollLast()
    {
        final ReentrantLock lock = this.lock;
        lock.lock();
        E result;
        try
        {
            int indexMax = indexOfLargerChild(deque, size, 0, comparator);
            int indexRemove = indexMax>0?indexMax:0;
            result = removeAt(indexRemove);
        }
        finally
        {
            lock.unlock();
        }
        return result;
    }

    @Override
    public E getFirst()
    {
        E x = peekFirst();
        if (x != null)
            return x;
        else
            throw new NoSuchElementException();
    }

    @Override
    public E getLast()
    {
        E x = peekLast();
        if (x != null)
            return x;
        else
            throw new NoSuchElementException();
    }

    @Override
    @SuppressWarnings("unchecked")
    public E peekFirst()
    {
        final ReentrantLock lock = this.lock;
        lock.lock();
        E result;
        try
        {
            result = size > 0 ? (E) deque[0] : null;
        }
        finally
        {
            lock.unlock();
        }
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public E peekLast()
    {
        final ReentrantLock lock = this.lock;
        lock.lock();
        E result = null;
        try
        {
            if (size > 0)
            {
                int indexMax = indexOfLargerChild(deque, size, 0, comparator);
                if (indexMax > 0)
                    result = (E) deque[indexMax];
                else
                    result = (E) deque[0];
            }
        }
        finally
        {
            lock.unlock();
        }
        return result;
    }

    @Override
    public void addFirst(E element)
    {
        add(element);
    }

    @Override
    public void addLast(E element)
    {
        add(element);
    }

    @Override
    public boolean offerFirst(E element)
    {
        return offer(element);
    }

    @Override
    public boolean offerLast(E element)
    {
        return offer(element);
    }

    @Override
    public void putFirst(E element) throws InterruptedException
    {
        offer(element);
    }

    @Override
    public void putLast(E element) throws InterruptedException
    {
        offer(element);
    }

    @Override
    public boolean offerFirst(E element, long timeout, TimeUnit unit) throws InterruptedException
    {
        return offer(element);
    }

    @Override
    public boolean offerLast(E element, long timeout, TimeUnit unit) throws InterruptedException
    {
        return offer(element);
    }

    @Override
    public E takeFirst() throws InterruptedException
    {
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        E result;
        try
        {
            while((result = removeAt(0)) == null)
                notEmpty.await();
        }
        finally
        {
            lock.unlock();
        }
        return result;
    }

    @Override
    public E takeLast() throws InterruptedException
    {
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        E result;
        try
        {
            int indexMax = indexOfLargerChild(deque, size, 0, comparator);
            int indexRemove = indexMax>0?indexMax:0;
            while((result = removeAt(indexRemove)) == null)
                notEmpty.await();
        }
        finally
        {
            lock.unlock();
        }
        return result;
    }

    @Override
    public E pollFirst(long timeout, TimeUnit unit) throws InterruptedException
    {
        long nanos = unit.toNanos(timeout);
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        E result;
        try
        {
            while((result = removeAt(0))== null && nanos>0)
                nanos = notEmpty.awaitNanos(nanos);
        }
        finally
        {
            lock.unlock();
        }
        return result;
    }

    @Override
    public E pollLast(long timeout, TimeUnit unit) throws InterruptedException
    {
        long nanos = unit.toNanos(timeout);
        final ReentrantLock lock = this.lock;
        lock.lock();
        E result;
        try
        {
            int indexMax = indexOfLargerChild(deque, size, 0, comparator);
            int indexRemove = indexMax>0?indexMax:0;
            while((result = removeAt(indexRemove))== null && nanos>0)
                nanos = notEmpty.awaitNanos(nanos);
        }
        finally
        {
            lock.unlock();
        }
        return result;
    }

    @Override
    public boolean removeFirstOccurrence(Object o)
    {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try
        {
            int firstIndex = -1;
            for (int count = 0; count < size; count++)
            {
                if (deque[count].equals(o))
                {
                    firstIndex = count;
                    break;
                }
            }
            if (firstIndex >= 0)
            {
                removeAt(firstIndex);
                return true;
            }
            else
                return false;
        }
        finally
        {
            lock.unlock();
        }
    }

    @Override
    public boolean removeLastOccurrence(Object o)
    {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try
        {
            int lastIndex = -1;
            for (int count = size - 1; count >= 0; count--)
            {
                if (deque[count].equals(o))
                {
                    lastIndex = count;
                    break;
                }
            }
            if (lastIndex >= 0)
            {
                removeAt(lastIndex);
                return true;
            }
            else
                return false;
        }
        finally
        {
            lock.unlock();
        }
    }

    public boolean remove(Object o)
    {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try
        {
            int i = indexOf(o, deque, size);
            if (i == -1)
                return false;
            else
            {
                removeAt(i);
                return true;
            }
        }
        finally
        {
            lock.unlock();
        }
    }

    @Override
    public boolean offer(E element)
    {
        if (element == null)
            throw new NullPointerException();
        final ReentrantLock lock = this.lock;
        lock.lock();
        int index, cap;
        Object[] array;
        while ((index = size) >= (cap = (array = deque).length))
            tryGrow(array, cap);
        try
        {
            size = index + 1;
            if (index == 0)
                deque[0] = element;
            else
            {
                deque[index] = element;
                if (comparator == null)
                    bubbleUp(deque, size, index);
                else
                    bubbleUpComparator(deque, size, index, comparator);
            }
            notEmpty.signal();
        }
        finally
        {
            lock.unlock();
        }
        return true;
    }

    @Override
    public void put(E element) throws InterruptedException
    {
        offer(element);
    }

    public boolean add(E element)
    {
        return offer(element);
    }

    @Override
    public boolean offer(E element, long timeout, TimeUnit unit) throws InterruptedException
    {
        return offer(element);
    }

    @Override
    public E poll()
    {
        return pollFirst();
    }

    @Override
    public E take() throws InterruptedException
    {
        return takeFirst();
    }

    @Override
    public E poll(long timeout, TimeUnit unit) throws InterruptedException
    {
        return pollFirst(timeout, unit);
    }

    @Override
    public E peek()
    {
        return peekFirst();
    }

    @Override
    public void push(E e)
    {
        throw new UnsupportedOperationException("Cannot use a priority blocking deque as a stack");

    }

    @Override
    public E pop()
    {
        throw new UnsupportedOperationException("Cannot use a priority blocking deque as a stack");
    }

    /**
     * Returns an iterator over the elements in this queue. The
     * iterator does not return the elements in any particular order.
     *
     * <p>The returned iterator is a "weakly consistent" iterator that
     * will never throw {@link java.util.ConcurrentModificationException
     * ConcurrentModificationException}, and guarantees to traverse
     * elements as they existed upon construction of the iterator, and
     * may (but is not guaranteed to) reflect any modifications
     * subsequent to construction.
     *
     * @return an iterator over the elements in this queue
     */
    @Override
    public Iterator<E> iterator()
    {
        return new Itr(toArray(), false);
    }

    /**
     * Returns an iterator over the elements in this queue. The
     * iterator does not return the elements in any particular order.
     *
     * <p>The returned iterator is a "weakly consistent" iterator that
     * will never throw {@link java.util.ConcurrentModificationException
     * ConcurrentModificationException}, and guarantees to reverse-traverse
     * elements as they existed upon construction of the iterator, and
     * may (but is not guaranteed to) reflect any modifications
     * subsequent to construction.
     *
     * @return an iterator over the elements in this queue
     */
    @Override
    public Iterator<E> descendingIterator()
    {
        return new Itr(toArray(), true);
    }

    /**
     * Snapshot iterator that works off copy of underlying q array.
     */
    final class Itr implements Iterator<E>
    {
        final Object[] array; // Array of all elements
        int cursor; // index of next element to return;
        int lastRet; // index of last element, or -1 if no such
        boolean desc;

        Itr(Object[] array, boolean desc)
        {
            lastRet = -1;
            this.array = array;
            this.desc = desc;
            if (desc)
                cursor = this.array.length - 1;
        }

        public boolean hasNext()
        {
            return (!desc && cursor < array.length) || (desc && cursor >= 0);
        }

        public E next()
        {
            if (!desc)
                return nextAsc();
            if (desc)
                return nextDesc();
            throw new NoSuchElementException();
        }

        public void remove()
        {
            if (lastRet < 0)
                throw new IllegalStateException();
            PriorityBlockingDeque.this.removeEQ(array[lastRet]);
            lastRet = -1;
        }

        @SuppressWarnings ("unchecked")
        private E nextAsc()
        {
            if (cursor < array.length)
                return (E) array[lastRet = cursor++];
            throw new NoSuchElementException();
        }

        @SuppressWarnings ("unchecked")
        private E nextDesc()
        {
            if (cursor >= 0)
                return (E) array[lastRet = cursor--];
            throw new NoSuchElementException();
        }
    }

    /**
     * Identity-based version for use in Itr.remove
     */
    private void removeEQ(Object o)
    {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try
        {
            Object[] array = deque;
            int n = size;
            for (int i = 0; i < n; i++)
            {
                if (o == array[i])
                {
                    removeAt(i);
                    break;
                }
            }
        }
        finally
        {
            lock.unlock();
        }
    }

    @Override
    public int size()
    {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try
        {
            return size;
        }
        finally
        {
            lock.unlock();
        }
    }

    public Comparator<? super E> comparator()
    {
        return comparator;
    }

    public boolean contains(Object o)
    {
        int index;
        final ReentrantLock lock = this.lock;
        lock.lock();
        try
        {
            index = indexOf(o, deque, size);
        }
        finally
        {
            lock.unlock();
        }
        return index != -1;
    }

    public Object[] toArray()
    {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try
        {
            return Arrays.copyOf(deque, size);
        }
        finally
        {
            lock.unlock();
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a)
    {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try
        {
            if (a.length < size)
                // Make a new array of a's runtime type, but my contents:
                return (T[]) Arrays.copyOf(deque, size, a.getClass());
            System.arraycopy(deque, 0, a, 0, size);
            if (a.length > size)
                a[size] = null;
            return a;
        }
        finally
        {
            lock.unlock();
        }
    }

    /**
     * Saves the state of the instance to a stream (that is, serializes it).
     *
     * @serialData The length of the array backing the instance is emitted
     *             (int), followed by all of its elements (each an
     *             {@code Object}) in the proper order.
     * @param s
     *            the stream
     */
    private void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException
    {
        lock.lock();
        try
        {
            // Write out element count, and any hidden stuff
            s.defaultWriteObject();

            // Write out array length, for compatibility with 1.5 version
            s.writeInt(Math.max(2, size + 1));

            // Write out all elements in the "proper order".
            for (int i = 0; i < size; i++)
                s.writeObject(deque[i]);
        }
        finally
        {
            lock.unlock();
        }
    }

    /**
     * Reconstitutes the {@code PriorityQueue} instance from a stream (that is,
     * deserializes it).
     *
     * @param s
     *            the stream
     */
    private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException
    {
        // Read in size, and any hidden stuff
        s.defaultReadObject();

        // Read in (and discard) array length
        s.readInt();

        deque = new Object[size];

        // Read in all elements.
        for (int i = 0; i < size; i++)
            deque[i] = s.readObject();
    }

    @SuppressWarnings ("unchecked")
    public String toString()
    {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try
        {
            int n = size;
            if (n == 0)
                return "[]";
            StringBuilder sb = new StringBuilder();
            sb.append('[');
            for (int i = 0; i < n; ++i)
            {
                E e = (E) deque[i];
                sb.append(e == this ? "(this Collection)" : e);
                if (i != n - 1)
                    sb.append(',').append(' ');
            }
            return sb.append(']').toString();
        }
        finally
        {
            lock.unlock();
        }
    }

    private static int indexOf(Object o, Object[] deque, int size)
    {
        if (o != null)
        {
            for (int i = 0; i < size; i++)
                if (o.equals(deque[i]))
                    return i;
        }
        return -1;
    }

    // min-max heap implementation

    @SuppressWarnings("unchecked")
    private E removeAt(int index)
    {
        if (size > 0)
        {
            E obj = (E) deque[index];
            size--;
            if (size > 0)
            {
                deque[index] = deque[size];
                deque[size] = null;
                trickleDown(deque, size, index, comparator);
            }
            else
                deque[index] = null;
            return obj;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static <T> void bubbleUp(final Object[] deque, final int size, int index)
    {
        int parentIndex = getParentIndex(index);
        if (Level.MIN.equals(getLevel(size, index)))
        {
            if (index > 0 && ((Comparable<? super T>) deque[index]).compareTo((T) deque[parentIndex]) > 0)
            {
                swap(deque, size, index, parentIndex);
                bubbleUpMax(deque, size, parentIndex);
            }
            else
            {
                bubbleUpMin(deque, size, index);
            }
        }
        else
        {
            if (index > 0 && ((Comparable<? super T>) deque[index]).compareTo((T) deque[parentIndex]) < 1)
            {
                swap(deque, size, index, parentIndex);
                bubbleUpMin(deque, size, parentIndex);
            }
            else
            {
                bubbleUpMax(deque, size, index);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> void bubbleUpComparator(final Object[] deque, final int size, int index, final Comparator<? super T> comparator)
    {
        int parentIndex = getParentIndex(index);
        if (Level.MIN.equals(getLevel(size, index)))
        {
            if (index > 0 && comparator.compare((T) deque[index], (T) deque[parentIndex]) > 0)
            {
                swap(deque, size, index, parentIndex);
                bubbleUpMaxComparator(deque, size, parentIndex, comparator);
            }
            else
            {
                bubbleUpMinComparator(deque, size, index, comparator);
            }
        }
        else
        {
            if (index > 0 && comparator.compare((T) deque[index], (T) deque[parentIndex]) < 1)
            {
                swap(deque, size, index, parentIndex);
                bubbleUpMinComparator(deque, size, parentIndex, comparator);
            }
            else
            {
                bubbleUpMaxComparator(deque, size, index, comparator);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> void bubbleUpMax(final Object[] deque, final int size, int index)
    {
        int grandParentIndex = getParentIndex(getParentIndex(index));
        while (grandParentIndex >= 0 && ((Comparable<? super T>) deque[index]).compareTo((T) deque[grandParentIndex]) > 0)
        {
            swap(deque, size, index, grandParentIndex);
            index = grandParentIndex;
            grandParentIndex = getParentIndex(getParentIndex(grandParentIndex));
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> void bubbleUpMaxComparator(final Object[] deque, final int size, int index, final Comparator<? super T> comparator)
    {
        int grandParentIndex = getParentIndex(getParentIndex(index));
        while (grandParentIndex >= 0 && comparator.compare((T) deque[index], (T) deque[grandParentIndex]) > 0)
        {
            swap(deque, size, index, grandParentIndex);
            index = grandParentIndex;
            grandParentIndex = getParentIndex(getParentIndex(grandParentIndex));
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> void bubbleUpMin(final Object[] deque, final int size, int index)
    {
        int grandParentIndex = getParentIndex(getParentIndex(index));
        while (grandParentIndex >= 0 && ((Comparable<? super T>) deque[index]).compareTo((T) deque[grandParentIndex]) < 1)
        {
            swap(deque, size, index, grandParentIndex);
            index = grandParentIndex;
            grandParentIndex = getParentIndex(getParentIndex(grandParentIndex));
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> void bubbleUpMinComparator(final Object[] deque, final int size, int index, final Comparator<? super T> comparator)
    {
        int grandParentIndex = getParentIndex(getParentIndex(index));
        while (grandParentIndex >= 0 && comparator.compare((T) deque[index], (T) deque[grandParentIndex]) < 1)
        {
            swap(deque, size, index, grandParentIndex);
            index = grandParentIndex;
            grandParentIndex = getParentIndex(getParentIndex(grandParentIndex));
        }
    }

    private static <T> void trickleDown(final Object[] deque, final int size, int index, final Comparator<? super T> comparator)
    {
        if (Level.MIN.equals(getLevel(size, index)))
        {
            if (comparator == null)
                trickleDownMin(deque, size, index);
            else
                trickleDownMinComparator(deque, size, index, comparator);
        }
        else
        {
            if (comparator == null)
                trickleDownMax(deque, size, index);
            else
                trickleDownMaxComparator(deque, size, index, comparator);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> void trickleDownMin(final Object[] deque, final int size, int index)
    {
        while (index >= 0 && (isLeftChildPresent(deque, size, index) || isRightChildPresent(deque, size, index)))
        {
            int iSmallestGc = indexOfSmallestGrandChild(deque, size, index, null);
            int iSmallestCh = indexOfSmallerChild(deque, size, index, null);
            if (iSmallestGc > 0 && ((Comparable<? super T>) deque[iSmallestGc]).compareTo((T) deque[index]) < 1)
            {
                swap(deque, size, index, iSmallestGc);
                int parent = getParentIndex(iSmallestGc);
                if (((Comparable<? super T>) deque[iSmallestGc]).compareTo((T) deque[parent]) > 0)
                    swap(deque, size, iSmallestGc, parent);
            }
            if (iSmallestCh > 0 && ((Comparable<? super T>) deque[iSmallestCh]).compareTo((T) deque[index]) < 1)
                swap(deque, size, index, iSmallestCh);
            index = iSmallestGc;
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> void trickleDownMinComparator(final Object[] deque, final int size, int index, final Comparator<? super T> comparator)
    {
        while (index >= 0 && (isLeftChildPresent(deque, size, index) || isRightChildPresent(deque, size, index)))
        {
            int iSmallestGc = indexOfSmallestGrandChild(deque, size, index, comparator);
            int iSmallestCh = indexOfSmallerChild(deque, size, index, comparator);
            if (iSmallestGc > 0 && comparator.compare((T) deque[iSmallestGc], (T) deque[index]) < 1)
            {
                swap(deque, size, index, iSmallestGc);
                int parent = getParentIndex(iSmallestGc);
                if (comparator.compare((T) deque[iSmallestGc], (T) deque[parent]) > 0)
                    swap(deque, size, iSmallestGc, parent);
            }
            if (iSmallestCh > 0 && comparator.compare((T) deque[iSmallestCh], (T) deque[index]) < 1)
                swap(deque, size, index, iSmallestCh);
            index = iSmallestGc;
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> void trickleDownMax(final Object[] deque, final int size, int index)
    {
        while (index >= 0 && (isLeftChildPresent(deque, size, index) || isRightChildPresent(deque, size, index)))
        {
            int iLargestGc = indexOfLargestGrandChild(deque, size, index, null);
            int iLargestCh = indexOfLargerChild(deque, size, index, null);
            if (iLargestGc > 0 && ((Comparable<? super T>) deque[iLargestGc]).compareTo((T) deque[index]) > 0)
            {
                swap(deque, size, index, iLargestGc);
                int parent = getParentIndex(iLargestGc);
                if (((Comparable<? super T>) deque[iLargestGc]).compareTo((T) deque[parent]) < 1)
                    swap(deque, size, iLargestGc, parent);
            }
            if (iLargestCh > 0 && ((Comparable<? super T>) deque[iLargestCh]).compareTo((T) deque[index]) > 0)
                swap(deque, size, index, iLargestCh);
            index = iLargestGc;
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> void trickleDownMaxComparator(final Object[] deque, final int size, int index, final Comparator<? super T> comparator)
    {
        while (index >= 0 && (isLeftChildPresent(deque, size, index) || isRightChildPresent(deque, size, index)))
        {
            int iLargestGc = indexOfLargestGrandChild(deque, size, index, comparator);
            int iLargestCh = indexOfLargerChild(deque, size, index, comparator);
            if (iLargestGc > 0 && comparator.compare((T) deque[iLargestGc], (T) deque[index]) > 0)
            {
                swap(deque, size, index, iLargestGc);
                int parent = getParentIndex(iLargestGc);
                if (comparator.compare((T) deque[iLargestGc], (T) deque[parent]) < 1)
                    swap(deque, size, iLargestGc, parent);
            }
            if (iLargestCh > 0 && comparator.compare((T) deque[iLargestCh], (T) deque[index]) > 0)
                swap(deque, size, index, iLargestCh);
            index = iLargestGc;
        }
    }

    private static Level getLevel(final int size, int index)
    {
        int noOfElements = 0;
        int depth = 0;
        for (; depth < size; depth++)
        {
            noOfElements += Math.pow(2, depth);
            if ((index - noOfElements) < 0)
                break;
        }

        return depth % 2 == 0 ? Level.MIN : Level.MAX;
    }

    private static boolean isLeftChildPresent(final Object[] deque, final int size, final int index)
    {
        int leftChildIndex = getLeftChildIndex(index);
        return (leftChildIndex < size && deque[leftChildIndex] != null);
    }

    private static boolean isRightChildPresent(final Object[] deque, final int size, final int index)
    {
        int rightChildIndex = getRightChildIndex(index);
        return (rightChildIndex < size && deque[rightChildIndex] != null);
    }

    private static int getParentIndex(int childIndex)
    {
        return childIndex > 0 ? (int) ((childIndex - 1) / 2) : -1;
    }

    private static int getLeftChildIndex(int parentIndex)
    {
        return (int) (2 * parentIndex + 1);
    }

    private static int getRightChildIndex(int parentIndex)
    {
        return (int) (2 * (parentIndex + 1));
    }

    private static Object[][] getChildren(final Object[] deque, final int size, int index)
    {
        Object[][] children = new Object[2][2];
        int leftChildIndex = getLeftChildIndex(index);
        int rightChildIndex = getRightChildIndex(index);
        if (isLeftChildPresent(deque, size, index))
        {
            children[0][0] = deque[leftChildIndex];
            children[1][0] = leftChildIndex;
        }
        if (isRightChildPresent(deque, size, index))
        {
            children[0][1] = deque[rightChildIndex];
            children[1][1] = rightChildIndex;
        }
        return children;
    }

    @SuppressWarnings("unchecked")
    private static <T> int indexOfSmallerChild(final Object[] deque, final int size, int index, final Comparator<? super T> comparator)
    {
        Object[][] children = getChildren(deque, size, index);
        if (children[0][0] == null && children[0][1] == null)
            return -1;
        else if (children[0][0] == null)
            return (int) children[1][1];
        else if (children[0][1] == null)
            return (int) children[1][0];
        else if (comparator == null)
        {
            if (((Comparable<? super T>) children[0][0]).compareTo((T) children[0][1]) < 1)
                return (int) children[1][0];
            else
                return (int) children[1][1];
        }
        else
        {
            if (comparator.compare((T) children[0][0], (T) children[0][1]) < 1)
                return (int) children[1][0];
            else
                return (int) children[1][1];
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> int indexOfLargerChild(final Object[] deque, final int size, int index, final Comparator<? super T> comparator)
    {
        Object[][] children = getChildren(deque, size, index);
        if (children[0][0] == null && children[0][1] == null)
            return -1;
        else if (children[0][0] == null)
            return (int) children[1][1];
        else if (children[0][1] == null)
            return (int) children[1][0];
        else if (comparator == null)
        {
            if (((Comparable<? super T>) children[0][0]).compareTo((T) children[0][1]) > 0)
                return (int) children[1][0];
            else
                return (int) children[1][1];
        }
        else
        {
            if (comparator.compare((T) children[0][0], (T) children[0][1]) > 0)
                return (int) children[1][0];
            else
                return (int) children[1][1];
        }
    }

    private static Object[][] getGrandChildren(final Object[] deque, final int size, int index)
    {
        Object[][] grandChildren = new Object[2][4];
        int leftChildIndex = getLeftChildIndex(index);
        int rightChildIndex = getRightChildIndex(index);
        int gcll = getLeftChildIndex(leftChildIndex), gclr = getRightChildIndex(leftChildIndex), gcrl = getLeftChildIndex(rightChildIndex), gcrr = getRightChildIndex(rightChildIndex);
        if (isLeftChildPresent(deque, size, index))
        {
            if (isLeftChildPresent(deque, size, leftChildIndex))
            {
                grandChildren[0][0] = deque[gcll];
                grandChildren[1][0] = gcll;
            }
            if (isRightChildPresent(deque, size, leftChildIndex))
            {
                grandChildren[0][1] = deque[gclr];
                grandChildren[1][1] = gclr;
            }
        }
        if (isRightChildPresent(deque, size, index))
        {
            if (isLeftChildPresent(deque, size, rightChildIndex))
            {
                grandChildren[0][2] = deque[gcrl];
                grandChildren[1][2] = gcrl;
            }
            if (isRightChildPresent(deque, size, rightChildIndex))
            {
                grandChildren[0][3] = deque[gcrr];
                grandChildren[1][3] = gcrr;
            }
        }
        return grandChildren;
    }

    @SuppressWarnings("unchecked")
    private static <T> int indexOfSmallestGrandChild(final Object[] deque, final int size, int index, final Comparator<? super T> comparator)
    {
        Object[][] grandChildren = getGrandChildren(deque, size, index);
        Object smallest = grandChildren[0][0];
        Object smallestIndex = grandChildren[1][0];
        int count = 1;
        for (; count < grandChildren[0].length; count++)
        {
            if (smallest == null)
            {
                smallest = grandChildren[0][count];
                smallestIndex = grandChildren[1][count];
            }
            else if (grandChildren[0][count] != null)
            {
                if (comparator == null)
                {
                    if (((Comparable<? super T>) smallest).compareTo((T) grandChildren[0][count]) > 0)
                    {
                        smallest = grandChildren[0][count];
                        smallestIndex = grandChildren[1][count];
                    }
                }
                else
                {
                    if (comparator.compare((T) smallest, (T) grandChildren[0][count]) > 0)
                    {
                        smallest = grandChildren[0][count];
                        smallestIndex = grandChildren[1][count];
                    }
                }
            }
        }

        return smallestIndex != null ? (int) smallestIndex : -1;
    }

    @SuppressWarnings("unchecked")
    private static <T> int indexOfLargestGrandChild(final Object[] deque, final int size, int index, final Comparator<? super T> comparator)
    {
        Object[][] grandChildren = getGrandChildren(deque, size, index);
        Object largest = grandChildren[0][0];
        Object largestIndex = grandChildren[1][0];
        int count = 1;
        for (; count < grandChildren[0].length; count++)
        {
            if (largest == null)
            {
                largest = grandChildren[0][count];
                largestIndex = grandChildren[1][count];
            }
            else if (grandChildren[0][count] != null)
            {
                if (comparator == null)
                {
                    if (((Comparable<? super T>) largest).compareTo((T) grandChildren[0][count]) < 0)
                    {
                        largest = grandChildren[0][count];
                        largestIndex = grandChildren[1][count];
                    }
                }
                else
                {
                    if (comparator.compare((T) largest, (T) grandChildren[0][count]) < 0)
                    {
                        largest = grandChildren[0][count];
                        largestIndex = grandChildren[1][count];
                    }
                }
            }
        }

        return largestIndex != null ? (int) largestIndex : -1;
    }

    private static void swap(final Object[] deque, final int size, int index1, int index2)
    {
        if (index1 < 0 || index1 >= size || index2 < 0 || index2 >= size)
            throw new IllegalArgumentException();
        Object temp = deque[index1];
        deque[index1] = deque[index2];
        deque[index2] = temp;
        temp = null;
    }

}
