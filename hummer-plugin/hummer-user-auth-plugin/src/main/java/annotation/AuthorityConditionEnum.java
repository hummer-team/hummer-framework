package annotation;

public enum AuthorityConditionEnum {
    ANY_OF(1),
    ALL_OF(2);

    private Integer value;

    private AuthorityConditionEnum(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }
}
