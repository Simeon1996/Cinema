package system.cinema.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UserPrincipal implements UserDetails {

    private User user;

    public UserPrincipal(User user)
    {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(this.user.getRole().toString());
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        authorities.add(authority);

        return authorities;
    }

    @Override
    public String getPassword() {
        return this.user.getPassword();
    }

    @Override
    public String getUsername() {
        return this.user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // @TODO
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // @TODO
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // @TODO
    }

    @Override
    public boolean isEnabled() {
        return true; // @TODO
    }
}
