
package domain;

import java.util.Collection;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

@Entity
@Access(AccessType.PROPERTY)
public class Customer extends Endorser implements Cloneable {

	private Collection<FixUpTask>	fixUpTasks;


	@Override
	public Object clone() {
		Object o = null;
		o = super.clone();
		return o;
	}

	@NotNull
	@OneToMany(mappedBy = "customer")
	public Collection<FixUpTask> getFixUpTasks() {
		return this.fixUpTasks;
	}

	public void setFixUpTasks(final Collection<FixUpTask> fixUpTasks) {
		this.fixUpTasks = fixUpTasks;
	}
}
