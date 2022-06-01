package my.service.services;

import my.service.entities.User;
import my.service.transporters.UserTrans;
import my.service.utilities.ResponseList;

public interface UserService {

    public ResponseList getAllUsers(String filter,
                                    String value,
                                    String startDate,
                                    String endDate,
                                    String sort,
                                    Integer pageNumber,
                                    Integer numberOfResults);

    public UserTrans getOneUser(String id);

    public User postUser(User user, String userData);

    public User patchUser(String id, User patchUser, String userData);

    public String deleteUser(String id, String userData);

}
