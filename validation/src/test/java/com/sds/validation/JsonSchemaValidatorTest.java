package com.sds.validation;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class JsonSchemaValidatorTest {

    JsonSchemaValidator v = new JsonSchemaValidator();
    
    @Test
    public void testValidElement() throws IOException {
        
        byte[] data = IOUtils.toByteArray(
                this.getClass().getResourceAsStream("/json/good.json"));
        v.validate(data, "message", "Sds.Imaging.Domain.Commands:GenerateImage");
    }
    
    @Test
    public void testValidMassTransitMessage() throws IOException {
        
        byte[] data = IOUtils.toByteArray(
                this.getClass().getResourceAsStream("/json/good.json"));
        v.validateMassTransitMessage(data);
    }
    
    @Test(expected=JsonValidationException.class)
    public void testNoCorrelation() throws IOException {
        
        byte[] data = IOUtils.toByteArray(
                this.getClass().getResourceAsStream("/json/no-correlation.json"));
        try {
            v.validateMassTransitMessage(data);
        } catch (JsonValidationException e) {
            Assert.assertEquals("[#: required key [correlationId] not found]", e.getMessage());
            throw e;
        }
    }
    
    @Ignore
    @Test(expected=JsonValidationException.class)
    public void testNullCorrelation() throws IOException {
        
        byte[] data = IOUtils.toByteArray(
                this.getClass().getResourceAsStream("/json/null-correlation.json"));
        try {
            v.validateMassTransitMessage(data);
        } catch (JsonValidationException e) {
            throw e;
        }
    }
    
    @Ignore
    @Test(expected=JsonValidationException.class)
    public void testBadGuid() throws IOException {
        
        byte[] data = IOUtils.toByteArray(
                this.getClass().getResourceAsStream("/json/bad-guid.json"));
        try {
            v.validateMassTransitMessage(data);
        } catch (JsonValidationException e) {
            throw e;
        }
    }
    
    @Test
    public void testValidObject() throws IOException {
        byte[] data = IOUtils.toByteArray(
                this.getClass().getResourceAsStream("/json/good-message.json"));
        v.validate(data, "Sds.Imaging.Domain.Commands:GenerateImage");
    }

    @Test(expected=JsonValidationException.class)
    public void testSchemaNotFound() throws IOException {
        byte[] data = IOUtils.toByteArray(
                this.getClass().getResourceAsStream("/json/good.json"));
        v.validate(data, "message", "Sds.Imaging.Domain.Commands:Foo");
    }

}
