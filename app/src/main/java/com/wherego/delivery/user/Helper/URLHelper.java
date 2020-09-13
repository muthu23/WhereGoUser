package com.wherego.delivery.user.Helper;

public class URLHelper {


 public final static long DEFAULT_CONNECTION_TIME_OUT = 60;
 public final static long DEFAULT_READ_TIME_OUT = 60;
 public final static long DEFAULT_WRITE_TIME_OUT = 60;
 public static final String REQUEST_WITH = "XMLHttpRequest";
 public static final String CONTENT_TYPE = "application/json";
 public static final String ACCEPT_TYPE = "application/json";

    public static final String base = "http://Delivery.wherego.com.my/"; // Base URL

    public static final String REDIRECT_URL = base;
    public static final String REDIRECT_SHARE_URL = "http://maps.google.com/maps?q=loc:";
    public static final String APP_URL = "";
    public static final int client_id = 2;
    public static final String client_secret = "WifS1rMi3LvuorP1G2UdtKZairUNSH2iMqrKivPf";
    public static final String STRIPE_TOKEN = "pk_test_qWAKEoT8qG3a7CP41i3jZT8300aq3poee3";
    public static final String login = base+"oauth/token";
    public static final String register = base+"api/user/signup";
    public static final String UserProfile = base+"api/user/details";
    public static final String UseProfileUpdate = base+"api/user/update/profile";
    public static final String UploadImages = base+"api/user/additem";
    public static final String getUserProfileUrl = base+"api/user/details";
    public static final String GET_SERVICE_LIST_API = base+"api/user/services";
    public static final String REQUEST_STATUS_CHECK_API = base+"api/user/request/check";
    public static final String ESTIMATED_FARE_DETAILS_API = base+"api/user/estimated/fare";
    public static final String SEND_REQUEST_API = base+"api/user/send/request";
    public static final String CANCEL_REQUEST_API = base+"api/user/cancel/request";
    public static final String PAY_NOW_API = base+"api/user/payment";
    public static final String RATE_PROVIDER_API = base+"api/user/rate/provider";
    public static final String CARD_PAYMENT_LIST = base+"api/user/card";
    public static final String ADD_CARD_TO_ACCOUNT_API = base+"api/user/card";
    public static final String DELETE_CARD_FROM_ACCOUNT_API = base+"api/user/card/destory";
    public static final String GET_HISTORY_API = base+"api/user/trips";
    public static final String GET_HISTORY_DETAILS_API = base+"api/user/trip/details";
    public static final String addCardUrl = base+"api/user/add/money";
    public static final String COUPON_LIST_API = base+"api/user/promocodes";
    public static final String ADD_COUPON_API = base+"api/user/promocode/add";
    public static final String CHANGE_PASSWORD_API = base+"api/user/change/password";
    public static final String UPCOMING_TRIP_DETAILS = base+"api/user/upcoming/trip/details";
    public static final String UPCOMING_TRIPS = base+"api/user/upcoming/trips";
    public static final String GET_PROVIDERS_LIST_API = base+"api/user/show/providers";
    public static final String FORGET_PASSWORD = base + "api/user/forgot/password";
    public static final String RESET_PASSWORD = base + "api/user/reset/password";

    public static final String FACEBOOK_LOGIN = base + "api/user/auth/facebook";
    public static final String GOOGLE_LOGIN = base + "api/user/auth/google";
    public static final String LOGOUT = base + "api/user/logout";
    public static final String HELP = base + "api/user/help";
    public static final String terms = base + "terms";

    public static final String CURRENT_TRIP = base + "api/user/live/trips";
    public static final String ADD_PAYMENT = base + "api/user/addpay/payment";
 public static final String SAVE_LOCATION = base + "api/user/createDefaultLocation";
 public static final String GET_USERREVIEW = base+"api/user/review";
   public static final String get_lost_item = base + "api/user/lost_item?trip_id=";
   public static final String add_lost_item = base + "api/user/add_lost_item";
   public static final  String ChatGetMessage = base + "api/user/firebase/getChat?request_id=";

    public static final String check_refrral = base + "api/user/check-referralcode";
    public static final String privacyPolicy = base + "/privacy-policy";
    public static final String termcondition = base + "/terms-conditions";
    public static final String refund = base + "/refund-policy";
}
