package de.tp.projects.usr.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.tp.projects.usr.config.ControllerTestConfig;
import de.tp.projects.usr.controller.model.UserDto;
import de.tp.projects.usr.jpa.entitites.User;
import de.tp.projects.usr.jpa.repositories.UserRepository;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith({SpringExtension.class, RestDocumentationExtension.class})
@SpringBootTest
@Import(ControllerTestConfig.class)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@AutoConfigureRestDocs
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext,
                      RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .alwaysDo(document("{method-name}",preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())))
                .build();
    }

    @Test
    void testGetEmptyUsers () throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().string(is(IsEqual.equalTo("[]"))));
    }

    @Test
    void testGetUsers() throws Exception {
        // given
        final String givenName = "name";
        final String givenVorname = "vorname";
        final String givenEmail = "vorname.name@test.io";

        User user = new User();
        user.setName(givenName);
        user.setVorname(givenVorname);
        user.setEmail(givenEmail);

        // when
        userRepository.saveAndFlush(user);

        // then
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].vorname").value(givenVorname))
                .andExpect(jsonPath("$.[0].name").value(givenName))
                .andExpect(jsonPath("$.[0].email").value(givenEmail));
    }

    @Test
    void testPostNewUser() throws Exception {
        // given
        final String givenMail = "mail@mail.com";
        final String givenName = "nachname";
        final String givenVorname = "hans";

        UserDto givenDto = new UserDto();
        givenDto.setEmail(givenMail);
        givenDto.setName(givenName);
        givenDto.setVorname(givenVorname);

        final String givenJsonString = objectMapper.writeValueAsString(givenDto);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        // when
        MvcResult result = mockMvc.perform(post("/api/users")
                .headers(headers)
                .content(givenJsonString))
                .andExpect(status().is(201)).andReturn();

        UserDto resultDto = objectMapper.readValue(result.getResponse().getContentAsString(), UserDto.class);

        Optional<User> optUser = userRepository.findById(resultDto.getId());

        // then
        assertTrue(optUser.isPresent());
        User user = optUser.get();
        assertEquals(givenMail, user.getEmail());
        assertEquals(givenName, user.getName());
        assertEquals(givenVorname, user.getVorname());
    }

    @Test
    void testPostExistingUser() throws Exception {
        // given
        final String givenMail = "mail@mail.com";
        final String givenName = "nachname";
        final String givenVorname = "hans";

        UserDto givenDto = new UserDto();
        givenDto.setEmail(givenMail);
        givenDto.setName(givenName);
        givenDto.setVorname(givenVorname);

        final String givenJsonString = objectMapper.writeValueAsString(givenDto);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        // when && then
        mockMvc.perform(post("/api/users")
                .headers(headers)
                .content(givenJsonString))
                .andExpect(status().is(201));

        MvcResult result = mockMvc.perform(post("/api/users")
                .headers(headers)
                .content(givenJsonString))
                .andExpect(status().is(409))
                .andReturn();

        String errorMessage = result.getResponse().getErrorMessage();
        assertNotNull(errorMessage);
        assertFalse(errorMessage.isEmpty());
        assertTrue(errorMessage.contains("exists"));
    }

    @Test
    void testGetUserById() throws Exception {
        // given
        final String givenMail = "mail@mail.com";
        final String givenName = "nachname";
        final String givenVorname = "hans";

        User user = new User();
        user.setEmail(givenMail);
        user.setName(givenName);
        user.setVorname(givenVorname);

        // when
        user = userRepository.saveAndFlush(user);

        // then
        assertEquals(1L, user.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

        mockMvc.perform(get("/api/users/1")
                .headers(headers))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void testGetUserNotFound() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/users/1"))
                .andExpect(status().is(404))
                .andReturn();

        String errorMessage = result.getResponse().getErrorMessage();
        assertNotNull(errorMessage);
        assertFalse(errorMessage.isEmpty());
        assertTrue(errorMessage.contains("not found"));
    }

    @Test
    void testUpdateUser() throws Exception {
        // given
        final String givenMail = "mail@mail.com";
        final String givenName = "nachname";
        final String givenVorname = "hans";
        final String updateVorname = "user";

        User user = new User();
        user.setEmail(givenMail);
        user.setName(givenName);
        user.setVorname(givenVorname);

        // when
        user = userRepository.saveAndFlush(user);

        UserDto dto = new UserDto();
        dto.setVorname(updateVorname);
        dto.setName(givenName);
        dto.setEmail(givenMail);
        dto.setId(user.getId());

        final String json = objectMapper.writeValueAsString(dto);
        final HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        // then
        mockMvc.perform(put("/api/users/1").content(json).headers(headers))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void testUpdateUserIdMismatch() throws Exception {
        // given
        UserDto dto = new UserDto();
        dto.setId(2L);
        dto.setName("test");
        dto.setVorname("test");
        dto.setEmail("test@test.io");

        final String json = objectMapper.writeValueAsString(dto);
        final HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        // when && then
        MvcResult result = mockMvc.perform(put("/api/users/1").content(json).headers(headers))
                .andExpect(status().is(400)).andReturn();

        final String msg = result.getResponse().getErrorMessage();
        assertNotNull(msg);
        assertFalse(msg.isEmpty());
        assertTrue(msg.contains("Path and Payload ID not equal"));
    }

    @Test
    void testDeleteExistingUser() throws Exception {
        // given
        User user = new User();
        user.setEmail("test@test.de");
        user.setName("name");
        user.setVorname("vorname");

        // when
        user = userRepository.saveAndFlush(user);

        // then
        mockMvc.perform(delete("/api/users/"+user.getId()))
                .andExpect(status().is(200));
    }

    @Test
    void testDeleteNotExistingUser() throws Exception {
        mockMvc.perform(delete("/api/users/1")).andExpect(status().is(404));
    }

    @Test
    void testFindUsersByVornameEmptyList() throws Exception {
        // given
        User user = new User();
        user.setName("name");
        user.setVorname("bla");
        user.setEmail("mail");

        final HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

        // when
        userRepository.saveAndFlush(user);

        // then
        mockMvc.perform(get("/api/users/findByVorname/test").headers(headers))
                .andExpect(status().is(200))
                .andExpect(content().json("[]"));
    }

    @Test
    void testFindUsersByVornameResults() throws Exception {
        // given

        final String vorname = "bla";

        User user = new User();
        user.setName("name");
        user.setVorname(vorname);
        user.setEmail("mail");

        User user2 = new User();
        user2.setName("name2");
        user2.setVorname(vorname);
        user2.setEmail("mail2");

        final HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

        // when
        userRepository.saveAndFlush(user);
        userRepository.saveAndFlush(user2);

        // then
        mockMvc.perform(get("/api/users/findByVorname/" + vorname).headers(headers))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$", Matchers.hasSize(2)));
    }
}
