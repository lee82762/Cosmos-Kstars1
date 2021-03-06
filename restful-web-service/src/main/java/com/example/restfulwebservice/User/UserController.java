package com.example.restfulwebservice.User;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@RestController
public class UserController {
    private UserDaoService service;

    //생성자를 통한 의존성 주입
    public UserController(UserDaoService service){
        this.service=service;
    }

    @GetMapping("/users")
    public List<User> retrieveAllUsers(){
        return service.findAll();

    }

   //GET/users/1 or/users/10 ->String
    @GetMapping("/users/{id}")
    public EntityModel<User> retrieveUser(@PathVariable int id){
        //Ctrl+Art+V
        User user = service.findOne(id);

        //예외발생 코드
        if(user==null){
            throw new UserNotFoundException(String.format("ID[%s] not found",id));
        }

        //Heteoas
        //하나의 리소스에서 파생할수있는 여러가지 추가적인 작업도 확인가능 링크 붙여주기
        EntityModel<User>model=new EntityModel<>(user);
        WebMvcLinkBuilder linkTo = linkTo(methodOn(this.getClass()).retrieveAllUsers());

        model.add(linkTo.withRel("all-users"));

        return model;
    }

    @PostMapping("/users")
    //@RequestBody는 객체형태의 매개변수를 받기위함
    public ResponseEntity<User> creatUser(@Valid @RequestBody User user){
        User savedUser =service.save(user);

        URI location =ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedUser.getId())
                .toUri();
         return ResponseEntity.created(location).build();
    }

    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable int id){
        User user=service.deleteById(id);

        if(user==null){
            throw new UserNotFoundException(String.format("ID[%s] not found",id));
        }

    }
    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable int id, @RequestBody User user) {
        User updateUser = service.updateById(id, user);
        if (updateUser == null) {
            throw  new UserNotFoundException(String.format("ID[%s] is not Found", id));

        }
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(updateUser.getId())
                .toUri();
        return ResponseEntity.noContent().build();

    }




}
