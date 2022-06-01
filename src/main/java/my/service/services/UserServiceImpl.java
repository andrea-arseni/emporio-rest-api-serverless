package my.service.services;

import my.service.entities.User;
import my.service.repositories.UserDAO;
import my.service.transporters.UserTrans;
import my.service.types.UserType;
import my.service.utilities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDAO userDAO;

    @Override
    @Transactional
    public ResponseList getAllUsers
            (String filter, String value, String startDate, String endDate,
             String sort, Integer pageNumber, Integer numberOfResults) {

        QueryClause whereClause = ListHandler.getQueryWhereClause(User.class, filter, "", 0, value, startDate, endDate, "", "");
        String sortClause = ListHandler.getSortClause(User.class, sort);

        List<User> users = this.userDAO.getAllUsers(whereClause, sortClause, pageNumber, numberOfResults);
        List<UserTrans> res = new ArrayList<>();
        for(User user : users){
            res.add(new UserTrans(user));
        }
        Long numberOfRes = this.userDAO.getListNumber(whereClause);
        return new ResponseList(numberOfRes, res);
    }

    @Override
    @Transactional
    public UserTrans getOneUser(String id) {
        User userFound = this.userDAO.getOneUser(id);
        if(userFound==null) throw new ItemNotFoundException("User con id "+id+" non presente");
        return new UserTrans(userFound);
    }

    /* Call disattivata - si crea lo user automaticamente al primo accesso */
    @Override
    @Transactional
    public User postUser(User user, String userData) {
        // se lo user non è admin non permettiamo la creazione nuovo user
        User currentUser = this.getUser(userData);
        if(!this.isUserAdmin(currentUser)) throw new ForbiddenException("User non abilitato ad effettuare l'operazione");
        user.setFirstAccess(LocalDateTime.now());
        user.setRole(UserType.NORMALE.name());
        return this.userDAO.addUser(user);
    }

    @Override
    @Transactional
    public User patchUser(String id, User patchUser, String userData) {
        // retrieve originalObject, if not found throw error
        User originalUser = userDAO.getOneUser(id);
        if(originalUser==null) throw new ItemNotFoundException("User con id "+id+" non presente");

        // if user diverso e non admin non permesso
        User currentUser = this.getUser(userData);

        if(!currentUser.getId().equalsIgnoreCase(originalUser.getId()) && !this.isUserAdmin(currentUser)){
            throw new ForbiddenException("User non abilitato ad effettuare l'operazione");
        }

        patchUser.setFirstAccess(null);

        ObjectPatcher.patchObject(User.class, originalUser, patchUser);

        return originalUser;
    }

    @Override
    @Transactional
    public String deleteUser(String id, String userData) {
        // check user che vuoi cancellare esista
        User user = this.userDAO.getOneUser(id);
        if(user==null) throw new ItemNotFoundException("User con id "+id+" non presente");
        // check che tu non sia lo stesso user, nel caso retrieve
        User currentUser = UserRetriever.retrieveUser(userData);
        if(!currentUser.getId().equalsIgnoreCase(user.getId())){
            currentUser = this.userDAO.getOneUser(currentUser.getId());
            if(!this.isUserAdmin(currentUser))
                throw new ForbiddenException("User non abilitato ad effettuare l'operazione");
        }
        this.userDAO.deleteUser(user);
        return "User con id "+id+" e nome "+user.getName()+" cancellato con successo";
    }

    private User getUser(String userData){
        User user = UserRetriever.retrieveUser(userData);
        User userFound = this.userDAO.getOneUser(user.getId());
        if(userFound==null) throw new ItemNotFoundException("User non trovato");
        return userFound;
    }

    private Boolean isUserAdmin(User user){
        return user.getRole().equalsIgnoreCase(UserType.ADMIN.name());
    }

}
