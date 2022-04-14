package by.sofy.game_cheats_store.service;

import by.sofy.game_cheats_store.dto.RefillRequest;
import by.sofy.game_cheats_store.dto.RegistrationRequest;
import by.sofy.game_cheats_store.dto.UserEditRequest;
import by.sofy.game_cheats_store.entity.User;
import by.sofy.game_cheats_store.entity.UserRoles;
import by.sofy.game_cheats_store.exceptions.BadRequestException;
import by.sofy.game_cheats_store.exceptions.CustomAuthenticationException;
import by.sofy.game_cheats_store.exceptions.NotFoundException;
import by.sofy.game_cheats_store.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.validation.Validator;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {

    private final ValidationService validationService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final Validator validator;

    @Value("${admin.login}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    @PostConstruct
    private void init(){
        Optional<User> optionalAdmin = userRepository.findByUsername(adminUsername);

        if (optionalAdmin.isPresent()){
            return;
        }

        User admin = new User();
        admin.setUsername(adminUsername);
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setRole(UserRoles.ROLE_ADMIN);
        userRepository.save(admin);
    }

    public List<User> findAllUsers(){
        return userRepository.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);

        if (user.isEmpty()){
            throw new UsernameNotFoundException("User not found");
        }

        return user.get();
    }

    public BigDecimal findBalanceForCurrentUser(Principal principal){

        BigDecimal value = BigDecimal.valueOf(0);

        Optional<User> optionalUser = Optional.empty();
        if (principal != null){
            optionalUser = userRepository.findByUsername(principal.getName());
        }

        if (optionalUser.isPresent()){
            value = optionalUser.get().getBalance();
        }

        return value;
    }

    public UserEditRequest findRequestById(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        UserEditRequest request = new UserEditRequest();
        request.setLogin(user.getUsername());
        request.setRoles(user.getRole().name());

        return request;
    }

    public void register(RegistrationRequest request){
        if (request.getPassword().isEmpty()){
            throw new BadRequestException("Password required");
        }

        if (!request.getPassword().equals(request.getReplyPassword())){
            throw new BadRequestException("Password mismatch");
        }

        validationService.checkValidation(request);

        if (userRepository.findByUsername(request.getLogin()).isPresent()){
            throw new BadRequestException("User with this username registered");
        }

        User user = new User();
        user.setUsername(request.getLogin());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);
    }

    public void register(UserEditRequest request){

        if (request.getPassword().isEmpty()){
            throw new BadRequestException("Password required");
        }

        if (!request.getPassword().equals(request.getReplyPassword())){
            throw new BadRequestException("Password mismatch");
        }

        validationService.checkValidation(request);

        if (userRepository.findByUsername(request.getLogin()).isPresent()){
            throw new BadRequestException("User with this username registered");
        }

        User user = new User();
        user.setUsername(request.getLogin());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user.setRole(UserRoles.valueOf(request.getRoles()));

        userRepository.save(user);
    }

    public void edit(Long id, UserEditRequest request){
        if (request.getPassword() != null){
            if (!request.getPassword().equals(request.getReplyPassword())){
                throw new BadRequestException("Password mismatch");
            }
        }

        validationService.checkValidation(request);

        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isEmpty()){
            throw new NotFoundException("Not fount user by id");
        }

        User user = optionalUser.get();

        checkUniqueUsername(request.getLogin(), user.getUsername());

        user.setUsername(request.getLogin());

        if (request.getPassword() != null){
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        user.setRole(UserRoles.valueOf(request.getRoles()));

        userRepository.save(user);
    }


    public void checkUniqueUsername(String username, String currentUsername){
        if (username.equals(currentUsername)){
            return;
        }
        if (userRepository.findByUsername(username).isPresent()){
            throw new BadRequestException("User with this username registered");
        }
    }

    public void refill(Principal principal, RefillRequest request){
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new CustomAuthenticationException("User not found"));

        user.setBalance(user.getBalance().add(request.getAmount()));

        userRepository.save(user);
    }

}
