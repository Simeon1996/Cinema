package system.cinema.security;

public class AuthDetails {
    public static final String LOGIN_URL = "/authenticate";

    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String TOKEN_TYPE = "JWT";
    public static final String TOKEN_ISSUER = "cinema-api";
    public static final String TOKEN_AUDIENCE = "cinema-client";
    public static final int PASSWORD_STRENGTH = 10;

    public static final int JWT_TOKEN_EXP_TIME = 15 * 1000 * 60;

    private AuthDetails()
    {
        throw new IllegalStateException("You cannot create an instance of this class.");
    }
}
