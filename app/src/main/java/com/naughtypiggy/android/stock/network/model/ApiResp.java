package com.naughtypiggy.android.stock.network.model;

import java.util.List;

/**
 * Created by yangyu on 5/12/17.
 */

public class ApiResp {
    public static class ApiBooleanResp {
        public boolean hasError;
        public String errorMsg;
        public boolean response;
    }

    public static class ApiProfileResp {
        public boolean hasError;
        public String errorMsg;
        public List<Profile> response;
    }
}
