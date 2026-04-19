package com.grocery.exception;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalExceptionHandlerClass {


    //	Exception Handler for Exception class
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> exceptionClassHandler(Exception pe, WebRequest req){

        ErrorDetails err = new ErrorDetails(LocalDateTime.now(), pe.getMessage(), req.getDescription(false));

        return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
    }

    //	Data validation exception handler
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDetails> dataValidationExceptionHandler(MethodArgumentNotValidException ex){

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        ErrorDetails err = new ErrorDetails();
        err.setTimestamp(LocalDateTime.now());
        err.setMessage("Validation Failed");
        err.setDetails(errors);

        return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
    }


    //	Exception Handler for No URI found or wrong uri
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorDetails> noUriHandlerFoundException(NoHandlerFoundException nfe,WebRequest req){

        ErrorDetails err = new ErrorDetails(LocalDateTime.now(),nfe.getMessage(),req.getDescription(false));

        return new ResponseEntity<ErrorDetails>(err,HttpStatus.OK);
    }

    //	Exception handler for Cart  Exception
    @ExceptionHandler(CartException.class)
    public ResponseEntity<ErrorDetails> cartExceptionHandler(CartException ce, WebRequest req){

        ErrorDetails err = new ErrorDetails(LocalDateTime.now(), ce.getMessage(), req.getDescription(false));
        return new ResponseEntity<ErrorDetails>(err,HttpStatus.BAD_REQUEST);
    }


    //	Exception handler for Category  Exception
    @ExceptionHandler(CategoryException.class)
    public ResponseEntity<ErrorDetails> categoryExceptionHandler(CategoryException ce, WebRequest req){

        ErrorDetails err = new ErrorDetails(LocalDateTime.now(), ce.getMessage(), req.getDescription(false));
        return new ResponseEntity<ErrorDetails>(err,HttpStatus.BAD_REQUEST);
    }


    //	Exception handler for OrderItem  Exception
    @ExceptionHandler(OrderItemException.class)
    public ResponseEntity<ErrorDetails> orderItemException(OrderItemException oie, WebRequest req){

        ErrorDetails err = new ErrorDetails(LocalDateTime.now(), oie.getMessage(), req.getDescription(false));
        return new ResponseEntity<ErrorDetails>(err,HttpStatus.BAD_REQUEST);
    }


    //	Exception handler for Order  Exception
    @ExceptionHandler(OrderException.class)
    public ResponseEntity<ErrorDetails> orderException(OrderException oe, WebRequest req){

        ErrorDetails err = new ErrorDetails(LocalDateTime.now(), oe.getMessage(), req.getDescription(false));
        return new ResponseEntity<ErrorDetails>(err,HttpStatus.BAD_REQUEST);
    }

    //	Exception handler for Payment  Exception
    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ErrorDetails> paymentException(PaymentException pe, WebRequest req){

        ErrorDetails err = new ErrorDetails(LocalDateTime.now(), pe.getMessage(), req.getDescription(false));
        return new ResponseEntity<ErrorDetails>(err,HttpStatus.BAD_REQUEST);
    }


    //	Exception handler for Product  Exception
    @ExceptionHandler(ProductException.class)
    public ResponseEntity<ErrorDetails> productException(ProductException pe, WebRequest req){

        ErrorDetails err = new ErrorDetails(LocalDateTime.now(), pe.getMessage(), req.getDescription(false));
        return new ResponseEntity<ErrorDetails>(err,HttpStatus.BAD_REQUEST);
    }


    //	Exception handler for User  Exception
    @ExceptionHandler(UserException.class)
    public ResponseEntity<ErrorDetails> userException(UserException ue, WebRequest req){

        ErrorDetails err = new ErrorDetails(LocalDateTime.now(), ue.getMessage(), req.getDescription(false));
        return new ResponseEntity<ErrorDetails>(err,HttpStatus.BAD_REQUEST);
    }



}
