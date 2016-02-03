package com.example.hexi.canvastest.concurrent;

import java.io.Serializable;
import java.util.AbstractQueue;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedSet;

/**
 * Created by hexi on 15/11/18.
 */
public class PriorityDeque<E> extends AbstractQueue<E> implements Deque<E>, Serializable {

    private static final long serialVersionUID = -5410497035045299533L;

    private static final int DEFAULT_INITIAL_CAPACITY = 11;

    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    private static enum Level
    {
        MIN, MAX
    };

    private transient Object[] deque;

    private final Comparator<? super E> comparator;

    private int size = 0;

    private transient int modCount = 0;

    public PriorityDeque()
    {
        this(DEFAULT_INITIAL_CAPACITY, null);
    }

    public PriorityDeque(int initialCapacity)
    {
        this(initialCapacity, null);
    }

    public PriorityDeque(int initialCapacity, Comparator<? super E> comparator)
    {
        // Note: This restriction of at least one is not actually needed,
        // but continues for 1.5 compatibility
        if (initialCapacity < 1)
            throw new IllegalArgumentException();
        this.deque = new Object[initialCapacity];
        this.comparator = comparator;
    }

    @SuppressWarnings("unchecked")
    public PriorityDeque(Collection<? extends E> c)
    {
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
        else
        {
            this.comparator = null;
            addAll(c);
        }
    }

    @SuppressWarnings("unchecked")
    public PriorityDeque(PriorityDeque<? extends E> c)
    {
        this.comparator = (Comparator<? super E>) c.comparator();
        initFromPriorityDeque(c);
    }

    @SuppressWarnings("unchecked")
    public PriorityDeque(SortedSet<? extends E> c)
    {
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

    @Override
    public void addFirst(E e)
    {
        add(e);
    }

    @Override
    public void addLast(E e)
    {
        add(e);
    }

    @Override
    public boolean offerFirst(E e)
    {
        return offer(e);
    }

    @Override
    public boolean offerLast(E e)
    {
        return offer(e);
    }

    public E remove()
    {
        E x = poll();
        if (x != null)
            return x;
        else
            throw new NoSuchElementException();
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
        modCount++;
        return removeAt(0);
    }

    @Override
    public E pollLast()
    {
        modCount++;
        int indexMax = indexOfLargerChild(deque, size, 0, comparator);
        int indexRemove = indexMax>0?indexMax:0;
        return removeAt(indexRemove);
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
        if (size > 0)
        {
            return (E) deque[0];
        }
        else
            return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public E peekLast()
    {
        E result = null;
        if (size > 0)
        {
            int indexMax = indexOfLargerChild(deque, size, 0, comparator);
            int indexPeek = indexMax>0?indexMax:0;
            result = (E) deque[indexPeek];
        }
        return result;
    }

    @Override
    public boolean removeFirstOccurrence(Object o)
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
            modCount++;
            removeAt(firstIndex);
            return true;
        }
        else
            return false;
    }

