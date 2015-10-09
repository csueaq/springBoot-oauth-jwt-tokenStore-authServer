package authenticationServer;

import authenticationServer.data.pojo.User;
import authenticationServer.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.ExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.util.Map;

/**
 * Created by Izzy on 09/10/15.
 */
public class CustomDefaultTokenServices extends DefaultTokenServices {

    private JwtAccessTokenConverter tokenConverter;

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    public JwtAccessTokenConverter getTokenConverter() {
        return tokenConverter;
    }

    public void setTokenConverter(JwtAccessTokenConverter tokenConverter) {
        this.tokenConverter = tokenConverter;
    }

    @Override
    protected boolean isExpired(OAuth2RefreshToken refreshToken) {

        if (super.isExpired(refreshToken) )
            return true;

        try {
            Map<String, Object> allProps = ((CustomTokenEnhancer) getTokenConverter()).decoder(refreshToken.getValue());

            // this could be anything, last password update time, password...
            String customerSecret = ((User) userDetailsService.loadUserByUsername(allProps.get("user_name").toString())).getPasswordUpdatedAt().getTime() + "";


            return !customerSecret.equals(allProps.get("user_secret").toString());
        }catch (Exception e){
            e.printStackTrace();
            return true;
        }


    }
}
