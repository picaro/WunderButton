package com.op.wunderbutton.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.Data;

/**
 * Created by Alex on 06.04.2015.
 */
@Data //AllArgsConstructor
public class TokenRequest implements Serializable {

    @SerializedName("client_id")
    private String client_id;

    @SerializedName("client_secret")
    private String client_secret;

    @SerializedName("code")
    private String code;

    //resp
    @SerializedName("access_token")
    private String access_token;

    @SerializedName("token_type")
    private String token_type;

}
