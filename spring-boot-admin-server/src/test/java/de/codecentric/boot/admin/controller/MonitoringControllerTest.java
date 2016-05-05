package de.codecentric.boot.admin.controller;


import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import de.codecentric.boot.admin.model.Application;
import de.codecentric.boot.admin.model.StatusInfo;
import de.codecentric.boot.admin.registry.ApplicationRegistry;
import de.codecentric.boot.admin.registry.HashingApplicationUrlIdGenerator;
import de.codecentric.boot.admin.registry.store.SimpleApplicationStore;

public class MonitoringControllerTest {

    private MonitoringController monitoringController;
    private ApplicationRegistry registry;

    @Before
    public void setup() {
        registry = new ApplicationRegistry(new SimpleApplicationStore(),
                new HashingApplicationUrlIdGenerator());
        registry.setApplicationEventPublisher(Mockito.mock(ApplicationEventPublisher.class));
        monitoringController = new MonitoringController(registry);
    }

    @Test
    public void enableMonitoring(){
        Application app = registry.register(Application.create("FOO")
                .withHealthUrl("http://localhost/mgmt/health").build());

        ResponseEntity<?> response = monitoringController.enableMonitoring(app.getId());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(app, response.getBody());
        assertEquals(app.getStatusInfo(), StatusInfo.ofUnknown());
    }

    @Test
    public void enableMonitoring_notFound(){
        registry.register(Application.create("FOO")
                .withHealthUrl("http://localhost/mgmt/health").build());

        ResponseEntity<?> response = monitoringController.enableMonitoring("unknown");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void disableMonitoring(){
        Application app = registry.register(Application.create("FOO")
                .withHealthUrl("http://localhost/mgmt/health").build());

        ResponseEntity<?> response = monitoringController.disableMonitoring(app.getId());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(app, response.getBody());
    }

    @Test
    public void disableMonitoring_notFound(){
        registry.register(Application.create("FOO")
                .withHealthUrl("http://localhost/mgmt/health").build());

        ResponseEntity<?> response = monitoringController.disableMonitoring("unknown");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
