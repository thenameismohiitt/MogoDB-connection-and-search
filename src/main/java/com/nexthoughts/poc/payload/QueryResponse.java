package com.nexthoughts.poc.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class QueryResponse {

    private String databaseName;
    private String collectionName;
    private String fieldName;
    private String id;
}
