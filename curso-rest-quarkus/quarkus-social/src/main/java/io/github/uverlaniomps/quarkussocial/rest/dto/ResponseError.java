package io.github.uverlaniomps.quarkussocial.rest.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.ws.rs.core.Response;
import lombok.Data;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class ResponseError {

    private String message;
    private Collection<FieldError> errors;
    public static final int UNPROCESSABLE_ENTITY = 422;

    public ResponseError(String message, Collection<FieldError> errors) {
        this.message = message;
        this.errors = errors;
    }

    // metodo para retornar o response de error com a mensagem de error (json)
    public static <T> ResponseError createFormValidation(Set<ConstraintViolation<CreateUserRequest>> violations) {
        List<FieldError> errors = violations.stream().map(cv -> new FieldError(cv.getPropertyPath().toString(), cv.getMessage())).collect(Collectors.toList());

        String message = "Validation Error";
        var responseError = new ResponseError(message, errors);
        return responseError;
    }

    //metodo para retornar o codigo de erro
    public Response errorWithStatus(int code) {
        return Response.status(code).entity(this).build();
    }
}
