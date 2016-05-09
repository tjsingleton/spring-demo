package name.tjsingleton.todoapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.ParametersAreNonnullByDefault;

@RestController
@ParametersAreNonnullByDefault
public class TodoController {

    @Autowired
    TodoRepository todos;

    @RequestMapping(value = "/todo", method = RequestMethod.GET)
    private Collection<Todo> index(IndexFilter filter) {
        return todos.all(filter);
    }

    @RequestMapping(value = "/todo", method = RequestMethod.POST)
    private ResponseEntity<Todo> create(@RequestBody Todo aTodo) {
        todos.save(aTodo);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(aTodo.getId()).toUri());
        return new ResponseEntity<>(aTodo, headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/todo/{id}", method = RequestMethod.GET)
    private ResponseEntity<Todo> show(@PathVariable("id") String id) {
        Optional<Todo> aTodo = todos.findOne(UUID.fromString(id));
        if (aTodo.isPresent()) {
            return new ResponseEntity<>(aTodo.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/todo/{id}", method = RequestMethod.PUT)
    private ResponseEntity<Todo> update(@PathVariable("id") String id, @RequestBody Todo aTodo) {
        Optional<Todo> originalTodo = todos.findOne(UUID.fromString(id));
        if (originalTodo.isPresent()) {
            todos.save(aTodo);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/todo/{id}", method = RequestMethod.DELETE)
    private ResponseEntity<Todo> delete(@PathVariable("id") String id) {
        Optional<Todo> aTodo = todos.remove(UUID.fromString(id));
        if (aTodo.isPresent()) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
