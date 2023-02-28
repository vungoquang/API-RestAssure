package utils;

import io.restassured.response.Response;
import org.hamcrest.Matcher;

import java.util.HashMap;
import java.util.Map;

public class Assertion {
    /**
     * Assert a Code from a Response plus getting UpStream Info
     * @param reason
     * @param actual
     * @param matcher
     */
    public static void assertResponseCode(String reason, Response actual, Matcher<APIResponseCode> matcher) {
        assertThat(actual, reason, APIResponseCode.fromCode(actual.statusCode()), matcher);
    }

    /**
     * Asserts a given actual value and includes the response UpStream info with the message.
     * @param response
     * @param reason
     * @param actual
     * @param matcher
     * @param <T>
     */
    public static <T> void assertThat(Response response, String reason, T actual, Matcher<T> matcher) {
        if (!matcher.matches(actual)) {
            throw new Error("Api wrong");
        }
    }

    public enum APIResponseCode {
        UNKNOWN_API_CODE(-1, "UNKNOWN_API_CODE"),
        OK (200, "OK"),
        CREATED(201, "Created"),
        BAD_REQUEST(400, "Bad request"),
        ITEM_NAME_INVALID(400, 1, "Item name invalid"),
        INVALID_REQUEST_PARAMETERS(400, 2, "Invalid input parameters in request"),
        UNAUTHORIZED(401, "Unauthorized"),
        WRONG_EMAIL_OR_PASSWORD(401, 1, "Your email or password is wrong"),
        AUTHORIZED_FAILURE(401, 2, "Authorized failure"),
        PASSWORD_TOKEN_EXPIRED(401, 3, "Password token has been expired"),
        FORBIDDEN(403, "Forbidden"),
        STORAGE_LIMIT_EXCEEDED(403, 1, "Account storage limit reached"),
        ACCESS_DENIED(403, 2, "Access Denied"),
        NOT_FOUND(404, "Not Found"),
        METHOD_NOT_ALLOWED(405, "Method not allowed"),
        CONFLICT(409, "Conflict"),
        SIMILAR_PATTERN_COMMENT(409, 1, "Similar Pattern Comments"),
        ITEM_NAME_IN_USE(409, 1, "Item with the same name already exists"),
        CONTENT_TYPE_ERROR(415, "Content type error"),
        REQUEST_RATE_LIMIT_EXCEEDED(419, "Request rate limit exceeded"),
        CLIENT_ERROR(498, "Client Error"),
        INTERNAL_SERVER_ERROR(500, "Internal server error"),
        BAD_GATEWAY(502, "Bad Gateway"),
        UNAVAILABLE(503, "Unavailable")
        ;

        private int code;
        private int subCode;
        private String upStreamInfo;
        private String desc;
        private final static Map<Integer, APIResponseCode> enumCodes = new HashMap<>();

        static {
            for (APIResponseCode code : APIResponseCode.values()) {
                enumCodes.put(code.getFullCode(), code);
            }
        }

        APIResponseCode(int code, String desc) {
            this(code, 0, desc);
        }

        APIResponseCode(int code, int subCode, String desc) {
            this.code = code;
            this.subCode = subCode;
            this.desc = desc;
        }

        public int getCode() {
            return code;
        }

        public int getSubCode() {
            return subCode;
        }

        public int getFullCode() {
            return (getSubCode() > 0) ? (getCode() * 10) + getSubCode() : getCode();
        }

        public String getDescription() {
            return desc;
        }

        public String getUpStreamInfo() {
            return upStreamInfo;
        }

        APIResponseCode setUpStreamInfo(String upStreamInfo) {
            this.upStreamInfo = upStreamInfo;
            return this;
        }

        @Override
        public String toString() {
            return getFullCode() + " (" + getDescription() + ")";
        }

        public static APIResponseCode fromCode(int code) {
            return fromSubCode(code, 0);
        }

        public static APIResponseCode fromSubCode(int code, int subCode) {
            APIResponseCode found = (subCode > 0) ? enumCodes.get(code * 10 + subCode) : enumCodes.get(code);
            if (found == null) {
                return UNKNOWN_API_CODE;
            }
            return found;
        }
    }
}
