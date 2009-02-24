/*
 * Copyright (C) 2007, 2008 Siemens AG
 *
 * This program and its interfaces are free software;
 * you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.siemens.ct.exi.util;

import java.util.NoSuchElementException;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.2.20081014
 */

public class UnsynchronizedStack<E> {
	private transient E[] elements;
	private transient int size;

	/**
	 * Constructs an empty stack with an initial capacity sufficient to hold 16
	 * elements.
	 */
	@SuppressWarnings("unchecked")
	public UnsynchronizedStack() {
		elements = (E[]) new Object[16];
	}

	/**
	 * Inserts the specified element at the top of this stack.
	 * 
	 * @param e
	 *            the element to add
	 */
	public void addLast(E e) {
		elements[size++] = e;
		if (size >= elements.length)
			doubleCapacity();
	}

	/**
	 * Returns the element at the top of this stack.
	 * 
	 * @return The object at the top of this stack (the last item).
	 * 
	 * @exception NoSuchElementException
	 *                if this stack is empty.
	 */
	public E peekLast() {
		if (size == 0)
			throw new NoSuchElementException();
		return elements[size - 1];
	}

	/**
	 * Removes the object at the top of this stack and returns that object as
	 * the value of this function.
	 * 
	 * @return The object at the top of this stack (the last item).
	 * 
	 * @throws NoSuchElementException
	 *             if this stack is empty.
	 */
	public E removeLast() {
		if (size == 0)
			throw new NoSuchElementException();
		E x = elements[size - 1];
		elements[--size] = null;
		return x;
	}

	/**
	 * Replaces the object at the top of this stack and returns that object as
	 * the value of this function.
	 * 
	 * @return The object at the top of this stack (the last item).
	 * 
	 * @throws NoSuchElementException
	 *             if this stack is empty.
	 */
	public E replaceLast(E x) {
		if (size == 0)
			throw new NoSuchElementException();
		E y = elements[size - 1];
		elements[size - 1] = x;
		return y;
	}

	/**
	 * Returns the number of items on the stack
	 * 
	 * @return item count
	 */
	public int size() {
		return size;
	}

	/**
	 * Tests if this stack is empty.
	 * 
	 * @return <code>true</code> if and only if this stack contains no items;
	 *         <code>false</code> otherwise.
	 */
	public boolean isEmpty() {
		return size == 0;
	}

	public void clear() {
		for (int i = 0; i < size; i++)
			elements[i] = null;

		size = 0;
	}

	@SuppressWarnings("unchecked")
	private void doubleCapacity() {
		int n = elements.length;
		int newCapacity = n << 1;
		if (newCapacity < 0)
			throw new IllegalStateException("Sorry, stack too big");
		Object[] a = new Object[newCapacity];
		System.arraycopy(elements, 0, a, 0, n);
		elements = (E[]) a;
	}

}
