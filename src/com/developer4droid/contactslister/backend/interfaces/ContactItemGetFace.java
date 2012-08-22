package com.developer4droid.contactslister.backend.interfaces;

import java.util.List;

/**
 * ContactItemGetFace class
 *
 * @author alien_roger
 * @created at: 22.08.12 8:08
 */
public interface ContactItemGetFace<E,T> extends TaskUpdateInterface<T> {

	void updateContacts(List<E> itemsList);
}
