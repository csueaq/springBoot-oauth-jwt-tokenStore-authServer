package authenticationServer.service;

import authenticationServer.data.pojo.BasicUserInfo;
import authenticationServer.data.pojo.User;
import authenticationServer.data.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Izzy on 09/09/15.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService, Serializable{

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username);
    }

    public UserDetails createUser(User user) {
        return userRepository.save(user);
    }

    public BasicUserInfo getBasicUserInfoForToken(User user) {
        return new BasicUserInfo(user);
    }
}
