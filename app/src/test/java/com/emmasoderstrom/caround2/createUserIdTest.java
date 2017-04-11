package com.emmasoderstrom.caround2;

import android.graphics.Bitmap;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Created by User on 2017-04-11.
 */

public class createUserIdTest {


    @Test
    public void canCreateUser(){


        /*Login login = new Login();
        String result = login.emailReplaceInvaid("emma?soderstrom@gmail.com");
        assertEquals("fel", "emma?soderstrom@gmail%1%com", result);

        Login loginUpper = new Login();
        String resultUpper = loginUpper.emailReplaceInvaid("Emmasoderstrom@gmail.com");
        assertEquals("fel", "emmasoderstrom@gmail%1%com", resultUpper);*/


        Login loginDot = new Login();
        String resultDot = loginDot.emailReplaceInvaid("emma.soderstrom@gmail.com");
        assertEquals("fel", "emma%1%soderstrom@gmail%1%com", resultDot);

        Login loginHas = new Login();
        String resultHas = loginHas.emailReplaceInvaid("emma#soderstrom@gmail.com");
        assertEquals("fel", "emma%2%soderstrom@gmail%1%com", resultHas);

        Login loginDol = new Login();
        String resultDol = loginDol.emailReplaceInvaid("emma$soderstrom@gmail.com");
        assertEquals("fel", "emma%3%soderstrom@gmail%1%com", resultDol);

        Login loginHak1 = new Login();
        String resultHak1 = loginHak1.emailReplaceInvaid("emma[soderstrom@gmail.com");
        assertEquals("fel", "emma%4%soderstrom@gmail%1%com", resultHak1);

        Login loginHak2 = new Login();
        String resultHak2 = loginHak2.emailReplaceInvaid("emma]soderstrom@gmail.com");
        assertEquals("fel", "emma%5%soderstrom@gmail%1%com", resultHak2);



    }

}
