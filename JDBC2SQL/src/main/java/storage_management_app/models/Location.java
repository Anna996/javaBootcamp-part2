package storage_management_app.models;

import java.util.Objects;

public class Location {
	private Integer id;
	private String name;
	private String accessCode;

	public Location(Integer id, String name, String accessCode) {
		this.id = id;
		this.name = name;
		this.accessCode = accessCode;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAccessCode() {
		return accessCode;
	}

	public void setAccessCode(String accessCode) {
		this.accessCode = accessCode;
	}

	@Override
	public String toString() {
		return "Location [id=" + id + ", name=" + name + ", accessCode=" + accessCode + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Location other = (Location) obj;
		return Objects.equals(accessCode, other.accessCode) && Objects.equals(id, other.id)
				&& Objects.equals(name, other.name);
	}
}
