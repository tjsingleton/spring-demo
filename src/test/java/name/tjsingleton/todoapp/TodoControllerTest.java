package name.tjsingleton.todoapp;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

import name.tjsingleton.SpringDemoApplication;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SpringDemoApplication.class})
@WebAppConfiguration
public class TodoControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;
    private HttpMessageConverter converter;
    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {
        this.converter = Arrays.asList(converters)
                .stream()
                .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
                .findAny()
                .get();

        Assert.assertNotNull("the JSON message converter must not be null", this.converter);
    }

    @Before
    public void setUp() throws Exception {
        mockMvc = webAppContextSetup(webApplicationContext).build();
        todoRepository.clear();
    }

    @Test
    public void index_listsEntries() throws Exception {
        todoRepository.save(new Todo(UUID.fromString("45ec5bfa-7813-4565-acdd-2d8c5662e36b"), "Take out the trash"));

        mockMvc.perform(get("/todo"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is("45ec5bfa-7813-4565-acdd-2d8c5662e36b")))
                .andExpect(jsonPath("$[0].description", is("Take out the trash")));
    }

    @Test
    public void index_filterEntriesByID() throws Exception {
        todoRepository.save(new Todo(UUID.fromString("45ec5bfa-7813-4565-acdd-2d8c5662e36b"), "Take out the trash"));
        todoRepository.save(new Todo(UUID.fromString("d76fa7ee-9d30-4632-8fab-ecef7473bbaf"), "Mow the lawn"));
        todoRepository.save(new Todo(UUID.fromString("f1e14ffa-2b61-4259-b2fc-380882aa5fb2"), "Bake a cake"));


        mockMvc.perform(get("/todo?ids=45ec5bfa-7813-4565-acdd-2d8c5662e36b,d76fa7ee-9d30-4632-8fab-ecef7473bbaf"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is("45ec5bfa-7813-4565-acdd-2d8c5662e36b")))
                .andExpect(jsonPath("$[1].id", is("d76fa7ee-9d30-4632-8fab-ecef7473bbaf")));
    }

    @Test
    public void çreate_addsEntry() throws Exception {
        Todo todo = new Todo(UUID.fromString("45ec5bfa-7813-4565-acdd-2d8c5662e36b"), "Take out the trash");

        mockMvc.perform(post("/todo").contentType(MediaType.APPLICATION_JSON_UTF8).content(json(todo)))
                .andExpect(status().isCreated())
                .andExpect(header().string("location",
                        is("http://localhost/todo/45ec5bfa-7813-4565-acdd-2d8c5662e36b")))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is("45ec5bfa-7813-4565-acdd-2d8c5662e36b")))
                .andExpect(jsonPath("$.description", is("Take out the trash")));
    }

    @Test
    public void show_returnsEntry() throws Exception {
        todoRepository.save(new Todo(UUID.fromString("45ec5bfa-7813-4565-acdd-2d8c5662e36b"), "Take out the trash"));

        mockMvc.perform(get("/todo/45ec5bfa-7813-4565-acdd-2d8c5662e36b"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is("45ec5bfa-7813-4565-acdd-2d8c5662e36b")))
                .andExpect(jsonPath("$.description", is("Take out the trash")));
    }

    @Test
    public void delete_removesEntry() throws Exception {
        UUID id = UUID.fromString("45ec5bfa-7813-4565-acdd-2d8c5662e36b");
        todoRepository.save(new Todo(id, "Take out the trash"));

        mockMvc.perform(delete("/todo/45ec5bfa-7813-4565-acdd-2d8c5662e36b"))
                .andExpect(status().isOk());
        assertThat(todoRepository.findOne(id).isPresent(), is(false));
    }

    @Test
    public void update_modifiesEntry() throws Exception {
        UUID id = UUID.fromString("45ec5bfa-7813-4565-acdd-2d8c5662e36b");
        todoRepository.save(new Todo(id, "Take out the trash"));

        mockMvc.perform(put("/todo/45ec5bfa-7813-4565-acdd-2d8c5662e36b").contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(json(new Todo(id, "Trash be gone"))))
                .andExpect(status().isNoContent());

        assertThat(todoRepository.findOne(id).get().getDescription(), is("Trash be gone"));
    }


    private String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.converter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }
}
