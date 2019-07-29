package tech.pauly.old.data.models

enum class StatusCode(private val code: Int) {

    PFAPI_OK(100),
    PFAPI_ERR_INVALID(200),
    PFAPI_ERR_NOENT(201),
    PFAPI_ERR_LIMIT(202),
    PFAPI_ERR_LOCATION(203),
    PFAPI_ERR_UNAUTHORIZED(300),
    PFAPI_ERR_AUTHFAIL(301),
    PFAPI_ERR_INTERNAL(999),
    ERR_LOCAL(1000),
    ERR_NO_ANIMALS(1001),
    ERR_NO_LOCATION(1002),
    ERR_FETCH_LOCATION(1002);

    companion object {
        fun fromInt(code: Int): StatusCode {
            return StatusCode.values()
                    .singleOrNull { it.code == code } ?: ERR_LOCAL
        }
    }
}
