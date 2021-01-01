package com.osrsstreamers.twitch;

import lombok.Data;

import java.util.List;

@Data
public class TwitchAuthenticateResponse {

    public String client_id;

    public String login;

    public List<String> scopes;

    public String user_id;

    private Long expires_in;

}
