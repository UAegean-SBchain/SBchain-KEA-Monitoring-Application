/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.ethereumserviceapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author nikos
 */
@Getter
@Setter
@NoArgsConstructor
public class ColletionOfCredentials {

    @JsonProperty("credentialIds")
    private CredsAndExp[] credentialIds;
}
