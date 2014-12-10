package com.ros.smartrocket;

import android.test.ApplicationTestCase;
import android.test.IsolatedContext;

public class MatrixAppTest extends ApplicationTestCase<App> {
    public MatrixAppTest() {
        super(App.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        setContext(new IsolatedContext(getContext().getContentResolver(), getContext()));
    }

    public void testInstance() {
        createApplication();

        assertNotNull(App.getInstance());
        assertEquals(getApplication(), App.getInstance());
    }

    public void testMyAccountCreation() {
        createApplication();

        App app = getApplication();
        assertNotNull(app.getMyAccount());

        terminateApplication();

        assertNull(app.getMyAccount());
    }

}
