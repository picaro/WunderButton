package com.op.wunderbutton.model;

import java.io.Serializable;

import lombok.Data;

/**
 * Created by Alex on 12.04.2015.
 */
@Data
public class WList implements Serializable {



    private Integer id;

    private String created_at;

    private String title;

    private String list_type;

    private String type;

    private Integer revision;

}
