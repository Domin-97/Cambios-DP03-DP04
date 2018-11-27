
package domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

@Entity
@Access(AccessType.PROPERTY)
public class SocialProfile extends DomainEntity implements Cloneable{

	private String	nick;
	private String	socialNetworkName;
	private String	link;

	@Override
	public Object clone() {
		Object o = null;
		try {
			o = super.clone();
		} catch (CloneNotSupportedException ex) {
		}
		return o;
	}

	@NotBlank
	public String getNick() {
		return this.nick;
	}

	public void setNick(final String nick) {
		this.nick = nick;
	}
	@NotBlank
	public String getSocialNetworkName() {
		return this.socialNetworkName;
	}

	public void setSocialNetworkName(final String socialNetworkName) {
		this.socialNetworkName = socialNetworkName;
	}
	@NotBlank
	@URL
	public String getLink() {
		return this.link;
	}

	public void setLink(final String link) {
		this.link = link;
	}

}
