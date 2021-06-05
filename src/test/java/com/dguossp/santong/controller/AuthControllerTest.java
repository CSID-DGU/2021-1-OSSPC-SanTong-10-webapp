package com.dguossp.santong.controller;

import com.dguossp.santong.controller.auth.AuthController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void loginTest() throws Exception {
        String nickname = "클라이언트A";
        String passowrd = "1";

        mvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().string("test"));
    }

}
