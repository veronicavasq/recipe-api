package com.recipes.demo.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorInfo {

    private String code;
    private HashMap<String, Object> additionalData;

    public ErrorInfo(String code, Object messageError) {
        this.code = code;
        this.additionalData = new HashMap<>();
        this.additionalData.put("message", messageError);
    }

}
