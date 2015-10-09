package authenticationServer;

import authenticationServer.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.*;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import java.security.KeyPair;


/**
 * Created by Izzy on 05/09/15.
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServer extends AuthorizationServerConfigurerAdapter {

    private static final String RESOURCE_ID = "client_resource";

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Value("${oauth.client.id}")
    private String clientId;

    @Value("${oauth.client.secret}")
    private String clientSecret;

    @Value("${jwt.keystore.key}")
    private String jwtKeyStoreKey;

    @Value("${jwt.keystore.pass}")
    private String jwtKeyStoreKeyPass;

    @Value("${jwt.keystore.store.pass}")
    private String jwtKeyStorePass;

    @Value("${jwt.keystore.file.name}")
    private String jwtKeyStoreFile;


    @Bean
    public JwtTokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }
    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter jwtAccessTokenConverter =  new CustomTokenEnhancer();
        jwtAccessTokenConverter.setAccessTokenConverter(getDefaultAccessTokenConverter());
        KeyPair keyPair = new KeyStoreKeyFactory(
                new ClassPathResource(jwtKeyStoreFile), jwtKeyStorePass.toCharArray())
                .getKeyPair(jwtKeyStoreKey, jwtKeyStoreKeyPass.toCharArray());
        jwtAccessTokenConverter.setKeyPair(keyPair);
        return jwtAccessTokenConverter;
    }


    @Override // [2]
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                .tokenServices(tokenServices())
                .tokenStore(tokenStore())
                .authenticationManager(authenticationManager)
                .userDetailsService(userDetailsService)
                .accessTokenConverter(accessTokenConverter());
    }

    @Bean
    public AuthorizationServerTokenServices tokenServices() {
        CustomDefaultTokenServices defaultTokenServices = new CustomDefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        defaultTokenServices.setReuseRefreshToken(true);
        defaultTokenServices.setSupportRefreshToken(true);
        defaultTokenServices.setSupportRefreshToken(true);


        //same thing
        defaultTokenServices.setTokenEnhancer(accessTokenConverter());
        defaultTokenServices.setTokenConverter(accessTokenConverter()); // until there is a getTokenEnhancer
        return defaultTokenServices;
    }


    @Bean
    AccessTokenConverter getDefaultAccessTokenConverter(){
        DefaultAccessTokenConverter defaultAccessTokenConverter = new DefaultAccessTokenConverter();
        defaultAccessTokenConverter.setUserTokenConverter(getCustomUserAuthenticationConvertor());
        return defaultAccessTokenConverter;
    }

    @Bean
    UserAuthenticationConverter getCustomUserAuthenticationConvertor(){
        return new CustomUserAuthenticationConvertor();
    }

    @Override // [3]
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient(clientId)
                .authorizedGrantTypes("client_credentials", "password","refresh_token")
                .authorities("ROLE_CLIENT")
                .scopes("SIGNED_IN", "REMEMBERED")
                .resourceIds(RESOURCE_ID)
                .secret(clientSecret)
                .accessTokenValiditySeconds(3600)
                .refreshTokenValiditySeconds(3600*24*30);
    }



}
