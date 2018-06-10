package tool1.mapping;

public enum SupplierStatus {

	ACTIVE(11), BLACKLIST(12), DEACTIVE(13), DELETED(15), ONHOLD(16);

	private Integer value;

	private SupplierStatus(Integer value) {
		this.value = value;
	}

	public Integer getValue() {
		return value;
	}

}
