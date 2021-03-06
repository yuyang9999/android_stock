package com.naughtypiggy.android.stock.network.model;

import java.util.List;

/**
 * Created by yangyu on 5/12/17.
 */

public class ApiResp {
    public static class ApiGeneralResp {
        public boolean hasError;
        public String errorMsg;
        public Object response;
    }

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

    public static class AuthResp {
        public String access_token;
        public String token_type;
        public String refresh_token;
        public int expires_in;
        public String scope;
    }

    public static class ApiProfileSymbolResp {
        public boolean hasError;
        public String errorMsg;
        public List<ProfileStock> response;
    }

    public static class ApiQueryStockResp {
        public List<StockQueryInfo> resp;
    }

    public static class ApiStockHistoryResp {
        public boolean hasError;
        public String errorMsg;
        public List<StockHistory> response;
    }
}
