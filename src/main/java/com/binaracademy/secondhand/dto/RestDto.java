package com.binaracademy.secondhand.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RestDto {

    private Integer status;
    private String message;
    private Object data;
}
