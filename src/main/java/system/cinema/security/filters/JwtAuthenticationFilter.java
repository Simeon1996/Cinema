package system.cinema.security.filters;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import system.cinema.AppConfiguration;
import system.cinema.model.UserPrincipal;
import system.cinema.security.AuthDetails;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager)
    {
        this.authenticationManager = authenticationManager;

        setFilterProcessesUrl(AuthDetails.LOGIN_URL);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);

        return this.authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain chain,
        Authentication authResult
    ) throws IOException, ServletException {
        UserPrincipal user = (UserPrincipal) authResult.getPrincipal();
        WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(
            request.getServletContext()
        );

        assert webApplicationContext != null;
        AppConfiguration config = webApplicationContext.getBean(AppConfiguration.class);

        List<String> roles = user.getAuthorities()
            .stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());

        byte[] signKey = config.getJwtSecret().getBytes();

        String token = Jwts.builder()
            .signWith(Keys.hmacShaKeyFor(signKey), SignatureAlgorithm.HS512)
            .setHeaderParam("typ", AuthDetails.TOKEN_TYPE)
            .setIssuer(AuthDetails.TOKEN_ISSUER)
            .setAudience(AuthDetails.TOKEN_AUDIENCE)
            .setSubject(user.getUsername())
            .setExpiration(new Date(System.currentTimeMillis() + AuthDetails.JWT_TOKEN_EXP_TIME))
            .claim("rol", roles)
            .compact();

        response.addHeader(AuthDetails.TOKEN_HEADER, AuthDetails.TOKEN_PREFIX + token);
    }
}
