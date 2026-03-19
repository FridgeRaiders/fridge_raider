package com.example.foodproject.model;

import com.sun.jdi.IntegerValue;
import org.junit.jupiter.api.Test;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class UserTest {
    private User userNoDisplayName = new User("test@test.com");
    private User userWithDisplayName = new User("test2@test2.com", "mr test");

    @Test
    public void userCreatedNoDisplayName() {
        assertNull(userNoDisplayName.getDisplayName());
    }

    @Test
    public void userCreatedWithDisplayName() {
        assertThat(userWithDisplayName.getDisplayName(), containsString("mr test"));

    }
    }

//assertThat(comment.getContent(), containsString("This is a comment"));
//        }