    @Override
    public boolean removeLastOccurrence(Object o)
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
            modCount++;
            removeAt(lastIndex);
            return true;
        }
        else
            return false;
    }

    @Override
    public boolean offer(E element)
    {
        modCount++;
        if (element == null)
            throw new NullPointerException();
        int index = size;
        if (index >= deque.length)
            grow(index + 1);
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

        return true;
    }

    @Override
    public E poll()
    {
        return pollFirst();
    }

    @Override
    public E peek()
    {
        return peekFirst();
    }

    @Override
    public void push(E e)
    {
        throw new UnsupportedOperationException("Cannot use a priority deque as a stack");
    }

    @Override
    public E pop()
    {
        throw new UnsupportedOperationException("Cannot use a priority deque as a stack");
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

    /**
     * Removes a single instance of the specified element from this queue, if it
     * is present. More formally, removes an element {@code e} such that
     * {@code o.equals(e)}, if this queue contains one or more such elements.
     * Returns {@code true} if and only if this queue contained the specified
     * element (or equivalently, if this queue changed as a result of the call).
     *
     * @param o
     *            element to be removed from this queue, if present
     * @return {@code true} if this queue changed as a result of the call
     */
    public boolean remove(Object o)
    {
        int i = indexOf(o, deque, size);
        if (i == -1)
            return false;
        else
        {
            modCount++;
            removeAt(i);
            return true;
        }
    }

    /**
     * Returns {@code true} if this queue contains the specified element. More
     * formally, returns {@code true} if and only if this queue contains at
     * least one element {@code e} such that {@code o.equals(e)}.
     *
     * @param o
     *            object to be checked for containment in this queue
     * @return {@code true} if this queue contains the specified element
     */
    public boolean contains(Object o)
    {
        return indexOf(o, deque, size) != -1;
    }

    /**
     * Returns an array containing all of the elements in this queue. The
     * elements are in no particular order.
     *
     * <p>
     * The returned array will be "safe" in that no references to it are
     * maintained by this queue. (In other words, this method must allocate a
     * new array). The caller is thus free to modify the returned array.
     *
     * <p>
     * This method acts as bridge between array-based and collection-based APIs.
     *
     * @return an array containing all of the elements in this queue
     */
    public Object[] toArray()
    {
        return Arrays.copyOf(deque, size);
    }

    /**
     * Returns an array containing all of the elements in this queue; the
     * runtime type of the returned array is that of the specified array. The
     * returned array elements are in no particular order. If the queue fits in
     * the specified array, it is returned therein. Otherwise, a new array is
     * allocated with the runtime type of the specified array and the size of
     * this queue.
     *
     * <p>
     * If the queue fits in the specified array with room to spare (i.e., the
     * array has more elements than the queue), the element in the array
     * immediately following the end of the collection is set to {@code null}.
     *
     * <p>
     * Like the {@link #toArray()} method, this method acts as bridge between
     * array-based and collection-based APIs. Further, this method allows
     * precise control over the runtime type of the output array, and may, under
     * certain circumstances, be used to save allocation costs.
     *
     * <p>
     * Suppose <tt>x</tt> is a queue known to contain only strings. The
     * following code can be used to dump the queue into a newly allocated array
     * of <tt>String</tt>:
     *
     * <pre>
     * String[] y = x.toArray(new String[0]);
     * </pre>
     *
     * Note that <tt>toArray(new Object[0])</tt> is identical in function to
     * <tt>toArray()</tt>.
     *
     * @param a
     *            the array into which the elements of the queue are to be
     *            stored, if it is big enough; otherwise, a new array of the
     *            same runtime type is allocated for this purpose.
     * @return an array containing all of the elements in this queue
     * @throws ArrayStoreException
     *             if the runtime type of the specified array is not a supertype
     *             of the runtime type of every element in this queue
     * @throws NullPointerException
     *             if the specified array is null
     */
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a)
    {
        if (a.length < size)
            // Make a new array of a's runtime type, but my contents:
            return (T[]) Arrays.copyOf(deque, size, a.getClass());
        System.arraycopy(deque, 0, a, 0, size);
        if (a.length > size)
            a[size] = null;
        return a;
    }

    @SuppressWarnings("unchecked")
    private boolean removeAtIter(int index, boolean desc)
    {
        if(!desc)
        {
            if (index < size - 1)
            {
                E moved = (E) deque[size - 1];
                modCount++;
                removeAt(index);
                if (moved == deque[index])
                    return true;
            }
        }
        else
        {
            if(index >= 0)
            {
                removeAt(index);
            }
        }


        return false;
    }

    @Override
    public Iterator<E> descendingIterator()
    {
        return new Itr(true);
    }

    @Override
    public Iterator<E> iterator()
    {
        return new Itr(false);
    }

    private final class Itr implements Iterator<E>
    {
        private boolean desc = false;

        /**
         * Index (into queue array) of element to be returned by subsequent call
         * to next.
         */
        private int cursor = 0;

        /**
         * Index of element returned by most recent call to next, unless that
         * element came from the forgetMeNot list. Set to -1 if element is
         * deleted by a call to remove.
         */
        private int lastRet = -1;

        /**
         * The modCount value that the iterator believes that the backing Queue
         * should have. If this expectation is violated, the iterator has
         * detected concurrent modification.
         */
        private int expectedModCount = modCount;

        private Itr(boolean desc)
        {
            this.desc = desc;
            if (desc)
                cursor = size - 1;
        }

        public boolean hasNext()
        {
            return (!desc && cursor < size) || (desc && cursor >= 0);
        }

        public E next()
        {
            if (expectedModCount != modCount)
                throw new ConcurrentModificationException();
            if (!desc)
                return nextAsc();
            if (desc)
                return nextDesc();
            throw new NoSuchElementException();
        }

        public void remove()
        {
            if (expectedModCount != modCount)
                throw new ConcurrentModificationException();
            if (lastRet != -1)
            {
                boolean decrementCounter = PriorityDeque.this.removeAtIter(lastRet, desc);
                lastRet = -1;
                if (decrementCounter)
                {
                    cursor--;
                }
            }
            else
            {
                throw new IllegalStateException();
            }
            expectedModCount = modCount;
        }

        @SuppressWarnings("unchecked")
        private E nextAsc()
        {
            if (cursor < size)
                return (E) PriorityDeque.this.deque[lastRet = cursor++];
            throw new NoSuchElementException();
        }

        @SuppressWarnings("unchecked")
        private E nextDesc()
        {
            if (cursor >= 0)
                return (E) PriorityDeque.this.deque[lastRet = cursor--];
            throw new NoSuchElementException();
        }
    }

    @Override
    public int size()
    {
        return size;
    }

    private void grow(int minCapacity)
    {
        int oldCapacity = deque.length;
        // Double size if small; else grow by 50%
        int newCapacity = oldCapacity + ((oldCapacity < 64) ? (oldCapacity + 2) : (oldCapacity >> 1));
        // overflow-conscious code
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = hugeCapacity(minCapacity);
        deque = Arrays.copyOf(deque, newCapacity);
    }

    private static int hugeCapacity(int minCapacity)
    {
        if (minCapacity < 0) // overflow
            throw new OutOfMemoryError();
        return (minCapacity > MAX_ARRAY_SIZE) ? Integer.MAX_VALUE : MAX_ARRAY_SIZE;
    }

    public void clear()
    {
        modCount++;
        for (int i = 0; i < size; i++)
            deque[i] = null;
        size = 0;
    }

    /**
     * Returns the comparator used to order the elements in this queue, or
     * {@code null} if this queue is sorted according to the
     * {@linkplain Comparable natural ordering} of its elements.
     *
     * @return the comparator used to order this queue, or {@code null} if this
     *         queue is sorted according to the natural ordering of its elements
     */
    public Comparator<? super E> comparator()
    {
        return comparator;
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
        // Write out element count, and any hidden stuff
        s.defaultWriteObject();

        // Write out array length, for compatibility with 1.5 version
        s.writeInt(Math.max(2, size + 1));

        // Write out all elements in the "proper order".
        for (int i = 0; i < size; i++)
            s.writeObject(deque[i]);
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

    // min-max heap implementation

    @SuppressWarnings("unchecked")
    private E removeAt(int index)
    {
        if (index < 0 || index >= size)
            throw new IllegalArgumentException();
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
