package com.tmavn.sample.dto;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.tmavn.sample.entity.Note;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(value = Include.NON_NULL)
public class OrderDataDTO {

    private String id;

    private String description;

    private String state;

    private String orderDate;

    private String modifyDate;
    
    private Set<Note> notes;
}
