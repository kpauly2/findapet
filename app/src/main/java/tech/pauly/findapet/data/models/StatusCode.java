package tech.pauly.findapet.data.models;

public enum StatusCode {
    PFAPI_OK(100),
    PFAPI_ERR_INVALID(200),
    PFAPI_ERR_NOENT(201),
    PFAPI_ERR_LIMIT(202),
    PFAPI_ERR_LOCATION(203),
    PFAPI_ERR_UNAUTHORIZED(300),
    PFAPI_ERR_AUTHFAIL(301),
    PFAPI_ERR_INTERNAL(999),
    ERR_LOCAL(1000),
    ERR_NO_ANIMALS(1001);

    private int code;

    StatusCode(int code) {
        this.code = code;
    }

    public static StatusCode fromInt(int input) {
        for (StatusCode code : StatusCode.values()) {
            if (code.code == input) {
                return code;
            }
        }
        return ERR_LOCAL;
    }
}
