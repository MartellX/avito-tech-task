package org.martellx.avitotech.other;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ApiAnswer {

    @JsonIgnore
    HttpStatus status;

    @JsonProperty
    String message;



    public ApiAnswer(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    @JsonIgnore
    public ResponseEntity<Object>  getResponseEntity() {
        return new ResponseEntity<>(this, status);
    }

    @JsonGetter
    public String getStatus() {
        return status.getReasonPhrase();
    }
}
