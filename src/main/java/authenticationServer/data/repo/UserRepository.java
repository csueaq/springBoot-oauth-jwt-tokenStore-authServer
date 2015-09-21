package authenticationServer.data.repo;

import authenticationServer.data.pojo.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

/**
 * Created by Izzy on 17/08/15.
 */
public interface UserRepository extends MongoRepository<User, String> {

    //List<User> findAll(Sort sort);
    User findByEmail(String email);
    User save(User user);
    //@Query("{'address.postcode' : ?0}")
    //public List<User> findUserByPostCode(String postcode);

}
