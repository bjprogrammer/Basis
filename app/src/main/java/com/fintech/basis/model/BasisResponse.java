package com.fintech.basis.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class BasisResponse {
        public List<BasisData> getData() {
            return results;
        }

        public void setData(List<BasisData> results) {
            this.results = results;
        }

        @SerializedName("data")
        @Expose
        private List<BasisData> results;


        public class BasisData{
            @SerializedName("text")
            @Expose
            private String text;

            @SerializedName("id")
            @Expose
            private String id;

            public String getText() {
                return text;
            }

            public void setText(String text) {
                this.text = text;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }
         }
    }
