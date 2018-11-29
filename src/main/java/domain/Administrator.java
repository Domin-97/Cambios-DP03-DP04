
package domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;

@Entity
@Access(AccessType.PROPERTY)
public class Administrator extends Actor implements Cloneable {

	@Override
	public Object clone() {
		Object o = null;
		o = super.clone();
		return o;
	}

}
