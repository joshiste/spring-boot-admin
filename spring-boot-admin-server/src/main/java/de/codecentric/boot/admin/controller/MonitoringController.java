package de.codecentric.boot.admin.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.codecentric.boot.admin.model.Application;
import de.codecentric.boot.admin.registry.ApplicationRegistry;

/**
 *
 * REST controller for controlling monitoring on managed applications
 *
 * @author Vlad Oltean
 */
@RestController
@RequestMapping("/api/applications/monitor")
public class MonitoringController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MonitoringController.class);

    private final ApplicationRegistry registry;

    public MonitoringController(ApplicationRegistry registry) {
        this.registry = registry;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public ResponseEntity<Application> enableMonitoring(@PathVariable String id){
        LOGGER.debug("Enabling monitoring on application with ID '{}'", id);
        Application app = registry.enableMonitoring(id);
        if (app != null) {
            return new ResponseEntity<>(app, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Application> disableMonitoring(@PathVariable String id){
        LOGGER.debug("Disabling monitoring on application with ID '{}'", id);
        Application app = registry.disableMonitoring(id);
        if (app != null) {
            return new ResponseEntity<>(app, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
