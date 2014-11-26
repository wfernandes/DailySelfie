package com.coursera.wfernandes.dailyselfie;

import junit.framework.Assert;
import junit.framework.TestCase;

public class UtilTests extends TestCase {

    public void testGetReadableSelfieName() {

        String readableName = Utils.getReadableSelfieName("SELFIE_20141124_091906_148298876.jpg");
        Assert.assertEquals("Nov 24, 2014 9:19:06 AM", readableName);
    }
}
