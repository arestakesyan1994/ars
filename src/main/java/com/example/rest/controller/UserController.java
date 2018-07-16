package com.example.rest.controller;


import com.example.rest.dto.AuthenticationRequest;
import com.example.rest.dto.AuthenticationResponse;
import com.example.rest.dto.UserDto;
import com.example.rest.mail.EmailServiceImpl;
import com.example.rest.model.User;
import com.example.rest.repository.UserRepository;
import com.example.rest.security.JwtTokenUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/users")
public class UserController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailServiceImpl emailService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Value("${image.folder}")
    private String imageUploadDir;


    @ApiOperation(value = "all users", notes = "get all users")
    @GetMapping
    public List<User> users() {
        return userRepository.findAll();
    }


    @GetMapping("/names")
    public List<UserDto> userNames() {
        List<User> name = userRepository.findAll();
        List<UserDto> userDtos = new LinkedList<>();
        name.forEach(e -> userDtos.add(new UserDto(e.getId(), e.getUsername(), e.getNickname(), e.getPicUrl())));
        return userDtos;
    }


    @GetMapping("/{id}")
    public ResponseEntity getUserById(@PathVariable(name = "id") int id) {
        User one = userRepository.findById(id);
        if (one == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with " + id + " id no found");
        }
        return ResponseEntity.ok(one);
    }


    @PostMapping("/register")
    public ResponseEntity saveUser(@RequestBody User user) {
        if (userRepository.findAllByUsername(user.getUsername()) == null) {
            user.setPassword(user.getPassword());
            user.setToken(UUID.randomUUID().toString());
            userRepository.save(user);
            String url = String.format("http://http://localhost:8080/verify?token=%s&email=%s", user.getToken(), user.getUsername());
            String text = String.format("Dear %s Thank you, you have successfully registered to  our Evens_Tracker. Please visit by link in order to activate your profile.  %s", user.getNickname(),url);
            emailService.sendSipmleMessage(user.getUsername(),"Իրադարձությունների վերահսկում - ուղարկված է MobileApp-ից",text);
            return ResponseEntity.ok("created");
        }
        return ResponseEntity.badRequest().body("User with " + user.getUsername() + " already exist");
    }

    @PostMapping()
    public ResponseEntity saveUsers(@RequestBody User user) {
        if (userRepository.findAllByUsername(user.getUsername()) == null) {
            userRepository.save(user);
            return ResponseEntity.ok("created");
        }
        return ResponseEntity.badRequest().body("User with " + user.getUsername() + " already exist");
    }

    @PostMapping("/auth")
    public ResponseEntity users(@RequestBody AuthenticationRequest authenticationRequest) {
        User user = userRepository.findAllByUsername(authenticationRequest.getUsername());
        if (user != null ) {
            String token = jwtTokenUtil.generateToken(user.getUsername());
            return ResponseEntity.ok(AuthenticationResponse.builder()
                    .token(token)
                    .userDto(UserDto.builder()
                            .id(user.getId())
                            .username(user.getUsername())
                            .nikname(user.getNickname())
                            .picUrl(user.getPicUrl())
                            .build())
                    .build());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteUserById(@PathVariable(name = "id") int id,
                                         @AuthenticationPrincipal UserDetails userDetails) {
        try{
            System.out.println("user with " + userDetails.getUsername() + "trying to delete user by" + id);
            userRepository.delete(id);
            return ResponseEntity.ok("Delete");
        } catch (Exception e){
            return ResponseEntity.badRequest().body("user with " + id + "does not exist");
        }

    }

    @PostMapping("/{username}/{password}")
    public ResponseEntity getUserById(@PathVariable(name = "username") String username,
                                      @PathVariable(name= "password") String password) {
        User one = userRepository.findAllByUsernameAndPassword(username, password);
        if (one == null) {

            return ResponseEntity.ok(0);
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with " + username + " username not found");
        }
        char a = '"';


        return ResponseEntity.ok("{" + a +"success" + a +":" + a + "1" + a + ","   + a +"message" + a +":" + a + "URAAAAAAAAAA" + a +  "}");
//        return ResponseEntity.status(HttpStatus.FOUND).body("Login is ok");
    }

    @PutMapping("/{username}")
    public ResponseEntity update(@PathVariable String  username,
                                 @RequestBody User user) {
        User currentUser = userRepository.findAllByUsername(username);
        if(user.getUsername()==null){
            currentUser.setUsername(currentUser.getUsername());
        }else{
            currentUser.setUsername(user.getUsername());
        }
        if(user.getPassword()==null){
            currentUser.setPassword(currentUser.getPassword());
        }
        else{
            currentUser.setPassword(user.getPassword());
        }
        if(user.getNickname()==null){
            currentUser.setNickname(currentUser.getNickname());
        }else {
            currentUser.setNickname(user.getNickname());
        }
        if(user.getPicUrl()==null){
            currentUser.setPicUrl(currentUser.getPicUrl());
        }else {
            currentUser.setPicUrl(user.getPicUrl());
        }
        userRepository.save(currentUser);
        return ResponseEntity.ok("update");
    }
//    @PostMapping("/auth")
//    public ResponseEntity users(@RequestBody AuthenticationRequest authenticationRequest) {
//        User user = userRepository.findAllByUsername(authenticationRequest.User);
//        if (user != null && passwordEncoder.matches(authenticationRequest.getPassword(), user.getPassword())) {
//            String token = jwtTokenUtil.generateToken(user.getEmail());
//            return ResponseEntity.ok(AuthenticationResponse.builder()
//                    .token(token)
//                    .userDto(UserDto.builder()
//                            .id(user.getId())
//                            .name(user.getName())
//                            .surname(user.getSurname())
//                            .email(user.getEmail())
//                            .userType(user.getType())
//                            .build())
//                    .build());
//        }
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
//    }

//    @GetMapping("/verify")
//    public String verify(@RequestParam("token") String token, @RequestParam("username") String username){
//        User allByUsername = userRepository.findAllByUsername(username);
//        if(allByUsername!=null){
//            if(allByUsername.getToken()!=null && allByUsername.getToken().equals(token)){
//                allByUsername.setToken(null);
//                allByUsername.setVerify(true);
//                userRepository.save(allByUsername);
//            }
//        }
//        return "/index.html";
//    }

//    @PostMapping("/register")
//    public ResponseEntity Register(@RequestBody User users) {
//        System.out.println("barev");
//        User user = userRepository.findAllByUsername(users.getUsername());
//        if (user != null && passwordEncoder.matches(users.getPassword(), user.getPassword())) {
//            String token = jwtTokenUtil.generateToken(user.getUsername());
//            UserDto userDto = new UserDto();
//            userDto.setId(user.getId());
//            userDto.setUsername(user.getUsername());
//            userDto.setNikname(user.getNickname());
//            userDto.setPicUrl(user.getPicUrl());
//            AuthenticationResponse authenticationResponse = new AuthenticationResponse();
//            authenticationResponse.setToken(token);
//            authenticationResponse.setUserDto(userDto);
//            return ResponseEntity.ok(authenticationResponse);
//        }
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
//    }
}
