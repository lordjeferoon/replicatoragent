package com.hacom.replicatoragent.util;

import lombok.Data;

@Data
public class ReplicatorObject {
    String collection;
    String operation;
    Object document;
}
